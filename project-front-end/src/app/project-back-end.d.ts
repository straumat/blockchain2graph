/* tslint:disable */
// Generated using typescript-generator version 2.2.413 on 2018-05-18 21:35:29.

export interface ApplicationStatus extends Observable, Observer {
  blocksCountInBitcoinCore: number;
  blocksCountInNeo4j: number;
  currentBlockStatus: CurrentBlockStatus;
  averageBlockProcessDuration: number;
  lastErrorMessage: string;
}

export interface CurrentBlockStatus extends Observable {
  blockHeight: number;
  processStep: CurrentBlockStatusProcessStep;
  transactionsCount: number;
  addressesCount: number;
  loadedTransactions: number;
  processedAddresses: number;
  processedTransactions: number;
}

export interface Observable {
}

export interface Observer {
}

export type CurrentBlockStatusProcessStep = "NOTHING_TO_PROCESS" | "NEW_BLOCK_TO_PROCESS" | "LOADING_DATA_FROM_BITCOIN_CORE" | "CREATING_ADDRESSES" | "CREATING_TRANSACTIONS" | "SAVING_BLOCK" | "BLOCK_SAVED";
