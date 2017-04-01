package com.oakinvest.b2g;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.util.Collections;

/**
 * Application launcher.
 *
 * @author straumat
 */
@SpringBootApplication
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
	private BitcoindService bitcoindService;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinBlockRepository bitcoinBlockRepository;

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

	/**
	 * Neo4j session.
	 */
	@Autowired
	private Session session;

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
		status.setImportedBlockCount(bitcoinBlockRepository.countBlockByState(BitcoinBlockState.IMPORTED));
		status.setTotalBlockCount(bitcoindService.getBlockCount().getResult());

		// Create unique constraints in neo4j for blocks, transactions, addresses & index on block state.
		try {
			// Constraints.
			session.query("CREATE CONSTRAINT ON (n:BitcoinBlock) ASSERT n.height IS UNIQUE", Collections.emptyMap());
			session.query("CREATE CONSTRAINT ON (n:BitcoinBlock) ASSERT n.hash IS UNIQUE", Collections.emptyMap());
			session.query("CREATE CONSTRAINT ON (n:BitcoinTransaction) ASSERT n.txid IS UNIQUE", Collections.emptyMap());
			session.query("CREATE CONSTRAINT ON (n:BitcoinAddress) ASSERT n.address IS UNIQUE", Collections.emptyMap());
			// Indexes.
			session.query("CREATE INDEX ON :BitcoinBlock(state)", Collections.emptyMap());
		} catch (Exception e) {
			log.error("Error while creating constraints in neo4j : " + e.getMessage());
		}
	}

}
