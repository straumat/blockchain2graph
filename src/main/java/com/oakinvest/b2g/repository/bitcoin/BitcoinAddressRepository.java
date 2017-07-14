package com.oakinvest.b2g.repository.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * BitcoinAddress repository.
 * Created by straumat on 10/09/16.
 */
@Repository
public interface BitcoinAddressRepository extends GraphRepository<BitcoinAddress> {

	/**
	 * Returns 1 if the address is already in the database.
	 *
	 * @param address address
	 * @return 1 if the address exists.
	 */
	@Query("MATCH (a:BitcoinAddress) WHERE a.address = {0} return count(*) = 1")
	boolean exists(String address);

    /**
     * Find an address.
     *
     * @param address address
     * @return address
     */
    BitcoinAddress findByAddress(String address);

    /**
     * Find a bitcoin address (with depth 0).
     *
     * @param address address
     * @return bitcoin address
     */
    @Query("MATCH (a:BitcoinAddress) USING INDEX a:BitcoinAddress(address) WHERE a.address = {0} return a")
	BitcoinAddress findByAddressWithoutDepth(String address);

}
