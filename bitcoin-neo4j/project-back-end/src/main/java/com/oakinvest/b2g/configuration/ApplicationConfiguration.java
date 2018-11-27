package com.oakinvest.b2g.configuration;

import java.util.concurrent.TimeUnit;

/**
 * Parameters configuration.
 * <p>
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
     * Delay before trying to get a new block when no new block is available from bitcoin core.
     */
    public static final long PAUSE_BEFORE_SEARCHING_FOR_NEW_BLOCK = TimeUnit.MINUTES.toMillis(1);

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
