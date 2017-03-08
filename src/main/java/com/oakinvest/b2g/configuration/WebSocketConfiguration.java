package com.oakinvest.b2g.configuration;

import com.oakinvest.b2g.web.StatusHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Web socket configuration.
 * Created by straumat on 31/10/16.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

	/**
	 * Status handler.
	 */
	@Autowired
	private StatusHandler statusHandler;

	/**
	 * Register.
	 *
	 * @param registry registry
	 */
	@Override
	public final void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
		registry.addHandler(statusHandler, "/status").setAllowedOrigins("*");
	}

}
