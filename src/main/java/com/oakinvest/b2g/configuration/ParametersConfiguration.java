package com.oakinvest.b2g.configuration;

/**
 * Parameters configuration.
 * Created by straumat on 20/05/17.
 */
public final class ParametersConfiguration {

    /**
     * Private configuration.
     */
    private ParametersConfiguration() { }

    /**
     * Bitcoind buffer size.
     */
    public static final int BITCOIND_BUFFER_SIZE = 100;

    /**
     * Delay between each block generation (10 minutes).
     */
    public static final int BITCOIN_BLOCK_GENERATION_DELAY = 10;

}
