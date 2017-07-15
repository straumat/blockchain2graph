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
     * @param key transaction id = index
     * @return transaction
     */
    @Query("MATCH (n:BitcoinTransactionOutput) USING INDEX n:BitcoinTransactionOutput(key) WHERE n.key = {0} RETURN n")
    BitcoinTransactionOutput findByKey(String key);

}
