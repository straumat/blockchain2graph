package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Optional;

/**
 * Bitcoin import transactions batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinImportBatchTransactions extends BitcoinImportBatch {

	/**
	 * Initial delay before importing a block transactions.
	 */
	private static final int BLOCK_TRANSACTIONS_IMPORT_INITIAL_DELAY = 3000;

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
	@Scheduled(initialDelay = BLOCK_TRANSACTIONS_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void importData() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final BitcoinBlock blockToTreat = getBbr().findFirstBlockWithoutTransactions();

		// -------------------------------------------------------------------------------------------------------------
		// If there is a block to work on.
		if (blockToTreat != null) {
			addLog("Starting to import transactions from block n°" + blockToTreat.getHeight());

			// ---------------------------------------------------------------------------------------------------------
			// Creating all the addresses.
			for (Iterator<String> transactionsHashs = blockToTreat.getTx().iterator(); transactionsHashs.hasNext(); ) {
				String transactionHash = transactionsHashs.next();
				// -----------------------------------------------------------------------------------------------------
				// For every transaction hash, we get and save the informations.
				if ((getBtr().findByTxId(transactionHash) == null) && !transactionHash.equals(GENESIS_BLOCK_TRANSACTION)) {
					// If the transaction is not in the database, we create it.
					GetRawTransactionResponse transactionResponse = getBds().getRawTransaction(transactionHash);
					if (transactionResponse.getError() == null) {
						// Success.
						try {
							// Saving the transaction in the database.
							BitcoinTransaction bt = getMapper().rawTransactionResultToBitcoinTransaction(transactionResponse.getResult());

							// For each Vin.
							Iterator<BitcoinTransactionInput> vins = bt.getInputs().iterator();
							while (vins.hasNext()) {
								BitcoinTransactionInput vin = vins.next();
								bt.getInputs().add(vin);
								vin.setTransaction(bt);
								if (vin.getTxId() != null) {
									// Not coinbase. We retrieve the original transaction.
									Optional<BitcoinTransactionOutput> originTransactionOutput = getBtr().findByTxId(vin.getTxId()).getOutputByIndex(vin.getvOut());
									if (originTransactionOutput.isPresent()) {
										vin.setTransactionOutput(originTransactionOutput.get());
										// We set the addresses "from" if it's not a coinbase transaction.
										originTransactionOutput.get().getAddresses().forEach(a -> (getBar().findByAddress(a)).getWithdrawals().add(vin));
										getLog().info("importBlockTransactions : Done treating vin : " + vin);
									} else {
										addError("Impossible to find the original output transaction " + vin.getTxId() + " / " + vin.getvOut());
										// As we did not find a transaction, we will use async to reimport it.
										return;
									}
								}
							}

							Iterator<BitcoinTransactionOutput> vouts = bt.getOutputs().iterator();
							while (vouts.hasNext()) {
								BitcoinTransactionOutput vout = vouts.next();
								bt.getOutputs().add(vout);
								vout.setTransaction(bt);
								vout.getAddresses().forEach(a -> (getBar().findByAddress(a)).getDeposits().add(vout));
								getLog().info("importBlockTransactions : Done treating vout : " + vout);
							}

							// Saving the transaction.
							getBtr().save(bt);
							addLog("Transaction " + transactionHash + " created with id " + bt.getId());
						} catch (Exception e) {
							addError("Error treating transaction " + transactionHash + " : " + e.getMessage());
							return;
						}
					} else {
						// Error.
						addError("Error in calling getrawtransaction on " + transactionHash + " : " + transactionResponse.getError());
						return;
					}
				}
			}
			blockToTreat.setTransactionsImported(true);
			getBbr().save(blockToTreat);
			final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
			addLog("Block n°" + blockToTreat.getHeight() + " treated in " + elapsedTime + " secs");
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
