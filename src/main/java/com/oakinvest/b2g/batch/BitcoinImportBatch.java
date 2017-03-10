package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.BitcoindService;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.util.bitcoin.BitcoindToDomainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Bitcoin import batch - abstract model.
 * Created by straumat on 27/02/17.
 */
public abstract class BitcoinImportBatch {

	/**
	 * Pause between imports - Not used for the moment because of multi threading problems.
	 */
	protected static final int PAUSE_BETWEEN_IMPORTS = 10;

	/**
	 * Log separator.
	 */
	protected static final String LOG_SEPARATOR = "-------------------------------------------------------------------------------------------------------";

	/**
	 * Pause between calls for checking if all transactions ar done.
	 */
	protected static final int PAUSE_BETWEEN_CHECKS = 1000;

	/**
	 * How many milli seconds in one second.
	 */
	protected static final float MILLISECONDS_IN_SECONDS = 1000F;

	/**
	 * Genesis transaction hash.
	 */
	protected static final String GENESIS_BLOCK_TRANSACTION = "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b";

	/**
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(BitcoinImportBatch.class);

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bds;

	/**
	 * Mapper.
	 */
	@Autowired
	private BitcoindToDomainMapper mapper;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinBlockRepository bbr;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinAddressRepository bar;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinTransactionRepository btr;

	/**
	 * Returns the block height in a formated way.
	 *
	 * @param blockHeight block height
	 * @return formated block height
	 */
	protected final String getFormatedBlock(final long blockHeight) {
		return String.format("%09d", blockHeight);
	}

	/**
	 * Returns the logger prefix to display in each logger.
	 *
	 * @return logger prefix
	 */
	public abstract String getLogPrefix();

	/**
	 * Import data.
	 */
	public abstract void importData();

	/**
	 * Add a logger to the status and the logs.
	 *
	 * @param message message
	 */
	protected final void addLog(final String message) {
		status.addLog(getLogPrefix() + " - " + message);
	}

	/**
	 * Add an error to the status and the logs.
	 *
	 * @param message message
	 */
	protected final void addError(final String message) {
		status.addError(getLogPrefix() + " - " + message);
	}

	/**
	 * Getter logger.
	 *
	 * @return logger
	 */
	public final Logger getLogger() {
		return logger;
	}

	/**
	 * Getter status.
	 *
	 * @return status
	 */
	public final StatusService getStatus() {
		return status;
	}

	/**
	 * Setter status.
	 *
	 * @param newStatus the status to set
	 */
	public final void setStatus(final StatusService newStatus) {
		status = newStatus;
	}

	/**
	 * Getter bds.
	 *
	 * @return bds
	 */
	public final BitcoindService getBds() {
		return bds;
	}

	/**
	 * Setter bds.
	 *
	 * @param newBds the bds to set
	 */
	public final void setBds(final BitcoindService newBds) {
		bds = newBds;
	}

	/**
	 * Getter mapper.
	 *
	 * @return mapper
	 */
	public final BitcoindToDomainMapper getMapper() {
		return mapper;
	}

	/**
	 * Setter mapper.
	 *
	 * @param newMapper the mapper to set
	 */
	public final void setMapper(final BitcoindToDomainMapper newMapper) {
		mapper = newMapper;
	}

	/**
	 * Getter bbr.
	 *
	 * @return bbr
	 */
	public final BitcoinBlockRepository getBbr() {
		return bbr;
	}

	/**
	 * Setter bbr.
	 *
	 * @param newBbr the bbr to set
	 */
	public final void setBbr(final BitcoinBlockRepository newBbr) {
		bbr = newBbr;
	}

	/**
	 * Getter bar.
	 *
	 * @return bar
	 */
	public final BitcoinAddressRepository getBar() {
		return bar;
	}

	/**
	 * Setter bar.
	 *
	 * @param newBar the bar to set
	 */
	public final void setBar(final BitcoinAddressRepository newBar) {
		bar = newBar;
	}

	/**
	 * Getter btr.
	 *
	 * @return btr
	 */
	public final BitcoinTransactionRepository getBtr() {
		return btr;
	}

	/**
	 * Setter btr.
	 *
	 * @param newBtr the btr to set
	 */
	public final void setBtr(final BitcoinTransactionRepository newBtr) {
		btr = newBtr;
	}
}
