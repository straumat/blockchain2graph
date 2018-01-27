package com.oakinvest.b2g.util.bitcoin.buffer;

import com.oakinvest.b2g.dto.bitcoin.bitcoind.getblock.GetBlockResult;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bitcoin data service buffer.
 * Created by straumat on 30/06/17.
 */
@Component
public class BitcoinDataServiceBuffer {

    /**
     * Bitcoin blocks buffer.
     */
    private final Map<Integer, GetBlockResult> blocksBuffer = new ConcurrentHashMap<>();

    /**
     * Bitcoin transactions buffer.
     */
    private final Map<String, GetRawTransactionResult> transactionsBuffer = new ConcurrentHashMap<>();

    /**
     * Clear buffer.
     */
    public final void clear() {
        blocksBuffer.clear();
        transactionsBuffer.clear();
    }

    /**
     * Purge the buffer of useless data.
     *
     * @param lastBlockProcessed last block height inserted in neo4j
     */
    public final void purge(final int lastBlockProcessed) {
        blocksBuffer.forEach((blockHeight, block) -> {
            // If the block is under the current block height, we remove everything
            if (blockHeight <= lastBlockProcessed) {
                block.getTx().forEach(this::removeTransactionInBuffer);
                removeBlockInBuffer(blockHeight);
            }
        });
    }

    /**
     * Add block in buffer.
     *
     * @param blockHeight    block height
     * @param getBlockResult block
     */
    public final void addBlockInBuffer(final int blockHeight, final GetBlockResult getBlockResult) {
        blocksBuffer.put(blockHeight, getBlockResult);
    }

    /**
     * Returns true if the block is in the buffer.
     *
     * @param blockHeight block height
     * @return true if it's in the buffer
     */
    public final boolean isBLockInBuffer(final int blockHeight) {
        return getBlockInBuffer(blockHeight).isPresent();
    }

    /**
     * Retrieve a block in buffer.
     *
     * @param blockHeight block height
     * @return block result
     */
    public final Optional<GetBlockResult> getBlockInBuffer(final int blockHeight) {
        GetBlockResult r = blocksBuffer.get(blockHeight);
        if (r != null) {
            // If it's in the buffer, we retrieve it.
            return Optional.of(r);
        } else {
            // If it's not in the buffer, we return empty.
            return Optional.empty();
        }
    }

    /**
     * Remove block in buffer.
     *
     * @param blockHeight block height
     */
    private void removeBlockInBuffer(final int blockHeight) {
        blocksBuffer.remove(blockHeight);
    }

    /**
     * Add transactions in buffer.
     *
     * @param txId                    transaction id
     * @param getRawTransactionResult bitcoin transaction
     */
    public final void addTransactionInBuffer(final String txId, final GetRawTransactionResult getRawTransactionResult) {
        transactionsBuffer.put(txId, getRawTransactionResult);
    }

    /**
     * Retrieve a transaction in buffer.
     *
     * @param txid transaction id
     * @return bitcoin transaction
     */
    public final Optional<GetRawTransactionResult> getTransactionInBuffer(final String txid) {
        GetRawTransactionResult r = transactionsBuffer.get(txid);
        if (r != null) {
            // If it's in the buffer, we retrieve it.
            return Optional.of(r);
        } else {
            // If it's not in the buffer, we return empty.
            return Optional.empty();
        }
    }

    /**
     * Remove a transaction in buffer.
     *
     * @param txId transaction id
     */
    public final void removeTransactionInBuffer(final String txId) {
        transactionsBuffer.remove(txId);
    }

}
