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
			// ---------------------------------------------------------------------------------------------------------
			// Getting the block informations.
			getStatus().addLog("importBlockRelations : Starting to import relations from block n°" + blockToTreat.getHeight());
			// ---------------------------------------------------------------------------------------------------------
			// Setting the relationship between blocks and transactions.
			blockToTreat.getTx().stream()
					.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_1))
					.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_2))
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
			getStatus().addLog("importBlockRelations : Block n°" + blockToTreat.getHeight() + " treated in " + elapsedTime + " secs");
			getStatus().setImportedBlockCount(getBbr().countImported());
		} else {
			getStatus().addLog("importBlockRelations : Nothing to do");
			try {
				Thread.sleep(PAUSE_BETWEEN_CHECKS);
			} catch (Exception e) {
				getLog().error("importBlockRelations : Error while waiting : " + e.getMessage());
			}
		}

	}

}
