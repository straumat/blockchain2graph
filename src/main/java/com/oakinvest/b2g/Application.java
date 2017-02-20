package com.oakinvest.b2g;

import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

import static java.util.Collections.EMPTY_MAP;

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
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(Application.class);

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
	 * Neo4j opérations.
	 */
	@Autowired
	private Neo4jOperations neo4jOperations;

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

		// Create unique constraints in neo4j for blocks, transactions, addresses.
		neo4jOperations.query("CREATE CONSTRAINT ON (t:BitcoinBlock) ASSERT t.height IS UNIQUE", EMPTY_MAP);
		neo4jOperations.query("CREATE CONSTRAINT ON (t:BitcoinBlock) ASSERT t.hash IS UNIQUE", EMPTY_MAP);
		neo4jOperations.query("CREATE CONSTRAINT ON (t:BitcoinTransaction) ASSERT t.txid IS UNIQUE", EMPTY_MAP);
		neo4jOperations.query("CREATE CONSTRAINT ON (t:BitcoinAddress) ASSERT t.address IS UNIQUE", EMPTY_MAP);
	}


}
