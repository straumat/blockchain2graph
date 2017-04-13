package com.oakinvest.b2g.batch.bitcoin.step1.blocks;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

/**
 * Bitcoin import blocks batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinBatchBlocks extends BitcoinBatchTemplate {

	/**
	 * Log prefix.
	 */
	private static final String PREFIX = "Blocks batch";

	/**
	 * Returns the log prefix to display in each log.
	 */
	@Override
	public final String getLogPrefix() {
		return PREFIX;
	}

	/**
	 * Import data.
	 */
	@Override
	//@Scheduled(initialDelay = BLOCK_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void process() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final long blockToTreat = getBlockRepository().count() + 1;

		// We retrieve the total number of blocks in bitcoind.
		GetBlockCountResponse blockCountResponse = getBitcoindService().getBlockCount();
		if (blockCountResponse.getError() == null) {
			// ---------------------------------------------------------------------------------------------------------
			// If there are still blocks to import...
			final long totalBlockCount = getBitcoindService().getBlockCount().getResult();
			if (blockToTreat <= totalBlockCount) {
				BitcoindBlockData blockData = getBitcoindService().getBlockData(blockToTreat);
				// -----------------------------------------------------------------------------------------------------
				// If we have the data
				if (blockData != null) {
					// -------------------------------------------------------------------------------------------------
					// Then we retrieve the block data...
					addLog(LOG_SEPARATOR);
					addLog("Starting to import block n°" + getFormattedBlock(blockToTreat) + " (" + blockData.getBlock().getHash() + ")");

					// -------------------------------------------------------------------------------------------------
					// Then, if the block doesn't exists, we save it.
					BitcoinBlock block = getBlockRepository().findByHash(blockData.getBlock().getHash());
					if (block == null) {
						block = getMapper().blockResultToBitcoinBlock(blockData.getBlock());
						getBlockRepository().save(block);
						final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
						addLog("Block n°" + getFormattedBlock(blockToTreat) + " saved with id " + block.getId() + " (treated in " + elapsedTime + " secs)");
					} else {
						final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
						addLog("Block n°" + getFormattedBlock(blockToTreat) + " already saved with id " + block.getId() + " (treated in " + elapsedTime + " secs)");
					}

					// -------------------------------------------------------------------------------------------------
					// Clear session.
					getSession().clear();
				}
			} else {
				addError("No response from bitcoind - block n°" + blockToTreat + " NOT imported");
			}
		} else {
			// Error while retrieving the number of blocks in bitcoind.
			addError("Error getting the number of blocks : " + blockCountResponse.getError());
		}

	}

}
