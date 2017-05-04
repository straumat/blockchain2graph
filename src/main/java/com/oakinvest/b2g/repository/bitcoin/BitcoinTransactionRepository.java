package com.oakinvest.b2g.repository.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * BitcoinTransaction repository.
 * Created by straumat on 27/09/16.
 */
public interface BitcoinTransactionRepository extends GraphRepository<BitcoinTransaction> {

	/**
	 * Returns 1 if the address is already in the database.
	 *
	 * @param txId transaction id
	 * @return 1 if the transaction exists.
	 */
	@Query("MATCH (t:BitcoinTransaction) WHERE t.txid = {0} return count(*) = 1")
	boolean exists(String txId);

	/**
	 * Find a block by its id.
	 *
	 * @param txId transaction id
	 * @return transaction
	 */
	BitcoinTransaction findByTxId(String txId);

}
