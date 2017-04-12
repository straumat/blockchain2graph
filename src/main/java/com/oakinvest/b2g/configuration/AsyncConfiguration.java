package com.oakinvest.b2g.configuration;

import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration.
 * Created by straumat on 22/03/17.
 */
public class AsyncConfiguration extends AsyncConfigurerSupport {

	/**
	 * Async configuration.
	 *
	 * @return configuration
	 */
	@Override
	public final Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setThreadNamePrefix("blockData-cache-");
		executor.initialize();
		return executor;
	}

}
