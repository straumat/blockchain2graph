package com.oakinvest.b2g.dto.bitcoin.status;

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
    private int blocksCountInBitcoinCore = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Number of blocks in neo4j (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private int blocksCountInNeo4j = NON_AVAILABLE_VALUE_NUMBER;

    /**
     * Status of the block being processed.
     */
    private CurrentBlockStatus currentBlockStatus = new CurrentBlockStatus();

    /**
     * Average block process duration (NON_AVAILABLE_VALUE_NUMBER means no value has been set yet).
     */
    private float averageBlockProcessDuration = NON_AVAILABLE_VALUE_NUMBER;

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
     * Gets blocksCountInBitcoinCore.
     *
     * @return value of blocksCountInBitcoinCore
     */
    public final int getBlocksCountInBitcoinCore() {
        return blocksCountInBitcoinCore;
    }

    /**
     * Sets blocksCountInBitcoinCore.
     *
     * @param newBlocksCountInBitcoinCore blocksCountInBitcoinCore
     */
    public final void setBlocksCountInBitcoinCore(final int newBlocksCountInBitcoinCore) {
        blocksCountInBitcoinCore = newBlocksCountInBitcoinCore;
        setChanged();
        notifyObservers();
    }

    /**
     * Gets blocksCountInNeo4j.
     *
     * @return value of blocksCountInNeo4j
     */
    public final int getBlocksCountInNeo4j() {
        return blocksCountInNeo4j;
    }

    /**
     * Sets blocksCountInNeo4j.
     *
     * @param newBlocksCountInNeo4j blocksCountInNeo4j
     */
    public final void setBlocksCountInNeo4j(final int newBlocksCountInNeo4j) {
        blocksCountInNeo4j = newBlocksCountInNeo4j;
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
     * Gets averageBlockProcessDuration.
     *
     * @return value of averageBlockProcessDuration
     */
    public final float getAverageBlockProcessDuration() {
        return averageBlockProcessDuration;
    }

    /**
     * Sets averageBlockProcessDuration.
     *
     * @param newAverageBlockProcessDuration averageBlockProcessDuration
     */
    public final void setAverageBlockProcessDuration(final float newAverageBlockProcessDuration) {
        averageBlockProcessDuration = newAverageBlockProcessDuration;
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
