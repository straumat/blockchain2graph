package com.oakinvest.b2g.batch.bitcoin.cache;

import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * Bitcoind cache loader.
 * Created by straumat on 27/05/17.
 */
@Component
public class BitcoindCacheLoader {

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(BitcoindCacheLoader.class);

    /**
     * Buffer size.
     */
    private static final int BUFFER_SIZE = 100;

    /**
     * Cache load range.
     */
    private static final int CACHE_LOAD_RANGE = 10;

    /**
     * Bitcoin block repository.
     */
    private final BitcoinBlockRepository blockRepository;

    /**
     * Bitcoind service.
     */
    private final BitcoindService bitcoindService;

    /**
     * Constructor.
     * @param newBlockRepository block repository
     * @param newBitcoindService bitcoind service
     */
    public BitcoindCacheLoader(final BitcoinBlockRepository newBlockRepository, final BitcoindService newBitcoindService) {
        this.blockRepository = newBlockRepository;
        this.bitcoindService = newBitcoindService;
    }

    /**
     * Update buffer.
     */
    @Scheduled(fixedDelay = 1)
    @SuppressWarnings("checkstyle:designforextension")
    public void updateBuffer() {
        try {
            long blockToCache = blockRepository.count() + BUFFER_SIZE;
            for (long i = blockToCache - CACHE_LOAD_RANGE; i <= blockToCache + CACHE_LOAD_RANGE; i++) {
                bitcoindService.getBlockData(i);
            }
        } catch (Exception e) {
            log.error("Error in updateBuffer " + e.getMessage(), e);
        }
    }

}
