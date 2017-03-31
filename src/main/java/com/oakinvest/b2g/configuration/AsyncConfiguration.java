package com.oakinvest.b2g.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration.
 * Created by straumat on 22/03/17.
 */
@EnableAsync
public class AsyncConfiguration extends AsyncConfigurerSupport {

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
	public final Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(transactionsMaxPoolSize);
		executor.setThreadNamePrefix("transaction-thread-");
		executor.initialize();
		return executor;
	}

}
