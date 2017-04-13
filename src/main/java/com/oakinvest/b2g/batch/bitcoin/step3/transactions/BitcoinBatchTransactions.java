package com.oakinvest.b2g.batch.bitcoin.step3.transactions;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

/**
 * Bitcoin import transactions batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinBatchTransactions extends BitcoinBatchTemplate {

	/**
	 * Log prefix.
	 */
	private static final String PREFIX = "Transactions batch";

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
	//@Scheduled(initialDelay = BLOCK_TRANSACTIONS_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void process() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final BitcoinBlock blockToTreat = getBlockRepository().findFirstBlockByState(BitcoinBlockState.ADDRESSES_IMPORTED);

		// -------------------------------------------------------------------------------------------------------------
		// If there is a block to work on.
		if (blockToTreat != null) {
			addLog(LOG_SEPARATOR);
			addLog("Starting to import transactions from block n°" + getFormattedBlock(blockToTreat.getHeight()));

			BitcoindBlockData blockData = getBitcoindService().getBlockData(blockToTreat.getHeight());
			// ---------------------------------------------------------------------------------------------------------
			// If we have the data
			if (blockData != null) {

				// ---------------------------------------------------------------------------------------------------------
				// Creating all the transactions.
				blockData.getTransactions()
						.parallelStream()
						// Only if the transaction is not already in the database.
						.filter(t -> getTransactionRepository().findByTxId(t.getTxid()) == null)
						// We save it in the database.
						.forEach(t -> {
							try {
								BitcoinTransaction transaction = getMapper().rawTransactionResultToBitcoinTransaction(t);
								getTransactionRepository().save(transaction);
								addLog(" - Transaction " + transaction.getTxId() + " created (id=" + transaction.getId() + ")");
							} catch (Exception e) {
								addError("Error treating transaction " + t.getTxid() + " : " + e.getMessage(), e);
								throw new RuntimeException("Error treating transaction " + t.getTxid() + " : " + e.getMessage());
							}
						});
				blockToTreat.setState(BitcoinBlockState.TRANSACTIONS_IMPORTED);
				getBlockRepository().save(blockToTreat);

				// We log.
				final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
				addLog("Block n°" + getFormattedBlock(blockToTreat.getHeight()) + " treated in " + elapsedTime + " secs");

				// Clear session.
				getSession().clear();
			} else {
				addError("No response from bitcoind - transactions from block n°" + getFormattedBlock(blockToTreat.getHeight()) + " NOT imported");
			}

		} else {
			addLog("Nothing to do");
		}

	}

}
