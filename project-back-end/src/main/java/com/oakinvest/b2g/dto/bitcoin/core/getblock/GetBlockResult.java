package com.oakinvest.b2g.dto.bitcoin.core.getblock;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * result inside the getblock response.
 * Created by straumat on 31/08/16.
 */
@SuppressWarnings("ALL")
public class GetBlockResult implements Serializable {

	/**
	 * the block hash (same as provided).
	 */
	private String hash;

	/**
	 * The number of confirmations, or -1 if the block is not on the main chain.
	 */
	private int confirmations;

	/**
	 * The block size.
	 */
	private int size;

	/**
	 * The block height or index.
	 */
	private int height;

	/**
	 * The block version.
	 */
	private int version;

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
	private ArrayList<String> tx = new ArrayList<>();

	/**
	 * Getter of tx.
	 *
	 * @return tx
	 */
	public final ArrayList<String> getTx() {
		return tx;
	}

	/**
	 * Setter of tx.
	 *
	 * @param newTx the tx to set
	 */
	public final void setTx(final ArrayList<String> newTx) {
		tx = newTx;
	}

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
	 * Getter of height.
	 *
	 * @return height
	 */
	public final int getHeight() {
		return height;
	}

	/**
	 * Setter of height.
	 *
	 * @param newHeight the height to set
	 */
	public final void setHeight(final int newHeight) {
		height = newHeight;
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
	 * Getter of merkleroot.
	 *
	 * @return merkleroot
	 */
	public final String getMerkleroot() {
		return merkleroot;
	}

	/**
	 * Setter of merkleroot.
	 *
	 * @param newMerkleroot the merkleroot to set
	 */
	public final void setMerkleroot(final String newMerkleroot) {
		merkleroot = newMerkleroot;
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
	 * Getter of mediantime.
	 *
	 * @return mediantime as date
	 */
	public final long getMediantime() {
		return mediantime;
	}

	/**
	 * Setter of mediantime.
	 *
	 * @param newMediantime the mediantime to set
	 */
	public final void setMediantime(final long newMediantime) {
		mediantime = newMediantime;
	}

	/**
	 * Getter of nonce.
	 *
	 * @return nonce
	 */
	public final long getNonce() {
		return nonce;
	}

	/**
	 * Setter of nonce.
	 *
	 * @param newNonce the nonce to set
	 */
	public final void setNonce(final long newNonce) {
		nonce = newNonce;
	}

	/**
	 * Getter of bits.
	 *
	 * @return bits
	 */
	public final String getBits() {
		return bits;
	}

	/**
	 * Setter of bits.
	 *
	 * @param newBits the bits to set
	 */
	public final void setBits(final String newBits) {
		bits = newBits;
	}

	/**
	 * Getter of difficulty.
	 *
	 * @return difficulty
	 */
	public final float getDifficulty() {
		return difficulty;
	}

	/**
	 * Setter of difficulty.
	 *
	 * @param newDifficulty the difficulty to set
	 */
	public final void setDifficulty(final float newDifficulty) {
		difficulty = newDifficulty;
	}

	/**
	 * Getter of chainwork.
	 *
	 * @return chainwork
	 */
	public final String getChainwork() {
		return chainwork;
	}

	/**
	 * Setter of chainwork.
	 *
	 * @param newChainwork the chainwork to set
	 */
	public final void setChainwork(final String newChainwork) {
		chainwork = newChainwork;
	}

	/**
	 * Getter of previousblockhash.
	 *
	 * @return previousblockhash
	 */
	public final String getPreviousblockhash() {
		return previousblockhash;
	}

	/**
	 * Setter of previousblockhash.
	 *
	 * @param newPreviousblockhash the previousblockhash to set
	 */
	public final void setPreviousblockhash(final String newPreviousblockhash) {
		previousblockhash = newPreviousblockhash;
	}

	/**
	 * Getter of nextblockhash.
	 *
	 * @return nextblockhash
	 */
	public final String getNextblockhash() {
		return nextblockhash;
	}

	/**
	 * Setter of nextblockhash.
	 *
	 * @param newNextblockhash the nextblockhash to set
	 */
	public final void setNextblockhash(final String newNextblockhash) {
		nextblockhash = newNextblockhash;
	}

    /**
     * Equals.
     *
     * @param o object
     * @return true if same
     */
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GetBlockResult)) {
            return false;
        }

        GetBlockResult that = (GetBlockResult) o;

        return getHash().equals(that.getHash());
    }

    /**
     * Hash code.
     * @return hash
     */
    @Override
    public final int hashCode() {
        return getHash().hashCode();
    }

}
