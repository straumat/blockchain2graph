package com.oakinvest.b2g.util.bitcoin.buffer;

import com.oakinvest.b2g.dto.bitcoin.bitcoind.getblock.GetBlockResult;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.oakinvest.b2g.configuration.bitcoin.BitcoinConfiguration.BITCOIN_BLOCK_GENERATION_DELAY;

/**
 * Bitcoin data service buffer and cache store
 * Created by straumat on 30/06/17.
 */
@Component
public class BitcoinDataServiceBufferStore {

    /**
     * How many milli seconds in 1 minute.
     */
    private static final float MILLISECONDS_IN_ONE_MINUTE = 60F * 1000F;

    /**
     * Last block count value.
     */
    private int lastBlockCountValue = -1;

    /**
     * Last block count access.
     */
    private long lastBlockCountValueAccess = -1;

    /**
     * Bitcoin blocks buffer.
     */
    private final Map<Integer, GetBlockResult> blocksBuffer = new ConcurrentHashMap<>();

    /**
     * Bitcoin transactions buffer.
     */
    private final Map<String, GetRawTransactionResult> transactionsBuffer = new ConcurrentHashMap<>();

    /**
     * Constructor.
     */
    public BitcoinDataServiceBufferStore() {
    }

    /**
     * Clear buffer.
     */
    @SuppressWarnings("checkstyle:designforextension")
    public void clear() {
        lastBlockCountValue = -1;
        lastBlockCountValueAccess = -1;
        blocksBuffer.clear();
        transactionsBuffer.clear();
    }

    /**
     * Returns true if it's time to refresh the block count.
     *
     * @return true if update is required
     */
    @SuppressWarnings("checkstyle:designforextension")
    boolean isBlockCountOutdated() {
        // If getBlockcount has never been call, of course, it's outdated.
        if (lastBlockCountValueAccess == -1) {
            return true;
        } else {
            // Getting the time elapsed since last call to getblockcount.
            float elapsedMinutesSinceLastCall = (System.currentTimeMillis() - lastBlockCountValueAccess) / MILLISECONDS_IN_ONE_MINUTE;
            // Return true if it's been more than 10 minutes.
            return elapsedMinutesSinceLastCall > BITCOIN_BLOCK_GENERATION_DELAY;
        }
    }

    /**
     * Update the cached blockcount value.
     *
     * @param blockCount block count
     */
    @SuppressWarnings("checkstyle:designforextension")
    void updateBlockCountInCache(final int blockCount) {
        lastBlockCountValue = blockCount;
        lastBlockCountValueAccess = System.currentTimeMillis();
    }

    /**
     * Returns the block count in cache.
     *
     * @return block count
     */
    @SuppressWarnings("checkstyle:designforextension")
    int getBlockCountFromCache() {
        return lastBlockCountValue;
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
    public final void removeBlockInBuffer(final int blockHeight) {
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
