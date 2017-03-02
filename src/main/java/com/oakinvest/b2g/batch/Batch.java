package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Temporary batch used because multi threading doesn't work..
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
	 * Import data.
	 */
	@Scheduled(fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void importData() {
		final long start = System.currentTimeMillis();
		// Importing the block.
		batchBlocks.importData();
		batchAddresses.importData();
		batchTransactions.importData();
		batchRelations.importData();
		// Adding statistics.
		final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
		status.addExecutionTimeStatistic(elapsedTime);
	}

}
