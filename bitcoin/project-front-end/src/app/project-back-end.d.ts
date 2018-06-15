/* tslint:disable */
// Generated using typescript-generator version 2.4.418 on 2018-06-16 00:00:46.

export interface ApplicationStatus {
    blockCountInBitcoinCore: number;
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
    LOADING_TRANSACTIONS_FROM_BITCOIN_CORE = "LOADING_TRANSACTIONS_FROM_BITCOIN_CORE",
    PROCESSING_ADDRESSES = "PROCESSING_ADDRESSES",
    PROCESSING_TRANSACTIONS = "PROCESSING_TRANSACTIONS",
    SAVING_BLOCK = "SAVING_BLOCK",
    BLOCK_SAVED = "BLOCK_SAVED",
}
