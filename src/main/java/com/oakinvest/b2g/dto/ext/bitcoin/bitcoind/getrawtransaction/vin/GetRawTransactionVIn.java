package com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.vin;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.vin.scriptsig.GetRawTransactionScriptSig;

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
    private int vout;

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
    public final int getVout() {
        return vout;
    }

    /**
     * Setter of vout.
     *
     * @param newVout the vout to set
     */
    public final void setVout(final int newVout) {
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

    /**
     * Returns true if it's a coinbase transaction.
     *
     * @return coinbase.
     */
    private boolean isCoinbase() {
        return getCoinbase() != null;
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
        if (!(o instanceof GetRawTransactionVIn)) {
            return false;
        }

        GetRawTransactionVIn that = (GetRawTransactionVIn) o;

        if (isCoinbase()) {
            // If it's a coinbase.
            return that.getTxid() == null && getCoinbase().equals(that.getCoinbase());
        } else {
            // if it's a transaction
            return (getTxid().equals(that.getTxid()) && (getVout() == that.getVout()));
        }
    }

    /**
     * Hashcode.
     * @return hashcode
     */
    @Override
    public final int hashCode() {
        if (isCoinbase()) {
            return getCoinbase().hashCode();
        } else {
            return (getTxid() + getVout()).hashCode();
        }
    }

    /**
     * toString.
     * @return value.
     */
    @Override
    public final String toString() {
        return txid + "-" + vout;
    }

}