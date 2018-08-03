package com.oakinvest.b2g.util.status;

/**
 * Define in which step we are.
 */
public enum CurrentBlockStatusProcessStep {

    /**
     * No block to process.
     */
    NO_BLOCK_TO_PROCESS,

    /**
     * New block.
     */
    NEW_BLOCK_TO_PROCESS,

    /**
     * Loading transactions from blockchain.
     */
    LOADING_TRANSACTIONS_FROM_BLOCKCHAIN,

    /**
     * Creating addresses.
     */
    PROCESSING_ADDRESSES,

    /**
     * Creating transactions.
     */
    PROCESSING_TRANSACTIONS,

    /**
     * Saving block.
     */
    SAVING_BLOCK,

    /**
     * Block saved.
     */
    BLOCK_SAVED

}
