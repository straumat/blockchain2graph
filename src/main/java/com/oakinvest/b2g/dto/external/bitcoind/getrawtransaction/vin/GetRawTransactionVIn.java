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
	 * Getter of txid.
	 *
	 * @return txid
	 */
	public final String getTxid() {
		return txid;
	}

	/**
	 * Setter of txid.
	 *
	 * @param newTxid the txid to set
	 */
	public final void setTxid(final String newTxid) {
		txid = newTxid;
	}

	/**
	 * Getter of coinbase.
	 *
	 * @return coinbase
	 */
	public final String getCoinbase() {
		return coinbase;
	}

	/**
	 * Setter of coinbase.
	 *
	 * @param newCoinbase the coinbase to set
	 */
	public final void setCoinbase(final String newCoinbase) {
		coinbase = newCoinbase;
	}

	/**
	 * Getter of vout.
	 *
	 * @return vout
	 */
	public final long getVout() {
		return vout;
	}

	/**
	 * Setter of vout.
	 *
	 * @param newVout the vout to set
	 */
	public final void setVout(final long newVout) {
		vout = newVout;
	}

	/**
	 * Getter of scriptSig.
	 *
	 * @return scriptSig
	 */
	public final GetRawTransactionScriptSig getScriptSig() {
		return scriptSig;
	}

	/**
	 * Setter of scriptSig.
	 *
	 * @param newScriptSig the scriptSig to set
	 */
	public final void setScriptSig(final GetRawTransactionScriptSig newScriptSig) {
		scriptSig = newScriptSig;
	}

	/**
	 * Getter of sequence.
	 *
	 * @return sequence
	 */
	public final long getSequence() {
		return sequence;
	}

	/**
	 * Setter of sequence.
	 *
	 * @param newSequence the sequence to set
	 */
	public final void setSequence(final long newSequence) {
		sequence = newSequence;
	}

}
