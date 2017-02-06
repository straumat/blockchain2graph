package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.IntegrationService;
import com.oakinvest.b2g.service.StatusService;
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
	private static final int BITCOIN_INTERVAL_BETWEEN_CALLS = 100;

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
	private IntegrationService is;

	/**
	 * Import a bitcoin block.
	 */
	@Scheduled(fixedDelay = BITCOIN_INTERVAL_BETWEEN_CALLS)
	public final void importNextBitcoinBlock() {
		log.info("Batch called");
		// Retrieving data.
		final long importedBlockCount = bbr.count();
		final long totalBlockCount = bds.getBlockCount().getResult();

		// Update status.
		status.setImportedBlockCount(importedBlockCount);
		status.setTotalBlockCount(totalBlockCount);

		// if there is another block to import, let's import it !
		if (importedBlockCount < totalBlockCount) {
			try {
				is.integrateBitcoinBlock(importedBlockCount + 1);
			} catch (Exception e) {
				status.addErrorMessage("Error in block " + (importedBlockCount + 1) + " " + e.getMessage());
			}
		}
		log.info("Batch terminated");

	}

}
