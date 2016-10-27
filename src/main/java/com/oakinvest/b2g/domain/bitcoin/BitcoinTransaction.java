package com.oakinvest.b2g.domain.bitcoin;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Iterator;
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
	 * The transaction id (same as provided)?
	 */
	@Property(name = "txid")
	private String txId;

	/**
	 * The block.
	 */
	@Relationship(type = "IN_BLOCK", direction = Relationship.INCOMING)
	private BitcoinBlock block;

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
	private Set<BitcoinTransactionInput> inputs;

	/**
	 * Outputs.
	 */
	private Set<BitcoinTransactionOutput> outputs;

	/**
	 * Returns the output according to the index.
	 *
	 * @param n index
	 * @return output transaction
	 */
	public final BitcoinTransactionOutput getOutputByIndex(final int n) {
		Iterator<BitcoinTransactionOutput> it = getOutputs().iterator();
		while (it.hasNext()) {
			BitcoinTransactionOutput output = it.next();
			if (output.getN() == n) {
				return output;
			}
		}
		return null;
	}

	/**
	 * Getter de la propriété blockHash.
	 *
	 * @return blockHash
	 */
	public final String getBlockHash() {
		return blockHash;
	}

	/**
	 * Setter de la propriété blockHash.
	 *
	 * @param newBlockHash the blockHash to set
	 */
	public final void setBlockHash(final String newBlockHash) {
		blockHash = newBlockHash;
	}

	/**
	 * Getter de la propriété blockTime.
	 *
	 * @return blockTime
	 */
	public final long getBlockTime() {
		return blockTime;
	}

	/**
	 * Setter de la propriété blockTime.
	 *
	 * @param newBlockTime the blockTime to set
	 */
	public final void setBlockTime(final long newBlockTime) {
		blockTime = newBlockTime;
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
	 * Getter de la propriété txId.
	 *
	 * @return txId
	 */
	public final String getTxId() {
		return txId;
	}

	/**
	 * Setter de la propriété txId.
	 *
	 * @param newTxid the txId to set
	 */
	public final void setTxId(final String newTxid) {
		txId = newTxid;
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
	 * Getter de la propriété vSize.
	 *
	 * @return vSize
	 */
	public final long getvSize() {
		return vSize;
	}

	/**
	 * Setter de la propriété vSize.
	 *
	 * @param newvSize the vSize to set
	 */
	public final void setvSize(final long newvSize) {
		vSize = newvSize;
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
	 * Getter de la propriété lockTime.
	 *
	 * @return lockTime
	 */
	public final long getLockTime() {
		return lockTime;
	}

	/**
	 * Setter de la propriété lockTime.
	 *
	 * @param newLockTime the lockTime to set
	 */
	public final void setLockTime(final long newLockTime) {
		lockTime = newLockTime;
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
	 * Getter de la propriété inputs.
	 *
	 * @return inputs
	 */
	public final Set<BitcoinTransactionInput> getInputs() {
		return inputs;
	}

	/**
	 * Setter de la propriété inputs.
	 *
	 * @param newInputs the inputs to set
	 */
	public final void setInputs(final Set<BitcoinTransactionInput> newInputs) {
		inputs = newInputs;
	}

	/**
	 * Getter de la propriété outputs.
	 *
	 * @return outputs
	 */
	public final Set<BitcoinTransactionOutput> getOutputs() {
		return outputs;
	}

	/**
	 * Setter de la propriété outputs.
	 *
	 * @param newOutputs the outputs to set
	 */
	public final void setOutputs(final Set<BitcoinTransactionOutput> newOutputs) {
		outputs = newOutputs;
	}

	/**
	 * Getter de la propriété block.
	 *
	 * @return block
	 */
	public final BitcoinBlock getBlock() {
		return block;
	}

	/**
	 * Setter de la propriété block.
	 *
	 * @param newBlock the block to set
	 */
	public final void setBlock(final BitcoinBlock newBlock) {
		block = newBlock;
	}
}
