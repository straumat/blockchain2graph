package com.oakinvest.b2g.service.bitcoin;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;

import java.util.Optional;

/**
 * Bitcoin data service.
 * Created by straumat on 11/06/17.
 */
public interface BitcoinDataService {

    /**
     * Return getblockcount (The result stays in cache for 10 minutes).
     *
     * @return the number of blocks in the block chain.
     */
    Optional<Integer> getBlockCount();

    /**
     * Get block data from buffer.
     *
     * @param blockHeight block height
     * @return block data
     */
    Optional<BitcoindBlockData> getBlockData(int blockHeight);

    /**
     * Load block and transactions from a block in buffer.
     *
     * @param blockHeight block height
     */
    void putBlockInBuffer(int blockHeight);

    /**
     * Remove block and transactions from a block in buffer.
     *
     * @param blockHeight block height
     */
    void removeBlockInBuffer(int blockHeight);

}
