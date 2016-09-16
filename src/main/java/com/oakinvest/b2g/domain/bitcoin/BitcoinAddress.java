package com.oakinvest.b2g.domain.bitcoin;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

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
	 * Constructor.
	 *
	 * @param newAddress bitcoin newAddress
	 */
	public BitcoinAddress(final String newAddress) {
		this.address = newAddress;
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
