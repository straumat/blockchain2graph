package com.oakinvest.b2g.bitcoin.test.status;

import com.oakinvest.b2g.bitcoin.test.util.junit.BaseTest;
import com.oakinvest.b2g.bitcoin.test.util.status.StatusWebSocketSessionHandler;
import com.oakinvest.b2g.dto.bitcoin.status.ApplicationStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.oakinvest.b2g.dto.bitcoin.status.CurrentBlockStatusProcessStep.CREATING_TRANSACTIONS;
import static com.oakinvest.b2g.dto.bitcoin.status.CurrentBlockStatusProcessStep.NEW_BLOCK_TO_PROCESS;
import static com.oakinvest.b2g.dto.bitcoin.status.CurrentBlockStatusProcessStep.NOTHING_TO_PROCESS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the status service.
 * Created by straumat on 28/10/16.
 */
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
    private ApplicationStatus status;

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
    public final void observableBehaviorTest() throws InterruptedException, TimeoutException, ExecutionException {
        // Objects used to retrieve replies from websocket.
        StatusWebSocketSessionHandler webSocketResponse = new StatusWebSocketSessionHandler();
        ApplicationStatus valueFromWebSocket;

        // Transports layers for websocket.
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        // Websocket connection.
        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.doHandshake(webSocketResponse,"ws://localhost:8080/status/").get(1, TimeUnit.MINUTES);

        // Change blocksCountInBitcoinCore.
        status.setBlocksCountInBitcoinCore(1);
        valueFromWebSocket = webSocketResponse.getNewMessage();
        assertThat(valueFromWebSocket.getBlocksCountInBitcoinCore()).isEqualTo(1);
        assertThat(valueFromWebSocket.getBlocksCountInNeo4j()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getBlockHeight()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessStep()).isEqualTo(NOTHING_TO_PROCESS);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedAddresses()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getAddressesCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedTransactions()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getTransactionsCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getAverageBlockProcessDuration()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getLastErrorMessage()).isEqualTo(NON_AVAILABLE_VALUE_STRING);

        // Change blocksCountInNeo4j.
        status.setBlocksCountInNeo4j(1);
        valueFromWebSocket = webSocketResponse.getNewMessage();
        assertThat(valueFromWebSocket.getBlocksCountInBitcoinCore()).isEqualTo(1);
        assertThat(valueFromWebSocket.getBlocksCountInNeo4j()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getBlockHeight()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessStep()).isEqualTo(NOTHING_TO_PROCESS);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedAddresses()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getAddressesCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedTransactions()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getTransactionsCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getAverageBlockProcessDuration()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getLastErrorMessage()).isEqualTo(NON_AVAILABLE_VALUE_STRING);

        // Change currentBlockStatus.processStep.
        status.getCurrentBlockStatus().setProcessStep(CREATING_TRANSACTIONS);
        valueFromWebSocket = webSocketResponse.getNewMessage();
        assertThat(valueFromWebSocket.getBlocksCountInBitcoinCore()).isEqualTo(1);
        assertThat(valueFromWebSocket.getBlocksCountInNeo4j()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getBlockHeight()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessStep()).isEqualTo(CREATING_TRANSACTIONS);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedAddresses()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getAddressesCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedTransactions()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getTransactionsCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getAverageBlockProcessDuration()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getLastErrorMessage()).isEqualTo(NON_AVAILABLE_VALUE_STRING);

        // Change currentBlockStatus.processedAddresses.
        status.getCurrentBlockStatus().setProcessedAddresses(1);
        valueFromWebSocket = webSocketResponse.getNewMessage();
        assertThat(valueFromWebSocket.getBlocksCountInBitcoinCore()).isEqualTo(1);
        assertThat(valueFromWebSocket.getBlocksCountInNeo4j()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getBlockHeight()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessStep()).isEqualTo(CREATING_TRANSACTIONS);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedAddresses()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getAddressesCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedTransactions()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getTransactionsCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getAverageBlockProcessDuration()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getLastErrorMessage()).isEqualTo(NON_AVAILABLE_VALUE_STRING);

        // Change currentBlockStatus.addressesCount.
        status.getCurrentBlockStatus().setAddressesCount(1);
        valueFromWebSocket = webSocketResponse.getNewMessage();
        assertThat(valueFromWebSocket.getBlocksCountInBitcoinCore()).isEqualTo(1);
        assertThat(valueFromWebSocket.getBlocksCountInNeo4j()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getBlockHeight()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessStep()).isEqualTo(CREATING_TRANSACTIONS);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedAddresses()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getAddressesCount()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedTransactions()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getTransactionsCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getAverageBlockProcessDuration()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getLastErrorMessage()).isEqualTo(NON_AVAILABLE_VALUE_STRING);

        // Change currentBlockStatus.processedTransactions.
        status.getCurrentBlockStatus().setProcessedTransactions(1);
        valueFromWebSocket = webSocketResponse.getNewMessage();
        assertThat(valueFromWebSocket.getBlocksCountInBitcoinCore()).isEqualTo(1);
        assertThat(valueFromWebSocket.getBlocksCountInNeo4j()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getBlockHeight()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessStep()).isEqualTo(CREATING_TRANSACTIONS);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedAddresses()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getAddressesCount()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedTransactions()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getTransactionsCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getAverageBlockProcessDuration()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getLastErrorMessage()).isEqualTo(NON_AVAILABLE_VALUE_STRING);

        // Change currentBlockStatus.transactionsCount.
        status.getCurrentBlockStatus().setTransactionsCount(1);
        valueFromWebSocket = webSocketResponse.getNewMessage();
        assertThat(valueFromWebSocket.getBlocksCountInBitcoinCore()).isEqualTo(1);
        assertThat(valueFromWebSocket.getBlocksCountInNeo4j()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getBlockHeight()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessStep()).isEqualTo(CREATING_TRANSACTIONS);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedAddresses()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getAddressesCount()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedTransactions()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getTransactionsCount()).isEqualTo(1);
        assertThat(valueFromWebSocket.getAverageBlockProcessDuration()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getLastErrorMessage()).isEqualTo(NON_AVAILABLE_VALUE_STRING);

        // Change averageBlockProcessDuration.
        status.setAverageBlockProcessDuration(1);
        valueFromWebSocket = webSocketResponse.getNewMessage();
        assertThat(valueFromWebSocket.getBlocksCountInBitcoinCore()).isEqualTo(1);
        assertThat(valueFromWebSocket.getBlocksCountInNeo4j()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getBlockHeight()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessStep()).isEqualTo(CREATING_TRANSACTIONS);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedAddresses()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getAddressesCount()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedTransactions()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getTransactionsCount()).isEqualTo(1);
        assertThat(valueFromWebSocket.getAverageBlockProcessDuration()).isEqualTo(1);
        assertThat(valueFromWebSocket.getLastErrorMessage()).isEqualTo(NON_AVAILABLE_VALUE_STRING);

        // Change lastErrorMessage.
        status.setLastErrorMessage("");
        valueFromWebSocket = webSocketResponse.getNewMessage();
        assertThat(valueFromWebSocket.getBlocksCountInBitcoinCore()).isEqualTo(1);
        assertThat(valueFromWebSocket.getBlocksCountInNeo4j()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getBlockHeight()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessStep()).isEqualTo(CREATING_TRANSACTIONS);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedAddresses()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getAddressesCount()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedTransactions()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getTransactionsCount()).isEqualTo(1);
        assertThat(valueFromWebSocket.getAverageBlockProcessDuration()).isEqualTo(1);
        assertThat(valueFromWebSocket.getLastErrorMessage()).isEqualTo("");

        // If currentBlockStatus.blockHeight value change, automatically set others fields.
        status.getCurrentBlockStatus().setBlockHeight(1);
        valueFromWebSocket = webSocketResponse.getNewMessage();
        assertThat(valueFromWebSocket.getBlocksCountInBitcoinCore()).isEqualTo(1);
        assertThat(valueFromWebSocket.getBlocksCountInNeo4j()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getBlockHeight()).isEqualTo(1);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessStep()).isEqualTo(NEW_BLOCK_TO_PROCESS);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedAddresses()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getAddressesCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedTransactions()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getTransactionsCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getAverageBlockProcessDuration()).isEqualTo(1);
        assertThat(valueFromWebSocket.getLastErrorMessage()).isEqualTo("");

        // If currentBlockStatus.processStep change to NOTHING_TO_PROCESS, automatically set others fields.
        status.getCurrentBlockStatus().setBlockHeight(1);
        webSocketResponse.getNewMessage();
        status.getCurrentBlockStatus().setProcessedAddresses(1);
        webSocketResponse.getNewMessage();
        status.getCurrentBlockStatus().setAddressesCount(1);
        webSocketResponse.getNewMessage();
        status.getCurrentBlockStatus().setProcessedTransactions(1);
        webSocketResponse.getNewMessage();
        status.getCurrentBlockStatus().setTransactionsCount(1);
        webSocketResponse.getNewMessage();
        status.getCurrentBlockStatus().setProcessStep(NOTHING_TO_PROCESS);
        valueFromWebSocket = webSocketResponse.getNewMessage();
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getBlockHeight()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessStep()).isEqualTo(NOTHING_TO_PROCESS);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedAddresses()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getAddressesCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getProcessedTransactions()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getCurrentBlockStatus().getTransactionsCount()).isEqualTo(NON_AVAILABLE_VALUE_NUMBER);
        assertThat(valueFromWebSocket.getAverageBlockProcessDuration()).isEqualTo(1);
        assertThat(valueFromWebSocket.getLastErrorMessage()).isEqualTo("");
    }

}
