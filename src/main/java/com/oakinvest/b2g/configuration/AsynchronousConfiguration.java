package com.oakinvest.b2g.configuration;

import com.oakinvest.b2g.service.bitcoin.BitcoinTransactionIntegrationTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration.
 * Created by straumat on 17/02/17.
 */
@Configuration
@EnableAsync
public class AsynchronousConfiguration extends AsyncConfigurerSupport {

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(BitcoinTransactionIntegrationTask.class);

	/**
	 * TODO Set as parameter
	 * Core pool size.
	 */
	private final int corePoolSize = 5;

	/**
	 * Max pool size.
	 */
	private final int maxPoolSize = 50;

	@Override
	@Bean(name = "transaction-executor")
	@SuppressWarnings("checkstyle:designforextension")
	public Executor getAsyncExecutor() {
		log.debug("Creating Async Task Executor");
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("Async-Executor-");
		executor.initialize();
		return executor;
	}


}
