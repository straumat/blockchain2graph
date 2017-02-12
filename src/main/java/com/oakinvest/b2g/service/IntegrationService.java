package com.oakinvest.b2g.service;

/**
 * Integrates blockchain data into the database.
 * Created by straumat on 04/09/16.
 */
public interface IntegrationService {

	/**
	 * Integrate a bitcoin block into the database.
	 *
	 * @param blockHeight block number
	 */
	void integrateBitcoinBlock(long blockHeight);

}
