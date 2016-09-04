package com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vout.scriptpubkey;

import java.util.ArrayList;

/**
 * ScriptPubKey.
 * Created by straumat on 01/09/16.
 */
public class GetRawTransactionScriptPubKey {

	/**
	 * asm.
	 */
	private String asm;

	/**
	 * hex.
	 */
	private String hex;

	/**
	 * The required sigs.
	 */
	private long reqSigs;

	/**
	 * The type, eg 'type'.
	 */
	private String type;

	/**
	 * Addresses.
	 */
	private ArrayList<String> addresses;

	/**
	 * Getter de la propriété addresses.
	 *
	 * @return addresses
	 */
	public final ArrayList<String> getAddresses() {
		return addresses;
	}

	/**
	 * Setter de la propriété addresses.
	 *
	 * @param newAddresses the addresses to set
	 */
	public final void setAddresses(final ArrayList<String> newAddresses) {
		addresses = newAddresses;
	}

	/**
	 * Getter de la propriété asm.
	 *
	 * @return asm
	 */
	public final String getAsm() {
		return asm;
	}

	/**
	 * Setter de la propriété asm.
	 *
	 * @param newAsm the asm to set
	 */
	public final void setAsm(final String newAsm) {
		asm = newAsm;
	}

	/**
	 * Getter de la propriété hex.
	 *
	 * @return hex
	 */
	public final String getHex() {
		return hex;
	}

	/**
	 * Setter de la propriété hex.
	 *
	 * @param newHex the hex to set
	 */
	public final void setHex(final String newHex) {
		hex = newHex;
	}

	/**
	 * Getter de la propriété reqSigs.
	 *
	 * @return reqSigs
	 */
	public final long getReqSigs() {
		return reqSigs;
	}

	/**
	 * Setter de la propriété reqSigs.
	 *
	 * @param newReqSigs the reqSigs to set
	 */
	public final void setReqSigs(final long newReqSigs) {
		reqSigs = newReqSigs;
	}

	/**
	 * Getter de la propriété type.
	 *
	 * @return type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * Setter de la propriété type.
	 *
	 * @param newPubkeyhash the type to set
	 */
	public final void setType(final String newPubkeyhash) {
		type = newPubkeyhash;
	}
}
