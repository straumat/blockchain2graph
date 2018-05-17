package com.oakinvest.b2g.bitcoin.test.util.status;

import java.util.Observable;
import java.util.Observer;

/**
 * Used to test the status observable behavior.
 */
public class StatusObserver implements Observer {

    /**
     * True if status has changed.
     */
    private boolean statusChanged = false;

    /**
     * Gets statusChanged.
     *
     * @return value of statusChanged
     */
    public final boolean isStatusChanged() {
        return statusChanged;
    }

    /**
     * Make the observer ready for another test.
     */
    public void reset() {
        statusChanged = false;
    }

    @Override
    public void update(final Observable o, final Object arg) {
        statusChanged = true;
    }

}
