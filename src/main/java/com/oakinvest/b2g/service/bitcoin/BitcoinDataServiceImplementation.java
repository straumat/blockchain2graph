package com.oakinvest.b2g.service.bitcoin;

import com.oakinvest.b2g.dto.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.getblock.GetBlockResult;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.util.bitcoin.buffer.BitcoinDataServiceBuffer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Bitcoin data service implementation.
 * Created by straumat on 11/06/17.
 */
@Service
public class BitcoinDataServiceImplementation implements BitcoinDataService {

    /**
     * Genesis transaction hash.
     */
    private static final String GENESIS_BLOCK_TRANSACTION = "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b";

    /**
     * Status service.
     */
    private final StatusService status;

    /**
     * Bitcoind service.
     */
    private final BitcoindService bitcoindService;

    /**
     * Buffer.
     */
    private final BitcoinDataServiceBuffer buffer;

    /**
     * Constructor.
     *
     * @param newBitcoindService bitcoind service
     * @param newStatusService   status service
     * @param newBuffer          buffer
     */
    public BitcoinDataServiceImplementation(final BitcoindService newBitcoindService, final StatusService newStatusService, final BitcoinDataServiceBuffer newBuffer) {
        this.status = newStatusService;
        this.bitcoindService = newBitcoindService;
        this.buffer = newBuffer;
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

    /**
     * Return getblockcount (The result stays in cache for 10 minutes).
     *
     * @return the number of blocks in the block chain and -1 if error.
     */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public Optional<Integer> getBlockCount() {
        try {
            GetBlockCountResponse blockCountResponse = bitcoindService.getBlockCount();
            if (blockCountResponse.getError() == null) {
                return Optional.of(blockCountResponse.getResult());
            } else {
                // Error while retrieving the number of blocks in bitcoind.
                status.addError("Error getting the number of blocks : " + blockCountResponse.getError());
                return Optional.empty();
            }
        } catch (Exception e) {
            // Error while retrieving the number of blocks in bitcoind.
            status.addError("Error getting the number of blocks : " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Returns the block result from the buffer or bitcoind.
     *
     * @param blockHeight bloc height
     * @return block result
     */
    private Optional<GetBlockResult> getBlockResult(final int blockHeight) {
        Optional<GetBlockResult> result = buffer.getBlockInBuffer(blockHeight);
        if (!result.isPresent()) {
            result = getBlockResultFromBitcoind(blockHeight);
        }
        return result;
    }

    /**
     * Returns the transaction result from the buffer or bitcoind.
     *
     * @param txId transaction id
     * @return transaction result
     */
    private Optional<GetRawTransactionResult> getRawTransactionResult(final String txId) {
        Optional<GetRawTransactionResult> result = buffer.getTransactionInBuffer(txId);
        if (!result.isPresent()) {
            result = getRawTransactionResultFromBitcoind(txId);
        }
        return result;
    }

    /**
     * Return the block result from bitcoind.
     *
     * @param blockHeight block height
     * @return block result
     */
    private Optional<GetBlockResult> getBlockResultFromBitcoind(final int blockHeight) {
        try {
            // ---------------------------------------------------------------------------------------------------------
            // We retrieve the block hash.
            GetBlockHashResponse blockHashResponse = bitcoindService.getBlockHash(blockHeight);
            if (blockHashResponse.getError() == null) {
                // -----------------------------------------------------------------------------------------------------
                // Then we retrieve the block data.
                String blockHash = blockHashResponse.getResult();
                final GetBlockResponse blockResponse = bitcoindService.getBlock(blockHash);
                if (blockResponse.getError() == null) {
                    // Fix duplicated transactions.
                    fixDuplicatedTransaction(blockResponse.getResult());
                    return Optional.of(blockResponse.getResult());
                } else {
                    // Error while retrieving the block information.
                    status.addError("Error retrieving the block : " + blockResponse.getError(), null);
                    return Optional.empty();
                }
            } else {
                // Error while retrieving the block information.
                status.addError("Error retrieving the block : " + blockHashResponse.getError(), null);
                return Optional.empty();
            }
        } catch (Exception e) {
            status.addError("Error getting the block n°" + blockHeight + " : " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Return the transaction result from bitcoind.
     *
     * @param txId transaction id.
     * @return transaction result
     */
    private Optional<GetRawTransactionResult> getRawTransactionResultFromBitcoind(final String txId) {
        if (!GENESIS_BLOCK_TRANSACTION.equals(txId)) {
            try {
                GetRawTransactionResponse r = bitcoindService.getRawTransaction(txId);
                if (r != null && r.getError() == null && r.getResult() != null) {
                    return Optional.of(r.getResult());
                } else {
                    // Error in calling the services.
                    if (r == null) {
                        status.addError("Error getting transaction " + txId + " : Result is null");
                    } else {
                        if (r.getError() != null) {
                            status.addError("Error getting transaction " + txId + " : " + r.getError().getMessage());
                        }
                        if (r.getResult() == null) {
                            status.addError("Error getting transaction " + txId + " : Empty result");
                        }
                    }
                    return Optional.empty();
                }
            } catch (Exception e) {
                status.addError("Error getting the transaction " + txId + " : " + e.getMessage(), e);
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get block data from buffer.
     *
     * @param blockHeight block height
     * @return block data
     */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public Optional<BitcoindBlockData> getBlockData(final int blockHeight) {
        Optional<GetBlockResult> block = getBlockResult(blockHeight);

        // If we have the block.
        if (block.isPresent()) {

            // Transactions & addresses.
            final List<GetRawTransactionResult> transactions = new LinkedList<>();
            final Set<String> addresses = Collections.synchronizedSet(new HashSet<String>());

            // We retrieve all
            block.get().getTx()
                    .forEach(txId -> {
                        Optional<GetRawTransactionResult> transactionResponse = getRawTransactionResult(txId);
                        if (transactionResponse.isPresent()) {
                            // Adding the transaction.
                            transactions.add(transactionResponse.get());
                            // Adding the addresses.
                            transactionResponse.get().getVout().forEach(o -> addresses.addAll(o.getScriptPubKey().getAddresses()));
                        } else {
                            status.addError("Transaction " + txId + " missing");
                        }
                    });

            // We check that we have all transactions.
            if (transactions.size() != block.get().getTx().size()) {
                status.addError("All transactions were not retrieved");
                return Optional.empty();
            }

            // We return the data.
            return Optional.of(new BitcoindBlockData(block.get(), transactions, addresses));

        } else {
            status.addError("The block was not retrieved");
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
        Optional<GetBlockResult> block = getBlockResultFromBitcoind(blockHeight);
        block.ifPresent(getBlockResult -> {

            // Add the block in buffer.
            buffer.addBlockInBuffer(blockHeight, getBlockResult);

            // Invert the tx order to start by the end.
            ArrayList<String> transactions = getBlockResult.getTx();
            Collections.reverse(transactions);

            // Add the transactions in buffer.
            transactions
                    .parallelStream()
                    .forEach(txId -> {
                        Optional<GetRawTransactionResult> result = getRawTransactionResultFromBitcoind(txId);
                        result.ifPresent(getRawTransactionResult -> buffer.addTransactionInBuffer(txId, getRawTransactionResult));
                    });
        });
    }

}
