package com.oakinvest.b2g.repository.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * BitcoinBlock repository.
 * Created by straumat on 09/09/16.
 */
@Repository
public interface BitcoinBlockRepository extends GraphRepository<BitcoinBlock> {

	/**
	 * Count the number of block by state.
	 *
	 * @param state state
	 * @return number of block with this state
	 */
	@Query("MATCH (b:BitcoinBlock) WHERE b.state = {0} return count(*)")
	int countBlockByState(BitcoinBlockState state);

	/**
	 * Returns the first bitcoin block with the desired state.
	 *
	 * @param state state
	 * @return first block.
	 */
	@Query("MATCH (b:BitcoinBlock) where b.state = {0} RETURN b order by b.height limit 1")
	BitcoinBlock findFirstBlockByState(BitcoinBlockState state);

   /**
	 * Find a block by its height.
	 *
	 * @param height height
	 * @return block
	 */
	BitcoinBlock findByHeight(long height);

    /**
     * Find a block by its height.
     *
     * @param height height
     * @return block
     */
    @Query("MATCH (b:BitcoinBlock) WHERE b.height = {0} return b")
    BitcoinBlock findByHeightWithoutDepth(long height);

    /**
	 * Find a block by its hash.
	 *
	 * @param hash hash
	 * @return block
	 */
	BitcoinBlock findByHash(String hash);

    /**
     * Find a block by its hash.
     *
     * @param hash hash
     * @return block
     */
    @Query("MATCH (b:BitcoinBlock) WHERE b.hash= {0} return b")
    BitcoinBlock findByHashWithoutDepth(String hash);

}
