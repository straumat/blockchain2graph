package com.oakinvest.b2g;

import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

/**
 * Application launcher.
 *
 * @author straumat
 */
@SpringBootApplication
@EnableNeo4jRepositories
@EnableTransactionManagement
@EnableCaching
@ComponentScan
public class Application extends SpringBootServletInitializer {

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bds;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinBlockRepository bbr;

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

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
		// Update status.
		status.setImportedBlockCount(bbr.count());
		status.setTotalBlockCount(bds.getBlockCount().getResult());
	}

}
