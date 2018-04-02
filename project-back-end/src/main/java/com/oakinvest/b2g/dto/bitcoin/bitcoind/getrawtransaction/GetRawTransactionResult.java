package com.oakinvest.b2g.dto.bitcoin.bitcoind.getrawtransaction;

import com.oakinvest.b2g.dto.bitcoin.bitcoind.getrawtransaction.vin.GetRawTransactionVIn;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.getrawtransaction.vout.GetRawTransactionVOut;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Result inside the GetRawTransaction response.
 * Created by straumat on 01/09/16.
 */
@SuppressWarnings("unused")
public class GetRawTransactionResult implements Serializable {

	/**
	 * The serialized, hex-encoded data for 'txid'.
	 */
	private String hex;

	/**
	 * The transaction id (same as provided).
	 */
	private String txid;

	/**
	 * The transaction hash (differs from txid for witness transactions).
	 */
	private String hash;

	/**
	 * The transaction size.
	 */
	private int size;

	/**
	 * The virtual transaction size (differs from size for witness transactions).
	 */
	private int vsize;

	/**
	 * The version.
	 */
	private int version;

	/**
	 * The lock time.
	 */
	private long locktime;

	/**
	 * Vin.
	 */
	private ArrayList<GetRawTransactionVIn> vin = new ArrayList<>();

	/**
	 * Vout.
	 */
	private ArrayList<GetRawTransactionVOut> vout = new ArrayList<>();

	/**
	 * the block hash.
	 */
	private String blockhash;

	/**
	 * The confirmations.
	 */
	private int confirmations;

	/**
	 * The transaction time in seconds since epoch (Jan 1 1970 GMT).
	 */
	private long time;

	/**
	 * The block time in seconds since epoch (Jan 1 1970 GMT).
	 */
	private long blocktime;

	/**
	 * Getter of hash.
	 *
	 * @return hash
	 */
	public final String getHash() {
		return hash;
	}

	/**
	 * Setter of hash.
	 *
	 * @param newHash the hash to set
	 */
    public final void setHash(final String newHash) {
		hash = newHash;
	}

	/**
	 * Getter of vsize.
	 *
	 * @return vsize
	 */
	public final int getVsize() {
		return vsize;
	}

	/**
	 * Setter of vsize.
	 *
	 * @param newVsize the vsize to set
	 */
	public final void setVsize(final int newVsize) {
		vsize = newVsize;
	}

	/**
	 * Getter of vin.
	 *
	 * @return vin
	 */
	public final ArrayList<GetRawTransactionVIn> getVin() {
		return vin;
	}

	/**
	 * Setter of vin.
	 *
	 * @param newVin the vin to set
	 */
	public final void setVin(final ArrayList<GetRawTransactionVIn> newVin) {
		vin = newVin;
	}

	/**
	 * Getter of vout.
	 *
	 * @return vout
	 */
	public final ArrayList<GetRawTransactionVOut> getVout() {
		return vout;
	}

	/**
	 * Setter of vout.
	 *
	 * @param newVout the vout to set
	 */
	public final void setVout(final ArrayList<GetRawTransactionVOut> newVout) {
		vout = newVout;
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
	 * Getter of size.
	 *
	 * @return size
	 */
	public final int getSize() {
		return size;
	}

	/**
	 * Setter of size.
	 *
	 * @param newSize the size to set
	 */
	public final void setSize(final int newSize) {
		size = newSize;
	}

	/**
	 * Getter of version.
	 *
	 * @return version
	 */
	public final int getVersion() {
		return version;
	}

	/**
	 * Setter of version.
	 *
	 * @param newVersion the version to set
	 */
	public final void setVersion(final int newVersion) {
		version = newVersion;
	}

	/**
	 * Getter of locktime.
	 *
	 * @return locktime
	 */
	public final long getLocktime() {
		return locktime;
	}

	/**
	 * Setter of locktime.
	 *
	 * @param newLocktime the locktime to set
	 */
	public final void setLocktime(final long newLocktime) {
		locktime = newLocktime;
	}

	/**
	 * Getter of blockhash.
	 *
	 * @return blockhash
	 */
	public final String getBlockhash() {
		return blockhash;
	}

	/**
	 * Setter of blockhash.
	 *
	 * @param newBlockhash the blockhash to set
	 */
	public final void setBlockhash(final String newBlockhash) {
		blockhash = newBlockhash;
	}

	/**
	 * Getter of confirmations.
	 *
	 * @return confirmations
	 */
	public final int getConfirmations() {
		return confirmations;
	}

	/**
	 * Setter of confirmations.
	 *
	 * @param newConfirmations the confirmations to set
	 */
	public final void setConfirmations(final int newConfirmations) {
		confirmations = newConfirmations;
	}

	/**
	 * Getter of time.
	 *
	 * @return time
	 */
	public final long getTime() {
		return time;
	}

	/**
	 * Setter of time.
	 *
	 * @param newTime the time to set
	 */
	public final void setTime(final long newTime) {
		time = newTime;
	}

	/**
	 * Getter of blocktime.
	 *
	 * @return blocktime
	 */
	public final long getBlocktime() {
		return blocktime;
	}

	/**
	 * Setter of blocktime.
	 *
	 * @param newBlocktime the blocktime to set
	 */
	public final void setBlocktime(final long newBlocktime) {
		blocktime = newBlocktime;
	}

    /**
     * Equals.
     * @param o object
     * @return true if equals
     */
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GetRawTransactionResult)) {
            return false;
        }

        GetRawTransactionResult that = (GetRawTransactionResult) o;

        return getTxid().equals(that.getTxid());
    }

    /**
     * hash code.
     * @return hashcode
     */
    @Override
    public final int hashCode() {
        return getTxid().hashCode();
    }

}
