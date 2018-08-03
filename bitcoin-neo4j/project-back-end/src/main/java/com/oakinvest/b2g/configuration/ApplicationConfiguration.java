package com.oakinvest.b2g.configuration;

import java.util.concurrent.TimeUnit;

/**
 * Parameters configuration.
 *
 * Created by straumat on 20/05/17.
 */
public final class ApplicationConfiguration {

    /**
     * Log separator.
     */
    public static final String LOG_SEPARATOR = "===================================";

    /**
     * Pause before starting the import batch (2 minutes) - to let time for Bitcoin core docker to start.
     */
    public static final long PAUSE_BEFORE_STARTING_APPLICATION = 120000;

    /**
     * Number of blocks before the block count cache activate.
     */
    public static final int BLOCK_COUNT_CACHE_ACTIVATION_HEIGHT = 100000;

    /**
     * Delay between two block generation in milliseconds (10 minutes for bitcoin).
     */
    public static final long BLOCK_GENERATION_DELAY = TimeUnit.MINUTES.toMillis(10);

    /**
     * Private configuration.
     */
    private ApplicationConfiguration() {
    }

}
