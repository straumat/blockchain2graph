package com.oakinvest.b2g.repository;

import com.oakinvest.b2g.domain.BitcoinAddress;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * BitcoinAddress repository.
 *
 * Created by straumat on 10/09/16.
 */
@Repository
public interface AddressRepository extends Neo4jRepository<BitcoinAddress, Long> {

    /**
     * Find an address.
     *
     * @param address address
     * @return address
     */
    Optional<BitcoinAddress> findByAddress(String address);

    /**
     * Find a bitcoin address (with depth 0).
     *
     * @param address address
     * @return bitcoin address
     */
    @Query("MATCH (a:BitcoinAddress) USING INDEX a:BitcoinAddress(address) WHERE a.address = $0 return a")
    Optional<BitcoinAddress> findByAddressWithoutDepth(String address);

}
