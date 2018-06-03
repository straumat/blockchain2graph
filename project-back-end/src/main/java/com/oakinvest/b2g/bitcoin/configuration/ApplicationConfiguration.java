package com.oakinvest.b2g.bitcoin.configuration;

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
     * Delay between two block generation in milliseconds (10 minutes for bitcoin).
     */
    public static final long BLOCK_GENERATION_DELAY = TimeUnit.MINUTES.toMillis(10);

    /**
     * Private configuration.
     */
    private ApplicationConfiguration() {
    }

}
