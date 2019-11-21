package com.oakinvest.b2g.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oakinvest.b2g.util.status.ApplicationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Controller for the applicationStatus.
 * <p>
 * Created by straumat on 31/10/16.
 */
@Component
public class StatusHandler extends TextWebSocketHandler {

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(StatusHandler.class);

    /**
     * Sessions.
     */
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    /**
     * Object to Json mapper.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Status service.
     */
    private final ApplicationStatus applicationStatus;

    @Override
    public final void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        this.sessions.remove(session);
    }

    /**
     * Constructor.
     *
     * @param newStatus applicationStatus
     */
    public StatusHandler(final ApplicationStatus newStatus) {
        this.applicationStatus = newStatus;
    }

    @Override
    public final void afterConnectionEstablished(final WebSocketSession newSession) {
        this.sessions.add(newSession);
        sendApplicationStatus();
    }

    /**
     * Send a message message to all sessions.
     */
    public void sendApplicationStatus() {
        try {
            // We send the messages to all opened sessions. We remove the one that are closed.
            for (WebSocketSession session : this.sessions) {
                if (session.isOpen()) {
                    //noinspection SynchronizationOnLocalVariableOrMethodParameter
                    synchronized (session) {
                        session.sendMessage(new TextMessage(mapper.writeValueAsString(applicationStatus)));
                    }
                } else {
                    sessions.remove(session);
                }
            }
        } catch (Exception e) {
            log.warn("Error sending message : " + e.getMessage(), e);
        }
    }

}
