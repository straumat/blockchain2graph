package com.oakinvest.b2g.batch.bitcoin.step4.relations;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

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
	 * Return the block to treat.
	 *
	 * @return block to treat.
	 */
	@Override
	protected final Long getBlockToTreat() {
		BitcoinBlock blockToTreat = getBlockRepository().findFirstBlockByState(BitcoinBlockState.TRANSACTIONS_IMPORTED);
		if (blockToTreat != null) {
			return blockToTreat.getHeight();
		} else {
			return null;
		}
	}

	/**
	 * Treat block.
	 *
	 * @param blockNumber block number to treat.
	 */
	@Override
	protected final BitcoinBlock treatBlock(final long blockNumber) {
		final BitcoinBlock blockToTreat = getBlockRepository().findByHeight(blockNumber);
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
		getBlockRepository().save(blockToTreat);
		getSession().clear();

		// -----------------------------------------------------------------------------------------------------
		// We set the previous and the next block.
		BitcoinBlock previousBlock = getBlockRepository().findByHash(blockToTreat.getPreviousBlockHash());
		blockToTreat.setPreviousBlock(previousBlock);
		if (previousBlock != null) {
			previousBlock.setNextBlock(blockToTreat);
			getBlockRepository().save(previousBlock);
		}
		getBlockRepository().save(blockToTreat);
		getSession().clear();

		// -----------------------------------------------------------------------------------------------------
		// we link the addresses to the input and the origin transaction.
		blockToTreat.getTransactions()
				.stream()
				.forEach(
						t -> {
							addLog("- Transaction " + t.getTxId());
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
											Optional<BitcoinTransactionOutput> originTransactionOutput = originTransaction.getOutputByIndex(vin.getvOut());
											if (originTransactionOutput.isPresent()) {
												// We set the addresses "from" if it's not a coinbase transaction.
												vin.setTransactionOutput(originTransactionOutput.get());

												// We set all the addresses linked to this input
												originTransactionOutput.get().getAddresses()
														.stream()
														.filter(a -> a != null)
														.forEach(a -> {
															BitcoinAddress address = getAddressRepository().findByAddress(a);
															address.getInputTransactions().add(vin);
															getAddressRepository().save(address);
														});
												addLog("-- Done treating vin : " + vin);
											} else {
												addError("Impossible to find the original output transaction " + vin.getTxId() + " / " + vin.getvOut());
												throw new RuntimeException("Impossible to find the original output transaction " + vin.getTxId() + " / " + vin.getvOut());
											}
										} else {
											addError("Impossible to find the original transaction " + vin.getTxId());
											throw new RuntimeException("Impossible to find the original transaction " + vin.getTxId());
										}
										addLog("");
									});

							// For each Vout.
							t.getOutputs()
									.forEach(vout -> {
										vout.getAddresses().stream()
												.filter(a -> a != null)
												.forEach(a -> {
													BitcoinAddress address = getAddressRepository().findByAddress(a);
													address.getOutputTransactions().add(vout);
													getAddressRepository().save(address);
												});
										addLog("-- Done treating vout : " + vout);
									});
							addLog("-- Transaction " + t.getTxId() + " relations treated");
						}
				);
		getBlockRepository().save(blockToTreat);
		getSession().clear();

		return blockToTreat;
	}

	/**
	 * Return the state to set to the block that has been treated.
	 *
	 * @return state to set of the block that has been treated.
	 */
	@Override
	protected final BitcoinBlockState getNewStateOfTreatedBlock() {
		return BitcoinBlockState.IMPORTED;
	}

}
