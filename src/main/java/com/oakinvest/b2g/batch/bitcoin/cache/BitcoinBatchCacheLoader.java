package com.oakinvest.b2g.batch.bitcoin.cache;

import com.oakinvest.b2g.batch.bitcoin.step1.blocks.BitcoinBatchBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * This batch loads block data from bitcoind ahead of the import process.
 * Created by straumat on 20/03/17.
 */
@Service
public class BitcoinBatchCacheLoader {

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(BitcoinBatchCacheLoader.class);

	/**
	 * Load a block in cache.
	 *
	 * @param batchBlocks bat   ch block.
	 * @param blockNumber block number.
	 */
	@Async
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void loadInCache(final BitcoinBatchBlocks batchBlocks, final long blockNumber) {
		try {
			batchBlocks.getBlockDataFromBitcoind(blockNumber);
		} catch (Exception e) {
			log.debug("Error in loading block " + blockNumber + " in cache");
		}
	}

}
