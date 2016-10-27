package com.oakinvest.b2g.domain.bitcoin;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Property;

import java.util.Set;

/**
 * Bitcoin transaction output.
 * Created by straumat on 22/09/16.
 */
public class BitcoinTransactionOutput {

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
	 * The value in BTC.
	 */
	@Property(name = "value")
	private float value;

	/**
	 * index.
	 */
	@Property(name = "n")
	private long n;

	/**
	 * The scriptPubKey asm.
	 */
	@Property(name = "scriptPubKeyAsm")
	private String scriptPubKeyAsm;

	/**
	 * The scriptPubKey hex.
	 */
	@Property(name = "scriptPubKeyHex")
	private String scriptPubKeyHex;

	/**
	 * The required sigs.
	 */
	@Property(name = "scriptPubKeyReqSigs")
	private long scriptPubKeyReqSigs;

	/**
	 * The type, eg 'pubkeyhash'.
	 */
	@Property(name = "type")
	private String scriptPubKeyType;

	/**
	 * Adresses.
	 */
	@Property(name = "addresses")
	private Set<String> addresses;

	/**
	 * Getter de la propriété scriptPubKeyReqSigs.
	 *
	 * @return scriptPubKeyReqSigs
	 */
	public final long getScriptPubKeyReqSigs() {
		return scriptPubKeyReqSigs;
	}

	/**
	 * Setter de la propriété scriptPubKeyReqSigs.
	 *
	 * @param newScriptPubKeyReqSigs the scriptPubKeyReqSigs to set
	 */
	public final void setScriptPubKeyReqSigs(final long newScriptPubKeyReqSigs) {
		scriptPubKeyReqSigs = newScriptPubKeyReqSigs;
	}

	/**
	 * Getter de la propriété scriptPubKeyType.
	 *
	 * @return scriptPubKeyType
	 */
	public final String getScriptPubKeyType() {
		return scriptPubKeyType;
	}

	/**
	 * Setter de la propriété scriptPubKeyType.
	 *
	 * @param newScriptPubKeyType the scriptPubKeyType to set
	 */
	public final void setScriptPubKeyType(final String newScriptPubKeyType) {
		scriptPubKeyType = newScriptPubKeyType;
	}

	/**
	 * Getter de la propriété addresses.
	 *
	 * @return addresses
	 */
	public final Set<String> getAddresses() {
		return addresses;
	}

	/**
	 * Setter de la propriété addresses.
	 *
	 * @param newAddresses the addresses to set
	 */
	public final void setAddresses(final Set<String> newAddresses) {
		addresses = newAddresses;
	}

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
	 * Getter de la propriété value.
	 *
	 * @return value
	 */
	public final float getValue() {
		return value;
	}

	/**
	 * Setter de la propriété value.
	 *
	 * @param newValue the value to set
	 */
	public final void setValue(final float newValue) {
		value = newValue;
	}

	/**
	 * Getter de la propriété n.
	 *
	 * @return n
	 */
	public final long getN() {
		return n;
	}

	/**
	 * Setter de la propriété n.
	 *
	 * @param newN the n to set
	 */
	public final void setN(final long newN) {
		n = newN;
	}

	/**
	 * Getter de la propriété scriptPubKeyAsm.
	 *
	 * @return scriptPubKeyAsm
	 */
	public final String getScriptPubKeyAsm() {
		return scriptPubKeyAsm;
	}

	/**
	 * Setter de la propriété scriptPubKeyAsm.
	 *
	 * @param newScriptPubKeyAsm the scriptPubKeyAsm to set
	 */
	public final void setScriptPubKeyAsm(final String newScriptPubKeyAsm) {
		scriptPubKeyAsm = newScriptPubKeyAsm;
	}

	/**
	 * Getter de la propriété scriptPubKeyHex.
	 *
	 * @return scriptPubKeyHex
	 */
	public final String getScriptPubKeyHex() {
		return scriptPubKeyHex;
	}

	/**
	 * Setter de la propriété scriptPubKeyHex.
	 *
	 * @param newScriptPubKeyHex the scriptPubKeyHex to set
	 */
	public final void setScriptPubKeyHex(final String newScriptPubKeyHex) {
		scriptPubKeyHex = newScriptPubKeyHex;
	}
}
