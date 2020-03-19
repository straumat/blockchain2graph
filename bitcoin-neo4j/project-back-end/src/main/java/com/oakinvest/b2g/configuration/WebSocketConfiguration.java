package com.oakinvest.b2g.configuration;

import com.oakinvest.b2g.web.StatusHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Web socket configuration class.
 *
 * Created by straumat on 31/10/16.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    /**
     * Status handler.
     */
    private final StatusHandler statusHandler;

    /**
     * Constructor.
     *
     * @param newStatusHandler statusHandler
     */
    public WebSocketConfiguration(final StatusHandler newStatusHandler) {
        this.statusHandler = newStatusHandler;
    }

    /**
     * Register.
     *
     * @param registry registry
     */
    @Override
    public final void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        registry.addHandler(statusHandler, "/status").setAllowedOrigins("*").withSockJS();
    }

}
