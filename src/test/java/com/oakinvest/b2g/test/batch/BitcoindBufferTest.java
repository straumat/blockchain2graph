package com.oakinvest.b2g.test.batch;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.batch.bitcoin.BitcoinBatchBlocks;
import com.oakinvest.b2g.batch.bitcoin.BitcoindBufferLoader;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.service.bitcoin.BitcoinDataService;
import com.oakinvest.b2g.util.bitcoin.buffer.BitcoinDataServiceBuffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for bitcoind buffer.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class BitcoindBufferTest {

    /**
     * Pause between calls to bitcoindService.
     */
    private static final int PAUSE_BETWEEN_CALLS = 5000;

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
     * Buffer store.
     */
    @Autowired
    private BitcoinDataServiceBuffer buffer;

    /**
     * Buffer loader.
     */
    @Autowired
    private BitcoindBufferLoader bufferLoader;

    /**
     * Session factory.
     */
    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Buffer size.
     */
    @Value("${bitcoind.buffer.size}")
    private int bufferSize;

    /**
     * Setup.
     *
     * @throws Exception exception.
     */
    @Before
    public void setUp() throws Exception {
        buffer.clear();
    }

    /**
     * Buffer test.
     */
    @Test
    public final void bufferTest() throws InterruptedException {
        // Purge database.
        sessionFactory.openSession().purgeDatabase();

        // Block 1.
        final int testBlock1 = 1;
        final String transaction1InBlock1 = "0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098";
        // Block 2.
        final int testBlock2 = 2;
        final String transaction1InBlock2 = "9b0fc92260312ce44e74ef369f5c66bbb85848f2eddd5a7a1cde251e54ccfdd5";
        // Block 3.
        final int testBlock3 = 3;
        final String transaction1InBlock3 = "999e1c837c76a1b7fbb7e57baf87b309960f5ffefbf2a9b95dd890602272f644";
        // Block 4.
        final int testBlock4 = 4;
        final String transaction1InBlock4 = "df2b060fa2e5e9c8ed5eaf6a45c13753ec8c63282b2688322eba40cd98ea067a";
        // Block 5.
        final int testBlock5 = 5;
        final String transaction1InBlock5 = "63522845d294ee9b0188ae5cac91bf389a0c3723f084ca1025e7d9cdfe481ce1";
        // Block 6.
        final int testBlock6 = 6;
        final String transaction1InBlock6 = "20251a76e64e920e58291a30d4b212939aae976baca40e70818ceaa596fb9d37";
        // Block 7.
        final int testBlock7 = 7;
        final String transaction1InBlock7 = "8aa673bc752f2851fd645d6a0a92917e967083007d9c1684f9423b100540673f";
        // Block 8.
        final int testBlock8 = 8;
        final String transaction1InBlock8 = "a6f7f1c0dad0f2eb6b13c4f33de664b1b0e9f22efad5994a6d5b6086d85e85e3";
        // Block 9.
        final int testBlock9 = 9;
        final String transaction1InBlock9 = "0437cd7f8525ceed2324359c2d0ba26006d92d856a9c20fa0241106ee5a597c9";

        // -------------------------------------------------------------------------------------------------------------
        // We import the block 1.
        batchBlocks.execute();
        bufferLoader.loadInBuffer();
        Thread.sleep(PAUSE_BETWEEN_CALLS);
        // We check the data in buffer.
        assertThat(buffer.getBlockInBuffer(testBlock1).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock1).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock2).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock2).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock3).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock3).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock4).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock5).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock5).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock6).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock6).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock7).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock7).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock8).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock8).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock9).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock9).isPresent()).isFalse();

        // -------------------------------------------------------------------------------------------------------------
        // We import the block 2.
        batchBlocks.execute();
        bufferLoader.loadInBuffer();
        Thread.sleep(PAUSE_BETWEEN_CALLS);
        // We check the data in buffer.
        assertThat(buffer.getBlockInBuffer(testBlock1).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock1).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock2).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock2).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock3).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock3).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock4).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock5).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock5).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock6).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock6).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock7).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock7).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock8).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock8).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock9).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock9).isPresent()).isFalse();

        // -------------------------------------------------------------------------------------------------------------
        // We import the block 3.
        batchBlocks.execute();
        bufferLoader.loadInBuffer();
        Thread.sleep(PAUSE_BETWEEN_CALLS);
        // We check the data in buffer.
        assertThat(buffer.getBlockInBuffer(testBlock1).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock1).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock2).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock2).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock3).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock3).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock4).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock5).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock5).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock6).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock6).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock7).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock7).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock8).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock8).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock9).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock9).isPresent()).isFalse();

        // -------------------------------------------------------------------------------------------------------------
        // We import the block 4.
        batchBlocks.execute();
        bufferLoader.loadInBuffer();
        Thread.sleep(PAUSE_BETWEEN_CALLS);
        // We check the data in buffer.
        assertThat(buffer.getBlockInBuffer(testBlock1).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock1).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock2).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock2).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock3).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock3).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock4).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock5).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock5).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock6).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock6).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock7).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock7).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock8).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock8).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock9).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock9).isPresent()).isFalse();

        // -------------------------------------------------------------------------------------------------------------
        // We import the block 5.
        batchBlocks.execute();
        bufferLoader.loadInBuffer();
        Thread.sleep(PAUSE_BETWEEN_CALLS);
        // We check the data in buffer.
        assertThat(buffer.getBlockInBuffer(testBlock1).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock1).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock2).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock2).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock3).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock3).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock4).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock5).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock5).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock6).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock6).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock7).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock7).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock8).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock8).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock9).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock9).isPresent()).isFalse();

        // -------------------------------------------------------------------------------------------------------------
        // We import the block 6.
        batchBlocks.execute();
        bufferLoader.loadInBuffer();
        Thread.sleep(PAUSE_BETWEEN_CALLS);
        // We check the data in buffer.
        assertThat(buffer.getBlockInBuffer(testBlock1).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock1).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock2).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock2).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock3).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock3).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock4).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock5).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock5).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock6).isPresent()).isFalse();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock6).isPresent()).isFalse();
        assertThat(buffer.getBlockInBuffer(testBlock7).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock7).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock8).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock8).isPresent()).isTrue();
        assertThat(buffer.getBlockInBuffer(testBlock9).isPresent()).isTrue();
        assertThat(buffer.getTransactionInBuffer(transaction1InBlock9).isPresent()).isTrue();
    }

    /**
     * Transaction missing in buffer.
     */
    @Test
    public void transactionMissingInBuffer() throws InterruptedException {
        // Block 1.
        final int testBlock1 = 67886;
        // Block 2.
        final int testBlock2 = 67887;
        final String transaction2InBlock2 = "0d01c58b8d1ba9d2a4893b5fda4092b3237f04ecc251583b41aefd7ee42ae77d";

        // We get the testBlock1.
        bitcoinDataService.getBlockData(testBlock1);
        Thread.sleep(PAUSE_BETWEEN_CALLS);

        // We remove a transaction from block 2.
        buffer.removeTransactionInBuffer(transaction2InBlock2);

        // We retrieve the block 2.
        Optional<BitcoindBlockData> block2 = bitcoinDataService.getBlockData(testBlock2);
        assertThat(block2.isPresent()).isTrue();

        // We test that the transaction 2 in block 2 is actually retrieved.
        block2.ifPresent(bitcoindBlockData -> assertThat(bitcoindBlockData.getRawTransactionResult(transaction2InBlock2).isPresent()).isTrue());
    }

    /**
     * No response from bitcoind error.
     */
    @Test
    public void testIssue163() {
        // Blocks for tests.
        final int startBlock = 92151;
        final int numberOfBlocksToLoad = 10;

        // Loading blocks
        for (int i = startBlock; i < startBlock + numberOfBlocksToLoad; i++) {
            assertThat(bitcoinDataService.getBlockData(i).isPresent()).as("Missing block " + i).isTrue();
        }
    }

}
