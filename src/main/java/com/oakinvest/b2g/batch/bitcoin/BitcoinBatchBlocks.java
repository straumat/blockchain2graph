package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.repository.bitcoin.BitcoinRepositories;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoinDataServiceCacheLoader;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.oakinvest.b2g.configuration.ParametersConfiguration.BITCOIND_BUFFER_SIZE;

/**
 * Bitcoin import blocks batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinBatchBlocks extends BitcoinBatchTemplate {

    /**
     * Bitcoind cache loader.
     */
    private final BitcoinDataServiceCacheLoader bitcoindCacheLoader;

	/**
	 * Log prefix.
	 */
	private static final String PREFIX = "Blocks batch";

    /**
     * Constructor.
     *
     * @param newBitcoinRepositories    bitcoin repositories
     * @param newBitcoinDataService     bitcoin data service
     * @param newStatus                 status
     * @param newBitcoindCacheLoader    bitcoin cache loader
     */
    public BitcoinBatchBlocks(final BitcoinRepositories newBitcoinRepositories, final BitcoinDataService newBitcoinDataService, final StatusService newStatus, final BitcoinDataServiceCacheLoader newBitcoindCacheLoader) {
        super(newBitcoinRepositories, newBitcoinDataService, newStatus);
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
	 * Return the block to process.
	 *
	 * @return block to process.
	 */
	@Override
    protected final Optional<Long> getBlockHeightToProcess() {
		// We retrieve the next block to process according to the database.
		Long blockToProcess = getBlockRepository().count() + 1;
        final Optional<Long> totalBlockCount = getBitcoinDataService().getBlockCount();

		// We check if that next block exists by retrieving the block count.
        if (totalBlockCount.isPresent()) {
				// We update the global status of blockcount (if needed).
				if (totalBlockCount.get() != getStatus().getTotalBlockCount()) {
					getStatus().setTotalBlockCount(totalBlockCount.get());
				}
				// We return the block to process.
				if (blockToProcess <= totalBlockCount.get()) {
				    // We load the cache.
                    if (blockToProcess + BITCOIND_BUFFER_SIZE <= totalBlockCount.get()) {
                        bitcoindCacheLoader.loadCache(blockToProcess);
                    }
					// If there is still block after this one, we continue.
                    return Optional.of(blockToProcess);
				} else {
					return Optional.empty();
				}
			} else {
				// Error while retrieving the number of blocks in bitcoind.
				return Optional.empty();
			}
	}

	/**
	 * Process block.
	 *
	 * @param blockHeight block height to process.
	 */
	@Override
	protected final Optional<BitcoinBlock> processBlock(final long blockHeight) {
		Optional<BitcoindBlockData> blockData = getBitcoinDataService().getBlockData(blockHeight);

		// -------------------------------------------------------------------------------------------------------------
		// If we have the data.
		if (blockData.isPresent()) {

			// ---------------------------------------------------------------------------------------------------------
			// Then, if the block doesn't exists, we map it to save it.
			BitcoinBlock blockToProcess = getBlockRepository().findByHash(blockData.get().getBlock().getHash());
			if (blockToProcess == null) {
				blockToProcess = getMapper().blockDataToBitcoinBlock(blockData.get());
            }
			addLog("This block has " + blockToProcess.getTx().size() + " transaction(s)");

			// ---------------------------------------------------------------------------------------------------------
			// We set the previous and the next block.
			BitcoinBlock previousBlock = getBlockRepository().findByHashWithoutDepth(blockToProcess.getPreviousBlockHash());
			blockToProcess.setPreviousBlock(previousBlock);
            addLog("Setting previous block of this block");
			if (previousBlock != null) {
				previousBlock.setNextBlock(blockToProcess);
                addLog("Setting this block next block of the previous one");
			}

			// ---------------------------------------------------------------------------------------------------------
			// We return the block.
			return Optional.of(blockToProcess);
		} else {
			// Or nothing if we did not retrieve the data.
			addError("No response from bitcoind for block n°" + getFormattedBlockHeight(blockHeight));
			return Optional.empty();
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
