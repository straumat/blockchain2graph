package com.oakinvest.b2g.batch.bitcoin.step4.relations;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

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
						.stream()
						.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION))
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

				// -----------------------------------------------------------------------------------------------------
				// we link the addresses to the input and the origin transaction.
				blockToTreat.getTransactions()
						.parallelStream()
						.forEach(
								t -> {
									// For each Vin.
									t.getInputs()
											.stream()
											// If the txid set in the VIN is null, it's a coinbase transaction.
											.filter(vin -> vin.getTxId() != null)
											.forEach(vin -> {
												// We retrieve the original transaction.
												BitcoinTransaction originTransaction = getTransactionRepository().findByTxId(vin.getTxId());
												if (originTransaction != null) {
													// We retrieve the original transaction output.
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
																});
														addLog(" - Done treating vin : " + vin);
													} else {
														addError("Impossible to find the original output transaction " + vin.getTxId() + " / " + vin.getvOut());
														throw new RuntimeException("Impossible to find the original output transaction " + vin.getTxId() + " / " + vin.getvOut());
													}
												} else {
													addError("Impossible to find the original transaction " + vin.getTxId());
													throw new RuntimeException("Impossible to find the original transaction " + vin.getTxId());
												}
											});

									// For each Vout.
									t.getOutputs()
											.stream()
											.forEach(vout -> {
												vout.getAddresses().stream()
														.filter(a -> a != null)
														.forEach(a -> {
															BitcoinAddress address = getAddressRepository().findByAddress(a);
															address.getOutputTransactions().add(vout);
															getAddressRepository().save(address);
														});
												addLog(" - Done treating vout : " + vout);
											});
								}
						);

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
