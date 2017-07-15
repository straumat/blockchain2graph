package com.oakinvest.b2g.repository.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * BitcoinTransactionOutput repository.
 * Created by straumat on 22/03/17.
 */
@Repository
public interface BitcoinTransactionOutputRepository extends GraphRepository<BitcoinTransactionOutput> {

    /**
     * Find a block by its id.
     *
     * @param txId transaction id
     * @param index index
     * @return transaction
     */
    @Query("MATCH (to:BitcoinTransactionOutput) USING INDEX to:BitcoinTransactionOutput(txid, n) WHERE to.txid = {0} and to.n = {1} RETURN to")
    BitcoinTransactionOutput findByTxIdAndIndex(String txId, int index);

}
