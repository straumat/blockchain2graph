package com.oakinvest.b2g.domain.bitcoin;

/**
 * Different state of a bitcoin block.
 * Created by straumat on 19/03/17.
 */
public enum BitcoinBlockState {

	/**
	 * State 1 - Block just created.
	 */
	BLOCK_IMPORTED,

	/**
	 * State 2 - Addresses imported.
	 */
	ADDRESSES_IMPORTED,

	/**
	 * State 3 - Transactions imported.
	 */
	TRANSACTIONS_IMPORTED,

	/**
	 * State 4 - Relations imported.
	 */
	RELATIONS_IMPORTED,

	/**
	 * Final State - Imported.
	 */
	IMPORTED

}
