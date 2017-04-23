package com.oakinvest.b2g.service;

import com.oakinvest.b2g.web.StatusHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Status service implementation.
 * Created by straumat on 28/10/16.
 */
@Service
public class StatusServiceImplementation implements StatusService {

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(StatusService.class);

	/**
	 * Status handler.
	 */
	private final StatusHandler statusHandler;

	/**
	 * Date format.
	 */
	@Value("${blockchain2graph.date.format}")
	private String dateFormat;

	/**
	 * Last log message.
	 */
	private String lastLog = "";

	/**
	 * Last error message.
	 */
	private String lastError = "";

	/**
	 * Total block count.
	 */
	private long totalBlockCount = 0;

	/**
	 * Imported block count.
	 */
	private long importedBlockCount = 0;

	/**
	 * Constructor.
	 *
	 * @param newStatusHandler statusHandler
	 */
	public StatusServiceImplementation(final StatusHandler newStatusHandler) {
		this.statusHandler = newStatusHandler;
	}

	/**
	 * Returns the current date with the configured format.
	 *
	 * @return date formatted
	 */
	private String getFormattedCurrentDate() {
		return new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());
	}

	/**
	 * Returns the total number of blocks in the blockchain.
	 *
	 * @return total block count
	 */
	@Override
	public final long getTotalBlockCount() {
		return totalBlockCount;
	}

	/**
	 * Set the total number of blocks in the blockchain.
	 *
	 * @param newTotalBlockCount new value
	 */
	@Override
	public final void setTotalBlockCount(final long newTotalBlockCount) {
		totalBlockCount = newTotalBlockCount;
		statusHandler.updateTotalBlockCount(totalBlockCount);
	}

	/**
	 * Set the average block import duration.
	 *
	 * @param averageBlockImportDuration averageBlockImportDuration
	 */
	@Override
	public final void setAverageBlockImportDuration(final float averageBlockImportDuration) {
		statusHandler.updateAverageBlockImportDuration(averageBlockImportDuration);
	}

	/**
	 * Returns the number of the last block integrated.
	 *
	 * @return block number.
	 */
	@Override
	public final long getImportedBlockCount() {
		return importedBlockCount;
	}

	/**
	 * Set the number of the last block imported.
	 *
	 * @param newImportedBlockCount new value
	 */
	@Override
	public final void setImportedBlockCount(final long newImportedBlockCount) {
		importedBlockCount = newImportedBlockCount;
		statusHandler.updateImportedBlockCount(importedBlockCount);
	}

	/**
	 * Returns the last log message.
	 *
	 * @return last log message.
	 */
	@Override
	public final String getLastLog() {
		return lastLog;
	}

	/**
	 * Add a log message.
	 *
	 * @param logMessage log message
	 */
	@Override
	public final void addLog(final String logMessage) {
		String date = getFormattedCurrentDate();
		lastLog = "[" + date + "] " + logMessage;
		statusHandler.updateLog("[" + date + "] " + logMessage);
		log.info(logMessage);
	}

	/**
	 * Returns the last error message.
	 *
	 * @return last error message.
	 */
	@Override
	public final String getLastError() {
		return lastError;
	}

	/**
	 * Add an error message.
	 *
	 * @param errorMessage error message
	 * @param e               exception raised.
	 */
	@Override
	public final void addError(final String errorMessage, final Exception e) {
		String date = getFormattedCurrentDate();
		lastError = "[" + date + "] " + errorMessage;
		statusHandler.updateLog("[" + date + "] " + errorMessage);
		statusHandler.updateError("[" + date + "] " + errorMessage);
		if (e == null) {
			log.error(lastError);
		} else {
			log.error(lastError, e);
		}
	}

}
