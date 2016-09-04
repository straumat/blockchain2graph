package com.oakinvest.b2g.dto.external.bitcoind.getblock;

import java.util.ArrayList;

/**
 * result inside the getblock response.
 * Created by straumat on 31/08/16.
 */
public class GetBlockResult {

	/**
	 * the block hash (same as provided).
	 */
	private String hash;

	/**
	 * The number of confirmations, or -1 if the block is not on the main chain.
	 */
	private long confirmations;

	/**
	 * The block size.
	 */
	private long size;

	/**
	 * The block height or index.
	 */
	private long height;

	/**
	 * The block version.
	 */
	private long version;

	/**
	 * The merkle root.
	 */
	private String merkleroot;

	/**
	 * The block time in seconds since epoch (Jan 1 1970 GMT).
	 */
	private long time;

	/**
	 * The median block time in seconds since epoch (Jan 1 1970 GMT).
	 */
	private long mediantime;

	/**
	 * The nonce.
	 */
	private long nonce;

	/**
	 * The bits.
	 */
	private String bits;

	/**
	 * The difficulty.
	 */
	private float difficulty;

	/**
	 * Expected number of hashes required to produce the chain up to this block (in hex).
	 */
	private String chainwork;

	/**
	 * The hash of the previous block.
	 */
	private String previousblockhash;

	/**
	 * The hash of the next block.
	 */
	private String nextblockhash;

	/**
	 * The transaction ids.
	 */
	private ArrayList<String> tx;

	/**
	 * Getter de la propriété tx.
	 *
	 * @return tx
	 */
	public final ArrayList<String> getTx() {
		return tx;
	}

	/**
	 * Setter de la propriété tx.
	 *
	 * @param newTx the tx to set
	 */
	public final void setTx(final ArrayList<String> newTx) {
		tx = newTx;
	}

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
	 * Getter de la propriété confirmations.
	 *
	 * @return confirmations
	 */
	public final long getConfirmations() {
		return confirmations;
	}

	/**
	 * Setter de la propriété confirmations.
	 *
	 * @param newConfirmations the confirmations to set
	 */
	public final void setConfirmations(final long newConfirmations) {
		confirmations = newConfirmations;
	}

	/**
	 * Getter de la propriété size.
	 *
	 * @return size
	 */
	public final long getSize() {
		return size;
	}

	/**
	 * Setter de la propriété size.
	 *
	 * @param newSize the size to set
	 */
	public final void setSize(final long newSize) {
		size = newSize;
	}

	/**
	 * Getter de la propriété height.
	 *
	 * @return height
	 */
	public final long getHeight() {
		return height;
	}

	/**
	 * Setter de la propriété height.
	 *
	 * @param newHeight the height to set
	 */
	public final void setHeight(final long newHeight) {
		height = newHeight;
	}

	/**
	 * Getter de la propriété version.
	 *
	 * @return version
	 */
	public final long getVersion() {
		return version;
	}

	/**
	 * Setter de la propriété version.
	 *
	 * @param newVersion the version to set
	 */
	public final void setVersion(final long newVersion) {
		version = newVersion;
	}

	/**
	 * Getter de la propriété merkleroot.
	 *
	 * @return merkleroot
	 */
	public final String getMerkleroot() {
		return merkleroot;
	}

	/**
	 * Setter de la propriété merkleroot.
	 *
	 * @param newMerkleroot the merkleroot to set
	 */
	public final void setMerkleroot(final String newMerkleroot) {
		merkleroot = newMerkleroot;
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
	 * Getter de la propriété mediantime.
	 *
	 * @return mediantime
	 */
	public final long getMediantime() {
		return mediantime;
	}

	/**
	 * Setter de la propriété mediantime.
	 *
	 * @param newMediantime the mediantime to set
	 */
	public final void setMediantime(final long newMediantime) {
		mediantime = newMediantime;
	}

	/**
	 * Getter de la propriété nonce.
	 *
	 * @return nonce
	 */
	public final long getNonce() {
		return nonce;
	}

	/**
	 * Setter de la propriété nonce.
	 *
	 * @param newNonce the nonce to set
	 */
	public final void setNonce(final long newNonce) {
		nonce = newNonce;
	}

	/**
	 * Getter de la propriété bits.
	 *
	 * @return bits
	 */
	public final String getBits() {
		return bits;
	}

	/**
	 * Setter de la propriété bits.
	 *
	 * @param newBits the bits to set
	 */
	public final void setBits(final String newBits) {
		bits = newBits;
	}

	/**
	 * Getter de la propriété difficulty.
	 *
	 * @return difficulty
	 */
	public final float getDifficulty() {
		return difficulty;
	}

	/**
	 * Setter de la propriété difficulty.
	 *
	 * @param newDifficulty the difficulty to set
	 */
	public final void setDifficulty(final float newDifficulty) {
		difficulty = newDifficulty;
	}

	/**
	 * Getter de la propriété chainwork.
	 *
	 * @return chainwork
	 */
	public final String getChainwork() {
		return chainwork;
	}

	/**
	 * Setter de la propriété chainwork.
	 *
	 * @param newChainwork the chainwork to set
	 */
	public final void setChainwork(final String newChainwork) {
		chainwork = newChainwork;
	}

	/**
	 * Getter de la propriété previousblockhash.
	 *
	 * @return previousblockhash
	 */
	public final String getPreviousblockhash() {
		return previousblockhash;
	}

	/**
	 * Setter de la propriété previousblockhash.
	 *
	 * @param newPreviousblockhash the previousblockhash to set
	 */
	public final void setPreviousblockhash(final String newPreviousblockhash) {
		previousblockhash = newPreviousblockhash;
	}

	/**
	 * Getter de la propriété nextblockhash.
	 *
	 * @return nextblockhash
	 */
	public final String getNextblockhash() {
		return nextblockhash;
	}

	/**
	 * Setter de la propriété nextblockhash.
	 *
	 * @param newNextblockhash the nextblockhash to set
	 */
	public final void setNextblockhash(final String newNextblockhash) {
		nextblockhash = newNextblockhash;
	}
}
