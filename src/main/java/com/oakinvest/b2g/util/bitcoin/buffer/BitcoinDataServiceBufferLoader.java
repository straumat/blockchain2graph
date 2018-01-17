package com.oakinvest.b2g.util.bitcoin.buffer;

import com.oakinvest.b2g.service.bitcoin.BitcoinDataService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Bitcoind buffer loader.
 * Created by straumat on 04/06/17.
 */
@Component
public class BitcoinDataServiceBufferLoader {

    /**
     * Bitcoind service.
     */
    private final BitcoinDataService bitcoinDataService;

    /**
     * Constructor.
     *
     * @param newBitcoinDataService bitcoin data service
     */
    public BitcoinDataServiceBufferLoader(final BitcoinDataService newBitcoinDataService) {
        this.bitcoinDataService = newBitcoinDataService;
    }

    /**
     * Load buffer. We will load the next block with this method.
     *
     * @param requestedBlock id of the block requested.
     */
    @Async
    @SuppressWarnings("checkstyle:designforextension")
    void loadBuffer(final int requestedBlock) {
        bitcoinDataService.putBlockInBuffer(requestedBlock + 1);
        bitcoinDataService.removeBlockInBuffer(requestedBlock - 1);
    }

}
