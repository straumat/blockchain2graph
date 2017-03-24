package com.oakinvest.b2g.domain.bitcoin;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a bitcoin address.
 * Created by straumat on 10/09/16.
 */
@NodeEntity(label = "BitcoinAddress")
public class BitcoinAddress {

	/**
	 * ID.
	 */
	@GraphId
	private Long id;

	/**
	 * Bitcoin address.
	 */
	@Property(name = "address")
	private String address;

	/**
	 * Input transactions.
	 */
	@Relationship(type = "IN_TRANSACTION_INPUTS")
	private Set<BitcoinTransactionInput> inputTransactions = new HashSet<>();

	/**
	 * Output transactions.
	 */
	@Relationship(type = "IN_TRANSACTION_OUTPUTS")
	private Set<BitcoinTransactionOutput> outputTransactions = new HashSet<>();

	/**
	 * Default constructor.
	 */
	public BitcoinAddress() {
	}

	/**
	 * Constructor.
	 *
	 * @param newAddress bitcoin newAddress
	 */
	public BitcoinAddress(final String newAddress) {
		this.address = newAddress;
	}

	/**
	 * Getter inputTransactions.
	 *
	 * @return inputTransactions
	 */
	public final Set<BitcoinTransactionInput> getInputTransactions() {
		return inputTransactions;
	}

	/**
	 * Setter of inputTransactions.
	 *
	 * @param newInputTransactions the inputTransactions to set
	 */
	@Relationship
	public final void setInputTransactions(final Set<BitcoinTransactionInput> newInputTransactions) {
		inputTransactions = newInputTransactions;
	}

	/**
	 * Getter outputTransactions.
	 *
	 * @return outputTransactions
	 */
	public final Set<BitcoinTransactionOutput> getOutputTransactions() {
		return outputTransactions;
	}

	/**
	 * Setter of outputTransactions.
	 *
	 * @param newOutputTransactions the outputTransactions to set
	 */
	@Relationship
	public final void setOutputTransactions(final Set<BitcoinTransactionOutput> newOutputTransactions) {
		outputTransactions = newOutputTransactions;
	}

	/**
	 * Getter of inputTransactions.
	 *
	 * @return inputTransactions
	 */
	public final Set<BitcoinTransactionInput> getWithdrawals() {
		return inputTransactions;
	}

	/**
	 * Getter of outputTransactions.
	 *
	 * @return outputTransactions
	 */
	public final Set<BitcoinTransactionOutput> getDeposits() {
		return outputTransactions;
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
	 * Getter of address.
	 *
	 * @return address
	 */
	public final String getAddress() {
		return address;
	}

	/**
	 * Setter of address.
	 *
	 * @param newAddress the address to set
	 */
	public final void setAddress(final String newAddress) {
		address = newAddress;
	}

	/**
	 * Using bitcoin address.
	 *
	 * @param o object
	 * @return true if equals
	 */
	@Override
	public final boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BitcoinAddress)) {
			return false;
		}

		final BitcoinAddress that = (BitcoinAddress) o;

		return getAddress().equals(that.getAddress());
	}

	/**
	 * Using bitcoin address.
	 *
	 * @return hash
	 */
	@Override
	public final int hashCode() {
		return getAddress().hashCode();
	}

}
