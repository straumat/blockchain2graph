package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoinIntegrationService;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tasks for integrating bitcoin.
 * Created by straumat on 31/10/16.
 */
@Component
public class IntegrationBatch {

	/**
	 * Interval between calls.
	 */
	private static final int BITCOIN_INTERVAL_BETWEEN_CALLS = 1;

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(IntegrationBatch.class);

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bds;

	/**
	 * Bitcoin blcok repository.
	 */
	@Autowired
	private BitcoinBlockRepository bbr;

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

	/**
	 * Integration service.
	 */
	@Autowired
	private BitcoinIntegrationService is;

	/**
	 * Import a bitcoin block.
	 */
	@Scheduled(fixedDelay = BITCOIN_INTERVAL_BETWEEN_CALLS)
	public final void importNextBitcoinBlock() {
		log.info("Batch being called");

		// Retrieving data & status update.
		final long importedBlockCount = bbr.count();
		final long totalBlockCount = bds.getBlockCount().getResult();
		status.setImportedBlockCount(importedBlockCount);
		status.setTotalBlockCount(totalBlockCount);

		// Block designation.
		final long lastImportedBlock = importedBlockCount;
		final long blockToImport = lastImportedBlock + 1;
		final long blockToCache = blockToImport + 1;

		// Check if the last block has been fully integrated. If not, we re integrate it.
		BitcoinBlock b = bbr.findByHeight(lastImportedBlock);
		if (b != null && !b.isIntegrated()) {
			try {
				is.importBitcoinBlock(lastImportedBlock);
			} catch (Exception e) {
				status.addError("Error in block " + lastImportedBlock + " " + e.getMessage());
			}
		} else {
			// Else, if the last block was well integrated, we createTransaction an async method to cache the data the block that
			// will be integrated in the next call to this batch.
			is.loadBlockInCache(blockToCache);

			// If there is a block available to import, let's import it !
			if (importedBlockCount < totalBlockCount) {
				try {
					is.importBitcoinBlock(blockToImport);
				} catch (Exception e) {
					status.addError("Error in block " + blockToImport + " " + e.getMessage());
				}
			}
		}
	}

}
