package com.oakinvest.b2g.bitcoin.configuration;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.Collections;

/**
 * Neo4j configuration class.
 *
 * Created by straumat on 02/03/17.
 */
@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories(basePackages = "com.oakinvest.b2g.bitcoin")
@EntityScan(basePackages = "com.oakinvest.b2g.bitcoin.domain")
public class Neo4jConfiguration {

    /**
     * Session factory.
     */
    private final SessionFactory sessionFactory;

    /**
     * Constructor.
     *
     * @param newSessionFactory session factory
     */
    public Neo4jConfiguration(final SessionFactory newSessionFactory) {
        this.sessionFactory = newSessionFactory;
    }

    /**
     * Creates constraints and indexes.
     */
    @PostConstruct
    public final void createConstraintsAndIndexes() {
        Logger log = LoggerFactory.getLogger(Neo4jConfiguration.class);
        try {
            log.info("Creating constraints and indexes...");
            // Session.
            Session session = sessionFactory.openSession();
            // Constraints.
            session.query("CREATE CONSTRAINT ON (n:BitcoinAddress) ASSERT n.address IS UNIQUE", Collections.emptyMap());
            // Indexes.
            session.query("CREATE INDEX ON :BitcoinBlock(height)", Collections.emptyMap());
            session.query("CREATE INDEX ON :BitcoinTransactionOutput(txid, n)", Collections.emptyMap());
            log.info("Constraints and indexes created");
        } catch (Exception e) {
            log.error("Error creating constraints and indexes : " + e.getMessage(), e);
        }
    }

}
