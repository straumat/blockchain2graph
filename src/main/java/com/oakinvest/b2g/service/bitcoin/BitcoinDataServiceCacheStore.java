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
    private long lastBlockCountAccess = -1;

    /**
     * Constructor.
     */
    public BitcoinDataServiceCacheStore() {
        buffer = new ConcurrentSkipListSet<>(new BitcoindBlockDataComparator());
    }

    /**
     * Returns true if it's time to refresh the block count.
     * @return true if update is required
     */
    @SuppressWarnings("checkstyle:designforextension")
    public boolean isBlockCountOutdated() {
        // If getBlockcount has never been call, of course, it's outdated.
        if (lastBlockCountAccess == -1) {
            return true;
        } else {
            // Getting the time elapsed since last call to getblockcount.
            float elapsedMinutesSinceLastCall = (System.currentTimeMillis() - lastBlockCountAccess) / MILLISECONDS_IN_ONE_MINUTE;
            // Return true if it's been more than 10 minutes.
            return elapsedMinutesSinceLastCall > BITCOIN_BLOCK_GENERATION_DELAY;
        }
    }

    /**
     * Update the cached blockcount value.
     * @param blockCount block count
     */
    @SuppressWarnings("checkstyle:designforextension")
    public void updateBlockCount(final long blockCount) {
        // We update the value.
        lastBlockCountValue = blockCount;
        lastBlockCountAccess = System.currentTimeMillis();

        // We reduce the cache size at this moment.
        while (buffer.size() > BITCOIND_BUFFER_SIZE) {
            buffer.pollFirst();
        }

    }

    /**
     * Returns the block count in cache.
     * @return block count
     */
    @SuppressWarnings("checkstyle:designforextension")
    public long getBlockCount() {
        return lastBlockCountValue;
    }

    /**
     * Add a block data to the cache.
     * @param blockData block
     */
    @SuppressWarnings("checkstyle:designforextension")
    public void addBlockData(final BitcoindBlockData blockData) {
        buffer.add(blockData);
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
    public Optional<BitcoindBlockData> getBlockData(final long blockHeight) {
        // We get it from the cache.
        Optional<BitcoindBlockData> block = buffer.stream().filter(b -> b.getBlock().getHeight() == blockHeight).findFirst();
        return block;
    }





}
