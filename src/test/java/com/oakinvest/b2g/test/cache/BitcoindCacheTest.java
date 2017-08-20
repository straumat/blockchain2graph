package com.oakinvest.b2g.test.cache;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.batch.bitcoin.BitcoinBatchBlocks;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.bitcoin.BitcoinDataServiceCacheStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static com.oakinvest.b2g.configuration.ParametersConfiguration.BITCOIND_BUFFER_SIZE;
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
     * Spring context.
     */
    @Autowired
    private ApplicationContext ctx;

    /**
     * Cache store.
     */
    @Autowired
    private BitcoinDataServiceCacheStore cacheStore;

    /**
     * Import batch.
     */
    @Autowired
    private BitcoinBatchBlocks batchBlocks;

    /**
     * Bitcoin block repository.
     */
    @Autowired
    private BitcoinBlockRepository bitcoinBlockRepository;

    @Before
    public void setUp() throws Exception {
        Map<String, GraphRepository> graphRepositories = ctx.getBeansOfType(GraphRepository.class);
        for (GraphRepository graphRepository : graphRepositories.values()) {
            graphRepository.deleteAll();
        }
        cacheStore.clear();
    }

    /**
     * Testing cache is working on getBlockDataFromCache().
      */
    @Test
    public final void getBlockDataCacheTest() throws InterruptedException {
        // We import 101 blocks.
        while (bitcoinBlockRepository.count() < BITCOIND_BUFFER_SIZE + 1) {
            batchBlocks.execute();
        }
        Thread.sleep(60000);

        // So we must have 101 block in the database.
        long blockCount = bitcoinBlockRepository.findByHeight(bitcoinBlockRepository.count()).getHeight();
        assertThat(blockCount)
                .as("Checking that lastBlockSaved is database has an height of 101")
                .isEqualTo(101);

        // Checking that the next block to read 102 is in cache.
        assertThat(cacheStore.isBlockDataInCache(blockCount + 1))
                .as("Checking that the block %s is in cache", blockCount + 1)
                .isTrue();
        // Checking that the block 201 (lastBlockSaved + BUFFER_SIZE) is in cache   .
        assertThat(cacheStore.isBlockDataInCache(blockCount + BITCOIND_BUFFER_SIZE))
                .as("Checking that the block %s is in cache", blockCount + BITCOIND_BUFFER_SIZE)
                .isTrue();
        // Checking that the block 202 (lastBlockSaved + BUFFER_SIZE + 1) is NOT in cache.
        assertThat(cacheStore.isBlockDataInCache(blockCount + BITCOIND_BUFFER_SIZE + 1))
                .as("Checking that the block %s is not in cache", blockCount + BITCOIND_BUFFER_SIZE + 1)
                .isFalse();

        // We import 100 (BUFFER_SIZE) blocks.
        while (bitcoinBlockRepository.count() < blockCount + BITCOIND_BUFFER_SIZE) {
            batchBlocks.execute();
        }
        Thread.sleep(60000);

        // So we must have 201 block in the database.
        assertThat(bitcoinBlockRepository.findByHeight(bitcoinBlockRepository.count()).getHeight())
                .as("Checking that the last block in database is 201")
                .isEqualTo(201);

        // We check that 202 is waiting in the buffer.
        assertThat(cacheStore.isBlockDataInCache(blockCount + BITCOIND_BUFFER_SIZE + 1))
                .as("Checking that the block %s is in cache", blockCount + BITCOIND_BUFFER_SIZE + 1)
                .isTrue();

        // We check that the block 100 is no more in cache.
        assertThat(cacheStore.isBlockDataInCache(BITCOIND_BUFFER_SIZE))
                .as("Checking that the block %s is in cache", BITCOIND_BUFFER_SIZE)
                .isFalse();
    }

}
