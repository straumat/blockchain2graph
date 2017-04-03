package com.oakinvest.b2g.batch.bitcoin;

/**
 * Result of a thread execution.
 * Created by straumat on 02/04/17.
 */
public class BitcoinBatchTransactionsThreadResult {

	/**
	 * State.
	 */
	private BitcoinBatchTransactionsThreadState state;

	/**
	 * Message.
	 */
	private String message;

	/**
	 * Constructor.
	 *
	 * @param newState   state
	 * @param newMessage message
	 */
	public BitcoinBatchTransactionsThreadResult(final BitcoinBatchTransactionsThreadState newState, final String newMessage) {
		this.state = newState;
		this.message = newMessage;
	}

	/**
	 * Getter de la propriété state.
	 *
	 * @return state
	 */
	public final BitcoinBatchTransactionsThreadState getState() {
		return state;
	}

	/**
	 * Getter de la propriété message.
	 *
	 * @return message
	 */
	public final String getMessage() {
		return message;
	}

}
