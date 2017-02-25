package com.oakinvest.b2g.repository.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Bitcoinblock repository.
 * Created by straumat on 09/09/16.
 */
@Repository
public interface BitcoinBlockRepository extends GraphRepository<BitcoinBlock> {

	/**
	 * Returns the number of blocks.
	 *
	 * @return count
	 */
	long count();

	/**
	 * Count the number of blocks imported.
	 *
	 * @return number of blocks completely imported
	 */
	@Query("MATCH (b:BitcoinBlock) WHERE b.imported = true return count(*)")
	int countImported();

	/**
	 * Find a block by its height.
	 *
	 * @param height height
	 * @return block
	 */
	BitcoinBlock findByHeight(long height);

	/**
	 * Find a block by its hash.
	 *
	 * @param hash hash
	 * @return block
	 */
	BitcoinBlock findByHash(String hash);

	/**
	 * Find the first block without addresses imported.
	 *
	 * @return block
	 */
	@Query("MATCH (b:BitcoinBlock) WHERE b.addressesImported = false RETURN b order by b.height limit 1")
	BitcoinBlock findFirstBlockWithoutAddresses();

	/**
	 * Find the first block without transactions imported.
	 *
	 * @return block
	 */
	@Query("MATCH (b:BitcoinBlock) WHERE b.addressesImported = true and b.transactionsImported = false RETURN b order by b.height limit 1")
	BitcoinBlock findFirstBlockWithoutTransactions();

	/**
	 * Find the first block without relations imported.
	 *
	 * @return block
	 */
	@Query("MATCH (b:BitcoinBlock) WHERE b.addressesImported = true and b.transactionsImported = true and b.relationsImported = false RETURN b order by b.height limit 1")
	BitcoinBlock findFirstBlockWithoutRelations();

}
