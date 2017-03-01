package com.oakinvest.b2g.domain.bitcoin;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
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
	private Set<BitcoinTransaction> transactions = new HashSet<>();

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
	 * The transaction ids.
	 */
	@Property(name = "tx")
	private ArrayList<String> tx = new ArrayList<>();

	/**
	 * Addresses imported.
	 */
	@Property(name = "addressesImported")
	private boolean addressesImported = false;

	/**
	 * Transactions imported.
	 */
	@Property(name = "transactionsImported")
	private boolean transactionsImported = false;

	/**
	 * Relations imported.
	 */
	@Property(name = "relationsImported")
	private boolean relationsImported = false;

	/**
	 * Indicates that the bloc is fully imported.
	 */
	@Property(name = "imported")
	private boolean imported = false;

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
	 * Getter of id.
	 *
	 * @return id
	 */
	public final Long getId() {
		return id;
	}

	/**
	 * Setter of id.
	 *
	 * @param newId the id to set
	 */
	public final void setId(final Long newId) {
		id = newId;
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
	 * Getter of height.
	 *
	 * @return height
	 */
	public final long getHeight() {
		return height;
	}

	/**
	 * Setter of height.
	 *
	 * @param newIndex the height to set
	 */
	public final void setHeight(final long newIndex) {
		height = newIndex;
	}

	/**
	 * Getter of size.
	 *
	 * @return size
	 */
	public final long getSize() {
		return size;
	}

	/**
	 * Setter of size.
	 *
	 * @param newSize the size to set
	 */
	public final void setSize(final long newSize) {
		size = newSize;
	}

	/**
	 * Getter of version.
	 *
	 * @return version
	 */
	public final long getVersion() {
		return version;
	}

	/**
	 * Setter of version.
	 *
	 * @param newVersion the version to set
	 */
	public final void setVersion(final long newVersion) {
		version = newVersion;
	}

	/**
	 * Getter of merkleRoot.
	 *
	 * @return merkleRoot
	 */
	public final String getMerkleRoot() {
		return merkleRoot;
	}

	/**
	 * Setter of merkleRoot.
	 *
	 * @param newMerkleroot the merkleRoot to set
	 */
	public final void setMerkleRoot(final String newMerkleroot) {
		merkleRoot = newMerkleroot;
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
	 * @param newCreationDate the time to set
	 */
	public final void setTime(final long newCreationDate) {
		time = newCreationDate;
	}

	/**
	 * Getter of medianTime.
	 *
	 * @return medianTime
	 */
	public final long getMedianTime() {
		return medianTime;
	}

	/**
	 * Setter of medianTime.
	 *
	 * @param newMediantime the medianTime to set
	 */
	public final void setMedianTime(final long newMediantime) {
		medianTime = newMediantime;
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
	 * Getter of chainWork.
	 *
	 * @return chainWork
	 */
	public final String getChainWork() {
		return chainWork;
	}

	/**
	 * Setter of chainWork.
	 *
	 * @param newChainwork the chainWork to set
	 */
	public final void setChainWork(final String newChainwork) {
		chainWork = newChainwork;
	}

	/**
	 * Getter of previousBlockHash.
	 *
	 * @return previousBlockHash
	 */
	public final String getPreviousBlockHash() {
		return previousBlockHash;
	}

	/**
	 * Setter of previousBlockHash.
	 *
	 * @param newPreviousblockhash the previousBlockHash to set
	 */
	public final void setPreviousBlockHash(final String newPreviousblockhash) {
		previousBlockHash = newPreviousblockhash;
	}

	/**
	 * Getter of nextBlockHash.
	 *
	 * @return nextBlockHash
	 */
	public final String getNextBlockHash() {
		return nextBlockHash;
	}

	/**
	 * Setter of nextBlockHash.
	 *
	 * @param newNextblockhash the nextBlockHash to set
	 */
	public final void setNextBlockHash(final String newNextblockhash) {
		nextBlockHash = newNextblockhash;
	}


	/**
	 * Getter of transactions.
	 *
	 * @return transactions
	 */
	public final Set<BitcoinTransaction> getTransactions() {
		return transactions;
	}

	/**
	 * Setter of transactions.
	 *
	 * @param newTransactions the transactions to set
	 */
	public final void setTransactions(final Set<BitcoinTransaction> newTransactions) {
		transactions = newTransactions;
	}

	/**
	 * Getter of imported.
	 *
	 * @return imported
	 */
	public final boolean isImported() {
		return imported;
	}

	/**
	 * Setter of imported.
	 *
	 * @param newImported the imported to set
	 */
	public final void setImported(final boolean newImported) {
		imported = newImported;
	}

	/**
	 * Getter de la propriété addressesImported.
	 *
	 * @return addressesImported
	 */
	public final boolean isAddressesImported() {
		return addressesImported;
	}

	/**
	 * Setter de la propriété addressesImported.
	 *
	 * @param newAddressesImported the addressesImported to set
	 */
	public final void setAddressesImported(final boolean newAddressesImported) {
		addressesImported = newAddressesImported;
	}

	/**
	 * Getter de la propriété transactionsImported.
	 *
	 * @return transactionsImported
	 */
	public final boolean isTransactionsImported() {
		return transactionsImported;
	}

	/**
	 * Setter de la propriété transactionsImported.
	 *
	 * @param newTransactionsImported the transactionsImported to set
	 */
	public final void setTransactionsImported(final boolean newTransactionsImported) {
		transactionsImported = newTransactionsImported;
	}

	/**
	 * Getter de la propriété relationsImported.
	 *
	 * @return relationsImported
	 */
	public final boolean isRelationsImported() {
		return relationsImported;
	}

	/**
	 * Setter de la propriété relationsImported.
	 *
	 * @param newRelationsImported the relationsImported to set
	 */
	public final void setRelationsImported(final boolean newRelationsImported) {
		relationsImported = newRelationsImported;
	}

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
	 * Using block hash.
	 *
	 * @param o object
	 * @return true if equals
	 */
	@Override
	public final boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BitcoinBlock)) {
			return false;
		}

		final BitcoinBlock that = (BitcoinBlock) o;

		return getHash().equals(that.getHash());
	}

	/**
	 * Using block hash.
	 *
	 * @return hash
	 */
	@Override
	public final int hashCode() {
		return getHash().hashCode();
	}
}
