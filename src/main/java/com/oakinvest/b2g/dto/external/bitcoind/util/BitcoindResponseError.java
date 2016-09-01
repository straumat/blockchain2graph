package com.oakinvest.b2g.dto.external.bitcoind.util;

/**
 * Error in bitcoind response.
 * Created by straumat on 01/09/16.
 */
public class BitcoindResponseError {

	/**
	 * Error code.
	 */
	private int code;

	/**
	 * Error message.
	 */
	private String message;

	/**
	 * Getter de la propriété code.
	 *
	 * @return code
	 */
	public final int getCode() {
		return code;
	}

	/**
	 * Setter de la propriété code.
	 *
	 * @param newCode the code to set
	 */
	public final void setCode(final int newCode) {
		code = newCode;
	}

	/**
	 * Getter de la propriété message.
	 *
	 * @return message
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * Setter de la propriété message.
	 *
	 * @param newMessage the message to set
	 */
	public final void setMessage(final String newMessage) {
		message = newMessage;
	}
}
