package com.oakinvest.b2g.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import java.util.Arrays;

/**
 * Cache configuration.
 * Created by straumat on 20/05/17.
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

    /**
     * Bitcoind buffer size.
     */
    public static final int BITCOIND_BUFFER_SIZE = 100;

    /**
     * Get cache manager.
     * @return cache manager
     */
    @Bean
    @SuppressWarnings("checkstyle:designforextension")
    public CacheManager cacheManager() {
        CaffeineCache blockDataCache = new CaffeineCache("blockCount", Caffeine.newBuilder().maximumSize(BITCOIND_BUFFER_SIZE).build());
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Arrays.asList(blockDataCache));
        return null;
    }

}
