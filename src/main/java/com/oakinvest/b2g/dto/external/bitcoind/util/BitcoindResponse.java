package com.oakinvest.b2g.dto.external.bitcoind.util;

/**
 * Generic reply from bitcoind.
 * Created by straumat on 30/08/16.
 */
public abstract class BitcoindResponse {

	/**
	 * Error field.
	 */
	private String error;

	/**
	 * Getter de la propriété error.
	 *
	 * @return error
	 */
	public final String getError() {
		return error;
	}

	/**
	 * Setter de la propriété error.
	 *
	 * @param newError the error to set
	 */
	public final void setError(final String newError) {
		error = newError;
	}

}
