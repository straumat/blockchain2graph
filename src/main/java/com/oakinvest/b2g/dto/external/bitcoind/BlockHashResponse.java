package com.oakinvest.b2g.dto.external.bitcoind;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Blockhash response.
 * Created by straumat on 26/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockHashResponse {

	/**
	 * Result field.
	 */
	private String result;

	/**
	 * Error field.
	 */
	private String error;

	/**
	 * Id field.
	 */
	private String id;

	/**
	 * Getter de la propriété result.
	 *
	 * @return result
	 */
	public final String getResult() {
		return result;
	}

	/**
	 * Setter de la propriété result.
	 *
	 * @param newResult the result to set
	 */
	public final void setResult(final String newResult) {
		result = newResult;
	}

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

	/**
	 * Getter de la propriété id.
	 *
	 * @return id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * Setter de la propriété id.
	 *
	 * @param newId the id to set
	 */
	public final void setId(final String newId) {
		id = newId;
	}
}
