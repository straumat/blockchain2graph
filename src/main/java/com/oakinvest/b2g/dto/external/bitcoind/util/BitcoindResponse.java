package com.oakinvest.b2g.dto.external.bitcoind.util;

import java.io.Serializable;

/**
 * Generic reply from bitcoind.
 * Created by straumat on 30/08/16.
 */
public abstract class BitcoindResponse implements Serializable {

	/**
	 * Error field.
	 */
	private BitcoindResponseError error;

	/**
	 * Getter of error.
	 *
	 * @return error
	 */
	public final BitcoindResponseError getError() {
		return error;
	}

	/**
	 * Setter of error.
	 *
	 * @param newError the error to set
	 */
	public final void setError(final BitcoindResponseError newError) {
		error = newError;
	}

}
