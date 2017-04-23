package com.oakinvest.b2g.service;

/**
 * Services displaying information about the status.
 * Created by straumat on 28/10/16.
 */
public interface StatusService {

	/**
	 * Returns the total number of blocks in the blockchain.
	 *
	 * @return total block count
	 */
	long getTotalBlockCount();

	/**
	 * Set the total number of blocks in the blockchain.
	 *
	 * @param newTotalBlockCount new value
	 */
	void setTotalBlockCount(long newTotalBlockCount);

	/**
	 * Returns the number of the last block imported.
	 *
	 * @return block number.
	 */
	long getImportedBlockCount();

	/**
	 * Set the number of the last block imported.
	 *
	 * @param newImportedBlockCount new value
	 */
	void setImportedBlockCount(long newImportedBlockCount);

	/**
	 * Returns the last log message.
	 *
	 * @return last log message.
	 */
	String getLastLog();

	/**
	 * Add a log message.
	 *
	 * @param logMessage log message
	 */
	void addLog(String logMessage);

	/**
	 * Returns the last error message.
	 *
	 * @return last error message.
	 */
	String getLastError();

	/**
	 * Add an error message.
	 *
	 * @param errorMessage error message
	 * @param e               exception raised
	 */
	void addError(String errorMessage, Exception e);


	/**
	 * Set the average block import duration.
	 *
	 * @param averageBlockImportDuration averageBlockImportDuration
	 */
	void setAverageBlockImportDuration(float averageBlockImportDuration);

}
