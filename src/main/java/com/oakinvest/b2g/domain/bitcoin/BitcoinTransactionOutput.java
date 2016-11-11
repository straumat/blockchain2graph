package com.oakinvest.b2g.domain.bitcoin;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
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
	 * Transaction.
	 */
	@Relationship(type = "IN_TRANSACTION")
	private BitcoinTransaction transaction;

	/**
	 * The value in BTC.
	 */
	@Property(name = "value")
	private float value;

	/**
	 * index.
	 */
	@Property(name = "n")
	private int n;

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
	private Set<String> addresses = new HashSet<String>();

	/**
	 * Adresses.
	 */
	@Relationship(type = "FOR_ADDRESS")
	private Set<BitcoinAddress> bitcoinAddresses = new HashSet<BitcoinAddress>();

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

	/**
	 * Getter of scriptPubKeyReqSigs.
	 *
	 * @return scriptPubKeyReqSigs
	 */
	public final long getScriptPubKeyReqSigs() {
		return scriptPubKeyReqSigs;
	}

	/**
	 * Setter of scriptPubKeyReqSigs.
	 *
	 * @param newScriptPubKeyReqSigs the scriptPubKeyReqSigs to set
	 */
	public final void setScriptPubKeyReqSigs(final long newScriptPubKeyReqSigs) {
		scriptPubKeyReqSigs = newScriptPubKeyReqSigs;
	}

	/**
	 * Getter of scriptPubKeyType.
	 *
	 * @return scriptPubKeyType
	 */
	public final String getScriptPubKeyType() {
		return scriptPubKeyType;
	}

	/**
	 * Setter of scriptPubKeyType.
	 *
	 * @param newScriptPubKeyType the scriptPubKeyType to set
	 */
	public final void setScriptPubKeyType(final String newScriptPubKeyType) {
		scriptPubKeyType = newScriptPubKeyType;
	}

	/**
	 * Getter of addresses.
	 *
	 * @return addresses
	 */
	public final Set<String> getAddresses() {
		return addresses;
	}

	/**
	 * Setter of addresses.
	 *
	 * @param newAddresses the addresses to set
	 */
	public final void setAddresses(final Set<String> newAddresses) {
		addresses = newAddresses;
	}

	/**
	 * Getter of bitcoinAddresses.
	 *
	 * @return bitcoinAddresses
	 */
	public final Set<BitcoinAddress> getBitcoinAddresses() {
		return bitcoinAddresses;
	}

	/**
	 * Setter of bitcoinAddresses.
	 *
	 * @param newBitcoinAddresses the bitcoinAddresses to set
	 */
	public final void setBitcoinAddresses(final Set<BitcoinAddress> newBitcoinAddresses) {
		bitcoinAddresses = newBitcoinAddresses;
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
	 * Getter of value.
	 *
	 * @return value
	 */
	public final float getValue() {
		return value;
	}

	/**
	 * Setter of value.
	 *
	 * @param newValue the value to set
	 */
	public final void setValue(final float newValue) {
		value = newValue;
	}

	/**
	 * Getter of n.
	 *
	 * @return n
	 */
	public final int getN() {
		return n;
	}

	/**
	 * Setter of n.
	 *
	 * @param newN the n to set
	 */
	public final void setN(final int newN) {
		n = newN;
	}

	/**
	 * Getter of scriptPubKeyAsm.
	 *
	 * @return scriptPubKeyAsm
	 */
	public final String getScriptPubKeyAsm() {
		return scriptPubKeyAsm;
	}

	/**
	 * Setter of scriptPubKeyAsm.
	 *
	 * @param newScriptPubKeyAsm the scriptPubKeyAsm to set
	 */
	public final void setScriptPubKeyAsm(final String newScriptPubKeyAsm) {
		scriptPubKeyAsm = newScriptPubKeyAsm;
	}

	/**
	 * Getter of scriptPubKeyHex.
	 *
	 * @return scriptPubKeyHex
	 */
	public final String getScriptPubKeyHex() {
		return scriptPubKeyHex;
	}

	/**
	 * Setter of scriptPubKeyHex.
	 *
	 * @param newScriptPubKeyHex the scriptPubKeyHex to set
	 */
	public final void setScriptPubKeyHex(final String newScriptPubKeyHex) {
		scriptPubKeyHex = newScriptPubKeyHex;
	}
}
