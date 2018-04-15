package com.oakinvest.b2g.util.bitcoin.exception;

/**
 * Exception : Origin transaction not found.
 */
public class OriginTransactionNotFoundException extends RuntimeException {

    /**
     * Default constructor.
     *
     * @param message error message
     */
    public OriginTransactionNotFoundException(final String message) {
        super(message);
    }

}
