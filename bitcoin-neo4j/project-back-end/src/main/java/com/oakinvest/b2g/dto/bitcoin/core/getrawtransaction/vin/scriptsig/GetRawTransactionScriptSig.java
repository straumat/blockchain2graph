package com.oakinvest.b2g.dto.bitcoin.core.getrawtransaction.vin.scriptsig;

import java.io.Serializable;

/**
 * Vin.
 * Created by straumat on 01/09/16.
 */
@SuppressWarnings("unused")
public class GetRawTransactionScriptSig implements Serializable {

	/**
	 * asm.
	 */
	private String asm;

	/**
	 * hex.
	 */
	private String hex;

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
}
