package com.oakinvest.b2g.dto.bitcoin.status;

/**
 * Status of the current block being processed.
 */
class CurrentBlockStatusDTO {

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
    private CurrentBlockStatusProcessStep processStep = CurrentBlockStatusProcessStep.NOTHING_TO_PROCESS;

    /**
     * Number of addresses processed (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int processedAddresses = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Number of addresses in the current block (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int addressesCount = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Number of transactions processed (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int processedTransactions = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Number of transactions in the current block (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int transactionsCount = NON_AVAILABLE_VALUE_NUMBER;

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
    }

}
