package com.oakinvest.b2g.domain.bitcoin;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * Bitcoin block.
 * Created by straumat on 05/09/16.
 */
@NodeEntity(label = "BitcoinBlock")
public class BitcoinBlock {

	/**
	 * ID.
	 */
	@GraphId
	private Long id;

	/**
	 * Block hash.
	 */
	@Property(name = "hash")
	private String hash;

	/**
	 * Block height.
	 */
	@Property(name = "height")
	private long height;

	/**
	 * The block size.
	 */
	@Property(name = "size")
	private long size;

	/**
	 * The block version.
	 */
	@Property(name = "version")
	private long version;

	/**
	 * The merkle root.
	 */
	@Property(name = "merkleroot")
	private String merkleroot;

	/**
	 * The block time.
	 */
	@Property(name = "time")
	private long time;

	/**
	 * The median block time.
	 */
	@Property(name = "mediantime")
	private long mediantime;

	/**
	 * The nonce.
	 */
	@Property(name = "nonce")
	private long nonce;

	/**
	 * The bits.
	 */
	@Property(name = "bits")
	private String bits;

	/**
	 * The difficulty.
	 */
	@Property(name = "difficulty")
	private float difficulty;

	/**
	 * Expected number of hashes required to produce the chain up to this block (in hex).
	 */
	@Property(name = "chainwork")
	private String chainwork;

	/**
	 * The hash of the previous block.
	 */
	@Property(name = "previousblockhash")
	private String previousblockhash;

	/**
	 * The hash of the next block.
	 */
	@Property(name = "nextblockhash")
	private String nextblockhash;

	/**
	 * For display.
	 *
	 * @return view
	 */
	@Override
	public String toString() {
		return "BitcoinBlock{hash = '" + hash + '\'' + ", height=" + height + '}';
	}

	/**
	 * Getter de la propriété id.
	 *
	 * @return id
	 */
	public final Long getId() {
		return id;
	}

	/**
	 * Setter de la propriété id.
	 *
	 * @param newId the id to set
	 */
	public final void setId(final Long newId) {
		id = newId;
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
	 * @param newIndex the height to set
	 */
	public final void setHeight(final long newIndex) {
		height = newIndex;
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
	 * @param newCreationDate the time to set
	 */
	public final void setTime(final long newCreationDate) {
		time = newCreationDate;
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
