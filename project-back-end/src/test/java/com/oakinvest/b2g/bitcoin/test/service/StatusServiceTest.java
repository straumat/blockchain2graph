package com.oakinvest.b2g.bitcoin.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.bitcoin.test.util.junit.BaseTest;
import com.oakinvest.b2g.dto.bitcoin.status.ApplicationStatusDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.oakinvest.b2g.dto.bitcoin.status.CurrentBlockStatusProcessStep.LOADING_DATA_FROM_BITCOIN_CORE;
import static com.oakinvest.b2g.dto.bitcoin.status.CurrentBlockStatusProcessStep.NOTHING_TO_PROCESS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the status service.
 * Created by straumat on 28/10/16.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StatusServiceTest extends BaseTest {

    /**
     * Non available value.
     */
    private static final String NON_AVAILABLE_VALUE_STRING = "n/a";

    /**
     * Non available value.
     */
    private static final int NON_AVAILABLE_VALUE_NUMBER = -1;

    /**
     * Status service.
     */
    @Autowired
    private ApplicationStatusDTO status;

    /**
     * Test for initial values.
     */
    @Test
    public final void initialValuesTest() {
        assertThat(status.getBlocksCountInBitcoinCore())
                .as("Check number of blocks in Bitcoin core")
                .isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(status.getBlocksCountInNeo4j())
                .as("Check number of blocks in neo4j")
                .isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(status.getCurrentBlockStatus().getBlockHeight())
                .as("Check the block being processed")
                .isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(status.getCurrentBlockStatus().getProcessStep())
                .as("Check the step in the process")
                .isEqualTo(NOTHING_TO_PROCESS);
        assertThat(status.getCurrentBlockStatus().getProcessedAddresses())
                .as("Check number of addresses processed")
                .isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(status.getCurrentBlockStatus().getAddressesCount())
                .as("Check number of addresses in the current block")
                .isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(status.getCurrentBlockStatus().getProcessedTransactions())
                .as("Check number of transactions processed")
                .isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(status.getCurrentBlockStatus().getTransactionsCount())
                .as("Check number of transactions in the current block")
                .isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(status.getAverageBlockProcessDuration())
                .as("Check average block process duration")
                .isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(status.getLastErrorMessage())
                .as("Check average block process duration")
                .isEqualTo(NON_AVAILABLE_VALUE_STRING);
    }

    /**
     * Test for observable behavior.
     */
    @Test
    public final void observableBehaviorTest() {
        StatusObserver statusObserver = new StatusObserver();
        status.addObserver(statusObserver);
        assertThat(statusObserver.isStatusChanged())
                .as("Check that the status is clean")
                .isFalse();

        // Changing number of blocks in Bitcoin core.
        statusObserver.reset();
        status.setBlocksCountInBitcoinCore(1);
        assertThat(statusObserver.isStatusChanged())
                .as("Check that changing number of blocks in Bitcoin core triggered observable")
                .isTrue();
        statusObserver.reset();
        assertThat(statusObserver.isStatusChanged())
                .as("Check that reset method works")
                .isFalse();

        // Changing number of blocks in Neo4j.
        statusObserver.reset();
        status.setBlocksCountInNeo4j(1);
        assertThat(statusObserver.isStatusChanged())
                .as("Check that changing number of blocks in neo4j triggered observable")
                .isTrue();

        // Changing the block being processed.
        statusObserver.reset();
        status.getCurrentBlockStatus().setBlockHeight(1);
        assertThat(statusObserver.isStatusChanged())
                .as("Check that changing the block being processed triggered observable")
                .isTrue();

        // Changing the step process.
        statusObserver.reset();
        status.getCurrentBlockStatus().setProcessStep(LOADING_DATA_FROM_BITCOIN_CORE);
        assertThat(statusObserver.isStatusChanged())
                .as("Check that changing the step process triggered observable")
                .isTrue();

        // Changing the number of addresses processed.
        statusObserver.reset();
        status.getCurrentBlockStatus().setProcessedAddresses(1);
        assertThat(statusObserver.isStatusChanged())
                .as("Check that changing the number of addresses processed triggered observable")
                .isTrue();

        // Changing the number of addresses in the current block.
        statusObserver.reset();
        status.getCurrentBlockStatus().setAddressesCount(1);
        assertThat(statusObserver.isStatusChanged())
                .as("Check that changing the number of addresses in the current block processed triggered observable")
                .isTrue();

        // Changing the number of transactions processed.
        statusObserver.reset();
        status.getCurrentBlockStatus().setProcessedTransactions(1);
        assertThat(statusObserver.isStatusChanged())
                .as("Check that changing the number of transaction processed triggered observable")
                .isTrue();

        // Changing the number of transactions in the current block.
        statusObserver.reset();
        status.getCurrentBlockStatus().setTransactionsCount(1);
        assertThat(statusObserver.isStatusChanged())
                .as("Check that changing the number of transactions in the current block processed triggered observable")
                .isTrue();

        // Changing the average block process duration.
        statusObserver.reset();
        status.setAverageBlockProcessDuration(1);
        assertThat(statusObserver.isStatusChanged())
                .as("Check that changing the average block process duration triggered observable")
                .isTrue();

        // Changing the last error message.
        statusObserver.reset();
        status.setLastErrorMessage("");
        assertThat(statusObserver.isStatusChanged())
                .as("Check that changing the last error message triggered observable")
                .isTrue();
    }

}
