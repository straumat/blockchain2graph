package com.oakinvest.b2g.test.cache;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.batch.bitcoin.step1.blocks.BitcoinBatchBlocks;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.service.BitcoindService;
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
     * Bitcoin data service.
     */
    @Autowired
    private BitcoinDataService bitcoinDataService;

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
    }

    /**
     * Testing cache is working on getBlockData().
      */
    @Test
    public final void getBlockDataCacheTest() throws InterruptedException {
        // We add 101 blocks. The last block loaded is 10.
        while (bitcoinBlockRepository.count() < BITCOIND_BUFFER_SIZE + 1) {
            batchBlocks.execute();
        }
        Thread.sleep(60000);

        // lastBlockSaved is database has an height of 101.
        long lastBlockSaved = bitcoinBlockRepository.findByHeight(bitcoinBlockRepository.count()).getHeight();
        assertThat(lastBlockSaved)
                .as("Checking that lastBlockSaved is database has an height of 101")
                .isEqualTo(101);

        // Checking that the next block to read 102 is in cache.
        assertThat(bitcoinDataService.getBuffer().stream().anyMatch(b -> b.getBlock().getHeight() == lastBlockSaved + 1))
                .as("Checking that the block %s is in cache", lastBlockSaved + 1)
                .isTrue();
        // Checking that the block 201 (lastBlockSaved + BUFFER_SIZE) is in cache thanks to the cache loader.
        assertThat(bitcoinDataService.getBuffer().stream().anyMatch(b -> b.getBlock().getHeight() == lastBlockSaved + BITCOIND_BUFFER_SIZE))
                .as("Checking that the block %s is in cache", lastBlockSaved + BITCOIND_BUFFER_SIZE)
                .isTrue();
        // Checking that the block 202 (lastBlockSaved + BUFFER_SIZE + 1) is NOT in cache.
        assertThat(bitcoinDataService.getBuffer().stream().anyMatch(b -> b.getBlock().getHeight() == lastBlockSaved + BITCOIND_BUFFER_SIZE + 1))
                .as("Checking that the block %s is not in cache", lastBlockSaved + BITCOIND_BUFFER_SIZE + 1)
                .isFalse();

        // We add 100 (BUFFER_SIZE) blocks. The last block in database is 201.
        while (bitcoinBlockRepository.count() < lastBlockSaved + BITCOIND_BUFFER_SIZE) {
            batchBlocks.execute();
        }
        assertThat(bitcoinBlockRepository.findByHeight(bitcoinBlockRepository.count()).getHeight())
                .as("Checking that the last block in database is 201")
                .isEqualTo(201);
        Thread.sleep(60000);

        // We check that 202 is waiting in the buffer
        // Checking that the block 202 (lastBlockSaved + BUFFER_SIZE + 1) is NOT in cache.
        assertThat(bitcoinDataService.getBuffer().stream().anyMatch(b -> b.getBlock().getHeight() == lastBlockSaved + BITCOIND_BUFFER_SIZE + 1))
                .as("Checking that the block %s is in cache", lastBlockSaved + BITCOIND_BUFFER_SIZE + 1)
                .isTrue();

        // In fact, all blocks from 202 to 302 should be in cache.
        // We check until 299 because of mock.
        for (long i = lastBlockSaved + BITCOIND_BUFFER_SIZE + 1;i <= 299;i++) {
            final long j = i;
            assertThat(bitcoinDataService.getBuffer().stream().anyMatch(b -> b.getBlock().getHeight() == j))
                    .as("Checking that the block %s is in cache", i)
                    .isTrue();
        }

        // We check that the block 101 should not be anymore in cache.
        assertThat(bitcoinDataService.getBuffer().stream().anyMatch(b -> b.getBlock().getHeight() == lastBlockSaved))
                .as("Checking that the block %s is NO MORE in cache", lastBlockSaved)
                .isFalse();

        // We check the size.
        assertThat(bitcoinDataService.getBuffer().size())
                .as("Checking that the buffer is equals to BUFFER_SIZE (" + BITCOIND_BUFFER_SIZE+ ")")
                .isEqualTo(BITCOIND_BUFFER_SIZE);
    }

}
