package com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.vout;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.vout.scriptpubkey.GetRawTransactionScriptPubKey;

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
	private int n;

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
	public final int getN() {
		return n;
	}

	/**
	 * Setter of n.
	 *
	 * @param newIndex the n to set
	 */
	public final void setN(final int newIndex) {
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

    /**
     * Equals method.
     * @param o object
     * @return true if equals
     */
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GetRawTransactionVOut)) {
            return false;
        }

        GetRawTransactionVOut that = (GetRawTransactionVOut) o;

        return getN() == that.getN();
    }

    /**
     * Hashcode.
     * @return hashcode
     */
    @Override
    public final int hashCode() {
        return getN();
    }

}
