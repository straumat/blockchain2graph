package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.IntegrationService;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tasks for integrating bitcoin.
 * Created by straumat on 31/10/16.
 */
@Component
public class IntegrationBatch {

	/**
	 * Interval beteween calls.
	 */
	public static final int BITCOIN_INTERVAL_BETWEEN_CALLS = 1000;

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(IntegrationBatch.class);

	/**
	 * Server address.
	 */
	@Value("${blockchain2graph.batch.enabled}")
	private boolean enabled = true;

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
	private StatusService ss;

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
		if (enabled) {
			final long totalBlockCount = bds.getBlockCount().getResult();
			ss.setTotalBlockCount(totalBlockCount);
			final long importedBlockCount = bbr.count();

			// if there is another block to import, let's import it !
			if (importedBlockCount < totalBlockCount) {
				is.integrateBitcoinBlock(importedBlockCount + 1);
				// Update status.
				ss.setImportedBlockCount(bbr.count());
			}
		}
	}

}
