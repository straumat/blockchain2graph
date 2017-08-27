package com.oakinvest.b2g.service.bitcoin;

import com.oakinvest.b2g.service.BitcoinDataService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.oakinvest.b2g.configuration.ParametersConfiguration.BITCOIND_BUFFER_SIZE;

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
     * @param newBitcoinDataService    bitcoin data service
     */
    public BitcoinDataServiceCacheLoader(final BitcoinDataService newBitcoinDataService) {
        this.bitcoinDataService = newBitcoinDataService;
    }

    /**
     * Load cache. We will load the lastBlockSaved + BUFFER_SIZE with this method.
     * @param lastBlockLoaded id of the last block loaded.
     */
    @Async
    @SuppressWarnings("checkstyle:designforextension")
    public void loadCache(final int lastBlockLoaded) {
        bitcoinDataService.getBlockData(lastBlockLoaded + BITCOIND_BUFFER_SIZE);
    }

}
