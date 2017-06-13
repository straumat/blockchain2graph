package com.oakinvest.b2g.batch.bitcoin.step1.blocks;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.repository.bitcoin.BitcoinRepositories;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.service.StatusService;
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
    private BitcoindCacheLoader bitcoindCacheLoader;

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
    public BitcoinBatchBlocks(final BitcoinRepositories newBitcoinRepositories, final BitcoinDataService newBitcoinDataService, final StatusService newStatus, final BitcoindCacheLoader newBitcoindCacheLoader) {
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
    protected final Long getBlockHeightToProcess() {
		// We retrieve the next block to process according to the database.
		Long blockToProcess = getBlockRepository().count() + 1;
		final long totalBlockCount = getBitcoinDataService().getBlockCount();

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
		Optional<BitcoindBlockData> blockData = getBitcoinDataService().getBlockData(blockHeight);
		// -------------------------------------------------------------------------------------------------------------
		// If we have the data.
		if (blockData.isPresent()) {
			// ---------------------------------------------------------------------------------------------------------
			// Then, if the block doesn't exists, we map it to save it.
			BitcoinBlock block = getBlockRepository().findByHash(blockData.get().getBlock().getHash());
			if (block == null) {
				block = getMapper().blockDataToBitcoinBlock(blockData.get());
                //block = getMapper().blockResultToBitcoinBlock(blockData.get().getBlock());
            }

            /*
            if (block.getHeight() > 1) {
                BitcoinBlock block2 = getBlockRepository().findByHash(blockData.get().getBlock().getPreviousblockhash());
                System.out.println("==============================");
                System.out.println(block2.getTransactions().size());
                System.out.println(block2.getTransactions().iterator().next().getOutputs().size());
                System.out.println(block2.getTransactions().iterator().next().getOutputs().iterator().next());
                System.out.println(block2.getTransactions().iterator().next().getBlock().getHeight());
                System.out.println("==============================");
            }*/

			/*
			block.getTransactions().forEach(t -> {
			    t.getInputs().forEach(i -> System.out.println("1 - ==> " + i));
			});
            block.getTransactions().forEach(t -> {
                t.getOutputs().forEach(i -> {
                    System.out.println("2 - ==> " + i);
                    i.getBitcoinAddresses().forEach(a -> System.out.println(" ==> " + a));
                });
            });
*/
			addLog("This block has " + block.getTx().size() + " transaction(s)");

			// -----------------------------------------------------------------------------------------------------
			// We set the previous and the next block.
			BitcoinBlock previousBlock = getBlockRepository().findByHash(block.getPreviousBlockHash());
			block.setPreviousBlock(previousBlock);
            addLog("Setting previous block of this block");
			if (previousBlock != null) {
				previousBlock.setNextBlock(block);
                addLog("Setting this block next block of the previous one");
				//getBlockRepository().save(previousBlock);
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
