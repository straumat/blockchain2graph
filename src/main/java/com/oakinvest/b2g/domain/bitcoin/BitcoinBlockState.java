package com.oakinvest.b2g.domain.bitcoin;

/**
 * Different state of a bitcoin block.
 * Created by straumat on 19/03/17.
 */
public enum BitcoinBlockState {

	/**
	 * State 1 - Block data just created.
	 */
    BLOCK_DATA_IMPORTED,

    /**
     * State 2 - block verified.
     */
    BLOCK_DATA_VERIFIED,

    /**
	 * Final State - Imported.
	 */
    BLOCK_FULLY_IMPORTED

}
