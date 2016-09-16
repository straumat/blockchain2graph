package com.oakinvest.b2g.repository.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Bitcoinblock repository.
 * Created by straumat on 09/09/16.
 */
@Repository
public interface BitcoinBlockRepository extends GraphRepository<BitcoinBlock> {

	/**
	 * Retuns the number of blocks.
	 *
	 * @return count
	 */
	long count();

	/**
	 * Find a block by its hash.
	 *
	 * @param hash hash
	 * @return corresponding block
	 */
	BitcoinBlock findByHash(final String hash);

}
