package com.oakinvest.b2g.dto.bitcoin.status;

/**
 * Define in which step we are.
 */
public enum CurrentBlockStatusProcessStep {

    /**
     * No block being processed.
     */
    NOTHING_TO_PROCESS,

    /**
     * Loading data from bitcoin core.
     */
    LOADING_DATA_FROM_BITCOIN_CORE,

    /**
     * Creating addresses.
     */
    CREATING_ADDRESSES,

    /**
     * Creating transactions.
     */
    CREATING_TRANSACTIONS,

    /**
     * Saving block.
     */
    SAVING_BLOCK

}
