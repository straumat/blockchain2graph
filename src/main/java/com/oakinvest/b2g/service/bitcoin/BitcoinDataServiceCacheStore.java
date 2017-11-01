package com.oakinvest.b2g.service.bitcoin;

import org.springframework.stereotype.Component;

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
     * Last block count value.
     */
    private int lastBlockCountValue = -1;

    /**
     * Last block count access.
     */
    private long lastBlockCountValueAccess = -1;

    /**
     * Constructor.
     */
    public BitcoinDataServiceCacheStore() {
    }

    /**
     * Clear cache.
     */
    @SuppressWarnings("checkstyle:designforextension")
    public void clear() {
        lastBlockCountValue = -1;
        lastBlockCountValueAccess = -1;
    }

    /**
     * Returns true if it's time to refresh the block count.
     *
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
     *
     * @param blockCount block count
     */
    @SuppressWarnings("checkstyle:designforextension")
    public void updateBlockCountInCache(final int blockCount) {
        lastBlockCountValue = blockCount;
        lastBlockCountValueAccess = System.currentTimeMillis();
    }

    /**
     * Returns the block count in cache.
     *
     * @return block count
     */
    @SuppressWarnings("checkstyle:designforextension")
    public int getBlockCountFromCache() {
        return lastBlockCountValue;
    }

}
