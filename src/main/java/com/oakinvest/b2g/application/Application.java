package com.oakinvest.b2g.application;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application launcher.
 * @author straumat
 */
@SpringBootApplication
public class Application {

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(Application.class);

	/**
	 * Application launcher.
	 * @param args parameters.
	 */
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Application initialization.
	 */
	@PostConstruct
	public final void initApplication() {
		log.debug("Application initialization.");
	}

}
