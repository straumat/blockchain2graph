package com.oakinvest.b2g.util.bitcoin.buffer;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * Aspect adding a buffer and a cache to BitcoinDataService.
 * Created by straumat on 30/06/17.
 */
@Configuration
@Aspect
public class BitcoinDataServiceBufferAspect {

    /**
     * Buffer.
     */
    private final BitcoinDataServiceBufferStore buffer;

    /**
     * Bitcoind cache loader.
     */
    private final BitcoinDataServiceBufferLoader bufferLoader;

    /**
     * Constructor.
     * @param newBufferStore buffer store
     * @param newBufferLoader buffer loader
     */
    public BitcoinDataServiceBufferAspect(final BitcoinDataServiceBufferStore newBufferStore, final BitcoinDataServiceBufferLoader newBufferLoader) {
        this.buffer = newBufferStore;
        this.bufferLoader = newBufferLoader;
    }

    /**
     * Cache for getBlockCountFromCache().
     * @param pjp object
     * @return block count
     * @throws Throwable exception
     */
    @SuppressWarnings("unchecked")
    @Around("execution(* com.oakinvest.b2g.service.bitcoin.BitcoinDataService.getBlockCount())")
    public final Optional<Integer> getBlockCount(final ProceedingJoinPoint pjp) throws Throwable {
        if (buffer.isBlockCountOutdated()) {
            // If the data is outdated.
            Optional<Integer> blockCount = ((Optional<Integer>) pjp.proceed(new Object[]{}));
            if (blockCount.isPresent()) {
                buffer.updateBlockCountInCache(blockCount.get());
                return Optional.of(blockCount.get());
            } else {
                return Optional.empty();
            }
        } else {
            // If the data is still in cache.
            if (buffer.getBlockCountFromCache() != -1) {
                return Optional.of(buffer.getBlockCountFromCache());
            } else {
                return Optional.empty();
            }
        }
    }

    /**
     * Buffer for getBlockData().
     * @param pjp object
     * @param blockHeight block height
     * @return block data
     * @throws Throwable exception
     */
    @SuppressWarnings("unchecked")
    @Around("execution(* com.oakinvest.b2g.service.bitcoin.BitcoinDataService.getBlockData(..)) && args(blockHeight))")
    public final Optional<BitcoindBlockData> getBlockData(final ProceedingJoinPoint pjp, final int blockHeight) throws Throwable {
        // Loading cache.
        bufferLoader.loadBuffer(blockHeight);

        // Returning the data.
        return (Optional<BitcoindBlockData>) pjp.proceed(new Object[]{ blockHeight });
    }

}
