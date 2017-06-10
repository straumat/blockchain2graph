package com.oakinvest.b2g.batch.bitcoin.step1.blocks;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.oakinvest.b2g.configuration.ParametersConfiguration.BITCOIND_BUFFER_SIZE;
import static com.oakinvest.b2g.configuration.ParametersConfiguration.BITCOIN_BLOCK_GENERATION_DELAY;

/**
 * Bitcoin import blocks batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinBatchBlocks extends BitcoinBatchTemplate {

    /**
     * How many milli seconds in 1 minute.
     */
    private static final float MILLISECONDS_IN_ONE_MINUTE = 60F * 1000F;

    /**
     * Bitcoind cache loader.
     */
    private BitcoindCacheLoader bitcoindCacheLoader;

	/**
	 * Log prefix.
	 */
	private static final String PREFIX = "Blocks batch";

    /**
     * Last block count value.
     */
    private long lastBlockCountValue = -1;

    /**
     * Last block count access.
     */
    private long lastBlockCountAccess = -1;


    /**
	 * Constructor.
	 *
	 * @param newBlockRepository       blockRepository
	 * @param newAddressRepository     addressRepository
	 * @param newTransactionRepository transactionRepository
	 * @param newBitcoindService       bitcoindService
	 * @param newStatus                status
     * @param newBitcoindCacheLoader bitcoindCacheLoader
	 */
	public BitcoinBatchBlocks(final BitcoinBlockRepository newBlockRepository, final BitcoinAddressRepository newAddressRepository, final BitcoinTransactionRepository newTransactionRepository, final BitcoindService newBitcoindService, final StatusService newStatus, final BitcoindCacheLoader newBitcoindCacheLoader) {
		super(newBlockRepository, newAddressRepository, newTransactionRepository, newBitcoindService, newStatus);
        bitcoindCacheLoader = newBitcoindCacheLoader;
	}

	/**
	 * Returns the log prefix to display in each log.
	 */
	@Override
	public final String getLogPrefix() {
		return PREFIX;
	}

    /**
     * Return getblockcount (The result stays in cache for 10 minutes).
     *
     * @return the number of blocks in the block chain. -1 if error.
     */
	private long getBlockCount() {
        // Getting the time elapsed since last call to getblockcount
        float elapsedMinutesSinceLastCall = (System.currentTimeMillis() - lastBlockCountAccess) / MILLISECONDS_IN_ONE_MINUTE;

        if (elapsedMinutesSinceLastCall < BITCOIN_BLOCK_GENERATION_DELAY && lastBlockCountAccess != -1) {
            // If the last call to getblockcount was made less than 10 minutes ago, we return the result in cache.
            return lastBlockCountValue;
        } else {
            // Else we get it from the bitcoind server.
            try {
                GetBlockCountResponse blockCountResponse = getBitcoindService().getBlockCount();
                if (blockCountResponse.getError() == null) {
                    lastBlockCountValue = blockCountResponse.getResult();
                    lastBlockCountAccess = System.currentTimeMillis();
                    return lastBlockCountValue;
                }  else {
                    // Error while retrieving the number of blocks in bitcoind.
                    addError("Error getting the number of blocks : " + blockCountResponse.getError());
                    lastBlockCountAccess = -1;
                    return -1;
                }
            } catch (Exception e) {
                // Error while retrieving the number of blocks in bitcoind.
                addError("Error getting the number of blocks : " + e.getMessage(), e);
                lastBlockCountAccess = -1;
                return -1;
            }
        }
    }

	/**
	 * Return the block to process.
	 *
	 * @return block to process.
	 */
	@Override
    protected final Long getBlockHeightToProcess() {
		// We retrieve the next block to process according to the database.
		Long blockToProcess = getBlockRepository().count() + 1;
		final long totalBlockCount = getBlockCount();

		// We check if that next block exists by retrieving the block count.
        if (totalBlockCount != -1) {
				// We update the global status of blockcount.
				if (totalBlockCount != getStatus().getTotalBlockCount()) {
					getStatus().setTotalBlockCount(totalBlockCount);
				}
				// We return the block to process.
				if (blockToProcess <= totalBlockCount) {
				    // We load in cache
                    if (blockToProcess + BITCOIND_BUFFER_SIZE <= totalBlockCount) {
                        bitcoindCacheLoader.loadCache(blockToProcess);
                    }
					// If there is still block after this one, we continue.
                    return blockToProcess;
				} else {
					return null;
				}
			} else {
				// Error while retrieving the number of blocks in bitcoind.
				return null;
			}
	}

	/**
	 * Process block.
	 *
	 * @param blockHeight block height to process.
	 */
	@Override
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	protected final BitcoinBlock processBlock(final long blockHeight) {
		Optional<BitcoindBlockData> blockData = getBitcoindService().getCachedBlockData(blockHeight);
		// -------------------------------------------------------------------------------------------------------------
		// If we have the data.
		if (blockData.isPresent()) {
			// ---------------------------------------------------------------------------------------------------------
			// Then, if the block doesn't exists, we map it to save it.
			BitcoinBlock block = getBlockRepository().findByHash(blockData.get().getBlock().getHash());
			if (block == null) {
				block = getMapper().blockResultToBitcoinBlock(blockData.get().getBlock());
			}
			addLog("This block has " + block.getTx().size() + " transaction(s)");

			// -----------------------------------------------------------------------------------------------------
			// We set the previous and the next block.
			BitcoinBlock previousBlock = getBlockRepository().findByHash(block.getPreviousBlockHash());
			block.setPreviousBlock(previousBlock);
			if (previousBlock != null) {
				previousBlock.setNextBlock(block);
				getBlockRepository().save(previousBlock);
			}

			// ---------------------------------------------------------------------------------------------------------
			// We return the block.
			return block;
		} else {
			// Or nothing if we did not retrieve the data.
			addError("No response from bitcoind for block n°" + getFormattedBlockHeight(blockHeight));
			return null;
		}
	}

	/**
	 * Return the state to set to the block that has been processed.
	 *
	 * @return state to set of the block that has been processed.
	 */
	@Override
	protected final BitcoinBlockState getNewStateOfProcessedBlock() {
		return BitcoinBlockState.BLOCK_IMPORTED;
	}

}
