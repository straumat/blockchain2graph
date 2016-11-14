package com.oakinvest.b2g;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

/**
 * Application launcher.
 *
 * @author straumat
 */
@SpringBootApplication
@EnableNeo4jRepositories
@EnableScheduling
@ComponentScan
public class Application extends SpringBootServletInitializer {

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(Application.class);

	/**
	 * Application launcher.
	 *
	 * @param args parameters.
	 */
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected final SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	/**
	 * Application initialization.
	 */
	@PostConstruct
	public final void initApplication() {
		log.debug("block2graph started...");
	}

}
