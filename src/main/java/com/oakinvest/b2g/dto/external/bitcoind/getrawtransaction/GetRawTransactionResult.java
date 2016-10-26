package com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction;

import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vin.GetRawTransactionVIn;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vout.GetRawTransactionVOut;

import java.util.ArrayList;
import java.util.Date;

/**
 * Result inside the GetRawTransaction response.
 * Created by straumat on 01/09/16.
 */
public class GetRawTransactionResult {

	/**
	 * The serialized, hex-encoded data for 'txid'.
	 */
	private String hex;

	/**
	 * The transaction id (same as provided).
	 */
	private String txid;

	/**
	 * The transaction hash (differs from txid for witness transactions)
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
	private int locktime;

	/**
	 * Vin.
	 */
	private ArrayList<GetRawTransactionVIn> vin;

	/**
	 * Vout.
	 */
	private ArrayList<GetRawTransactionVOut> vout;

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
	 * Getter de la propriété hash.
	 *
	 * @return hash
	 */
	public final String getHash() {
		return hash;
	}

	/**
	 * Setter de la propriété hash.
	 *
	 * @param newHash the hash to set
	 */
	public final void setHash(final String newHash) {
		hash = newHash;
	}

	/**
	 * Getter de la propriété vsize.
	 *
	 * @return vsize
	 */
	public final int getVsize() {
		return vsize;
	}

	/**
	 * Setter de la propriété vsize.
	 *
	 * @param newVsize the vsize to set
	 */
	public final void setVsize(final int newVsize) {
		vsize = newVsize;
	}

	/**
	 * Getter de la propriété vin.
	 *
	 * @return vin
	 */
	public final ArrayList<GetRawTransactionVIn> getVin() {
		return vin;
	}

	/**
	 * Setter de la propriété vin.
	 *
	 * @param newVin the vin to set
	 */
	public final void setVin(final ArrayList<GetRawTransactionVIn> newVin) {
		vin = newVin;
	}

	/**
	 * Getter de la propriété vout.
	 *
	 * @return vout
	 */
	public final ArrayList<GetRawTransactionVOut> getVout() {
		return vout;
	}

	/**
	 * Setter de la propriété vout.
	 *
	 * @param newVout the vout to set
	 */
	public final void setVout(final ArrayList<GetRawTransactionVOut> newVout) {
		vout = newVout;
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
	 * Getter de la propriété size.
	 *
	 * @return size
	 */
	public final int getSize() {
		return size;
	}

	/**
	 * Setter de la propriété size.
	 *
	 * @param newSize the size to set
	 */
	public final void setSize(final int newSize) {
		size = newSize;
	}

	/**
	 * Getter de la propriété version.
	 *
	 * @return version
	 */
	public final int getVersion() {
		return version;
	}

	/**
	 * Setter de la propriété version.
	 *
	 * @param newVersion the version to set
	 */
	public final void setVersion(final int newVersion) {
		version = newVersion;
	}

	/**
	 * Getter de la propriété locktime.
	 *
	 * @return locktime
	 */
	public final int getLocktime() {
		return locktime;
	}

	/**
	 * Setter de la propriété locktime.
	 *
	 * @param newLocktime the locktime to set
	 */
	public final void setLocktime(final int newLocktime) {
		locktime = newLocktime;
	}

	/**
	 * Getter de la propriété blockhash.
	 *
	 * @return blockhash
	 */
	public final String getBlockhash() {
		return blockhash;
	}

	/**
	 * Setter de la propriété blockhash.
	 *
	 * @param newBlockhash the blockhash to set
	 */
	public final void setBlockhash(final String newBlockhash) {
		blockhash = newBlockhash;
	}

	/**
	 * Getter de la propriété confirmations.
	 *
	 * @return confirmations
	 */
	public final int getConfirmations() {
		return confirmations;
	}

	/**
	 * Setter de la propriété confirmations.
	 *
	 * @param newConfirmations the confirmations to set
	 */
	public final void setConfirmations(final int newConfirmations) {
		confirmations = newConfirmations;
	}

	/**
	 * Getter de la propriété time.
	 *
	 * @return time
	 */
	public final long getTime() {
		return time;
	}

	/**
	 * Setter de la propriété time.
	 *
	 * @param newTime the time to set
	 */
	public final void setTime(final long newTime) {
		time = newTime;
	}

	/**
	 * Returns time as a Date
	 *
	 * @return time as a Date.
	 */
	public final Date getTimeAsDate() {
		return new Date(getTime() * 1000);
	}

	/**
	 * Getter de la propriété blocktime.
	 *
	 * @return blocktime
	 */
	public final long getBlocktime() {
		return blocktime;
	}

	/**
	 * Setter de la propriété blocktime.
	 *
	 * @param newBlocktime the blocktime to set
	 */
	public final void setBlocktime(final long newBlocktime) {
		blocktime = newBlocktime;
	}

	/**
	 * Returns blocktime as a date.
	 *
	 * @return blocktime as a date.
	 */
	public final Date getBlocktimeAsDate() {
		return new Date(getTime() * 1000);
	}

}
