package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.util.bitcoin.BitcoinBatchTemplate;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * This batch loads block data from bitcoind ahead of the import process.
 * Created by straumat on 20/03/17.
 */
public class BitcoinBatchCache extends BitcoinBatchTemplate {

	/**
	 * Number of blocks to cache.
	 */
	private static final int NUMBER_OF_BLOCKS_TO_CACHE = 10;

	/**
	 * Pause between imports.
	 */
	private static final int PAUSE_BETWEEN_IMPORTS = 100;

	/**
	 * Log prefix.
	 */
	private static final String PREFIX = "Cache batch";

	/**
	 * Higher block in cache.
	 */
	private long highestBlockInCache = 0;

	/**
	 * Returns the logger prefix to display in each logger.
	 *
	 * @return logger prefix
	 */
	@Override
	protected final String getLogPrefix() {
		return PREFIX;
	}

	/**
	 * Import data.
	 */
	@Override
	@Scheduled(fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void process() {
		final long currentBlockBeingImported = getBlockRepository().count();

		// We load in cache several blocks ahead.
		int i = 0;
		while ((highestBlockInCache - currentBlockBeingImported) < NUMBER_OF_BLOCKS_TO_CACHE) {
			try {
				getBlockDataFromBitcoind(currentBlockBeingImported + i);
				highestBlockInCache = currentBlockBeingImported + i;
				i++;
			} catch (Exception e) {
				getLogger().error("Error while loading cache " + e.getMessage());
			}
		}
	}
}
