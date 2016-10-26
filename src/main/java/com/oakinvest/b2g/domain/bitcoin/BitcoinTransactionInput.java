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
	 * The transaction id (same as provided)
	 */
	@Property(name = "txid")
	private String txid;

	/**
	 * vout.
	 */
	@Property(name = "vout")
	private long vout;

	/**
	 * The script asm.
	 */
	@Property(name = "scriptSig_asm")
	private String scriptSigAsm;

	/**
	 * The script hex.
	 */
	@Property(name = "scriptSig_hex")
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
	 * Getter de la propriété txid.
	 *
	 * @return txid
	 */
	public final String getTxid() {
		return txid;
	}

	/**
	 * Setter de la propriété txid.
	 *
	 * @param newTxid the txid to set
	 */
	public final void setTxid(final String newTxid) {
		txid = newTxid;
	}

	/**
	 * Getter de la propriété vout.
	 *
	 * @return vout
	 */
	public final long getVout() {
		return vout;
	}

	/**
	 * Setter de la propriété vout.
	 *
	 * @param newVout the vout to set
	 */
	public final void setVout(final long newVout) {
		vout = newVout;
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
