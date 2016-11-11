package com.oakinvest.b2g.domain.bitcoin;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

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
	 * Transactions in the block.
	 */
	@Relationship(type = "TRANSACTIONS")
	private Set<BitcoinTransaction> transactions = new HashSet<BitcoinTransaction>();

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
	 * The version.
	 */
	@Property(name = "version")
	private long version;

	/**
	 * The merkle root.
	 */
	@Property(name = "merkleroot")
	private String merkleRoot;

	/**
	 * The block time.
	 */
	@Property(name = "time")
	private long time;

	/**
	 * The median block time.
	 */
	@Property(name = "mediantime")
	private long medianTime;

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
	private String chainWork;

	/**
	 * The hash of the previous block.
	 */
	@Property(name = "previousblockhash")
	private String previousBlockHash;

	/**
	 * The hash of the next block.
	 */
	@Property(name = "nextblockhash")
	private String nextBlockHash;


	/**
	 * For display.
	 *
	 * @return view
	 */
	@Override
	public final String toString() {
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
	 * Getter de la propriété merkleRoot.
	 *
	 * @return merkleRoot
	 */
	public final String getMerkleRoot() {
		return merkleRoot;
	}

	/**
	 * Setter de la propriété merkleRoot.
	 *
	 * @param newMerkleroot the merkleRoot to set
	 */
	public final void setMerkleRoot(final String newMerkleroot) {
		merkleRoot = newMerkleroot;
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
	 * Getter de la propriété medianTime.
	 *
	 * @return medianTime
	 */
	public final long getMedianTime() {
		return medianTime;
	}

	/**
	 * Setter de la propriété medianTime.
	 *
	 * @param newMediantime the medianTime to set
	 */
	public final void setMedianTime(final long newMediantime) {
		medianTime = newMediantime;
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
	 * Getter de la propriété chainWork.
	 *
	 * @return chainWork
	 */
	public final String getChainWork() {
		return chainWork;
	}

	/**
	 * Setter de la propriété chainWork.
	 *
	 * @param newChainwork the chainWork to set
	 */
	public final void setChainWork(final String newChainwork) {
		chainWork = newChainwork;
	}

	/**
	 * Getter de la propriété previousBlockHash.
	 *
	 * @return previousBlockHash
	 */
	public final String getPreviousBlockHash() {
		return previousBlockHash;
	}

	/**
	 * Setter de la propriété previousBlockHash.
	 *
	 * @param newPreviousblockhash the previousBlockHash to set
	 */
	public final void setPreviousBlockHash(final String newPreviousblockhash) {
		previousBlockHash = newPreviousblockhash;
	}

	/**
	 * Getter de la propriété nextBlockHash.
	 *
	 * @return nextBlockHash
	 */
	public final String getNextBlockHash() {
		return nextBlockHash;
	}

	/**
	 * Setter de la propriété nextBlockHash.
	 *
	 * @param newNextblockhash the nextBlockHash to set
	 */
	public final void setNextBlockHash(final String newNextblockhash) {
		nextBlockHash = newNextblockhash;
	}


	/**
	 * Getter de la propriété transactions.
	 *
	 * @return transactions
	 */
	public final Set<BitcoinTransaction> getTransactions() {
		return transactions;
	}

	/**
	 * Setter de la propriété transactions.
	 *
	 * @param newTransactions the transactions to set
	 */
	public final void setTransactions(final Set<BitcoinTransaction> newTransactions) {
		transactions = newTransactions;
	}


}
