package com.oakinvest.b2g.configuration;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Neo4j configuration.
 * Created by straumat on 02/03/17.
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "com.oakinvest.b2g")
@EnableTransactionManagement
public class Neo4jConfiguration {

	/**
	 * Session.
	 *
	 * @return session factory
	 * @throws Exception exception
	 */
	@Bean
	@SuppressWarnings("checkstyle:designforextension")
	public Session getSession() throws Exception {
		return new SessionFactory("com.oakinvest.b2g").openSession();
	}

}
