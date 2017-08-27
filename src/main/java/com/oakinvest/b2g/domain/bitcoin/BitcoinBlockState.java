package com.oakinvest.b2g.domain.bitcoin;

/**
 * Different state of a bitcoin block.
 * Created by straumat on 19/03/17.
 */
public enum BitcoinBlockState {

	/**
	 * State 1 - Block just created.
	 */
    BLOCK_DATA_IMPORTED,

	/**
	 * Final State - Imported.
	 */
    BLOCK_FULLY_IMPORTED

}
