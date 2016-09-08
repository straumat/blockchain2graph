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
	 * Block number.
	 */
	@Property(name = "index")
	private long index;

}
