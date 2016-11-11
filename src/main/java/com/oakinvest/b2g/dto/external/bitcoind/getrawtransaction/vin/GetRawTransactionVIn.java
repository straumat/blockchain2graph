package com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vin;

import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vin.scriptsig.GetRawTransactionScriptSig;

import java.io.Serializable;

/**
 * Vin.
 * Created by straumat on 01/09/16.
 */
public class GetRawTransactionVIn implements Serializable {

	/**
	 * The transaction id.
	 */
	private String txid;

	/**
	 * Coinbase.
	 */
	private String coinbase;

	/**
	 * vout.
	 */
	private long vout;

	/**
	 * ScriptSig.
	 */
	private GetRawTransactionScriptSig scriptSig;

	/**
	 * The script sequence number.
	 */
	private long sequence;

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
	 * Getter de la propriété coinbase.
	 *
	 * @return coinbase
	 */
	public final String getCoinbase() {
		return coinbase;
	}

	/**
	 * Setter de la propriété coinbase.
	 *
	 * @param newCoinbase the coinbase to set
	 */
	public final void setCoinbase(final String newCoinbase) {
		coinbase = newCoinbase;
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
	 * Getter de la propriété scriptSig.
	 *
	 * @return scriptSig
	 */
	public final GetRawTransactionScriptSig getScriptSig() {
		return scriptSig;
	}

	/**
	 * Setter de la propriété scriptSig.
	 *
	 * @param newScriptSig the scriptSig to set
	 */
	public final void setScriptSig(final GetRawTransactionScriptSig newScriptSig) {
		scriptSig = newScriptSig;
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
