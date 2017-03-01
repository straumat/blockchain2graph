package com.oakinvest.b2g.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration.
 * Created by straumat on 17/02/17.
 */
//@Configuration
//@EnableAsync
public class AsynchronousConfiguration implements AsyncConfigurer {

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(AsynchronousConfiguration.class);

	/**
	 * Core pool size.
	 */
	@Value("${blockchain2graph.threads.corePoolSize}")
	private final int corePoolSize = 5;

	/**
	 * Max pool size.
	 */
	@Value("${blockchain2graph.threads.maxPoolSize}")
	private final int maxPoolSize = 50;

	@Override
	@Bean(name = "transaction-executor")
	@SuppressWarnings("checkstyle:designforextension")
	public Executor getAsyncExecutor() {
		log.debug("Creating Async Task Executor");
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setThreadNamePrefix("thread-");
		executor.initialize();
		return executor;
	}

	@Override
	public final AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (throwable, method, objects) -> throwable.printStackTrace();
	}


}
