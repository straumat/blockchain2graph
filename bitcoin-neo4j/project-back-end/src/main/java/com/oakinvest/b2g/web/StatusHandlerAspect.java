package com.oakinvest.b2g.web;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

/**
 * Application status aspect that sends status to websocket.
 */
@Configuration
@Aspect
public class StatusHandlerAspect {

	/**
	 * Status handler.
	 */
	private final StatusHandler statusHandler;

	/**
	 * Constructor.
	 * @param newStatusHandler status handler
	 */
	public StatusHandlerAspect(final StatusHandler newStatusHandler) {
		this.statusHandler = newStatusHandler;
	}

	/**
	 * Application status change.
	 * @param joinPoint join point
	 */
	@After("execution(* com.oakinvest.b2g.util.status.ApplicationStatus.set*(..))")
	public void applicationStatusChange(final JoinPoint joinPoint) {
		statusHandler.sendApplicationStatus();
	}

	/**
	 * Current block status change.
	 * @param joinPoint join point
	 */
	@After("execution(* com.oakinvest.b2g.util.status.CurrentBlockStatus.set*(..))")
	public void currentBlockStatusChange(final JoinPoint joinPoint) {
		statusHandler.sendApplicationStatus();
	}

}
