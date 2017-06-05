package com.oakinvest.b2g.batch.bitcoin.step1.blocks;

import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.oakinvest.b2g.configuration.CacheConfiguration.BITCOIND_BUFFER_SIZE;

/**
 * Bitcoind cache loader.
 * Created by straumat on 04/06/17.
 */
@Component
public class BitcoindCacheLoader {

    /**
     * Bitcoind service.
     */
    private final BitcoindService bitcoindService;

    /**
     * Constructor.
     * @param newBitcoindService bitcoind service
     */
    public BitcoindCacheLoader(final BitcoindService newBitcoindService) {
        this.bitcoindService = newBitcoindService;
    }

    /**
     * Load cache. We will load the lastBlockSaved + BUFFER_SIZE with this method.
     * @param lastBlockLoaded id of the last block loaded.
     */
    @Async
    @SuppressWarnings("checkstyle:designforextension")
    public void loadCache(final long lastBlockLoaded) {
        bitcoindService.getBlockData(lastBlockLoaded + BITCOIND_BUFFER_SIZE);
    }

}
