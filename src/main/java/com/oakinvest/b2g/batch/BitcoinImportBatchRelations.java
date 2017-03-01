package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Bitcoin import relations batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinImportBatchRelations extends BitcoinImportBatch {

	/**
	 * Initial delay before importing a block relations.
	 */
	private static final int BLOCK_RELATIONS_IMPORT_INITIAL_DELAY = 4000;

	/**
	 * Log prefix.
	 */
	private static final String PREFIX = "Relations batch";

	/**
	 * Returns the log prefix to display in each log.
	 */
	@Override
	public final String getLogPrefix() {
		return PREFIX;
	}

	/**
	 * Import data.
	 */
	@Override
	@Scheduled(initialDelay = BLOCK_RELATIONS_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void importData() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final BitcoinBlock blockToTreat = getBbr().findFirstBlockWithoutRelations();

		// -------------------------------------------------------------------------------------------------------------
		// If there is a block to work on.
		if (blockToTreat != null) {
			try {
				// ---------------------------------------------------------------------------------------------------------
				// Getting the block informations.
				addLog("Starting to import relations from block n°" + blockToTreat.getHeight());
				// ---------------------------------------------------------------------------------------------------------
				// Setting the relationship between blocks and transactions.
				blockToTreat.getTx().stream().filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION))
						.forEach(t -> {
							BitcoinTransaction bt = getBtr().findByTxId(t);
							bt.setBlock(blockToTreat);
							blockToTreat.getTransactions().add(bt);
						});
				// ---------------------------------------------------------------------------------------------------------
				// We update the block to say everything went fine.
				blockToTreat.setRelationsImported(true);
				blockToTreat.setImported(true);
				getBbr().save(blockToTreat);
				final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
				addLog("Block n°" + blockToTreat.getHeight() + " treated in " + elapsedTime + " secs");
				getStatus().setImportedBlockCount(getBbr().countImported());
			} catch (Exception e) {
				addError("Block n°" + blockToTreat.getHeight() + " raised an exception " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			addLog("Nothing to do");
			try {
				Thread.sleep(PAUSE_BETWEEN_CHECKS);
			} catch (Exception e) {
				addError("Error while waiting : " + e.getMessage());
			}
		}

	}

}
