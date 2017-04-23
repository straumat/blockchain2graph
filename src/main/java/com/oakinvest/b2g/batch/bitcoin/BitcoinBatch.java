package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.batch.bitcoin.step1.blocks.BitcoinBatchBlocks;
import com.oakinvest.b2g.batch.bitcoin.step2.addresses.BitcoinBatchAddresses;
import com.oakinvest.b2g.batch.bitcoin.step3.transactions.BitcoinBatchTransactions;
import com.oakinvest.b2g.batch.bitcoin.step4.relations.BitcoinBatchRelations;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoinStatisticService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Temporary batch used because multi threading doesn't work.
 * Created by straumat on 02/03/17.
 */
@Component
public class BitcoinBatch {

	/**
	 * Pause between imports.
	 */
	private static final int PAUSE_BETWEEN_TREATMENTS = 100;

	/**
	 * Import batch.
	 */
	private final BitcoinBatchBlocks batchBlocks;

	/**
	 * Import batch.
	 */
	private final BitcoinBatchAddresses batchAddresses;

	/**
	 * Import batch.
	 */
	private final BitcoinBatchTransactions batchTransactions;

	/**
	 * Import batch.
	 */
	private final BitcoinBatchRelations batchRelations;

	/**
	 * Status service.
	 */
	private final StatusService status;

	/**
	 * Bitcoin statistic service.
	 */
	private final BitcoinStatisticService bitcoinStatisticService;

	/**
	 * Bitcoin block repository.
	 */
	private final BitcoinBlockRepository blockRepository;

	/**
	 * Constructor.
	 *
	 * @param newBatchBlocks             batchBlocks
	 * @param newBatchAddresses          batchAddresses
	 * @param newBatchTransactions       batchTransactions
	 * @param newBatchRelations          batchRelations
	 * @param newStatus                  status
	 * @param newBitcoinStatisticService bitcoinStatisticService
	 * @param newBlockRepository         blockRepository
	 */
	public BitcoinBatch(final BitcoinBatchBlocks newBatchBlocks, final BitcoinBatchAddresses newBatchAddresses, final BitcoinBatchTransactions newBatchTransactions, final BitcoinBatchRelations newBatchRelations, final StatusService newStatus, final BitcoinStatisticService newBitcoinStatisticService, final BitcoinBlockRepository newBlockRepository) {
		this.batchBlocks = newBatchBlocks;
		this.batchAddresses = newBatchAddresses;
		this.batchTransactions = newBatchTransactions;
		this.batchRelations = newBatchRelations;
		this.status = newStatus;
		this.bitcoinStatisticService = newBitcoinStatisticService;
		this.blockRepository = newBlockRepository;
	}

	/**
	 * Import data.
	 */
	@Scheduled(fixedDelay = PAUSE_BETWEEN_TREATMENTS)
	@SuppressWarnings("checkstyle:designforextension")
	public void importData() {
		// Retrieve the number of block we have and update the status.
		long importedBlockCount = blockRepository.countBlockByState(BitcoinBlockState.IMPORTED);
		status.setImportedBlockCount(importedBlockCount);

		// Importing the next available block.
		final long start = System.currentTimeMillis();
		try {
			batchBlocks.execute();
			batchAddresses.execute();
			batchTransactions.execute();
			batchRelations.execute();
		} catch (Exception e) {
			status.addError("Error in the batch processes : " + e.getMessage(), e);
		}

		// Adding a statistic on duration.
		float averageBlockImportDuration = bitcoinStatisticService.addBlockImportDuration(System.currentTimeMillis() - start);
		status.setAverageBlockImportDuration(averageBlockImportDuration);
	}

}
