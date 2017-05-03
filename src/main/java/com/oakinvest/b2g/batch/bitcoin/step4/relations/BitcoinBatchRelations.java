package com.oakinvest.b2g.batch.bitcoin.step4.relations;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
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
	 * Constructor.
	 *
	 * @param newBlockRepository       blockRepository
	 * @param newAddressRepository     addressRepository
	 * @param newTransactionRepository transactionRepository
	 * @param newBitcoindService       bitcoindService
	 * @param newStatus                status
	 */
	public BitcoinBatchRelations(final BitcoinBlockRepository newBlockRepository, final BitcoinAddressRepository newAddressRepository, final BitcoinTransactionRepository newTransactionRepository, final BitcoindService newBitcoindService, final StatusService newStatus) {
		super(newBlockRepository, newAddressRepository, newTransactionRepository, newBitcoindService, newStatus);
	}

	/**
	 * Returns the log prefix to display in each log.
	 */
	@Override
	public final String getLogPrefix() {
		return PREFIX;
	}

	/**
	 * Fix a random bug. Sometimes, transactions are saved without vin & vout.
	 *
	 * @param txid transaction id
	 */
	public final void fixEmptyTransaction(final String txid) {
		addError("fixEmptyTransaction on transaction " + txid);
		BitcoinTransaction originTransaction = getTransactionRepository().findByTxId(txid);

		// We retrieve the original data from bitcoind.
		GetRawTransactionResult getRawTransactionResult = getBitcoindService().getRawTransaction(txid).getResult();

		// Treating all vin.
		getRawTransactionResult.getVin()
				.forEach(vin -> {
					BitcoinTransactionInput bti = getMapper().rawTransactionVIn(vin);
					originTransaction.getInputs().add(bti);
					bti.setTransaction(originTransaction);
				});

		// Treating all vout.
		getRawTransactionResult.getVout()
				.forEach(vout -> {
					BitcoinTransactionOutput bto = getMapper().rawTransactionVout(vout);
					originTransaction.getOutputs().add(bto);
					bto.setTransaction(originTransaction);
				});

		// We save.
		getTransactionRepository().save(originTransaction);
	}

	/**
	 * Return the block to process.
	 *
	 * @return block to process.
	 */
	@Override
	protected final Long getBlockHeightToProcess() {
		BitcoinBlock blockToTreat = getBlockRepository().findFirstBlockByState(BitcoinBlockState.TRANSACTIONS_IMPORTED);
		if (blockToTreat != null) {
			return blockToTreat.getHeight();
		} else {
			return null;
		}
	}

	/**
	 * Process block.
	 *
	 * @param blockHeight block height to process.
	 */
	@Override
	protected final BitcoinBlock processBlock(final long blockHeight) {
		final BitcoinBlock blockToTreat = getBlockRepository().findByHeight(blockHeight);

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
				.forEach(
						t -> {
							addLog("- Transaction " + t.getTxId());
							// For each Vin.
							t.getInputs()
									.stream()
									// If it's NOT a coinbase transaction.
									.filter(vin -> !vin.isCoinbase())
									.forEach(vin -> {
										// We retrieve the original transaction.
										BitcoinTransaction originTransaction = getTransactionRepository().findByTxId(vin.getTxId());
										if (originTransaction != null) {
											// Random bug - empty inputs and outputs.
											if (originTransaction.getInputs().size() == 0 || originTransaction.getOutputs().size() == 0) {
												String txid = originTransaction.getTxId();
												fixEmptyTransaction(txid);
											}

											// We retrieve the original transaction output.
											Optional<BitcoinTransactionOutput> originTransactionOutput = originTransaction.getOutputByIndex(vin.getvOut());
											if (originTransactionOutput.isPresent()) {
												// We set the addresses "from" if it's not a coinbase transaction.
												vin.setTransactionOutput(originTransactionOutput.get());

												// We set all the addresses linked to this input
												originTransactionOutput.get().getAddresses()
														.stream()
														.filter(Objects::nonNull)
														.forEach(a -> {
															BitcoinAddress address = getAddressRepository().findByAddress(a);
															address.getInputTransactions().add(vin);
															getAddressRepository().save(address);
														});
												addLog("-- Done processing vin : " + vin);
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
									.forEach(vout -> {
										vout.getAddresses().stream()
												.filter(Objects::nonNull)
												.forEach(a -> {
													BitcoinAddress address = getAddressRepository().findByAddress(a);
													address.getOutputTransactions().add(vout);
													getAddressRepository().save(address);
												});
										addLog("-- Done processing vout : " + vout);
									});
							addLog("-- Transaction " + t.getTxId() + " relations processed");
						}
				);
		return blockToTreat;
	}

	/**
	 * Return the state to set to the block that has been processed.
	 *
	 * @return state to set of the block that has been processed.
	 */
	@Override
	protected final BitcoinBlockState getNewStateOfProcessedBlock() {
		return BitcoinBlockState.IMPORTED;
	}

}
