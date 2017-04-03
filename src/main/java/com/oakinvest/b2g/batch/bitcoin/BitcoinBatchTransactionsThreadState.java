package com.oakinvest.b2g.batch.bitcoin;

/**
 * Transaction thread result state.
 * Created by straumat on 02/04/17.
 */
public enum BitcoinBatchTransactionsThreadState {

	/**
	 * Transaction already exists in database.
	 */
	TRANSACTION_ALREADY_EXISTS,

	/**
	 * Transaction created in the thread.
	 */
	TRANSACTION_CREATED,

	/**
	 * Error : origin transaction not found.
	 */
	ERROR_ORIGIN_TRANSACTION_NOT_FOUND,

	/**
	 * Error : execption in thread.
	 */
	ERROR_EXCEPTION

}
