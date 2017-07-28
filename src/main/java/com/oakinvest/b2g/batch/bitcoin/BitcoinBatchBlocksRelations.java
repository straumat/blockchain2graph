package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.repository.bitcoin.BitcoinRepositories;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.service.BitcoindService;
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
     * Bitcoind service.
     */
    private BitcoindService bitcoindService;

    /**
     * Constructor.
     *
     * @param newBitcoinRepositories    bitcoin repositories
     * @param newBitcoinDataService     bitcoin data service
     * @param newStatus                 status
     * @param newBitcoindService        bitcoind service
     */
    public BitcoinBatchBlocksRelations(final BitcoinRepositories newBitcoinRepositories, final BitcoinDataService newBitcoinDataService, final StatusService newStatus, final BitcoindService newBitcoindService) {
        super(newBitcoinRepositories, newBitcoinDataService, newStatus);
        bitcoindService = newBitcoindService;
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
        // We link the addresses to the input and the origin transaction.
        final AtomicInteger txCounter = new AtomicInteger();
        final int txSize = blockToProcess.getTx().size();
        blockToProcess.getTx()
                .parallelStream()
                .forEach(
                        txId -> {
                            // Retrieving the transaction.
                            getTransactionRepository().findByTxId(txId).forEach(t -> {

                                // For each Vin.
                                t.getInputs()
                                        .stream()
                                        .filter(vin -> !vin.isCoinbase()) // If it's NOT a coinbase transaction.
                                        .forEach(vin -> {

                                            // -------------------------------------------------------------------------
                                            // Test for missing transaction.
                                            BitcoinTransactionOutput originTransactionOutput = getTransactionOutputRepository().findByKey(vin.getTxId() + "-" + vin.getvOut());
                                            if (originTransactionOutput == null) {
                                                fixEmptyTransaction(vin.getTxId());
                                                originTransactionOutput = getTransactionOutputRepository().findByKey(vin.getTxId() + "-" + vin.getvOut());
                                            }

                                            // -------------------------------------------------------------------------
                                            // We retrieve the original transaction.
                                            vin.setTransactionOutput(originTransactionOutput);

                                            // -------------------------------------------------------------------------
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
                                addLog("- Transaction " + txCounter.incrementAndGet() + "/" + txSize + " treated (" + txId  + " : " + t.getInputs().size() + " vin(s) & " + t.getOutputs().size() + " vout(s))");
                        });
                });

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

    /**
     * Fix empty transaction (empty vin et vout).
     * @param txId transaction id
     */
    private void fixEmptyTransaction(final String txId) {
        addLog("Fixing empty transaction for transaction " + txId);
        BitcoinTransaction transaction = getTransactionRepository().findByTxId(txId).get(0);

        // We retrieve the transaction from bitcoind.
        GetRawTransactionResult getRawTransactionResult = bitcoindService.getRawTransaction(txId).getResult();

        // Treating all vin.
        /*
        getRawTransactionResult.getVin()
                .stream()
                .filter(vin -> vin.getCoinbase() != null)
                .forEach(vin -> {
                    BitcoinTransactionInput bti = getMapper().rawTransactionVIn(vin);
                    //bti.setBitcoinAddress(getAddressRepository().findByAddress());
                    //if (!transaction.getInput(vin.getTxid(), vin.getVout()).isPresent()) {
                        transaction.getInputs().add(bti);

                    //}
                });
        */

        // Treating all vout.
        getRawTransactionResult.getVout()
                .forEach(vout -> {
                    BitcoinTransactionOutput bto = getMapper().rawTransactionVout(vout);
                    bto.setTxId(txId);
                    bto.setKey(txId + "-" + bto.getN());
                    bto.setBitcoinAddress(getAddressRepository().findByAddress(vout.getScriptPubKey().getAddresses().get(0)));
                    //if (!transaction.getOutput(vout.getN()).isPresent()) {
                        transaction.getOutputs().add(bto);
                    //}
                });

        // We save.
        getTransactionRepository().save(transaction);
    }

}