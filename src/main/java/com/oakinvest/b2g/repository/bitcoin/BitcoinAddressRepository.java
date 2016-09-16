package com.oakinvest.b2g.repository.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Bitcoin address repository.
 * Created by straumat on 10/09/16.
 */
@Repository
public interface BitcoinAddressRepository extends GraphRepository<BitcoinAddress> {

	/**
	 * Retuns the number of blocks.
	 *
	 * @return count
	 */
	long count();

	/**
	 * Find an address.
	 *
	 * @param address address
	 * @return address
	 */
	BitcoinAddress findByAddress(final String address);

}
