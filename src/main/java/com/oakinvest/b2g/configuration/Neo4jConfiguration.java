package com.oakinvest.b2g.configuration;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;

/**
 * Neo4j configuration class.
 * Created by straumat on 02/03/17.
 */
@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories(basePackages = "com.oakinvest.b2g")
public class Neo4jConfiguration {

	/**
	 * Constructor - Creates unique constraints in neo4j for blocks, transactions, addresses & index on block state.
	 */
	public Neo4jConfiguration() {
		try {
			Session session = new SessionFactory("com.oakinvest.b2g").openSession();
			// Add constraints.
			session.query("CREATE CONSTRAINT ON (n:BitcoinBlock) ASSERT n.height IS UNIQUE", Collections.emptyMap());
			session.query("CREATE CONSTRAINT ON (n:BitcoinBlock) ASSERT n.hash IS UNIQUE", Collections.emptyMap());
			session.query("CREATE CONSTRAINT ON (n:BitcoinTransaction) ASSERT n.txid IS UNIQUE", Collections.emptyMap());
			session.query("CREATE CONSTRAINT ON (n:BitcoinAddress) ASSERT n.address IS UNIQUE", Collections.emptyMap());
			// Add indexes.
			session.query("CREATE INDEX ON :BitcoinBlock(state)", Collections.emptyMap());
			session.query("CREATE INDEX ON :BitcoinTransaction(hash)", Collections.emptyMap());
		} catch (Exception e) {
			LoggerFactory.getLogger(Neo4jConfiguration.class).error("Error while creating constraints & indexes : " + e.getMessage(), e);
		}
	}

}
