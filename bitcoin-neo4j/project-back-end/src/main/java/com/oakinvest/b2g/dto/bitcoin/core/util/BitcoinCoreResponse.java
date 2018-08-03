package com.oakinvest.b2g.dto.bitcoin.core.util;

import java.io.Serializable;

/**
 * Generic reply from bitcoin.
 *
 * Created by straumat on 30/08/16.
 */
public abstract class BitcoinCoreResponse implements Serializable {

	/**
	 * Error field.
	 */
	private BitcoinCoreResponseError error;

	/**
	 * Getter of error.
	 *
	 * @return error
	 */
	public final BitcoinCoreResponseError getError() {
		return error;
	}

	/**
	 * Setter of error.
	 *
	 * @param newError the error to set
	 */
	public final void setError(final BitcoinCoreResponseError newError) {
		error = newError;
	}

}
