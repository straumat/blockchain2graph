package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.util.bitcoin.BitcoinBatchTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This batch loads block data from bitcoind ahead of the import process.
 * Created by straumat on 20/03/17.
 */
@Component
public class BitcoinBatchCache extends BitcoinBatchTemplate {

	/**
	 * Number of blocks to cache.
	 */
	private static final int NUMBER_OF_BLOCKS_TO_CACHE = 20;

	/**
	 * Pause between imports.
	 */
	private static final int PAUSE_BETWEEN_IMPORTS = 100;

	/**
	 * Log prefix.
	 */
	private static final String PREFIX = "Cache batch";


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
		for (long i = 0; i < NUMBER_OF_BLOCKS_TO_CACHE; i++) {
			try {
				getBlockDataFromBitcoind(currentBlockBeingImported + i);
			} catch (Exception e) {
				getLogger().error("Error while loading cache " + e.getMessage());
			}
		}
	}
}
