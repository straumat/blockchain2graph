package com.oakinvest.b2g.bitcoin.service;

import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.BitcoinCoreBlockData;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getblock.GetBlockResponse;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getblock.GetBlockResult;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.bitcoin.util.buffer.BitcoinDataServiceBuffer;
import com.oakinvest.b2g.bitcoin.util.status.ApplicationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bitcoin data service implementation.
 *
 * Created by straumat on 11/06/17.
 */
@Service
public class BitcoinDataServiceImplementation implements BitcoinDataService {

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(BitcoinDataServiceImplementation.class);

    /**
     * Genesis transaction hash.
     */
    private static final String GENESIS_BLOCK_TRANSACTION = "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b";

    /**
     * Status service.
     */
    private final ApplicationStatus status;

    /**
     * Bitcoin core service.
     */
    private final BitcoinCoreService bitcoinCoreService;

    /**
     * Buffer.
     */
    private final BitcoinDataServiceBuffer buffer;

    /**
     * Constructor.
     *
     * @param newBitcoinCoreService core service
     * @param newStatusService      status service
     * @param newBuffer             buffer
     */
    public BitcoinDataServiceImplementation(final BitcoinCoreService newBitcoinCoreService, final ApplicationStatus newStatusService, final BitcoinDataServiceBuffer newBuffer) {
        this.status = newStatusService;
        this.bitcoinCoreService = newBitcoinCoreService;
        this.buffer = newBuffer;
    }

