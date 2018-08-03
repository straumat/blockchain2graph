package com.oakinvest.b2g.util.providers;

import com.oakinvest.b2g.service.BitcoinCoreService;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.util.buffer.BitcoinDataServiceBufferLoader;
import org.springframework.stereotype.Component;

/**
 * Services provider.
 *
 * Created by straumat on 01/06/18.
 */
@SuppressWarnings("unused")
@Component
public class ServicesProvider {

    /**
     * Bitcoin core service.
     */
    private final BitcoinCoreService bitcoinCoreService;

    /**
     * Bitcoin data service.
     */
    private final BitcoinDataService bitcoinDataService;

    /**
     * Bitcoin data service buffer loader.
     */
    private final BitcoinDataServiceBufferLoader bitcoinDataServiceBufferLoader;

    /**
     * Constructor.
     * @param newBitcoinCoreService bitcoin core service
     * @param newBitcoinDataService bitcoin data service
     * @param newBitcoinDataServiceBufferLoader bitcoin data service buffer
     */
    public ServicesProvider(final BitcoinCoreService newBitcoinCoreService, final BitcoinDataService newBitcoinDataService, final BitcoinDataServiceBufferLoader newBitcoinDataServiceBufferLoader) {
        this.bitcoinCoreService = newBitcoinCoreService;
        this.bitcoinDataService = newBitcoinDataService;
        this.bitcoinDataServiceBufferLoader = newBitcoinDataServiceBufferLoader;
    }

    /**
     * Get bitcoin core service.
     * @return bitcoin core service.
     */
    public final BitcoinCoreService getBitcoinCoreService() {
        return bitcoinCoreService;
    }

    /**
     * Get bitcoin data service.
     * @return bitcoin data service
     */
    public final BitcoinDataService getBitcoinDataService() {
        return bitcoinDataService;
    }

    /**
     * Get bitcoin data service buffer loader.
     * @return bitcoin data service buffer loader
     */
    public final BitcoinDataServiceBufferLoader getBitcoinDataServiceBufferLoader() {
        return bitcoinDataServiceBufferLoader;
    }

}
