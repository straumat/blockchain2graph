package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Bitcoin import blocks batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinImportBatchBlocks extends BitcoinImportBatch {

	/**
	 * Initial delay before importing a block.
	 */
	private static final int BLOCK_IMPORT_INITIAL_DELAY = 1000;

	/**
	 * Import data.
	 */
	@Override
	@Scheduled(initialDelay = BLOCK_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void importData() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final long blockToTreat = getBbr().count() + 1;

		// We retrieve the total number of blocks in bitcoind.
		GetBlockCountResponse blockCountResponse = getBds().getBlockCount();
		if (blockCountResponse.getError() == null) {
			// ---------------------------------------------------------------------------------------------------------
			// If there are still blocks to import...
			final long totalBlockCount = getBds().getBlockCount().getResult();
			if (blockToTreat <= totalBlockCount) {
				// -----------------------------------------------------------------------------------------------------
				// We retrieve the block hash...
				GetBlockHashResponse blockHashResponse = getBds().getBlockHash(blockToTreat);
				if (blockHashResponse.getError() == null) {
					// -------------------------------------------------------------------------------------------------
					// Then we retrieve the block data...
					String blockHash = blockHashResponse.getResult();
					getStatus().addLog("importBlock : Starting to import block n°" + blockToTreat + " (" + blockHash + ")");
					GetBlockResponse blockResponse = getBds().getBlock(blockHash);
					if (blockResponse.getError() == null) {
						// ---------------------------------------------------------------------------------------------
						// Then, if the block doesn't exists, we save it.
						BitcoinBlock block = getBbr().findByHash(blockHash);
						if (block == null) {
							block = getMapper().blockResultToBitcoinBlock(blockResponse.getResult());
							getBbr().save(block);
							getStatus().addLog("importBlock : Block n°" + blockToTreat + " saved with id " + block.getId());
						} else {
							getStatus().addLog("importBlock : Block n°" + blockToTreat + " already saved with id " + block.getId());
						}
						final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
						getStatus().addLog("importBlock : Block n°" + blockToTreat + " imported in " + elapsedTime + " secs");
					} else {
						// Error while retrieving the block informations.
						getStatus().addError("importBlock : Error getting block n°" + blockToTreat + " informations : " + blockResponse.getError());
					}
				} else {
					// Error while retrieving the block hash.
					getStatus().addError("importBlock : Error getting the hash of block n°" + blockToTreat + " : " + blockHashResponse.getError());
				}
			} else {
				getStatus().addLog("importBlock : All blocks are imported");
				try {
					Thread.sleep(PAUSE_BETWEEN_CHECKS);
				} catch (Exception e) {
					getLog().error("importBlock : Error while waiting : " + e.getMessage());
				}
			}
		} else {
			// Error while retrieving the number of blocks in bitcoind.
			getStatus().addError("importBlock : Error getting number of blocks : " + blockCountResponse.getError());
		}
	}

}
