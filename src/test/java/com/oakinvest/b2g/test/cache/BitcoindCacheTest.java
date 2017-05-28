package com.oakinvest.b2g.test.cache;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.batch.bitcoin.cache.BitcoindCacheLoader;
import com.oakinvest.b2g.batch.bitcoin.step1.blocks.BitcoinBatchBlocks;
import com.oakinvest.b2g.batch.bitcoin.step2.addresses.BitcoinBatchAddresses;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for bitcoind cache.
 * Created by straumat on 27/05/17.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class BitcoindCacheTest {

    /**
     * Cache manager.
     */
    @Autowired
    private CacheManager cacheManager;

    /**
     * Import batch.
     */
    @Autowired
    private BitcoinBatchBlocks batchBlocks;

    /**
     * Cache loader.
     */
    @Autowired
    private BitcoindCacheLoader cacheLoader;

    /**
     * Bitcoin block repository.
     */
    @Autowired
    private BitcoinBlockRepository bitcoinBlockRepository;

    /**
     * Testing cache is working.
      */
    @Test
    public final void cacheTest() {
        long lastBlockSaved = bitcoinBlockRepository.count();
        long blockInCache = lastBlockSaved + 100 + 1;

        assertThat(cacheManager.getCache("blockData").get(blockInCache))
                .as("Checking that the cache is empty")
                .isNull();
        batchBlocks.execute();
        assertThat(cacheManager.getCache("blockData").get(blockInCache))
                .as("Checking that the cache is empty")
                .isNull();
        cacheLoader.updateBuffer();
        assertThat(cacheManager.getCache("blockData").get(blockInCache))
                .as("Checking that the cache is NOT empty")
                .isNotNull();
        assertThat(cacheManager.getCache("blockData").get(blockInCache-10))
                .as("Checking that the cache is NOT empty")
                .isNotNull();
        assertThat(cacheManager.getCache("blockData").get(blockInCache+10))
                .as("Checking that the cache is NOT empty")
                .isNotNull();
    }

}
