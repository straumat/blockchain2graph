package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.repository.bitcoin.BitcoinRepositories;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bitcoin import relations batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinBatchBlocksRelations extends BitcoinBatchTemplate {

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
    public BitcoinBatchBlocksRelations(final BitcoinRepositories newBitcoinRepositories, final BitcoinDataService newBitcoinDataService, final StatusService newStatus) {
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
        final BitcoinBlock blockToProcess = getBlockRepository().findByHeightWithoutDepth(blockHeight);

        // -------------------------------------------------------------------------------------------------------------
        // we link the addresses to the input and the origin transaction.
        final AtomicInteger txCounter = new AtomicInteger();
        final int txSize = blockToProcess.getTx().size();
        blockToProcess.getTx()
                .parallelStream()
                .forEach(
                        txId -> {
                            // Retrieving the transaction.
                            BitcoinTransaction t = getTransactionRepository().findByTxId(txId);
                            addLog("- Treating transaction " + txId + " (vin:" + t.getInputs().size() + " / vout:" + t.getOutputs().size() + ")");

                            // For each Vin.
                            t.getInputs()
                                    .stream()
                                    .filter(vin -> !vin.isCoinbase()) // If it's NOT a coinbase transaction.
                                    .forEach(vin -> {
                                        // We retrieve the original transaction.
                                        BitcoinTransactionOutput originTransactionOutput = getBitcoinTransactionOutputRepository().findByKey(vin.getTxId() + "-" + vin.getvOut());
                                        vin.setTransactionOutput(originTransactionOutput);

                                        // We set all the addresses linked to this input.
                                        originTransactionOutput.getAddresses()
                                                .stream()
                                                .filter(Objects::nonNull)
                                                .forEach(a -> vin.setBitcoinAddress(getAddressRepository().findByAddressWithoutDepth(a)));
                                    });

                            // For each Vout.
                            t.getOutputs()
                                    .forEach(vout -> {
                                        // We set all the addresses linked to this output.
                                        vout.getAddresses()
                                                .stream()
                                                .filter(Objects::nonNull)
                                                .forEach(a -> vout.setBitcoinAddress(getAddressRepository().findByAddressWithoutDepth(a)));
                                    });

                            // Add log to say we are done.
                            addLog("-- Transaction " + txId + " treated (" + txCounter.incrementAndGet() + "/" + txSize + ")");
                        }
                );

        return Optional.of(blockToProcess);
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