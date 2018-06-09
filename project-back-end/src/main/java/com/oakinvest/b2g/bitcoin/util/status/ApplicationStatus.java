package com.oakinvest.b2g.bitcoin.util.status;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Observable;
import java.util.Observer;

/**
 * Application status DTO.
 */
@Component
@SuppressWarnings("unused")
public class ApplicationStatus extends Observable implements Observer {

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
    private int blockCountInBitcoinCore = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Number of blocks in neo4j (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int blockCountInNeo4j = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Status of the block being processed.
     */
    private CurrentBlockStatus currentBlockStatus = new CurrentBlockStatus();

    /**
     * Last block process duration (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private float lastBlockProcessDuration = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Last error message (if no error, value is set to NON_AVAILABLE_VALUE_STRING).
     */
    private String lastErrorMessage = NON_AVAILABLE_VALUE_STRING;

    /**
     * Configuration.
     */
    @PostConstruct
    public final void configure() {
        currentBlockStatus.addObserver(this);
    }

    @Override
    public final void update(final Observable o, final Object arg) {
        setChanged();
        notifyObservers();
    }

    /**
     * Gets blockCountInBitcoinCore.
     *
     * @return value of blockCountInBitcoinCore
     */
    public final int getBlockCountInBitcoinCore() {
        return blockCountInBitcoinCore;
    }

    /**
     * Sets blockCountInBitcoinCore.
     *
     * @param newBlocksCountInBitcoinCore blockCountInBitcoinCore
     */
    public final void setBlockCountInBitcoinCore(final int newBlocksCountInBitcoinCore) {
        blockCountInBitcoinCore = newBlocksCountInBitcoinCore;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets blockCountInNeo4j.
     *
     * @return value of blockCountInNeo4j
     */
    public final int getBlockCountInNeo4j() {
        return blockCountInNeo4j;
    }

    /**
     * Sets blockCountInNeo4j.
     *
     * @param newBlocksCountInNeo4j blockCountInNeo4j
     */
    public final void setBlockCountInNeo4j(final int newBlocksCountInNeo4j) {
        blockCountInNeo4j = newBlocksCountInNeo4j;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets currentBlockStatus.
     *
     * @return value of currentBlockStatus
     */
    public final CurrentBlockStatus getCurrentBlockStatus() {
        return currentBlockStatus;
    }

    /**
     * Sets currentBlockStatus.
     *
     * @param newCurrentBlockStatus currentBlockStatus
     */
    public final void setCurrentBlockStatus(final CurrentBlockStatus newCurrentBlockStatus) {
        currentBlockStatus = newCurrentBlockStatus;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets lastBlockProcessDuration.
     *
     * @return value of lastBlockProcessDuration
     */
    public final float getLastBlockProcessDuration() {
        return lastBlockProcessDuration;
    }

    /**
     * Sets lastBlockProcessDuration.
     *
     * @param newAverageBlockProcessDuration lastBlockProcessDuration
     */
    public final void setLastBlockProcessDuration(final float newAverageBlockProcessDuration) {
        lastBlockProcessDuration = newAverageBlockProcessDuration;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets lastErrorMessage.
     *
     * @return value of lastErrorMessage
     */
    public final String getLastErrorMessage() {
        return lastErrorMessage;
    }

    /**
     * Sets lastErrorMessage.
     *
     * @param newLastErrorMessage lastErrorMessage
     */
    public final void setLastErrorMessage(final String newLastErrorMessage) {
        lastErrorMessage = newLastErrorMessage;
        setChanged();
        notifyObservers();
    }

}
