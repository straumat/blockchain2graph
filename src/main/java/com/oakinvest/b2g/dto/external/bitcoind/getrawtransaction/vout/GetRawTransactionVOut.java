package com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vout;

import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vout.scriptpubkey.GetRawTransactionScriptPubKey;

import java.io.Serializable;

/**
 * vout.
 * Created by straumat on 01/09/16.
 */
public class GetRawTransactionVOut implements Serializable {

	/**
	 * The value in BTC.
	 */
	private float value;

	/**
	 * Index.
	 */
	private long n;

	/**
	 * ScriptPubKey.
	 */
	private GetRawTransactionScriptPubKey scriptPubKey;

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
	public final long getN() {
		return n;
	}

	/**
	 * Setter of n.
	 *
	 * @param newIndex the n to set
	 */
	public final void setN(final long newIndex) {
		n = newIndex;
	}

	/**
	 * Getter of scriptPubKey.
	 *
	 * @return scriptPubKey
	 */
	public final GetRawTransactionScriptPubKey getScriptPubKey() {
		return scriptPubKey;
	}

	/**
	 * Setter of scriptPubKey.
	 *
	 * @param newScriptPubKey the scriptPubKey to set
	 */
	public final void setScriptPubKey(final GetRawTransactionScriptPubKey newScriptPubKey) {
		scriptPubKey = newScriptPubKey;
	}
}
