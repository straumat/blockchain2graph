package com.oakinvest.b2g.bitcoin.repository;

import com.oakinvest.b2g.bitcoin.domain.BitcoinTransactionInput;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * BitcoinTransactionInput repository.
 *
 * Created by straumat on 22/03/17.
 */
@Repository
public interface TransactionInputRepository extends Neo4jRepository<BitcoinTransactionInput, Long> {

}
