/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 2.19.577 on 2020-02-14 13:23:41.

export interface ApplicationStatus {
    blockCountInBlockchain: number;
    blockCountInNeo4j: number;
    currentBlockStatus: CurrentBlockStatus;
    lastBlockProcessDuration: number;
    lastErrorMessage: string;
}

export interface CurrentBlockStatus {
    blockHeight: number;
    processStep: CurrentBlockStatusProcessStep;
    transactionCount: number;
    addressCount: number;
    loadedTransactions: number;
    processedAddresses: number;
    processedTransactions: number;
}

export const enum CurrentBlockStatusProcessStep {
    NO_BLOCK_TO_PROCESS = "NO_BLOCK_TO_PROCESS",
    NEW_BLOCK_TO_PROCESS = "NEW_BLOCK_TO_PROCESS",
    LOADING_TRANSACTIONS_FROM_BLOCKCHAIN = "LOADING_TRANSACTIONS_FROM_BLOCKCHAIN",
    PROCESSING_ADDRESSES = "PROCESSING_ADDRESSES",
    PROCESSING_TRANSACTIONS = "PROCESSING_TRANSACTIONS",
    SAVING_BLOCK = "SAVING_BLOCK",
    BLOCK_SAVED = "BLOCK_SAVED",
}