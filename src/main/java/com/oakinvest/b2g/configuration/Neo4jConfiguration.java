package com.oakinvest.b2g.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * Neo4j configuration class.
 * Created by straumat on 02/03/17.
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "com.oakinvest.b2g")
@EntityScan(basePackages = "com.oakinvest.b2g.domain")
public class Neo4jConfiguration {

}
