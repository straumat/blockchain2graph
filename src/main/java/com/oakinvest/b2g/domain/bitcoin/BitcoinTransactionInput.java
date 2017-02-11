package com.oakinvest.b2g.domain.bitcoin;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

/**
 * Bitcoin transaction input.
 * Created by straumat on 22/09/16.
 */
public class BitcoinTransactionInput {

	/**
	 * ID.
	 */
	@GraphId
	private Long id;

	/**
	 * Transaction.
	 */
	@Relationship(type = "IN_TRANSACTION")
	private BitcoinTransaction transaction;

	/**
	 * Transaction output.
	 */
	@Relationship(type = "TRANSACTION_OUTPUT")
	private BitcoinTransactionOutput transactionOutput;

	/**
	 * The transaction id (same as provided).
	 */
	@Property(name = "txid")
	private String txId;

	/**
	 * Coinbase.
	 */
	@Property(name = "coinbase")
	private String coinbase;

	/**
	 * vOut.
	 */
	@Property(name = "vout")
	private int vOut;

	/**
	 * The script asm.
	 */
	@Property(name = "scriptSigAsm")
	private String scriptSigAsm;

	/**
	 * The script hex.
	 */
	@Property(name = "scriptSigHex")
	private String scriptSigHex;

	/**
	 * The script sequence number.
	 */
	@Property(name = "sequence")
	private long sequence;

	/**
	 * Returns input description.
	 *
	 * @return description.
	 */
	public final String getDescription() {
		return getTxId() + "-" + getvOut();
	}

	/**
	 * Getter of transactionOutput.
	 *
	 * @return transactionOutput
	 */
	public final BitcoinTransactionOutput getTransactionOutput() {
		return transactionOutput;
	}

	/**
	 * Setter of transactionOutput.
	 *
	 * @param newTransactionOutput the transactionOutput to set
	 */
	public final void setTransactionOutput(final BitcoinTransactionOutput newTransactionOutput) {
		transactionOutput = newTransactionOutput;
	}

	/**
	 * Getter of id.
	 *
	 * @return id
	 */
	public final Long getId() {
		return id;
	}

	/**
	 * Setter of id.
	 *
	 * @param newId the id to set
	 */
	public final void setId(final Long newId) {
		id = newId;
	}

	/**
	 * Getter of txId.
	 *
	 * @return txId
	 */
	public final String getTxId() {
		return txId;
	}

	/**
	 * Setter of txId.
	 *
	 * @param newTxid the txId to set
	 */
	public final void setTxId(final String newTxid) {
		txId = newTxid;
	}

	/**
	 * Getter of coinbase.
	 *
	 * @return coinbase
	 */
	public final String getCoinbase() {
		return coinbase;
	}

	/**
	 * Setter of coinbase.
	 *
	 * @param newCoinbase the coinbase to set
	 */
	public final void setCoinbase(final String newCoinbase) {
		coinbase = newCoinbase;
	}

	/**
	 * Getter of vOut.
	 *
	 * @return vOut
	 */
	public final int getvOut() {
		return vOut;
	}

	/**
	 * Setter of vOut.
	 *
	 * @param newVout the vOut to set
	 */
	public final void setvOut(final int newVout) {
		vOut = newVout;
	}

	/**
	 * Getter of scriptSigAsm.
	 *
	 * @return scriptSigAsm
	 */
	public final String getScriptSigAsm() {
		return scriptSigAsm;
	}

	/**
	 * Setter of scriptSigAsm.
	 *
	 * @param newScriptSigAam the scriptSigAsm to set
	 */
	public final void setScriptSigAsm(final String newScriptSigAam) {
		scriptSigAsm = newScriptSigAam;
	}

	/**
	 * Getter of scriptSigHex.
	 *
	 * @return scriptSigHex
	 */
	public final String getScriptSigHex() {
		return scriptSigHex;
	}

	/**
	 * Setter of scriptSigHex.
	 *
	 * @param newScriptSigHex the scriptSigHex to set
	 */
	public final void setScriptSigHex(final String newScriptSigHex) {
		scriptSigHex = newScriptSigHex;
	}

	/**
	 * Getter of sequence.
	 *
	 * @return sequence
	 */
	public final long getSequence() {
		return sequence;
	}

	/**
	 * Setter of sequence.
	 *
	 * @param newSequence the sequence to set
	 */
	public final void setSequence(final long newSequence) {
		sequence = newSequence;
	}

	/**
	 * Getter of transaction.
	 *
	 * @return transaction
	 */
	public final BitcoinTransaction getTransaction() {
		return transaction;
	}

	/**
	 * Setter of transaction.
	 *
	 * @param newTransaction the transaction to set
	 */
	public final void setTransaction(final BitcoinTransaction newTransaction) {
		transaction = newTransaction;
	}
}
