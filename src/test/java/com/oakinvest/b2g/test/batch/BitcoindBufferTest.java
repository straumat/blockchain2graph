package com.oakinvest.b2g.test.batch;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.service.bitcoin.BitcoinDataService;
import com.oakinvest.b2g.util.bitcoin.buffer.BitcoinDataServiceBufferStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Buffer store.
     */
    @Autowired
    private BitcoinDataServiceBufferStore buffer;

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
        // How buffer works ?
        // When we get a block, we start to load block + 1 & we start to remove block - 1.

        // Block 1.
        final int testBlock1 = 67886;
        final String transaction1InBlock1 = "59ec104adc5e1642bc730aa7aa31e60a07b21e9f8ec2130faea64894850b47c4";
        // Block 2.
        final int testBlock2 = 67887;
        final String transaction1InBlock2 = "e952743233c95c0defde7371c53f0fabab234a0f555455b0e493c4c1fd1704ba";
        final String transaction2InBlock2 = "0d01c58b8d1ba9d2a4893b5fda4092b3237f04ecc251583b41aefd7ee42ae77d";
        final String transaction3InBlock2 = "a55124d54eb665825956ec08d74ae1420c501b2fdd6cb68264f45b154daf2e2b";
        // Block 3.
        final int testBlock3 = 67888;
        final String transaction1InBlock3 = "3ea32fb5a2e96e03419a6ee3f7c33aef0134de219676e248f0c8c1fe74ce68e4";
        // Block 4.
        final int testBlock4 = 67889;
        final String transaction1InBlock4 = "8026b9bd62d1850c33fd8bf5b67d71ca643fade6f5c7232371329a33447721e3";
        final String transaction2InBlock4 = "2195dabe6463f6d42e459bb004ede7ba428ee2e52fb015549dec1657892503a0";
        final String transaction3InBlock4 = "829158b7822bfb2128763e71203cb57e6b364c8e4d47332d71bc3aab65c17e5f";
        final String transaction4InBlock4 = "bbc93b46d7b2e578b9da6f4cc52ca9a3018d17e3b8b4f167de0af0751414784b";
        final String transaction5InBlock4 = "de279009954a0f302e6d487a533fa00baccc13d39b8d604463db0d0419b3c414";
        // Block 5.
        final int testBlock5 = 67890;
        final String transaction1InBlock5 = "1dbc747765eb6a8b9682d72e3ba423e9cd52fc9ec7dbb81421b5c39cd1a6d2a7";
        final String transaction2InBlock5 = "737498db30719e82d691183016e0f6467258d66e0cc6fff83adb9ed4f340af5a";
        final String transaction3InBlock5 = "83546f78b5ded47c35f0f7f0114d672cec5b6e7bef2304982ff3905892552a30";

        // We get the testBlock1.
        bitcoinDataService.getBlockData(testBlock1);
        Thread.sleep(PAUSE_BETWEEN_CALLS);

        // We check that the data in cache.
        assertThat(buffer.getBlockInCache(testBlock1).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock1).isPresent()).isFalse();
        assertThat(buffer.getBlockInCache(testBlock2).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction1InBlock2).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction2InBlock2).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction3InBlock2).isPresent()).isTrue();
        assertThat(buffer.getBlockInCache(testBlock3).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock3).isPresent()).isFalse();
        assertThat(buffer.getBlockInCache(testBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction2InBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction3InBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction4InBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction5InBlock4).isPresent()).isFalse();
        assertThat(buffer.getBlockInCache(testBlock5).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock5).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction2InBlock5).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction3InBlock5).isPresent()).isFalse();

        // We get the testBlock2.
        bitcoinDataService.getBlockData(testBlock2);
        Thread.sleep(PAUSE_BETWEEN_CALLS);

        // We check that the data in cache.
        assertThat(buffer.getBlockInCache(testBlock1).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock1).isPresent()).isFalse();
        assertThat(buffer.getBlockInCache(testBlock2).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction1InBlock2).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction2InBlock2).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction3InBlock2).isPresent()).isTrue();
        assertThat(buffer.getBlockInCache(testBlock3).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction1InBlock3).isPresent()).isTrue();
        assertThat(buffer.getBlockInCache(testBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction2InBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction3InBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction4InBlock4).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction5InBlock4).isPresent()).isFalse();
        assertThat(buffer.getBlockInCache(testBlock5).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock5).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction2InBlock5).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction3InBlock5).isPresent()).isFalse();

        // We get the testBlock3.
        bitcoinDataService.getBlockData(testBlock3);
        Thread.sleep(PAUSE_BETWEEN_CALLS);

        // We check that the data in cache.
        assertThat(buffer.getBlockInCache(testBlock1).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock1).isPresent()).isFalse();
        assertThat(buffer.getBlockInCache(testBlock2).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock2).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction2InBlock2).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction3InBlock2).isPresent()).isFalse();
        assertThat(buffer.getBlockInCache(testBlock3).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction1InBlock3).isPresent()).isTrue();
        assertThat(buffer.getBlockInCache(testBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction1InBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction2InBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction3InBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction4InBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction5InBlock4).isPresent()).isTrue();
        assertThat(buffer.getBlockInCache(testBlock5).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock5).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction2InBlock5).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction3InBlock5).isPresent()).isFalse();

        // We get the testBlock4.
        bitcoinDataService.getBlockData(testBlock4);
        Thread.sleep(PAUSE_BETWEEN_CALLS);

        // We check that the data in cache.
        assertThat(buffer.getBlockInCache(testBlock1).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock1).isPresent()).isFalse();
        assertThat(buffer.getBlockInCache(testBlock2).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock2).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction2InBlock2).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction3InBlock2).isPresent()).isFalse();
        assertThat(buffer.getBlockInCache(testBlock3).isPresent()).isFalse();
        assertThat(buffer.getTransactionInCache(transaction1InBlock3).isPresent()).isFalse();
        assertThat(buffer.getBlockInCache(testBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction1InBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction2InBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction3InBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction4InBlock4).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction5InBlock4).isPresent()).isTrue();
        assertThat(buffer.getBlockInCache(testBlock5).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction1InBlock5).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction2InBlock5).isPresent()).isTrue();
        assertThat(buffer.getTransactionInCache(transaction3InBlock5).isPresent()).isTrue();
    }

    /**
     * Transaction missing in cache.
     */
    @Test
    public void transactionMissingInCache() throws InterruptedException {
        // Block 1.
        final int testBlock1 = 67886;
        // Block 2.
        final int testBlock2 = 67887;
        final String transaction2InBlock2 = "0d01c58b8d1ba9d2a4893b5fda4092b3237f04ecc251583b41aefd7ee42ae77d";

        // We get the testBlock1.
        bitcoinDataService.getBlockData(testBlock1);
        Thread.sleep(PAUSE_BETWEEN_CALLS);

        // We remove a transaction from block 2.
        buffer.removeTransactionInCache(transaction2InBlock2);

        // We retrieve the block 2.
        Optional<BitcoindBlockData> block2 = bitcoinDataService.getBlockData(testBlock2);
        assertThat(block2.isPresent()).isTrue();

        // We test that the transaction 2 in block 2 is actually retrieved.
        block2.ifPresent(bitcoindBlockData -> assertThat(bitcoindBlockData.getRawTransactionResult(transaction2InBlock2).isPresent()).isTrue());
    }

}
