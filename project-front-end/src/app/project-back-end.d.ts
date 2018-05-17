/* tslint:disable */
// Generated using typescript-generator version 2.2.413 on 2018-05-17 11:35:01.

export interface ApplicationStatus {
    blocksCountInBitcoinCore: number;
    blocksCountInNeo4j: number;
    currentBlockStatus: CurrentBlockStatus;
    averageBlockProcessDuration: number;
    lastErrorMessage: string;
}

export interface CurrentBlockStatus {
    blockHeight: number;
    processStep: CurrentBlockStatusProcessStep;
    processedAddresses: number;
    addressesCount: number;
    processedTransactions: number;
    transactionsCount: number;
}

export type CurrentBlockStatusProcessStep = "NOTHING_TO_PROCESS" | "NEW_BLOCK_TO_PROCESS" | "LOADING_DATA_FROM_BITCOIN_CORE" | "CREATING_ADDRESSES" | "CREATING_TRANSACTIONS" | "SAVING_BLOCK" | "BLOCK_SAVED";
