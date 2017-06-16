package com.oakinvest.b2g.service;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Bitcoin data service.
 * Created by straumat on 11/06/17.
 */
public interface BitcoinDataService {

    /**
     * Return getblockcount (The result stays in cache for 10 minutes).
     *
     * @return the number of blocks in the block chain & -1 if error.
     */
    long getBlockCount();

    /**
     * Get block data from buffer.
     *
     * @param blockHeight block height
     * @return block data
     */
    Optional<BitcoindBlockData> getBlockData(long blockHeight);

    /**
     * Get the buffer.
     *
     * @return buffer
     */
    ConcurrentSkipListSet<BitcoindBlockData> getBuffer();

}
