package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.util.bitcoin.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Bitcoin import relations batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinBatchRelations extends BitcoinBatchTemplate {

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
	//@Scheduled(initialDelay = BLOCK_RELATIONS_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void process() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final BitcoinBlock blockToTreat = getBlockRepository().findFirstBlockByState(BitcoinBlockState.TRANSACTIONS_IMPORTED);

		// -------------------------------------------------------------------------------------------------------------
		// If there is a block to work on.
		if (blockToTreat != null) {
			try {
				// -----------------------------------------------------------------------------------------------------
				// Getting the block informations.
				addLog(LOG_SEPARATOR);
				addLog("Starting to import relations from block n°" + getFormattedBlock(blockToTreat.getHeight()));

				// -----------------------------------------------------------------------------------------------------
				// Setting the relationship between blocks and transactions.
				blockToTreat.getTx()
						.forEach(t -> {
							BitcoinTransaction bt = getTransactionRepository().findByTxId(t);
							bt.setBlock(blockToTreat);
							blockToTreat.getTransactions().add(bt);
						});

				// -----------------------------------------------------------------------------------------------------
				// We set the previous and the next block.
				BitcoinBlock previousBlock = getBlockRepository().findByHash(blockToTreat.getPreviousBlockHash());
				blockToTreat.setPreviousBlock(previousBlock);
				if (previousBlock != null) {
					previousBlock.setNextBlock(blockToTreat);
					getBlockRepository().save(previousBlock);
				}

				// ---------------------------------------------------------------------------------------------------------
				// We update the block to say everything went fine.
				blockToTreat.setState(BitcoinBlockState.IMPORTED);
				getBlockRepository().save(blockToTreat);

				// We log.
				final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
				addLog("Block n°" + getFormattedBlock(blockToTreat.getHeight()) + " treated in " + elapsedTime + " secs");
				getLogger().info(getLogPrefix() + " - Block n°" + blockToTreat.getHeight() + " treated in " + elapsedTime + " secs");

				// Clear session.
				getSession().clear();
			} catch (Exception e) {
				addError("Block n°" + getFormattedBlock(blockToTreat.getHeight()) + " raised an exception " + e.getMessage());
				getLogger().error("Error in treating relations : " + Arrays.toString(e.getStackTrace()));
			}
		} else {
			addLog("Nothing to do");
		}
	}

}
