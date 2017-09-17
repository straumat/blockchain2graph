package com.oakinvest.b2g.domain.bitcoin;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Bitcoin transaction output.
 * Created by straumat on 22/09/16.
 */
@NodeEntity(label = "BitcoinTransactionOutput")
public class BitcoinTransactionOutput {

	/**
	 * ID.
	 */
	@GraphId
	private Long id;

    /**
     * Link to the transaction where the transaction output is.
     */
    @Property(name = "txid")
    private String txId;

    /**
     * Key (txid-n).
     */
    @Index
    @Property(name = "key")
    private String key;

	/**
	 * The value in BTC.
	 */
	@Property(name = "value")
	private float value;

	/**
	 * index.
	 */
	@Property(name = "n")
	private int n;

	/**
	 * The scriptPubKey asm.
	 */
	@Property(name = "scriptPubKeyAsm")
	private String scriptPubKeyAsm;

	/**
	 * The scriptPubKey hex.
	 */
	@Property(name = "scriptPubKeyHex")
	private String scriptPubKeyHex;

	/**
	 * The required sigs.
	 */
	@Property(name = "scriptPubKeyReqSigs")
	private int scriptPubKeyReqSigs;

	/**
	 * The type, eg 'pubkeyhash'.
	 */
	@Property(name = "type")
	private BitcoinTransactionOutputType scriptPubKeyType;

	/**
	 * Addresses.
	 */
	@Property(name = "addresses")
	private Set<String> addresses = new HashSet<>();

    /**
     * Address.
     */
    @Relationship(type = "ADDRESS", direction = Relationship.INCOMING)
    private BitcoinAddress bitcoinAddress;

	@Override
	public final String toString() {
		StringBuilder description = new StringBuilder(getN() + " " + getValue() + " -> ");
		if (getAddresses() == null || getAddresses().size() == 0) {
			description.append("No bitcoin address");
		} else {
			Iterator<String> it = getAddresses().iterator();
			while (it.hasNext()) {
				description.append(it.next());
				if (it.hasNext()) {
					description.append(", ");
				}
			}
		}
		return description.toString();
	}

    /**
     * Getter.
     *
     * @return bitcoin address
     */
    public final BitcoinAddress getBitcoinAddress() {
        return bitcoinAddress;
    }

    /**
     * Setter.
     * @param newAddress bitcoin bitcoinAddress
     */
    public final void setBitcoinAddress(final BitcoinAddress newAddress) {
        this.bitcoinAddress = newAddress;
    }

    /**
	 * Getter of scriptPubKeyReqSigs.
	 *
	 * @return scriptPubKeyReqSigs
	 */
	public final int getScriptPubKeyReqSigs() {
		return scriptPubKeyReqSigs;
	}

	/**
	 * Setter of scriptPubKeyReqSigs.
	 *
	 * @param newScriptPubKeyReqSigs the scriptPubKeyReqSigs to set
	 */
	public final void setScriptPubKeyReqSigs(final int newScriptPubKeyReqSigs) {
		scriptPubKeyReqSigs = newScriptPubKeyReqSigs;
	}

	/**
	 * Getter of scriptPubKeyType.
	 *
	 * @return scriptPubKeyType
	 */
	public final BitcoinTransactionOutputType getScriptPubKeyType() {
		return scriptPubKeyType;
	}

	/**
	 * Setter of scriptPubKeyType.
	 *
	 * @param newScriptPubKeyType the scriptPubKeyType to set
	 */
	public final void setScriptPubKeyType(final BitcoinTransactionOutputType newScriptPubKeyType) {
		scriptPubKeyType = newScriptPubKeyType;
	}

	/**
	 * Getter of addresses.
	 *
	 * @return addresses
	 */
	public final Set<String> getAddresses() {
		return addresses;
	}

	/**
	 * Setter of addresses.
	 *
	 * @param newAddresses the addresses to set
	 */
	public final void setAddresses(final Set<String> newAddresses) {
		addresses = newAddresses;
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
     * Getter.
     * @return transaction id
     */
    public final String getTxId() {
        return txId;
    }

    /**
     * Setter.
     * @param newTxId new tx id
     */
    public final void setTxId(final String newTxId) {
        this.txId = newTxId;
    }

    /**
	 * Getter of value.
	 *
	 * @return value
	 */
	public final float getValue() {
		return value;
	}

	/**
	 * Setter of value.
	 *
	 * @param newValue the value to set
	 */
	public final void setValue(final float newValue) {
		value = newValue;
	}

	/**
	 * Getter of n.
	 *
	 * @return n
	 */
	public final int getN() {
		return n;
	}

	/**
	 * Setter of n.
	 *
	 * @param newN the n to set
	 */
	public final void setN(final int newN) {
		n = newN;
	}

	/**
	 * Getter of scriptPubKeyAsm.
	 *
	 * @return scriptPubKeyAsm
	 */
	public final String getScriptPubKeyAsm() {
		return scriptPubKeyAsm;
	}

	/**
	 * Setter of scriptPubKeyAsm.
	 *
	 * @param newScriptPubKeyAsm the scriptPubKeyAsm to set
	 */
	public final void setScriptPubKeyAsm(final String newScriptPubKeyAsm) {
		scriptPubKeyAsm = newScriptPubKeyAsm;
	}

	/**
	 * Getter of scriptPubKeyHex.
	 *
	 * @return scriptPubKeyHex
	 */
	public final String getScriptPubKeyHex() {
		return scriptPubKeyHex;
	}

	/**
	 * Setter of scriptPubKeyHex.
	 *
	 * @param newScriptPubKeyHex the scriptPubKeyHex to set
	 */
	public final void setScriptPubKeyHex(final String newScriptPubKeyHex) {
		scriptPubKeyHex = newScriptPubKeyHex;
	}

    /**
     * Getter.
     *
     * @return key.
     */
    public final String getKey() {
        return key;
    }

    /**
     * Setter.
     * @param newKey key
     */
    public final void setKey(final String newKey) {
        this.key = newKey;
    }

    /**
     * Equals.
     * @param o object
     * @return true if same object
     */
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BitcoinTransactionOutput)) {
            return false;
        }

        BitcoinTransactionOutput that = (BitcoinTransactionOutput) o;

        return getN() == that.getN();
    }

    /**
     * Hash.
     * @return hash
     */
    @Override
    public final int hashCode() {
        return getN();
    }



}
