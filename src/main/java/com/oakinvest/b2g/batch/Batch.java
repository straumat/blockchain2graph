package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.BitcoindService;
import com.oakinvest.b2g.service.StatusService;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Temporary batch used because multi threading doesn't work.
 * Created by straumat on 02/03/17.
 */
@Component
public class Batch {

	/**
	 * Pause between imports.
	 */
	private static final int PAUSE_BETWEEN_IMPORTS = 1000;

	/**
	 * How many milli seconds in one second.
	 */
	private static final float MILLISECONDS_IN_SECONDS = 1000F;

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(Batch.class);

	/**
	 * Import batch.
	 */
	@Autowired
	private BitcoinImportBatchBlocks batchBlocks;

	/**
	 * Import batch.
	 */
	@Autowired
	private BitcoinImportBatchAddresses batchAddresses;

	/**
	 * Import batch.
	 */
	@Autowired
	private BitcoinImportBatchTransactions batchTransactions;

	/**
	 * Import batch.
	 */
	@Autowired
	private BitcoinImportBatchRelations batchRelations;

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

	/**
	 * Neo4j session.
	 */
	@Autowired
	private Session session;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinBlockRepository bbr;

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bds;

	/**
	 * Import data.
	 */
	@Scheduled(fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings("checkstyle:designforextension")
	public void importData() {
		final long start = System.currentTimeMillis();

		// Update block statistics.
		status.setImportedBlockCount(bbr.countImported());
		status.setTotalBlockCount(bds.getBlockCount().getResult());

		// Importing the block.
		try {
			batchBlocks.importData();
			batchAddresses.importData();
			batchTransactions.importData();
			batchRelations.importData();
		} catch (Exception e) {
			status.addError("Error in the batch processes : " + e.getMessage());
			log.error(e.getStackTrace().toString());
		} finally {
			session.clear();
		}

		// Adding a statistic on duration.
		final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
		status.addBlockImportDurationStatistic(elapsedTime);
	}

}
