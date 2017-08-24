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
     * Transaction count.
     *
     * @param txId tx id
     * @return number of transaction
     */
    @Query("MATCH (t:BitcoinTransaction) WHERE t.txid = {0} return count(*)")
    int transactionCount(String txId);

	/**
	 * Find a block by its id.
	 *
	 * @param txId transaction id
	 * @return transaction
	 */
    @Query("MATCH (n:BitcoinTransaction) USING INDEX n:BitcoinTransaction(txid) WHERE n.txid = {0} WITH n MATCH p=(n)-[*0..1]-(m) RETURN p, ID(n)")
    BitcoinTransaction findByTxId(String txId);

}
