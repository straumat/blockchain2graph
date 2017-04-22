package com.oakinvest.b2g.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Cache configuration class.
 * Created by straumat on 21/02/17.
 */
@Configuration
@EnableCaching
@Profile("!test")
public class CacheConfiguration {

}
