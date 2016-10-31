package com.oakinvest.b2g.service;

import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Status service implementation.
 * Created by straumat on 28/10/16.
 */
@Service
public class StatusServiceImplementation implements StatusService {

	/**
	 * Bitcoind service.
	 */
	@Autowired
	@Qualifier("BitcoindServiceImplementation")   // FIXME Find a way to not set this in production.
	private BitcoindService bds;

	/**
	 * Bitcoin blcok repository.
	 */
	@Autowired
	private BitcoinBlockRepository bbr;

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
	 * Returns the total nubmer of blocks in the blockchain.
	 *
	 * @return total block count
	 */
	@Override
	public final long getTotalBlockCount() {
		return totalBlockCount;
	}

	/**
	 * Set the total nubmer of blocks in the blockchain.
	 *
	 * @param newTotalBlockCount new value
	 */
	@Override
	public final void setTotalBlockCount(final long newTotalBlockCount) {
		totalBlockCount = newTotalBlockCount;
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
	public final void addLogMessage(final String newLogMessage) {
		lastLogMessage = newLogMessage;
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
	public final void addErrorMessage(final String newErrorMessage) {
		lastErrorMessage = newErrorMessage;
	}
}
