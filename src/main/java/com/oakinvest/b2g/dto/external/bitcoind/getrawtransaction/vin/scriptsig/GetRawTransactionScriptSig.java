package com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vin.scriptsig;

import java.io.Serializable;

/**
 * Vin.
 * Created by straumat on 01/09/16.
 */
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
}
