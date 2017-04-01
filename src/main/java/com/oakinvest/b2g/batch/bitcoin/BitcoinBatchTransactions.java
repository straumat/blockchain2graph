package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.util.bitcoin.BitcoinBatchTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Bitcoin import transactions batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinBatchTransactions extends BitcoinBatchTemplate {

	/**
	 * Pause between calls for checking if all transactions ar done.
	 */
	private static final int PAUSE_BETWEEN_THREADS_CHECK = 5 * 1000;

	/**
	 * Log prefix.
	 */
	private static final String PREFIX = "Transactions batch";

	/**
	 * Number of seconds before displaying threads statistics.
	 */
	private static final int PAUSE_BEFORE_DISPLAYING_STATISTICS = 5;

	/**
	 * Transaction process thread.
	 */
	@Autowired
	private BitcoinBatchTransactionsThread transactionProcessThread;

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

				// -----------------------------------------------------------------------------------------------------
				// Creating one thread to treat one transaction..
				int i = 1;
				int numberOfTransactions = blockData.getTransactions().size();
				HashMap<String, Future<Boolean>> threads = new HashMap<>();
				for (Map.Entry<String, GetRawTransactionResult> entry : blockData.getTransactions().entrySet()) {
					// -------------------------------------------------------------------------------------------------
					// For every transaction hash, we get and save the informations.
					threads.put(entry.getKey(), transactionProcessThread.process(entry.getValue()));
					addLog("> Created a thread for transaction " + entry.getKey() + " (" + i + "/" + numberOfTransactions + ")");
					i++;
				}

				// -------------------------------------------------------------------------------------------------
				// Waiting for all the transactions to be done.
				boolean allThreadsDone = false;
				while (!allThreadsDone) {
					// Statistics.
					int threadsWithoutError = 0;
					int threadsWithErrors = 0;
					int threadsNotYetDone = 0;

					// We see if we have all the results we expected.
					for (Map.Entry<String, Future<Boolean>> t : threads.entrySet()) {
						if (t.getValue().isDone()) {
							// Work is done. Is the result ok ?
							Boolean executionResult;
							try {
								executionResult = t.getValue().get();
							} catch (Exception e) {
								getLogger().error("error in getting result from thread " + e.getMessage());
								executionResult = false;
							}
							// If the result is ok.
							if (executionResult) {
								threadsWithoutError++;
							} else {
								// If it's done and it's null, an error occured so we restart it.
								threadsWithErrors++;
								addLog("Thread for transaction " + t.getKey() + " had an error");
								// We launch again a thread task on this transaction hash.
								threads.put(t.getKey(), transactionProcessThread.process(blockData.getTransactions().get(t.getKey())));
							}
						} else {
							// If the transaction work is not yet done.
							threadsNotYetDone++;
						}
					}

					// Everything is imported if all the transactions are imported without errors.
					allThreadsDone = (threadsWithoutError == blockData.getTransactions().size());

					// If not has been imported, we log statics if we are already running for 2 secs.
					if (!allThreadsDone & ((System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS) > PAUSE_BEFORE_DISPLAYING_STATISTICS) {
						String message = "Block n°" + getFormattedBlock(blockToTreat.getHeight()) + " statistics on threads :";
						message += threadsWithoutError + " ok / ";
						message += threadsWithErrors + " not ok / ";
						message += threadsNotYetDone + " not done";
						addLog(message);

						// And we wait a bit to let time for the threads to finish before testing again.
						try {
							Thread.sleep(PAUSE_BETWEEN_THREADS_CHECK);
						} catch (InterruptedException e) {
							getLogger().error("Error while waiting : " + e.getMessage());
						}
					}
				}

				blockToTreat.setState(BitcoinBlockState.TRANSACTIONS_IMPORTED);
				getBlockRepository().save(blockToTreat);

				// We log.
				final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
				addLog("Block n°" + getFormattedBlock(blockToTreat.getHeight()) + " treated in " + elapsedTime + " secs");

				// Clear session.
				getSession().clear();
			} else {
				addLog("No response from bitcoind - transactions from block n°" + getFormattedBlock(blockToTreat.getHeight()) + " NOT imported");
			}

		} else {
			addLog("Nothing to do");
		}

	}

}
