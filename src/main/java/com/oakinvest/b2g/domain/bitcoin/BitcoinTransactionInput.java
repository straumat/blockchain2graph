package com.oakinvest.b2g.domain.bitcoin;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Property;

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
	 * The transaction id (same as provided).
	 */
	@Property(name = "txid")
	private String txId;

	/**
	 * vOut.
	 */
	@Property(name = "vout")
	private long vOut;

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
	 * Getter de la propriété id.
	 *
	 * @return id
	 */
	public final Long getId() {
		return id;
	}

	/**
	 * Setter de la propriété id.
	 *
	 * @param newId the id to set
	 */
	public final void setId(final Long newId) {
		id = newId;
	}

	/**
	 * Getter de la propriété txId.
	 *
	 * @return txId
	 */
	public final String getTxId() {
		return txId;
	}

	/**
	 * Setter de la propriété txId.
	 *
	 * @param newTxid the txId to set
	 */
	public final void setTxId(final String newTxid) {
		txId = newTxid;
	}

	/**
	 * Getter de la propriété vOut.
	 *
	 * @return vOut
	 */
	public final long getvOut() {
		return vOut;
	}

	/**
	 * Setter de la propriété vOut.
	 *
	 * @param newVout the vOut to set
	 */
	public final void setvOut(final long newVout) {
		vOut = newVout;
	}

	/**
	 * Getter de la propriété scriptSigAsm.
	 *
	 * @return scriptSigAsm
	 */
	public final String getScriptSigAsm() {
		return scriptSigAsm;
	}

	/**
	 * Setter de la propriété scriptSigAsm.
	 *
	 * @param newScriptSigAam the scriptSigAsm to set
	 */
	public final void setScriptSigAsm(final String newScriptSigAam) {
		scriptSigAsm = newScriptSigAam;
	}

	/**
	 * Getter de la propriété scriptSigHex.
	 *
	 * @return scriptSigHex
	 */
	public final String getScriptSigHex() {
		return scriptSigHex;
	}

	/**
	 * Setter de la propriété scriptSigHex.
	 *
	 * @param newScriptSigHex the scriptSigHex to set
	 */
	public final void setScriptSigHex(final String newScriptSigHex) {
		scriptSigHex = newScriptSigHex;
	}

	/**
	 * Getter de la propriété sequence.
	 *
	 * @return sequence
	 */
	public final long getSequence() {
		return sequence;
	}

	/**
	 * Setter de la propriété sequence.
	 *
	 * @param newSequence the sequence to set
	 */
	public final void setSequence(final long newSequence) {
		sequence = newSequence;
	}

}
