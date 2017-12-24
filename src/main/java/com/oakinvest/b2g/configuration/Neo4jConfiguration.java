package com.oakinvest.b2g.configuration;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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
@EntityScan(basePackages = "com.oakinvest.b2g.domain")
public class Neo4jConfiguration {

    /**
     * Constructor - Create constraints and indexes.
     */
    public Neo4jConfiguration() {
        try {
            // Session.
            Session session = new SessionFactory("com.oakinvest.b2g").openSession();
            // Constraints.
            session.query("CREATE CONSTRAINT ON (n:BitcoinAddress) ASSERT n.address IS UNIQUE", Collections.emptyMap());
            // Indexes.
            session.query("CREATE INDEX ON :BitcoinBlock(height)", Collections.emptyMap());
            session.query("CREATE INDEX ON :BitcoinBlock(hash)", Collections.emptyMap());
            session.query("CREATE INDEX ON :BitcoinTransaction(txid)", Collections.emptyMap());
            session.query("CREATE INDEX ON :BitcoinTransactionOutput(txid, n)", Collections.emptyMap());
        } catch (Exception e) {
            LoggerFactory.getLogger(Neo4jConfiguration.class).error("Error creating constraints & indexes : " + e.getMessage(), e);
        }
    }

}
