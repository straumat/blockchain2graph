package com.oakinvest.b2g.batch.bitcoin.step3.relations;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.repository.bitcoin.BitcoinRepositories;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
     * @param newBitcoinRepositories    bitcoin repositories
     * @param newBitcoinDataService     bitcoin data service
     * @param newStatus                 status
     */
    public BitcoinBatchRelations(final BitcoinRepositories newBitcoinRepositories, final BitcoinDataService newBitcoinDataService, final StatusService newStatus) {
        super(newBitcoinRepositories, newBitcoinDataService, newStatus);
    }

	/**
	 * Returns the log prefix to display in each log.
	 */
	@Override
	public final String getLogPrefix() {
		return PREFIX;
	}

	/**
	 * Return the block to process.
	 *
	 * @return block to process.
	 */
	@Override
    protected final Optional<Long> getBlockHeightToProcess() {
		BitcoinBlock blockToTreat = getBlockRepository().findFirstBlockByState(BitcoinBlockState.ADDRESSES_IMPORTED);
		if (blockToTreat != null) {
			return Optional.of(blockToTreat.getHeight());
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Process block.
	 *
	 * @param blockHeight block height to process.
	 */
	@Override
	protected final Optional<BitcoinBlock> processBlock(final long blockHeight) {
		final BitcoinBlock blockToTreat = getBlockRepository().findByHeight(blockHeight);

        // -----------------------------------------------------------------------------------------------------
        // We retrieve the list of all the origin transaction and all the addresses required.
        final Map<String, BitcoinTransaction> originTransactions =  Collections.synchronizedMap(new HashMap<>());
        final Map<String, BitcoinAddress> addresses = Collections.synchronizedMap(new HashMap<>());
        final Map<String, BitcoinTransaction> transactions = Collections.synchronizedMap(new HashMap<>());
        addLog("Retrieving all address");
        blockToTreat.getTx()
                .parallelStream()
                .forEach(txId -> {
                    // Retrieving the transaction.
                    BitcoinTransaction t = getTransactionRepository().findByTxId(txId);
                    addLog("- Inspecting transaction " + t.getTxId());
                    transactions.putIfAbsent(t.getTxId(), t);

                    // Getting all the origin transactions and there addresses.
                    t.getInputs()
                        .parallelStream()
                        .filter(vin -> !vin.isCoinbase())
                        .forEach(vin -> {
                            BitcoinTransaction originTransaction = getTransactionRepository().findByTxId(vin.getTxId());
                            // adding origin transaction.
                            originTransactions.putIfAbsent(vin.getTxId(), originTransaction);
                            // adding addresses.
                            originTransaction.getOutputByIndex(vin.getvOut()).get().getAddresses()
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .forEach(a -> addresses.putIfAbsent(a, getAddressRepository().findByAddress(a)));
                        });

                    // Getting all the addresses of output.
                    t.getOutputs()
                            .parallelStream()
                            .forEach(o -> o.getAddresses()
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .forEach(a -> addresses.putIfAbsent(a, getAddressRepository().findByAddress(a))));

                });

        // -----------------------------------------------------------------------------------------------------
		// we link the addresses to the input and the origin transaction.
		blockToTreat.getTx()
				.forEach(
						txId -> {
						    BitcoinTransaction t = transactions.get(txId);
							addLog("- Transaction " + t.getTxId());

							// For each Vin.
							t.getInputs()
									.stream()
									// If it's NOT a coinbase transaction.
									.filter(vin -> !vin.isCoinbase())
									.forEach(vin -> {
										// We retrieve the original transaction.
										BitcoinTransaction originTransaction = originTransactions.get(vin.getTxId());

                                        // We retrieve the original transaction output.
										Optional<BitcoinTransactionOutput> originTransactionOutput = originTransaction.getOutputByIndex(vin.getvOut());
										if (originTransactionOutput.isPresent()) {
											// We set the addresses "from" if it's not a coinbase transaction.
											vin.setTransactionOutput(originTransactionOutput.get());

											// We set all the addresses linked to this input
											originTransactionOutput.get().getAddresses()
													.stream()
													.filter(Objects::nonNull)
													.forEach(a -> addresses.get(a).getInputTransactions().add(vin));
											addLog("-- Done processing vin : " + vin);
										} else {
											addError("Impossible to find the original output transaction " + vin.getTxId() + " / " + vin.getvOut());
											throw new RuntimeException("Impossible to find the original output transaction " + vin.getTxId() + " / " + vin.getvOut());
										}
									});

							// For each Vout.
							t.getOutputs()
									.forEach(vout -> {
										vout.getAddresses().stream()
												.filter(Objects::nonNull)
												.forEach(a -> addresses.get(a).getOutputTransactions().add(vout));
										addLog("-- Done processing vout : " + vout);
									});

						}
				);

		// We save all the addresses.
        addresses.values().forEach(a -> getAddressRepository().save(a));

		return Optional.of(blockToTreat);
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
