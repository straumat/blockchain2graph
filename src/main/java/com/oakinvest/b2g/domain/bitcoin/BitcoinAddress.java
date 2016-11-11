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
	private Set<BitcoinTransactionInput> inputTransactions = new HashSet<BitcoinTransactionInput>();

	/**
	 * Output transactions.
	 */
	@Relationship(type = "IN_TRANSACTION_OUTPUTS")
	private Set<BitcoinTransactionOutput> outputTransactions = new HashSet<BitcoinTransactionOutput>();

	/**
	 * Defaut constructor.
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
	 * Getter de la propriété inputTransactions.
	 *
	 * @return inputTransactions
	 */
	public final Set<BitcoinTransactionInput> getWithdrawals() {
		return inputTransactions;
	}

	/**
	 * Setter de la propriété inputTransactions.
	 *
	 * @param newInputTransactions the inputTransactions to set
	 */
	public final void setInputTransactions(final Set<BitcoinTransactionInput> newInputTransactions) {
		inputTransactions = newInputTransactions;
	}

	/**
	 * Getter de la propriété outputTransactions.
	 *
	 * @return outputTransactions
	 */
	public final Set<BitcoinTransactionOutput> getDeposits() {
		return outputTransactions;
	}

	/**
	 * Setter de la propriété outputTransactions.
	 *
	 * @param newOutputTransactions the outputTransactions to set
	 */
	public final void setOutputTransactions(final Set<BitcoinTransactionOutput> newOutputTransactions) {
		outputTransactions = newOutputTransactions;
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
	 * Getter de la propriété address.
	 *
	 * @return address
	 */
	public final String getAddress() {
		return address;
	}

	/**
	 * Setter de la propriété address.
	 *
	 * @param newAddress the address to set
	 */
	public final void setAddress(final String newAddress) {
		address = newAddress;
	}

}
