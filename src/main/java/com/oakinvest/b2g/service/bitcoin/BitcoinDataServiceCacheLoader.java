package com.oakinvest.b2g.service.bitcoin;

import com.oakinvest.b2g.service.BitcoinDataService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Bitcoind cache loader.
 * Created by straumat on 04/06/17.
 */
@Component
public class BitcoinDataServiceCacheLoader {

    /**
     * Bitcoind service.
     */
    private final BitcoinDataService bitcoinDataService;

    /**
     * Constructor.
     *
     * @param newBitcoinDataService bitcoin data service
     */
    public BitcoinDataServiceCacheLoader(final BitcoinDataService newBitcoinDataService) {
        this.bitcoinDataService = newBitcoinDataService;
    }

    /**
     * Load cache. We will load the next block with this method.
     *
     * @param requestedBlock id of the block requested.
     */
    @Async
    @SuppressWarnings("checkstyle:designforextension")
    public void loadCache(final int requestedBlock) {
        bitcoinDataService.putBlockInCache(requestedBlock + 1);
        bitcoinDataService.removeBlockInCache(requestedBlock - 1);
    }

}
