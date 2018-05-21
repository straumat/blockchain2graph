package com.oakinvest.b2g.dto.bitcoin.status;

import java.util.Observable;

import static com.oakinvest.b2g.dto.bitcoin.status.CurrentBlockStatusProcessStep.NEW_BLOCK_TO_PROCESS;
import static com.oakinvest.b2g.dto.bitcoin.status.CurrentBlockStatusProcessStep.NO_BLOCK_TO_PROCESS;

/**
 * Status of the current block being processed.
 */
@SuppressWarnings("unused")
public class CurrentBlockStatus extends Observable {

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
    private CurrentBlockStatusProcessStep processStep = CurrentBlockStatusProcessStep.NO_BLOCK_TO_PROCESS;

    /**
     * Number of transactions in the current block (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int transactionsCount = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Number of addresses in the current block (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int addressesCount = NON_AVAILABLE_VALUE_NUMBER;

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
    public final int getBlockHeight() {
        return blockHeight;
    }

    /**
     * Sets blockHeight.
     *
     * @param newBlockHeight blockHeight
     */
    public final void setBlockHeight(final int newBlockHeight) {
        blockHeight = newBlockHeight;
        processStep = NEW_BLOCK_TO_PROCESS;
        transactionsCount = NON_AVAILABLE_VALUE_NUMBER;
        addressesCount = NON_AVAILABLE_VALUE_NUMBER;
        loadedTransactions = NON_AVAILABLE_VALUE_NUMBER;
        processedAddresses = NON_AVAILABLE_VALUE_NUMBER;
        processedTransactions = NON_AVAILABLE_VALUE_NUMBER;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets processStep.
     *
     * @return value of processStep
     */
    public final CurrentBlockStatusProcessStep getProcessStep() {
        return processStep;
    }

    /**
     * Sets processStep.
     *
     * @param newProcessStep processStep
     */
    public final void setProcessStep(final CurrentBlockStatusProcessStep newProcessStep) {
        processStep = newProcessStep;
        // If there there is nothing to process, we change the other values to non avaliable.
        if (newProcessStep.equals(NO_BLOCK_TO_PROCESS)) {
            blockHeight = NON_AVAILABLE_VALUE_NUMBER;
            transactionsCount = NON_AVAILABLE_VALUE_NUMBER;
            addressesCount = NON_AVAILABLE_VALUE_NUMBER;
            loadedTransactions = NON_AVAILABLE_VALUE_NUMBER;
            processedAddresses = NON_AVAILABLE_VALUE_NUMBER;
            processedTransactions = NON_AVAILABLE_VALUE_NUMBER;
        }
        if (newProcessStep.equals(NEW_BLOCK_TO_PROCESS)) {
            transactionsCount = 0;
            addressesCount = 0;
            loadedTransactions = 0;
            processedAddresses = 0;
            processedTransactions = 0;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Gets loadedTransactions.
     *
     * @return value of loadedTransactions
     */
    public final int getLoadedTransactions() {
        return loadedTransactions;
    }

    /**
     * Sets loadedTransactions.
     *
     * @param newLoadedTransactions loadedTransactions
     */
    public final void setLoadedTransactions(final int newLoadedTransactions) {
        loadedTransactions = newLoadedTransactions;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets processedAddresses.
     *
     * @return value of processedAddresses
     */
    public final int getProcessedAddresses() {
        return processedAddresses;
    }

    /**
     * Sets processedAddresses.
     *
     * @param newProcessedAddresses processedAddresses
     */
    public final void setProcessedAddresses(final int newProcessedAddresses) {
        processedAddresses = newProcessedAddresses;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets addressesCount.
     *
     * @return value of addressesCount
     */
    public final int getAddressesCount() {
        return addressesCount;
    }

    /**
     * Sets addressesCount.
     *
     * @param newAddressesCount addressesCount
     */
    public final void setAddressesCount(final int newAddressesCount) {
        addressesCount = newAddressesCount;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets processedTransactions.
     *
     * @return value of processedTransactions
     */
    public final int getProcessedTransactions() {
        return processedTransactions;
    }

    /**
     * Sets processedTransactions.
     *
     * @param newProcessedTransactions processedTransactions
     */
    public final void setProcessedTransactions(final int newProcessedTransactions) {
        processedTransactions = newProcessedTransactions;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets transactionsCount.
     *
     * @return value of transactionsCount
     */
    public final int getTransactionsCount() {
        return transactionsCount;
    }

    /**
     * Sets transactionsCount.
     *
     * @param newTransactionsCount transactionsCount
     */
    public final void setTransactionsCount(final int newTransactionsCount) {
        transactionsCount = newTransactionsCount;
        setChanged();
        notifyObservers();
    }

}
