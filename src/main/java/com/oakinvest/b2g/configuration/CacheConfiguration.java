package com.oakinvest.b2g.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Profile;

/**
 * Cache configuration.
 * Created by straumat on 21/02/17.
 */
@EnableCaching
@Profile("!test")
public class CacheConfiguration {

}
