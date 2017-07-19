package com.oakinvest.b2g.service.bitcoin;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * Aspect adding a cache to BitcoinDataService.
 * Created by straumat on 30/06/17.
 */
@Configuration
@Aspect
public class BitcoinDataServiceCacheAspect {

    /**
     * Cache store.
     */
    private final BitcoinDataServiceCacheStore cacheStore;

    /**
     * Constructor.
     * @param newCacheStore cache store
     */
    public BitcoinDataServiceCacheAspect(final BitcoinDataServiceCacheStore newCacheStore) {
        this.cacheStore = newCacheStore;
    }

    /**
     * Cache for getBlockCount().
     * @param pjp object
     * @return block count
     * @throws Throwable exception
     */
    @SuppressWarnings("unchecked")
    @Around("execution(* com.oakinvest.b2g.service.BitcoinDataService.getBlockCount())")
    public final Optional<Long> getBlockCount(final ProceedingJoinPoint pjp) throws Throwable {
        // If the data is outdated.
        if (cacheStore.isBlockCountOutdated()) {
             Optional<Long> blockCount = ((Optional<Long>) pjp.proceed(new Object[]{}));
            if (blockCount.isPresent()) {
                cacheStore.updateBlockCount(blockCount.get());
                return Optional.of(blockCount.get());
            } else {
                return Optional.empty();
            }
        // If the data is still in cache.
        } else {
            if (cacheStore.getBlockCount() != -1) {
                return Optional.of(cacheStore.getBlockCount());
            } else {
                return Optional.empty();
            }
        }
    }

    /**
     * Cache for getBlockData().
     * @param pjp object
     * @param blockHeight block height
     * @return block data
     * @throws Throwable exception
     */
    @SuppressWarnings("unchecked")
    @Around("execution(* com.oakinvest.b2g.service.BitcoinDataService.getBlockData(..)) && args(blockHeight))")
    public final Optional<BitcoindBlockData> getBlockData(final ProceedingJoinPoint pjp, final long blockHeight) throws Throwable {

        // Returning the data.
        Optional<BitcoindBlockData> blockData = cacheStore.getBlockData(blockHeight);
        if (blockData.isPresent()) {
            // If the block is in the cache, we return it.
            return blockData;
        } else {
            // If it's not in the cache, we retrieve it.
            blockData = (Optional<BitcoindBlockData>) pjp.proceed(new Object[]{ blockHeight });
            // If we retrieve if for the first time, we let it in cache.
            blockData.ifPresent(cacheStore::addBlockData);
            return blockData;
        }
    }


}
