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
	 * Transactions core pool size.
	 */
	@Value("${blockchain2graph.import.transaction-relations.threads.core-pool-size}")
	private int transactionRelationsCorePoolSize;

	/**
	 * Transactions thread max pool size.
	 */
	@Value("${blockchain2graph.import.transaction-relations.threads.max-pool-size}")
	private int transactionsRelationsMaxPoolSize;

	/**
	 * Async configuration for transactions treatment.
	 *
	 * @return configuration
	 */
	@Bean(name = "transactionPoolTaskExecutor")
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public Executor getTransactionsExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(transactionsCorePoolSize);
		executor.setMaxPoolSize(transactionsMaxPoolSize);
		executor.setThreadNamePrefix("transaction-thread-");
		executor.initialize();
		return executor;
	}

	/**
	 * Async configuration for transaction relations treatment.
	 *
	 * @return pool
	 */
	@Bean(name = "transactionRelationsPoolTaskExecutor")
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public Executor getTransactionRelationsExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(transactionRelationsCorePoolSize);
		executor.setMaxPoolSize(transactionsRelationsMaxPoolSize);
		executor.setThreadNamePrefix("transaction-relation-thread-");
		executor.initialize();
		return executor;
	}

}
