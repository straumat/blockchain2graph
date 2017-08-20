package com.oakinvest.b2g.service.bitcoin;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockDataComparator;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.oakinvest.b2g.configuration.ParametersConfiguration.BITCOIND_BUFFER_SIZE;
import static com.oakinvest.b2g.configuration.ParametersConfiguration.BITCOIN_BLOCK_GENERATION_DELAY;

/**
 * Bitcoin data service cache store
 * Created by straumat on 30/06/17.
 */
@Component
public class BitcoinDataServiceCacheStore {

    /**
     * How many milli seconds in 1 minute.
     */
    private static final float MILLISECONDS_IN_ONE_MINUTE = 60F * 1000F;

    /**
     * Buffer content.
     */
    private final ConcurrentSkipListSet<BitcoindBlockData> buffer;

    /**
     * Last block count value.
     */
    private long lastBlockCountValue = -1;

    /**
     * Last block count access.
     */
    private long lastBlockCountValueAccess = -1;

    /**
     * Constructor.
     */
    public BitcoinDataServiceCacheStore() {
        buffer = new ConcurrentSkipListSet<>(new BitcoindBlockDataComparator());
    }

    /**
     * Clear cache.
     */
    public void clear() {
        lastBlockCountValue = -1;
        lastBlockCountValueAccess = -1;
        buffer.clear();
    }

    /**
     * Returns true if it's time to refresh the block count.
     * @return true if update is required
     */
    @SuppressWarnings("checkstyle:designforextension")
    public boolean isBlockCountOutdated() {
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
     * @param blockCount block count
     */
    @SuppressWarnings("checkstyle:designforextension")
    public void updateBlockCountInCache(final long blockCount) {
        lastBlockCountValue = blockCount;
        lastBlockCountValueAccess = System.currentTimeMillis();
    }

    /**
     * Returns the block count in cache.
     * @return block count
     */
    @SuppressWarnings("checkstyle:designforextension")
    public long getBlockCountFromCache() {
        return lastBlockCountValue;
    }

    /**
     * Add a block data to the cache.
     * @param blockData block
     */
    @SuppressWarnings("checkstyle:designforextension")
    public void addBlockDataInCache(final BitcoindBlockData blockData) {
        // Reducing the cache.
        while (buffer.size() > BITCOIND_BUFFER_SIZE) {
            buffer.pollFirst();
        }

        // Add the block in cache.
        if (!isBlockDataInCache(blockData.getBlock().getHeight())) {
            buffer.add(blockData);
        }
    }

    /**
     * Returns true if the block is in cache.
     * @param blockHeight block height
     * @return true if in cache
     */
    @SuppressWarnings("checkstyle:designforextension")
    public boolean isBlockDataInCache(final long blockHeight) {
        return buffer.stream().anyMatch(b -> b.getBlock().getHeight() == blockHeight);
    }

    /**
     * Get block data from buffer.
     * @param blockHeight block height
     * @return block data
     */
    @SuppressWarnings("checkstyle:designforextension")
    public Optional<BitcoindBlockData> getBlockDataFromCache(final long blockHeight) {
        return buffer.stream().filter(b -> b.getBlock().getHeight() == blockHeight).findFirst();
    }

    /**
     * Remove a block data to the cache.
     * @param blockHeight block height
     */
    @SuppressWarnings("checkstyle:designforextension")
    public void removeBlockDataFromCache(final long blockHeight) {
        Optional<BitcoindBlockData> blockData = buffer.stream().filter(b -> b.getBlock().getHeight() == blockHeight).findFirst();
        blockData.ifPresent(buffer::remove);
    }

}
