package com.oakinvest.b2g.service;

/**
 * Services displaying information about the status.
 * Created by straumat on 28/10/16.
 */
public interface StatusService {

	/**
	 * Returns the total nubmer of blocks in the blockchain.
	 *
	 * @return total block count
	 */
	long getTotalBlockCount();

	/**
	 * Returns the number of the last block integrated.
	 *
	 * @return block number.
	 */
	long getLastBlockIntegrated();

	/**
	 * Returns the last log message.
	 *
	 * @return last log message.
	 */
	String getLastLogMessage();

	/**
	 * Add a log message.
	 *
	 * @param newLogMessage log message
	 */
	void addLogMessage(String newLogMessage);

	/**
	 * Returns the last error message.
	 *
	 * @return last error message.
	 */
	String getLastErrorMessage();

	/**
	 * Add an error message.
	 *
	 * @param newErrorMessage error message
	 */
	void addErrorMessage(String newErrorMessage);

}
