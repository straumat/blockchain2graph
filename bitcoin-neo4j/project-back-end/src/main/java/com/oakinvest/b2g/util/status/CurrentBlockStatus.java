package com.oakinvest.b2g.util.status;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.stereotype.Component;

import static com.oakinvest.b2g.util.status.CurrentBlockStatusProcessStep.NEW_BLOCK_TO_PROCESS;
import static com.oakinvest.b2g.util.status.CurrentBlockStatusProcessStep.NO_BLOCK_TO_PROCESS;

/**
 * Status of the current block being processed.
 */
@SuppressWarnings("unused")
@Component
@JsonSerialize(as = CurrentBlockStatus.class)
public class CurrentBlockStatus {

    /**
     * Non available value.
     */
    private static final int NON_AVAILABLE_VALUE_NUMBER = -1;

    /**
     * The block being processed (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int blockHeight = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Step in the process.
     */
    private CurrentBlockStatusProcessStep processStep = NO_BLOCK_TO_PROCESS;

    /**
     * Number of transactions in the current block (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int transactionCount = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Number of addresses in the current block (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int addressCount = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Number of loaded transactions from bitcoin core.
     */
    private int loadedTransactions = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Number of addresses processed (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int processedAddresses = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Number of transactions processed (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int processedTransactions = NON_AVAILABLE_VALUE_NUMBER;


    /**
     * Gets blockHeight.
     *
     * @return value of blockHeight
     */
    public int getBlockHeight() {
        return blockHeight;
    }

    /**
     * Sets blockHeight.
     *
     * @param newBlockHeight blockHeight
     */
    public void setBlockHeight(final int newBlockHeight) {
        blockHeight = newBlockHeight;
        processStep = NEW_BLOCK_TO_PROCESS;
        transactionCount = NON_AVAILABLE_VALUE_NUMBER;
        addressCount = NON_AVAILABLE_VALUE_NUMBER;
        loadedTransactions = NON_AVAILABLE_VALUE_NUMBER;
        processedAddresses = NON_AVAILABLE_VALUE_NUMBER;
        processedTransactions = NON_AVAILABLE_VALUE_NUMBER;
    }

    /**
     * Gets processStep.
     *
     * @return value of processStep
     */
    public CurrentBlockStatusProcessStep getProcessStep() {
        return processStep;
    }

    /**
     * Sets processStep.
     *
     * @param newProcessStep processStep
     */
    public void setProcessStep(final CurrentBlockStatusProcessStep newProcessStep) {
        processStep = newProcessStep;
        // If there there is nothing to process, we change the other values to non available.
        if (newProcessStep.equals(NO_BLOCK_TO_PROCESS)) {
            blockHeight = NON_AVAILABLE_VALUE_NUMBER;
            transactionCount = NON_AVAILABLE_VALUE_NUMBER;
            addressCount = NON_AVAILABLE_VALUE_NUMBER;
            loadedTransactions = NON_AVAILABLE_VALUE_NUMBER;
            processedAddresses = NON_AVAILABLE_VALUE_NUMBER;
            processedTransactions = NON_AVAILABLE_VALUE_NUMBER;
        }
        if (newProcessStep.equals(NEW_BLOCK_TO_PROCESS)) {
            transactionCount = 0;
            addressCount = 0;
            loadedTransactions = 0;
            processedAddresses = 0;
            processedTransactions = 0;
        }
    }

    /**
     * Gets loadedTransactions.
     *
     * @return value of loadedTransactions
     */
    public int getLoadedTransactions() {
        return loadedTransactions;
    }

    /**
     * Sets loadedTransactions.
     *
     * @param newLoadedTransactions loadedTransactions
     */
    public void setLoadedTransactions(final int newLoadedTransactions) {
        loadedTransactions = newLoadedTransactions;
    }

    /**
     * Gets processedAddresses.
     *
     * @return value of processedAddresses
     */
    public int getProcessedAddresses() {
        return processedAddresses;
    }

    /**
     * Sets processedAddresses.
     *
     * @param newProcessedAddresses processedAddresses
     */
    public void setProcessedAddresses(final int newProcessedAddresses) {
        processedAddresses = newProcessedAddresses;
    }

    /**
     * Gets addressCount.
     *
     * @return value of addressCount
     */
    public int getAddressCount() {
        return addressCount;
    }

    /**
     * Sets addressCount.
     *
     * @param newAddressesCount addressCount
     */
    public void setAddressCount(final int newAddressesCount) {
        addressCount = newAddressesCount;
    }

    /**
     * Gets processedTransactions.
     *
     * @return value of processedTransactions
     */
    public int getProcessedTransactions() {
        return processedTransactions;
    }

    /**
     * Sets processedTransactions.
     *
     * @param newProcessedTransactions processedTransactions
     */
    public void setProcessedTransactions(final int newProcessedTransactions) {
        processedTransactions = newProcessedTransactions;
    }

    /**
     * Gets transactionCount.
     *
     * @return value of transactionCount
     */
    public int getTransactionCount() {
        return transactionCount;
    }

    /**
     * Sets transactionCount.
     *
     * @param newTransactionsCount transactionCount
     */
    public void setTransactionCount(final int newTransactionsCount) {
        transactionCount = newTransactionsCount;
    }

}
