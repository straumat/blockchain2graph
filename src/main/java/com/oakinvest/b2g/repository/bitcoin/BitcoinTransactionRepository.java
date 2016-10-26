package com.oakinvest.b2g.repository.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * Bitcoin transaction.
 * Created by straumat on 27/09/16.
 */
public interface BitcoinTransactionRepository extends GraphRepository<BitcoinTransaction> {

	/**
	 * Find a block by its id.
	 *
	 * @param txid transaction id
	 * @return transaction
	 */
	BitcoinTransaction findByTxid(String txid);

}
