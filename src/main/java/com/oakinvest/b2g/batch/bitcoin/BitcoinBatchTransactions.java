package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.util.bitcoin.BitcoinBatchTemplate;
import com.oakinvest.b2g.util.bitcoin.BitcoindBlockData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

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

			BitcoindBlockData blockData = getBlockDataFromBitcoind(blockToTreat.getHeight());
			// ---------------------------------------------------------------------------------------------------------
			// If we have the data
			if (blockData != null) {

				// ---------------------------------------------------------------------------------------------------------
				// Creating all the addresses.
				for (Map.Entry<String, GetRawTransactionResult> entry : blockData.getTransactions().entrySet()) {
					int i = 1;
					// -----------------------------------------------------------------------------------------------------
					// For every transaction hash, we get and save the informations.
					if ((getTransactionRepository().findByTxId(entry.getKey()) == null) && !entry.getKey().equals(GENESIS_BLOCK_TRANSACTION)) {
						// Success.
						try {
							// Saving the transaction in the database.
							BitcoinTransaction transaction = getMapper().rawTransactionResultToBitcoinTransaction(entry.getValue());
							getTransactionRepository().save(transaction);
							addLog("Treating transaction " + entry.getKey() + " (" + i + "/" + blockData.getTransactions().size() + ")");

							// For each Vin.
							Iterator<BitcoinTransactionInput> vins = transaction.getInputs().iterator();
							while (vins.hasNext()) {
								BitcoinTransactionInput vin = vins.next();
								transaction.getInputs().add(vin);
								vin.setTransaction(transaction);

								if (vin.getTxId() != null) {
									// Not coinbase. We retrieve the original transaction.
									Optional<BitcoinTransactionOutput> originTransactionOutput = getTransactionRepository().findByTxId(vin.getTxId()).getOutputByIndex(vin.getvOut());
									if (originTransactionOutput.isPresent()) {
										// We set the addresses "from" if it's not a coinbase transaction.
										vin.setTransactionOutput(originTransactionOutput.get());

										// We set all the addresses linked to this input
										originTransactionOutput.get().getAddresses()
												.stream().filter(a -> a != null)
												.forEach(a -> {
													BitcoinAddress address = getAddressRepository().findByAddress(a);
													address.getInputTransactions().add(vin);
													getAddressRepository().save(address);
												});                                        //getTransactionInputRepository().save(vin);

										addLog(" - Done treating vin : " + vin);
									} else {
										addError("Impossible to find the original output transaction " + vin.getTxId() + " / " + vin.getvOut());
										return;
									}
								}
								//getTransactionInputRepository().save(vin);
							}

							Iterator<BitcoinTransactionOutput> vouts = transaction.getOutputs().iterator();
							while (vouts.hasNext()) {
								BitcoinTransactionOutput vout = vouts.next();
								transaction.getOutputs().add(vout);
								vout.setTransaction(transaction);
								vout.getAddresses().stream()
										.filter(a -> a != null)
										.forEach(a -> {
											BitcoinAddress address = getAddressRepository().findByAddress(a);
											address.getOutputTransactions().add(vout);
											getAddressRepository().save(address);
										});                                //getTransactionOutputRepository().save(vout);
								addLog(" - Done treating vout : " + vout);
							}

							// Saving the transaction.
							getTransactionRepository().save(transaction);
							addLog(" - Transaction " + entry.getKey() + " saved (id=" + transaction.getId() + ")");
							getLogger().info(getLogPrefix() + " - Transaction " + entry.getKey() + " (id=" + transaction.getId() + ")");
						} catch (Exception e) {
							addError("Error treating transaction " + entry.getKey() + " : " + e.getMessage());
							getLogger().error("Error treating transactions " + Arrays.toString(e.getStackTrace()));
							return;
						}
						i++;
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
