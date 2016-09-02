package com.oakinvest.b2g;

import com.oakinvest.b2g.service.BitcoindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * Application launcher.
 *
 * @author straumat
 */
@SpringBootApplication
@ComponentScan
public class Application {

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(Application.class);

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bitcoindService;

	/**
	 * Application launcher.
	 *
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
		log.debug("block2graph initialization...");
	}

}
