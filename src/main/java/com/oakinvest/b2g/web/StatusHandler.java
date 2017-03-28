package com.oakinvest.b2g.web;

import com.google.gson.Gson;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoinStatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Controller for the status.
 * Created by straumat on 31/10/16.
 */
@Component
public class StatusHandler extends TextWebSocketHandler {

	/**
	 * Param for message type.
	 */
	private static final String PARAM_MESSAGE_TYPE = "messageType";

	/**
	 * Param for message value.
	 */
	private static final String PARAM_MESSAGE_VALUE = "messageValue";

	/**
	 * Imported block count.
	 */
	private static final String TYPE_IMPORTED_BLOCK_COUNT = "importedBlockCount";

	/**
	 * Total block count.
	 */
	private static final String TYPE_TOTAL_BLOCK_COUNT = "totalBlockCount";

	/**
	 * Log type.
	 */
	private static final String TYPE_LOG = "log";

	/**
	 * Error message.
	 */
	private static final String TYPE_ERROR = "error";

	/**
	 * Execution time.
	 */
	private static final String TYPE_AVERAGE_BLOCK_DURATION = "averageBlockImportDuration";

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(StatusHandler.class);

	/**
	 * Sessions.
	 */
	private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	/**
	 * Gson.
	 */
	private final Gson gson = new Gson();

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

	/**
	 * Bitcoin statistic service.
	 */
	@Autowired
	private BitcoinStatisticService bitcoinStatisticService;

	@Override
	public final void afterConnectionEstablished(final WebSocketSession newSession) {
		this.sessions.add(newSession);
		updateImportedBlockCount(status.getImportedBlockCount());
		updateTotalBlockCount(status.getTotalBlockCount());
		updateError(status.getLastErrorMessage());
		updateLog(status.getLastLogMessage());
		updateAverageBlockImportDuration(bitcoinStatisticService.getAverageBlockImportDuration());
	}

	/**
	 * Updates importedBlockCount.
	 *
	 * @param count new value.
	 */
	public final void updateImportedBlockCount(final long count) {
		HashMap<Object, Object> information = new HashMap<>();
		information.put(PARAM_MESSAGE_TYPE, TYPE_IMPORTED_BLOCK_COUNT);
		information.put(PARAM_MESSAGE_VALUE, count);
		sendMessage(gson.toJson(information));
	}

	/**
	 * Updates totalBlockCount.
	 *
	 * @param count new value.
	 */
	public final void updateTotalBlockCount(final long count) {
		HashMap<Object, Object> information = new HashMap<>();
		information.put(PARAM_MESSAGE_TYPE, TYPE_TOTAL_BLOCK_COUNT);
		information.put(PARAM_MESSAGE_VALUE, count);
		sendMessage(gson.toJson(information));
	}

	/**
	 * Update the log.
	 *
	 * @param logMessage log message
	 */
	public final void updateLog(final String logMessage) {
		HashMap<Object, Object> information = new HashMap<>();
		information.put(PARAM_MESSAGE_TYPE, TYPE_LOG);
		information.put(PARAM_MESSAGE_VALUE, logMessage);
		sendMessage(gson.toJson(information));
	}

	/**
	 * Update error message.
	 *
	 * @param errorMessage error message.
	 */
	public final void updateError(final String errorMessage) {
		HashMap<Object, Object> information = new HashMap<>();
		information.put(PARAM_MESSAGE_TYPE, TYPE_ERROR);
		information.put(PARAM_MESSAGE_VALUE, errorMessage);
		sendMessage(gson.toJson(information));
	}

	/**
	 * Update execution time statistic.
	 *
	 * @param averageBlockImportDuration new execution time statistics.
	 */
	public final void updateAverageBlockImportDuration(final float averageBlockImportDuration) {
		HashMap<Object, Object> information = new HashMap<>();
		information.put(PARAM_MESSAGE_TYPE, TYPE_AVERAGE_BLOCK_DURATION);
		information.put(PARAM_MESSAGE_VALUE, averageBlockImportDuration);
		sendMessage(gson.toJson(information));
	}

	/**
	 * Send a message message to all sessions.
	 *
	 * @param message message
	 */
	private void sendMessage(final String message) {
		try {
			// First, we clean all sessions that are closed.
			List<WebSocketSession> sessionsToRemove = new ArrayList<>();
			for (WebSocketSession session : this.sessions) {
				if (!session.isOpen()) {
					sessionsToRemove.add(session);
				}
			}
			this.sessions.removeAll(sessionsToRemove);

			// Then we send the messages to all opened sessions.
			for (WebSocketSession session : this.sessions) {
				synchronized (session) {
					if (session.isOpen()) {
						session.sendMessage(new TextMessage(message));
					}
				}
			}
		} catch (Exception e) {
			log.error("Error sending message " + e);
			log.error("Error : " + Arrays.toString(e.getStackTrace()));
		}
	}

}
