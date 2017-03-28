package com.oakinvest.b2g.service.bitcoin;

/**
 * Bitcoin statistic service.
 * Created by straumat on 25/03/17.
 */
public interface BitcoinStatisticService {

	/**
	 * Add an execution time statistics and return the execution mean.
	 *
	 * @param newDuration new execution time.
	 * @return mean time
	 */
	float addBlockImportDuration(float newDuration);

	/**
	 * Return execution time mean.
	 *
	 * @return execution time mean
	 */
	float getAverageBlockImportDuration();

}
