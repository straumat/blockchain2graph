package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.util.bitcoin.BitcoinBatchTemplate;
import com.oakinvest.b2g.util.bitcoin.BitcoindBlockData;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
		final BitcoinBlock blockToTreat = getBlockRepository().findFirstBlockWithoutTransactions();

		// -------------------------------------------------------------------------------------------------------------
		// If there is a block to work on.
		if (blockToTreat != null) {
			addLog(LOG_SEPARATOR);
			addLog("Starting to import transactions from block n°" + getFormattedBlock(blockToTreat.getHeight()));

			BitcoindBlockData blockData = getBlockDataFromBitcoind(blockToTreat.getHeight());
			// ---------------------------------------------------------------------------------------------------------
			// If we have the data
			if (blockData != null) {

				// ---------------------------------------------------------------------------------------------------------
				// Creating all the addresses.
				for (Map.Entry<String, GetRawTransactionResult> entry : blockData.getTransactions().entrySet()) {
					// -----------------------------------------------------------------------------------------------------
					// For every transaction hash, we get and save the informations.
					if ((getTransactionRepository().findByTxId(entry.getKey()) == null) && !entry.getKey().equals(GENESIS_BLOCK_TRANSACTION)) {
						// Success.
						try {
							// Saving the transaction in the database.
							BitcoinTransaction bt = getMapper().rawTransactionResultToBitcoinTransaction(entry.getValue());
							addLog("Treating transaction " + entry.getKey());

							// For each Vin.
							Iterator<BitcoinTransactionInput> vins = bt.getInputs().iterator();
							while (vins.hasNext()) {
								BitcoinTransactionInput vin = vins.next();
								bt.getInputs().add(vin);
								vin.setTransaction(bt);
								if (vin.getTxId() != null) {
									// Not coinbase. We retrieve the original transaction.
									Optional<BitcoinTransactionOutput> originTransactionOutput = getTransactionRepository().findByTxId(vin.getTxId()).getOutputByIndex(vin.getvOut());
									if (originTransactionOutput.isPresent()) {
										vin.setTransactionOutput(originTransactionOutput.get());
										// We set the addresses "from" if it's not a coinbase transaction.
										originTransactionOutput.get().getAddresses().forEach(a -> (getAddressRepository().findByAddress(a)).getInputTransactions().add(vin));
										addLog(" - Done treating vin : " + vin);
									} else {
										addError("Impossible to find the original output transaction " + vin.getTxId() + " / " + vin.getvOut());
										return;
									}
								}
							}

							Iterator<BitcoinTransactionOutput> vouts = bt.getOutputs().iterator();
							while (vouts.hasNext()) {
								BitcoinTransactionOutput vout = vouts.next();
								bt.getOutputs().add(vout);
								vout.setTransaction(bt);
								vout.getAddresses().stream()
										.filter(a -> a != null)
										.forEach(a -> (getAddressRepository().findByAddress(a)).getOutputTransactions().add(vout));
								addLog(" - Done treating vout : " + vout);
							}

							// Saving the transaction.
							getTransactionRepository().save(bt);
							addLog("Transaction " + entry.getKey() + " saved (id=" + bt.getId() + ")");
							getLogger().info(getLogPrefix() + " - Transaction " + entry.getKey() + " (id=" + bt.getId() + ")");
						} catch (Exception e) {
							addError("Error treating transaction " + entry.getKey() + " : " + e.getMessage());
							getLogger().error(e.getStackTrace().toString());
							return;
						}
					}
				}
				blockToTreat.setTransactionsImported(true);
				getBlockRepository().save(blockToTreat);

				// We log.
				final float elapsedTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start);
				addLog("Block n°" + getFormattedBlock(blockToTreat.getHeight()) + " treated in " + elapsedTime + " secs");
			} else {
				addLog("No response from bitcoind - transactions from block n°" + getFormattedBlock(blockToTreat.getHeight()) + " NOT imported");
			}

		} else {
			addLog("Nothing to do");
		}

	}

}
