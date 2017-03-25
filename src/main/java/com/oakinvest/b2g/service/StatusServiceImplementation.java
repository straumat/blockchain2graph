package com.oakinvest.b2g.service;

import com.oakinvest.b2g.web.StatusHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	 * Date format.
	 */
	@Value("${blockchain2graph.date.format}")
	private String dateFormat;

	/**
	 * Status handler.
	 */
	@Autowired
	private StatusHandler statusHandler;

	/**
	 * Last log message.
	 */
	private String lastLogMessage = "";

	/**
	 * Last error message.
	 */
	private String lastErrorMessage = "";

	/**
	 * Total block count.
	 */
	private long totalBlockCount = 0;

	/**
	 * Imported block count.
	 */
	private long importedBlockCount = 0;

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
	public final String getLastLogMessage() {
		return lastLogMessage;
	}

	/**
	 * Add a log message.
	 *
	 * @param newLogMessage log message
	 */
	@Override
	public final void addLog(final String newLogMessage) {
		String date = new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());
		lastLogMessage = "[" + date + "] " + newLogMessage;
		statusHandler.updateLog("[" + date + "] " + newLogMessage);
	}

	/**
	 * Returns the last error message.
	 *
	 * @return last error message.
	 */
	@Override
	public final String getLastErrorMessage() {
		return lastErrorMessage;
	}

	/**
	 * Add an error message.
	 *
	 * @param newErrorMessage error message
	 */
	@Override
	public final void addError(final String newErrorMessage) {
		String date = new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());
		lastErrorMessage = "[" + date + "] " + newErrorMessage;
		statusHandler.updateLog("[" + date + "] " + newErrorMessage);
		statusHandler.updateError("[" + date + "] " + newErrorMessage);
		log.error(lastErrorMessage);
	}

}
