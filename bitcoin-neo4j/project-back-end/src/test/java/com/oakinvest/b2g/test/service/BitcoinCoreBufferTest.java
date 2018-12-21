package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.test.util.junit.BaseTest;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for core getBuffer().
 */
public class BitcoinCoreBufferTest extends BaseTest {

    /**
     * Time to wait to let the buffer finish its work.
     */
    private static final long WAITING_TIME_FOR_LIVE_TEST = TimeUnit.MINUTES.toMillis(2);

    /**
     * Time to wait to let the buffer finish its work.
     */
    private static final long WAITING_TIME_FOR_CACHED_TEST = TimeUnit.SECONDS.toMillis(2);

    /**
     * returns waiting time depending if the test is live or not.
     *
     * @return waiting time
     */
    private final long getWaitingTime() {
        if (isLiveTest()) {
            return WAITING_TIME_FOR_LIVE_TEST;
        } else {
            return WAITING_TIME_FOR_CACHED_TEST;
        }
    }

    /**
     * Buffer test.
     */
    @Test
    public final void bufferTest() throws InterruptedException {
        // Purge database.
        getSessionFactory().openSession().purgeDatabase();

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
        getBatchBlocks().execute();
        Thread.sleep(getWaitingTime());
        // We check the data in getBuffer().
        assertThat(getBuffer().getBlockInBuffer(testBlock1).isPresent()).isTrue();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock1).isPresent()).isTrue();
        assertThat(getBuffer().getBlockInBuffer(testBlock2).isPresent()).isTrue();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock2).isPresent()).isTrue();
        assertThat(getBuffer().getBlockInBuffer(testBlock3).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock3).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock4).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock4).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock5).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock5).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock6).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock6).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock7).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock7).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock8).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock8).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock9).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock9).isPresent()).isFalse();

        // -------------------------------------------------------------------------------------------------------------
        // We import the block 2.
        getBatchBlocks().execute();
        Thread.sleep(getWaitingTime());
        // We check the data in getBuffer().
        assertThat(getBuffer().getBlockInBuffer(testBlock1).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock1).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock2).isPresent()).isTrue();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock2).isPresent()).isTrue();
        assertThat(getBuffer().getBlockInBuffer(testBlock3).isPresent()).isTrue();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock3).isPresent()).isTrue();
        assertThat(getBuffer().getBlockInBuffer(testBlock4).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock4).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock5).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock5).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock6).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock6).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock7).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock7).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock8).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock8).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock9).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock9).isPresent()).isFalse();

        // -------------------------------------------------------------------------------------------------------------
        // We import the block 3.
        getBatchBlocks().execute();
        Thread.sleep(getWaitingTime());
        // We check the data in getBuffer().
        assertThat(getBuffer().getBlockInBuffer(testBlock1).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock1).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock2).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock2).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock3).isPresent()).isTrue();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock3).isPresent()).isTrue();
        assertThat(getBuffer().getBlockInBuffer(testBlock4).isPresent()).isTrue();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock4).isPresent()).isTrue();
        assertThat(getBuffer().getBlockInBuffer(testBlock5).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock5).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock6).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock6).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock7).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock7).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock8).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock8).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock9).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock9).isPresent()).isFalse();

        // -------------------------------------------------------------------------------------------------------------
        // We import the block 4.
        getBatchBlocks().execute();
        Thread.sleep(getWaitingTime());
        // We check the data in getBuffer().
        assertThat(getBuffer().getBlockInBuffer(testBlock1).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock1).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock2).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock2).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock3).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock3).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock4).isPresent()).isTrue();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock4).isPresent()).isTrue();
        assertThat(getBuffer().getBlockInBuffer(testBlock5).isPresent()).isTrue();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock5).isPresent()).isTrue();
        assertThat(getBuffer().getBlockInBuffer(testBlock6).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock6).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock7).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock7).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock8).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock8).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock9).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock9).isPresent()).isFalse();

        // -------------------------------------------------------------------------------------------------------------
        // We import the block 5.
        getBatchBlocks().execute();
        Thread.sleep(getWaitingTime());
        // We check the data in getBuffer().
        assertThat(getBuffer().getBlockInBuffer(testBlock1).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock1).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock2).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock2).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock3).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock3).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock4).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock4).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock5).isPresent()).isTrue();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock5).isPresent()).isTrue();
        assertThat(getBuffer().getBlockInBuffer(testBlock6).isPresent()).isTrue();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock6).isPresent()).isTrue();
        assertThat(getBuffer().getBlockInBuffer(testBlock7).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock7).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock8).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock8).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock9).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock9).isPresent()).isFalse();

        // -------------------------------------------------------------------------------------------------------------
        // We import the block 6.
        getBatchBlocks().execute();
        Thread.sleep(getWaitingTime());
        // We check the data in getBuffer().
        assertThat(getBuffer().getBlockInBuffer(testBlock1).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock1).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock2).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock2).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock3).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock3).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock4).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock4).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock5).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock5).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock6).isPresent()).isTrue();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock6).isPresent()).isTrue();
        assertThat(getBuffer().getBlockInBuffer(testBlock7).isPresent()).isTrue();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock7).isPresent()).isTrue();
        assertThat(getBuffer().getBlockInBuffer(testBlock8).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock8).isPresent()).isFalse();
        assertThat(getBuffer().getBlockInBuffer(testBlock9).isPresent()).isFalse();
        assertThat(getBuffer().getTransactionInBuffer(transaction1InBlock9).isPresent()).isFalse();
    }

}
