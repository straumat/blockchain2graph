package com.oakinvest.b2g.util.status;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Application status DTO.
 */
@SuppressWarnings("unused")
@Component
@JsonSerialize(as = ApplicationStatus.class)
public class ApplicationStatus {

    /**
     * Non available value.
     */
    private static final String NON_AVAILABLE_VALUE_STRING = "n/a";

    /**
     * Non available value.
     */
    private static final int NON_AVAILABLE_VALUE_NUMBER = -1;

    /**
     * Number of blocks in Bitcoin core (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int blockCountInBlockchain = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Number of blocks in neo4j (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int blockCountInNeo4j = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Status of the block being processed.
     */
    @Autowired
    private CurrentBlockStatus currentBlockStatus;

    /**
     * Last block process duration (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private float lastBlockProcessDuration = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Last error message (if no error, value is set to NON_AVAILABLE_VALUE_STRING).
     */
    private String lastErrorMessage = NON_AVAILABLE_VALUE_STRING;

    /**
     * Gets blockCountInBlockchain.
     *
     * @return value of blockCountInBlockchain
     */
    public int getBlockCountInBlockchain() {
        return blockCountInBlockchain;
    }

    /**
     * Sets blockCountInBlockchain.
     *
     * @param newBlocksCountInBitcoinCore blockCountInBlockchain
     */
    public void setBlockCountInBlockchain(final int newBlocksCountInBitcoinCore) {
        blockCountInBlockchain = newBlocksCountInBitcoinCore;
    }

    /**
     * Gets blockCountInNeo4j.
     *
     * @return value of blockCountInNeo4j
     */
    public int getBlockCountInNeo4j() {
        return blockCountInNeo4j;
    }

    /**
     * Sets blockCountInNeo4j.
     *
     * @param newBlocksCountInNeo4j blockCountInNeo4j
     */
    public void setBlockCountInNeo4j(final int newBlocksCountInNeo4j) {
        blockCountInNeo4j = newBlocksCountInNeo4j;
    }

    /**
     * Gets currentBlockStatus.
     *
     * @return value of currentBlockStatus
     */
    public CurrentBlockStatus getCurrentBlockStatus() {
        return currentBlockStatus;
    }

    /**
     * Sets currentBlockStatus.
     *
     * @param newCurrentBlockStatus currentBlockStatus
     */
    public void setCurrentBlockStatus(final CurrentBlockStatus newCurrentBlockStatus) {
        currentBlockStatus = newCurrentBlockStatus;
    }

    /**
     * Gets lastBlockProcessDuration.
     *
     * @return value of lastBlockProcessDuration
     */
    public float getLastBlockProcessDuration() {
        return lastBlockProcessDuration;
    }

    /**
     * Sets lastBlockProcessDuration.
     *
     * @param newAverageBlockProcessDuration lastBlockProcessDuration
     */
    public void setLastBlockProcessDuration(final float newAverageBlockProcessDuration) {
        lastBlockProcessDuration = newAverageBlockProcessDuration;
    }

    /**
     * Gets lastErrorMessage.
     *
     * @return value of lastErrorMessage
     */
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    /**
     * Sets lastErrorMessage.
     *
     * @param newLastErrorMessage lastErrorMessage
     */
    public void setLastErrorMessage(final String newLastErrorMessage) {
        lastErrorMessage = newLastErrorMessage;
    }

}
