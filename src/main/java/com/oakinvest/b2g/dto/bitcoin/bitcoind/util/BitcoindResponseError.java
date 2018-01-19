package com.oakinvest.b2g.dto.bitcoin.bitcoind.util;

import java.io.Serializable;

/**
 * Error in bitcoin response.
 * Created by straumat on 01/09/16.
 */
@SuppressWarnings("unused")
public class BitcoindResponseError implements Serializable {

	/**
	 * Error code.
	 */
	private int code;

	/**
	 * Error message.
	 */
	private String message;

	/**
	 * Getter of code.
	 *
	 * @return code
	 */
	public final int getCode() {
		return code;
	}

	/**
	 * Setter of code.
	 *
	 * @param newCode the code to set
	 */
	public final void setCode(final int newCode) {
		code = newCode;
	}

	/**
	 * Getter of message.
	 *
	 * @return message
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * Setter of message.
	 *
	 * @param newMessage the message to set
	 */
	public final void setMessage(final String newMessage) {
		message = newMessage;
	}

	/**
	 * returns the response.
	 *
	 * @return message
	 */
	@Override
	public final String toString() {
		return "BitcoindResponseError{" + "code=" + code + ", message='" + message + '\'' + '}';
	}

}
