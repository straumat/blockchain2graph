package com.oakinvest.b2g.repository.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * BitcoinTransactionInput repository.
 * Created by straumat on 22/03/17.
 */
@Repository
public interface BitcoinTransactionInputRepository extends Neo4jRepository<BitcoinTransactionInput, Long> {

}
