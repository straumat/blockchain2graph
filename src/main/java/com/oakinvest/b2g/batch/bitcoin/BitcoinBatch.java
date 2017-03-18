package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Temporary batch used because multi threading doesn't work.
 * Created by straumat on 02/03/17.
 */
@Component
public class BitcoinBatch {

	/**
	 * How much time it takes to create a new block for bitcoin (10 minutes).
	 */
	private static final int TIME_BEFORE_NEW_BITCOIN_BLOCK = 10 * 60 * 1000;

	/**
	 * Pause between imports.
	 */
	private static final int PAUSE_BETWEEN_IMPORTS = 1000;

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(BitcoinBatch.class);

	/**
	 * Import batch.
	 */
	@Autowired
	private BitcoinBatchBlocks batchBlocks;

	/**
	 * Import batch.
	 */
	@Autowired
	private BitcoinBatchAddresses batchAddresses;

	/**
	 * Import batch.
	 */
	@Autowired
	private BitcoinBatchTransactions batchTransactions;

	/**
	 * Import batch.
	 */
	@Autowired
	private BitcoinBatchRelations batchRelations;

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
		final long startTime = System.currentTimeMillis();

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
		status.addBlockImportDurationStatistic(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime));

		// If we are up to date with the blockchain last block.
		if (bbr.countImported() == bds.getBlockCount().getResult()) {
			try {
				Thread.sleep(TIME_BEFORE_NEW_BITCOIN_BLOCK);
			} catch (InterruptedException e) {
				log.error("Error while pause : " + e.getMessage());
			}
		}

	}

}