    /**
     * Return getblockcount (The result stays in cache for 10 minutes).
     *
     * @return the number of blocks in the block chain.
     */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public Optional<Integer> getBlockCount() {
        try {
            GetBlockCountResponse blockCountResponse = bitcoinCoreService.getBlockCount();
            if (blockCountResponse.getError() == null) {
                return Optional.of(blockCountResponse.getResult());
            } else {
                // Error while retrieving the number of blocks in core.
                log.error("Error getting the number of blocks : " + blockCountResponse.getError());
                status.setLastErrorMessage("Error getting the number of blocks : " + blockCountResponse.getError());
                return Optional.empty();
            }
        } catch (Exception e) {
            // Error while retrieving the number of blocks in core.
            log.error("Error getting the number of blocks : " + e.getMessage());
            status.setLastErrorMessage("Error getting the number of blocks : " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get block data.
     *
     * @param blockHeight block height
     * @return block data
     */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public Optional<BitcoinCoreBlockData> getBlockData(final int blockHeight) {
        Optional<GetBlockResult> block = getBlockResult(blockHeight);

        // If we have the block.
        if (block.isPresent()) {

            // Transactions & addresses.
            final List<GetRawTransactionResult> transactions = new LinkedList<>();
            final Set<String> addresses = Collections.synchronizedSet(new HashSet<>());

            // We retrieve all
            final AtomicInteger loadedTransactionsCounter = new AtomicInteger(0);
            block.get().getTx()
                    .forEach(txId -> {
                        Optional<GetRawTransactionResult> transactionResponse = getRawTransactionResult(txId);
                        if (transactionResponse.isPresent()) {
                            // Adding the transaction.
                            transactions.add(transactionResponse.get());
                            // Adding the addresses.
                            transactionResponse.get().getVout().forEach(o -> addresses.addAll(o.getScriptPubKey().getAddresses()));
                            status.getCurrentBlockStatus().setLoadedTransactions(loadedTransactionsCounter.incrementAndGet());
                        } else {
                            log.error("Transaction " + txId + " missing");
                            status.setLastErrorMessage("Transaction " + txId + " missing");
                        }
                    });

            // We check that we have all transactions.
            if (transactions.size() != block.get().getTx().size()) {
                log.error("All transactions were not retrieved");
                status.setLastErrorMessage("All transactions were not retrieved");
                return Optional.empty();
            }

            // We return the data.
            return Optional.of(new BitcoinCoreBlockData(block.get(), transactions, addresses));

        } else {
            log.error("The block was not retrieved");
            status.setLastErrorMessage("The block was not retrieved");
            return Optional.empty();
        }
    }

    /**
     * Returns the block result from the buffer or core.
     *
     * @param blockHeight bloc height
     * @return block result
     */
    private Optional<GetBlockResult> getBlockResult(final int blockHeight) {
        Optional<GetBlockResult> result = buffer.getBlockInBuffer(blockHeight);
        if (!result.isPresent()) {
            result = getBlockResultFromBitcoinCore(blockHeight);
            // We add it so the buffer loader won't try to add it.
            result.ifPresent(getBlockResult -> buffer.addBlockInBuffer(blockHeight, getBlockResult));
        }
        return result;
    }

    /**
     * Returns the transaction result from the buffer or core.
     *
     * @param txId transaction id
     * @return transaction result
     */
    private Optional<GetRawTransactionResult> getRawTransactionResult(final String txId) {
        Optional<GetRawTransactionResult> result = buffer.getTransactionInBuffer(txId);
        if (!result.isPresent()) {
            result = getRawTransactionResultFromBitcoinCore(txId);
            // We add it so the buffer loader won't try to add it.
            result.ifPresent(getRawTransactionResult -> buffer.addTransactionInBuffer(txId, getRawTransactionResult));
        }
        return result;
    }

    /**
     * Return the block result from core.
     *
     * @param blockHeight block height
     * @return block result
     */
    private Optional<GetBlockResult> getBlockResultFromBitcoinCore(final int blockHeight) {
        try {
            // ---------------------------------------------------------------------------------------------------------
            // We retrieve the block hash.
            GetBlockHashResponse blockHashResponse = bitcoinCoreService.getBlockHash(blockHeight);
            if (blockHashResponse.getError() == null) {
                // -----------------------------------------------------------------------------------------------------
                // Then we retrieve the block data.
                String blockHash = blockHashResponse.getResult();
                final GetBlockResponse blockResponse = bitcoinCoreService.getBlock(blockHash);
                if (blockResponse.getError() == null) {
                    // Fix duplicated transactions.
                    fixDuplicatedTransaction(blockResponse.getResult());
                    return Optional.of(blockResponse.getResult());
                } else {
                    // Error while retrieving the block information.
                    log.error("Error retrieving the block : " + blockResponse.getError());
                    status.setLastErrorMessage("Error retrieving the block : " + blockResponse.getError());
                    return Optional.empty();
                }
            } else {
                // Error while retrieving the block information.
                log.error("Error retrieving the block : " + blockHashResponse.getError());
                status.setLastErrorMessage("Error retrieving the block : " + blockHashResponse.getError());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error getting the block n°" + blockHeight + " : " + e.getMessage(), e);
            status.setLastErrorMessage("Error getting the block n°" + blockHeight + " : " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Return the transaction result from core.
     *
     * @param txId transaction id.
     * @return transaction result
     */
    private Optional<GetRawTransactionResult> getRawTransactionResultFromBitcoinCore(final String txId) {
        if (!GENESIS_BLOCK_TRANSACTION.equals(txId)) {
            try {
                GetRawTransactionResponse r = bitcoinCoreService.getRawTransaction(txId);
                if (r != null && r.getError() == null && r.getResult() != null) {
                    return Optional.of(r.getResult());
                } else {
                    // Error in calling the services.
                    if (r == null) {
                        status.setLastErrorMessage("Error getting transaction " + txId + " : Result is null");
                    } else {
                        if (r.getError() != null) {
                            log.error("Error getting transaction " + txId + " : " + r.getError().getMessage());
                            status.setLastErrorMessage("Error getting transaction " + txId + " : " + r.getError().getMessage());
                        }
                        if (r.getResult() == null) {
                            log.error("Error getting transaction " + txId + " : Empty result");
                            status.setLastErrorMessage("Error getting transaction " + txId + " : Empty result");
                        }
                    }
                    return Optional.empty();
                }
            } catch (Exception e) {
                log.error("Error getting the transaction " + txId + " : " + e.getMessage(), e);
                status.setLastErrorMessage("Error getting the transaction " + txId + " : " + e.getMessage());
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * Load transactions from a block in buffer.
     *
     * @param blockHeight block height
     */
    @Override
    public final void addBlockInBuffer(final int blockHeight) {
        Optional<GetBlockResult> block = getBlockResultFromBitcoinCore(blockHeight);
        block.ifPresent(getBlockResult -> {

            // Add the block in buffer.
            buffer.addBlockInBuffer(blockHeight, getBlockResult);

            // Invert the tx order to start by the end.
            ArrayList<String> transactions = getBlockResult.getTx();
            Collections.reverse(transactions);

            // Add the transactions in buffer.
            transactions.parallelStream()
                    .filter(txId -> !buffer.getTransactionInBuffer(txId).isPresent())
                    .forEach(txId -> {
                        Optional<GetRawTransactionResult> result = getRawTransactionResultFromBitcoinCore(txId);
                        result.ifPresent(getRawTransactionResult -> buffer.addTransactionInBuffer(txId, getRawTransactionResult));
                    });
        });
    }

    /**
     * Suppress duplicated transaction in blocks.
     *
     * @param getBlockResult block
     */
    private void fixDuplicatedTransaction(final GetBlockResult getBlockResult) {
        // First duplicated transaction.
        final int duplicatedTxIdBlock1 = 91812;
        final String duplicatedTxId1 = "d5d27987d2a3dfc724e359870c6644b40e497bdc0589a033220fe15429d88599";
        if (getBlockResult.getHeight() == duplicatedTxIdBlock1) {
            getBlockResult.getTx().remove(duplicatedTxId1);
        }

        // Second duplicated transaction.
        final int duplicatedTxIdBlock2 = 91722;
        final String duplicatedTxId2 = "e3bf3d07d4b0375638d5f1db5255fe07ba2c4cb067cd81b84ee974b6585fb468";
        if (getBlockResult.getHeight() == duplicatedTxIdBlock2) {
            getBlockResult.getTx().remove(duplicatedTxId2);
        }
    }

}
