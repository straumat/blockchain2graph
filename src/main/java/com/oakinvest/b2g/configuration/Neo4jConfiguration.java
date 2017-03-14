package com.oakinvest.b2g.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * Neo4j configuration.
 * Created by straumat on 02/03/17.
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "com.oakinvest.b2g")
public class Neo4jConfiguration {

	/**
	 * Session.
	 *
	 * @return session factory
	 * @throws Exception exception
	 */
/*	@Bean
	@SuppressWarnings("checkstyle:designforextension")
	public Session getSession() throws Exception {
		return new SessionFactory("com.oakinvest.b2g").openSession();
	}*/

}
