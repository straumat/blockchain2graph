package com.oakinvest.b2g.service.bitcoin;

import java.util.concurrent.ExecutionException;

/**
 * Integrates blockchain data into the database.
 * Created by straumat on 04/09/16.
 */
public interface BitcoinIntegrationService {

	/**
	 * Load a block in cache.
	 *
	 * @param blockHeight block number
	 */
	void loadBlockInCache(long blockHeight);

	/**
	 * Integrate a bitcoin block into the database.
	 *
	 * @param blockHeight block number
	 * @throws InterruptedException exception
	 * @throws ExecutionException   exception
	 */
	// TODO Are execptions usefull ?
	void integrateBitcoinBlock(long blockHeight) throws InterruptedException, ExecutionException;

}
