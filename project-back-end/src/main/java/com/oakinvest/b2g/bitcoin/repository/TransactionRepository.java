package com.oakinvest.b2g.bitcoin.repository;

import com.oakinvest.b2g.bitcoin.domain.BitcoinTransaction;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

/**
 * BitcoinTransaction repository.
 *
 * Created by straumat on 27/09/16.
 */
public interface TransactionRepository extends Neo4jRepository<BitcoinTransaction, Long> {

	/**
	 * Find a block by its id.
	 *
	 * @param txId transaction id
	 * @return transaction
	 */
    Optional<BitcoinTransaction> findByTxId(String txId);

}
