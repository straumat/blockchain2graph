package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.repository.bitcoin.BitcoinRepositories;
import com.oakinvest.b2g.service.bitcoin.BitcoinDataService;
import com.oakinvest.b2g.service.StatusService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bitcoin import blocks batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinBatchBlocks extends BitcoinBatchTemplate {

    /**
     * Constructor.
     *
     * @param newBitcoinRepositories bitcoin repositories
     * @param newBitcoinDataService  bitcoin data service
     * @param newStatus              status
     */
    public BitcoinBatchBlocks(final BitcoinRepositories newBitcoinRepositories, final BitcoinDataService newBitcoinDataService, final StatusService newStatus) {
        super(newBitcoinRepositories, newBitcoinDataService, newStatus);
    }

    /**
     * Bitcoin addresses cache.
     */
    private final Map<String, BitcoinAddress> addressesCache = new ConcurrentHashMap<>();

    /**
     * Return the block to process.
     *
     * @return block to process.
     */
    @Override
    protected final Optional<Integer> getBlockHeightToProcess() {
        // We retrieve the next block to process according to the database.
        int blockToProcess = (int) (getBlockRepository().count() + 1);
        final Optional<Integer> totalBlockCount = getBitcoinDataService().getBlockCount();

        // We check if that next block exists by retrieving the block count.
        if (totalBlockCount.isPresent()) {
            // We update the global status of blockcount (if needed).
            if (totalBlockCount.get() != getStatus().getTotalBlockCount()) {
                getStatus().setTotalBlockCount(totalBlockCount.get());
            }
            // We return the block to process.
            if (blockToProcess <= totalBlockCount.get()) {
                return Optional.of(blockToProcess);
            } else {
                return Optional.empty();
            }
        } else {
            // Error while retrieving the number of blocks in bitcoind.
            return Optional.empty();
        }
    }

    /**
     * Retrieve a bitcoin address from the cache or from neo4j.
     *
     * @param address bitcoin address
     * @return bitcoin address object
     */
    private BitcoinAddress getBitcoinAddress(final String address) {
        BitcoinAddress bitcoinAddress = addressesCache.get(address);
        if (bitcoinAddress == null) {
            bitcoinAddress = getAddressRepository().findByAddressWithoutDepth(address);
        }
        return bitcoinAddress;
    }

    /**
     * Process block.
     *
     * @param blockHeight block height to process.
     */
    @Override
    protected final Optional<BitcoinBlock> processBlock(final int blockHeight) {
        Optional<BitcoindBlockData> blockData = getBitcoinDataService().getBlockData(blockHeight);

        // -------------------------------------------------------------------------------------------------------------
        // If we have the data.
        if (blockData.isPresent()) {

            // ---------------------------------------------------------------------------------------------------------
            // We create the block to save. We retrieve the data from bitcoind and map it.
            final BitcoinBlock block = getMapper().blockDataToBitcoinBlock(blockData.get());

            // ---------------------------------------------------------------------------------------------------------
            // We create all the addresses.
            addressesCache.clear();
            addLog("Treating addresses from " + block.getTx().size() + " transaction(s)");
            blockData.get().getAddresses()
                    .parallelStream() // In parallel.
                    .filter(Objects::nonNull) // If the address is not null.
                    .filter(address -> !getAddressRepository().exists(address)) // If the address doesn't exists.
                    .forEach(a -> {
                        addressesCache.put(a, new BitcoinAddress(a));
                        addLog("- Address " + a + " created");
                    });

            // ---------------------------------------------------------------------------------------------------------
            // We link the addresses to the input and the origin transaction.
            final AtomicInteger txCounter = new AtomicInteger();
            final int txSize = block.getTx().size();
            addLog("Treating " + txSize + " transaction(s)");
            block.getTransactions()
                    .parallelStream()
                    .forEach(
                            t -> {
                                // -------------------------------------------------------------------------------------
                                // For each Vin.
                                t.getInputs()
                                        .parallelStream()
                                        .filter(vin -> !vin.isCoinbase()) // If it's NOT a coinbase transaction.
                                        .forEach(vin -> {
                                            // -------------------------------------------------------------------------
                                            // We retrieve the original transaction.
                                            BitcoinTransactionOutput originTransactionOutput = getTransactionOutputRepository().findByTxIdAndN(vin.getTxId(), vin.getvOut());

                                            // if we don't find in the database, this transaction must be in the block.
                                            if (originTransactionOutput == null) {
                                                Optional<BitcoinTransaction> temp = block.getTransactions()
                                                        .stream()
                                                        .filter(o -> o.getTxId().equals(vin.getTxId()))
                                                        .findFirst();
                                                if (temp.isPresent()) {
                                                    if (temp.get().getOutputByIndex(vin.getvOut()).isPresent()) {
                                                        originTransactionOutput = temp.get().getOutputByIndex(vin.getvOut()).get();
                                                    }
                                                }
                                            }

                                            // -------------------------------------------------------------------------
                                            // We create the link.
                                            vin.setTransactionOutput(originTransactionOutput);

                                            // -------------------------------------------------------------------------
                                            // We set all the addresses linked to this input.
                                            originTransactionOutput.getAddresses()
                                                    .stream()
                                                    .filter(Objects::nonNull)
                                                    .forEach(a -> vin.setBitcoinAddress(getBitcoinAddress(a)));
                                        });

                                // -------------------------------------------------------------------------------------
                                // For each Vout.
                                t.getOutputs()
                                        .parallelStream()
                                        .forEach(vout -> {
                                            // -------------------------------------------------------------------------
                                            // We set all the addresses linked to this output.
                                            vout.getAddresses()
                                                    .stream()
                                                    .filter(Objects::nonNull)
                                                    .forEach(a -> vout.setBitcoinAddress(getBitcoinAddress(a)));
                                        });

                                // -------------------------------------------------------------------------------------
                                // Save the transaction and add log to say we are done.
                                //getTransactionRepository().save(t);
                                addLog("- Transaction " + txCounter.incrementAndGet() + "/" + txSize + " created (" + t.getTxId() + " : " + t.getInputs().size() + " vin(s) & " + t.getOutputs().size() + " vout(s))");
                            });

            // ---------------------------------------------------------------------------------------------------------
            // We set the previous and the next block.
            if (block.getHeight() > 1) {
                BitcoinBlock previousBlock = getBlockRepository().findByHashWithoutDepth(block.getPreviousBlockHash());
                block.setPreviousBlock(previousBlock);
                addLog("Setting the previous block of this block");
                previousBlock.setNextBlock(block);
                addLog("Setting this block as next block of the previous one");
            }

            // ---------------------------------------------------------------------------------------------------------
            // We return the block.
            return Optional.of(block);

        } else {
            // Or nothing if we did not retrieve the data.
            addError("No response from bitcoind for block n°" + getFormattedBlockHeight(blockHeight));
            return Optional.empty();
        }
    }

}
