package com.oakinvest.b2g.bitcoin.domain;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

/**
 * Bitcoin transaction input.
 * Created by straumat on 22/09/16.
 */
@SuppressWarnings("unused")
@NodeEntity(label = "BitcoinTransactionInput")
public class BitcoinTransactionInput {

	/**
	 * ID.
	 */
    @Id
    @GeneratedValue
	private Long id;

	/**
	 * Transaction output.
	 */
	@Relationship(type = "TRANSACTION_OUTPUT", direction = Relationship.INCOMING)
	private BitcoinTransactionOutput transactionOutput;

	/**
	 * The transaction id (same as provided).
	 */
	@Property(name = "txid")
	private String txId;

	/**
	 * Coinbase.
	 */
	@Property(name = "coinbase")
	private String coinbase;

	/**
	 * vOut.
	 */
	@Property(name = "vout")
	private int vOut;

	/**
	 * The script asm.
	 */
	@Property(name = "scriptSigAsm")
	private String scriptSigAsm;

	/**
	 * The script hex.
	 */
	@Property(name = "scriptSigHex")
	private String scriptSigHex;

	/**
	 * The script sequence number.
	 */
	@Property(name = "sequence")
	private long sequence;

    /**
     * Address.
     */
    @Relationship(type = "ADDRESS", direction = Relationship.INCOMING)
    private BitcoinAddress bitcoinAddress;

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
	 * Getter of transactionOutput.
	 *
	 * @return transactionOutput
	 */
	public final BitcoinTransactionOutput getTransactionOutput() {
		return transactionOutput;
	}

	/**
	 * Setter of transactionOutput.
	 *
	 * @param newTransactionOutput the transactionOutput to set
	 */
	public final void setTransactionOutput(final BitcoinTransactionOutput newTransactionOutput) {
		transactionOutput = newTransactionOutput;
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
	 * Getter of coinbase.
	 *
	 * @return coinbase
	 */
	public final String getCoinbase() {
		return coinbase;
	}

	/**
	 * Returns true if it's a coinbase transaction.
	 *
	 * @return coinbase.
	 */
	public final boolean isCoinbase() {
		return getCoinbase() != null;
	}

	/**
	 * Setter of coinbase.
	 *
	 * @param newCoinbase the coinbase to set
	 */
	public final void setCoinbase(final String newCoinbase) {
		coinbase = newCoinbase;
	}

	/**
	 * Getter of vOut.
	 *
	 * @return vOut
	 */
	public final int getvOut() {
		return vOut;
	}

	/**
	 * Setter of vOut.
	 *
	 * @param newVout the vOut to set
	 */
	public final void setvOut(final int newVout) {
		vOut = newVout;
	}

	/**
	 * Getter of scriptSigAsm.
	 *
	 * @return scriptSigAsm
	 */
	public final String getScriptSigAsm() {
		return scriptSigAsm;
	}

	/**
	 * Setter of scriptSigAsm.
	 *
	 * @param newScriptSigAam the scriptSigAsm to set
	 */
	public final void setScriptSigAsm(final String newScriptSigAam) {
		scriptSigAsm = newScriptSigAam;
	}

	/**
	 * Getter of scriptSigHex.
	 *
	 * @return scriptSigHex
	 */
	public final String getScriptSigHex() {
		return scriptSigHex;
	}

	/**
	 * Setter of scriptSigHex.
	 *
	 * @param newScriptSigHex the scriptSigHex to set
	 */
	public final void setScriptSigHex(final String newScriptSigHex) {
		scriptSigHex = newScriptSigHex;
	}

	/**
	 * Getter of sequence.
	 *
	 * @return sequence
	 */
	public final long getSequence() {
		return sequence;
	}

	/**
	 * Setter of sequence.
	 *
	 * @param newSequence the sequence to set
	 */
	public final void setSequence(final long newSequence) {
		sequence = newSequence;
	}

    @Override
    public final String toString() {
        if (getTxId() == null) {
            return "Coinbase";
        } else {
            return getTxId() + "-" + getvOut();
        }
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
        if (!(o instanceof BitcoinTransactionInput)) {
            return false;
        }

        // Converting.
        BitcoinTransactionInput that = (BitcoinTransactionInput) o;

        if (isCoinbase()) {
            // If it's a coinbase.
            return that.getTxId() == null && getCoinbase().equals(that.getCoinbase());
        } else {
            // if it's a transaction
            return (getTxId().equals(that.getTxId()) && (getvOut() == that.getvOut()));
        }
    }

    /**
     * Hash.
     * @return hash
     */
    @Override
    public final int hashCode() {
        if (isCoinbase()) {
            return getCoinbase().hashCode();
        } else {
            return (getTxId() + getvOut()).hashCode();
        }
    }

}
