package com.oakinvest.b2g.domain.bitcoin;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Bitcoin transaction.
 * Created by straumat on 22/09/16.
 */
@NodeEntity(label = "BitcoinTransaction")
public class BitcoinTransaction {

	/**
	 * ID.
	 */
	@GraphId
	private Long id;

	/**
	 * The serialized, hex-encoded data for 'txId'.
	 */
	@Property(name = "hex")
	private String hex;

	/**
	 * The transaction id (same as provided).
	 */
	@Index(unique = true, primary = true)
	@Property(name = "txid")
	private String txId;

	/**
	 * The transaction hash (differs from txId for witness transactions).
	 */
	@Property(name = "hash")
	private String hash;

	/**
	 * The serialized transaction size.
	 */
	@Property(name = "size")
	private long size;

	/**
	 * The virtual transaction size (differs from size for witness transactions).
	 */
	@Property(name = "vsize")
	private long vSize;

	/**
	 * The version.
	 */
	@Property(name = "version")
	private long version;

	/**
	 * The lock time.
	 */
	@Property(name = "locktime")
	private long lockTime;

	/**
	 * The block hash.
	 */
	@Property(name = "blockhash")
	private String blockHash;

	/**
	 * The transaction time in seconds since epoch (Jan 1 1970 GMT).
	 */
	@Property(name = "time")
	private long time;

	/**
	 * The block time in seconds since epoch (Jan 1 1970 GMT).
	 */
	@Property(name = "blocktime")
	private long blockTime;

	/**
	 * Inputs.
	 */
	@Relationship(type = "INPUTS", direction = Relationship.OUTGOING)
	private Set<BitcoinTransactionInput> inputs = new HashSet<>();

	/**
	 * Outputs.
	 */
	@Relationship(type = "OUTPUTS", direction = Relationship.OUTGOING)
	private Set<BitcoinTransactionOutput> outputs = new HashSet<>();

	/**
	 * Returns the output according to the index.
	 *
	 * @param n index
	 * @return output transaction
	 */
	public final Optional<BitcoinTransactionOutput> getOutputByIndex(final int n) {
		return getOutputs().stream().filter(o -> o.getN() == n).findFirst();
	}

	@Override
	public final String toString() {
		return "BitcoinTransaction{" + "txId='" + txId + '}';
	}

	@Override
	public final String toString() {
		return "BitcoinTransaction{" + "txId='" + txId + '}';
	}

	/**
	 * Getter of blockHash.
	 *
	 * @return blockHash
	 */
	public final String getBlockHash() {
		return blockHash;
	}

	/**
	 * Setter of blockHash.
	 *
	 * @param newBlockHash the blockHash to set
	 */
	public final void setBlockHash(final String newBlockHash) {
		blockHash = newBlockHash;
	}

	/**
	 * Getter of blockTime.
	 *
	 * @return blockTime
	 */
	public final long getBlockTime() {
		return blockTime;
	}

	/**
	 * Setter of blockTime.
	 *
	 * @param newBlockTime the blockTime to set
	 */
	public final void setBlockTime(final long newBlockTime) {
		blockTime = newBlockTime;
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
	 * Getter of txId.
	 *
	 * @return txId
	 */
	public final String getTxId() {
		return txId;
	}

	/**
	 * Setter of txId.
	 *
	 * @param newTxid the txId to set
	 */
	public final void setTxId(final String newTxid) {
		txId = newTxid;
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
	 * Getter of vSize.
	 *
	 * @return vSize
	 */
	public final long getvSize() {
		return vSize;
	}

	/**
	 * Setter of vSize.
	 *
	 * @param newVSize the vSize to set
	 */
	public final void setvSize(final long newVSize) {
		vSize = newVSize;
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
	 * Getter of lockTime.
	 *
	 * @return lockTime
	 */
	public final long getLockTime() {
		return lockTime;
	}

	/**
	 * Setter of lockTime.
	 *
	 * @param newLockTime the lockTime to set
	 */
	public final void setLockTime(final long newLockTime) {
		lockTime = newLockTime;
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
	 * Getter of inputs.
	 *
	 * @return inputs
	 */
	public final Set<BitcoinTransactionInput> getInputs() {
		return inputs;
	}

	/**
	 * Setter of inputs.
	 *
	 * @param newInputs the inputs to set
	 */
	public final void setInputs(final Set<BitcoinTransactionInput> newInputs) {
		inputs = newInputs;
	}

	/**
	 * Getter of outputs.
	 *
	 * @return outputs
	 */
	public final Set<BitcoinTransactionOutput> getOutputs() {
		return outputs;
	}

	/**
	 * Setter of outputs.
	 *
	 * @param newOutputs the outputs to set
	 */
	public final void setOutputs(final Set<BitcoinTransactionOutput> newOutputs) {
		outputs = newOutputs;
	}

	/**
	 * Using transaction hash.
	 *
	 * @param o object
	 * @return true if equals
	 */
	@Override
	public final boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BitcoinTransaction)) {
			return false;
		}

		final BitcoinTransaction that = (BitcoinTransaction) o;

		return getTxId().equals(that.getTxId());
	}

	/**
	 * Using transaction hash.
	 *
	 * @return hash
	 */
	@Override
	public final int hashCode() {
		return getTxId().hashCode();
	}

}
