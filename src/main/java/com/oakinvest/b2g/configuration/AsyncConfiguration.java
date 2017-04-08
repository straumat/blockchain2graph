package com.oakinvest.b2g.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration.
 * Created by straumat on 22/03/17.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration extends AsyncConfigurerSupport {

	/**
	 * Transactions core pool size.
	 */
	@Value("${blockchain2graph.import.transactions.threads.core-pool-size}")
	private int transactionsCorePoolSize;

	/**
	 * Transactions thread max pool size.
	 */
	@Value("${blockchain2graph.import.transactions.threads.max-pool-size}")
	private int transactionsMaxPoolSize;

	/**
	 * Async configuration.
	 *
	 * @return configuration
	 */
	@Override
	@Bean(name = "transactionPoolTaskExecutor")
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(transactionsCorePoolSize);
		executor.setMaxPoolSize(transactionsMaxPoolSize);
		executor.setThreadNamePrefix("transaction-thread-");
		executor.initialize();
		return executor;
	}

}
