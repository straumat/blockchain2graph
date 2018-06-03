package com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getrawtransaction.vout.scriptpubkey;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ScriptPubKey.
 * Created by straumat on 01/09/16.
 */
@SuppressWarnings("unused")
public class GetRawTransactionScriptPubKey implements Serializable {

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
	private int reqSigs;

	/**
	 * The type, eg 'type'.
	 */
	private String type;

	/**
	 * Addresses.
	 */
	private ArrayList<String> addresses = new ArrayList<>();

	/**
	 * Getter of addresses.
	 *
	 * @return addresses
	 */
	public final ArrayList<String> getAddresses() {
		return addresses;
	}

	/**
	 * Setter of addresses.
	 *
	 * @param newAddresses the addresses to set
	 */
	public final void setAddresses(final ArrayList<String> newAddresses) {
		addresses = newAddresses;
	}

	/**
	 * Getter of asm.
	 *
	 * @return asm
	 */
	public final String getAsm() {
		return asm;
	}

	/**
	 * Setter of asm.
	 *
	 * @param newAsm the asm to set
	 */
	public final void setAsm(final String newAsm) {
		asm = newAsm;
	}

	/**
	 * Getter of hex.
	 *
	 * @return hex
	 */
	public final String getHex() {
		return hex;
	}

	/**
	 * Setter of hex.
	 *
	 * @param newHex the hex to set
	 */
	public final void setHex(final String newHex) {
		hex = newHex;
	}

	/**
	 * Getter of reqSigs.
	 *
	 * @return reqSigs
	 */
	public final int getReqSigs() {
		return reqSigs;
	}

	/**
	 * Setter of reqSigs.
	 *
	 * @param newReqSigs the reqSigs to set
	 */
	public final void setReqSigs(final int newReqSigs) {
		reqSigs = newReqSigs;
	}

	/**
	 * Getter of type.
	 *
	 * @return type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * Setter of type.
	 *
	 * @param newPubkeyhash the type to set
	 */
	public final void setType(final String newPubkeyhash) {
		type = newPubkeyhash;
	}
}
