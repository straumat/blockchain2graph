package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.repository.bitcoin.BitcoinRepositories;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoinDataServiceCacheStore;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState.BLOCK_DATA_IMPORTED;
import static com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState.BLOCK_FULLY_IMPORTED;

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
     * @param newCacheStore             cache store
     */
    public BitcoinBatchRelations(final BitcoinRepositories newBitcoinRepositories, final BitcoinDataService newBitcoinDataService, final StatusService newStatus, final BitcoinDataServiceCacheStore newCacheStore) {
        super(newBitcoinRepositories, newBitcoinDataService, newStatus, newCacheStore);
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
    protected final Optional<Integer> getBlockHeightToProcess() {
        BitcoinBlock blockToTreat = getBlockRepository().findFirstBlockByState(BLOCK_DATA_IMPORTED);
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
    protected final Optional<BitcoinBlock> processBlock(final int blockHeight) {
        final BitcoinBlock blockToProcess = getBlockRepository().findFullByHeight(blockHeight);

        // -------------------------------------------------------------------------------------------------------------
        // We link the addresses to the input and the origin transaction.
        final AtomicInteger txCounter = new AtomicInteger();
        final int txSize = blockToProcess.getTx().size();
        blockToProcess.getTransactions()
                .parallelStream()
                .forEach(
                        t -> {
                            // For each Vin.
                            t.getInputs()
                                .stream()
                                .filter(vin -> !vin.isCoinbase()) // If it's NOT a coinbase transaction.
                                .forEach(vin -> {
                                    // -------------------------------------------------------------------------
                                    // We retrieve the original transaction.
                                    BitcoinTransactionOutput originTransactionOutput = getTransactionOutputRepository().findByKey(vin.getTxId() + "-" + vin.getvOut());

                                    // -------------------------------------------------------------------------
                                    // We check if this output is not missing.
                                    if (originTransactionOutput == null) {
                                            addError("*");
                                            addError("* Transaction " + t.getTxId() + " requires a missing origin transaction output : " + vin.getTxId() + " / " + vin.getvOut());
                                            BitcoinTransaction missingTransaction = getTransactionRepository().findByTxId(vin.getTxId());
                                            missingTransaction.getOutputs()
                                                .stream()
                                                .sorted(Comparator.comparingInt(BitcoinTransactionOutput::getN))
                                                .forEach(o -> addError("* " + missingTransaction.getTxId() + " - vout : " + o.getN()));
                                            addError("*");
                                        throw new RuntimeException("Treating transaction " + t.getTxId() + " requires a missing origin transaction output : " + vin.getTxId() + " / " + vin.getvOut());
                                    } else {
                                        // -------------------------------------------------------------------------
                                        // We create the link.
                                        vin.setTransactionOutput(originTransactionOutput);

                                        // -------------------------------------------------------------------------
                                        // We set all the addresses linked to this input.
                                        originTransactionOutput.getAddresses()
                                            .stream()
                                            .filter(Objects::nonNull)
                                            .forEach(a -> vin.setBitcoinAddress(getAddressRepository().findByAddressWithoutDepth(a)));
                                        }
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
                            addLog("- Transaction " + txCounter.incrementAndGet() + "/" + txSize + " treated (" + t.getTxId()  + " : " + t.getInputs().size() + " vin(s) & " + t.getOutputs().size() + " vout(s))");
                        });

        // ---------------------------------------------------------------------------------------------------------
        // We set the previous and the next block.
        BitcoinBlock previousBlock = getBlockRepository().findByHashWithoutDepth(blockToProcess.getPreviousBlockHash());
        blockToProcess.setPreviousBlock(previousBlock);
        addLog("Setting the previous block of this block");
        if (previousBlock != null) {
            previousBlock.setNextBlock(blockToProcess);
            addLog("Setting this block as next block of the previous one");
        }

        return Optional.of(blockToProcess);
    }

    /**
     * Return the state to set to the block that has been processed.
     *
     * @return state to set of the block that has been processed.
     */
    @Override
    protected final BitcoinBlockState getNewStateOfProcessedBlock() {
        return BLOCK_FULLY_IMPORTED;
    }

}