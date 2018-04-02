package com.oakinvest.b2g.service;

/**
 * Statistic service.
 * Created by straumat on 25/03/17.
 */
public interface StatisticService {

    /**
     * Add an execution time statistics and return the average execution time.
     *
     * @param newDuration new duration in milliseconds.
     * @return average time in seconds
     */
    float addBlockImportDuration(float newDuration);

    /**
     * Return average execution time.
     *
     * @return average execution
     */
    float getAverageBlockImportDuration();

    /**
     * Reset all statistics.
     */
    void reset();

}
