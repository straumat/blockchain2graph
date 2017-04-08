package com.oakinvest.b2g.batch.bitcoin.cache;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.util.bitcoin.BitcoinBatchTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * This batch loads block data from bitcoind ahead of the import process.
 * Created by straumat on 20/03/17.
 */
@Service
public class BitcoinBatchCacheLoader extends BitcoinBatchTemplate {

	/**
	 * Number of blocks to cache.
	 */
	private static final long NUMBER_OF_BLOCKS_TO_CACHE = 10;

	/**
	 * Pause between load in cache.
	 */
	private static final int PAUSE_BETWEEN_LOAD_IN_CACHE = 1000;

	/**
	 * Load a block in cache.
	 */
	@Override
	@Scheduled(fixedDelay = PAUSE_BETWEEN_LOAD_IN_CACHE)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void process() {
		try {
			long importedBlockCount = getBlockRepository().countBlockByState(BitcoinBlockState.IMPORTED);

			// Set in cache NUMBER_OF_BLOCKS_TO_CACHE blocks ahead.
			for (int i = 1; i < NUMBER_OF_BLOCKS_TO_CACHE; i++) {
				getBitcoindService().getBlockData(importedBlockCount + i);
			}
		} catch (Exception e) {
			getLogger().debug("Error in loading block in cache");
		}
	}

	/**
	 * Returns the logger prefix to display in each logger.
	 *
	 * @return logger prefix
	 */
	@Override
	protected final String getLogPrefix() {
		return "cache";
	}

}
