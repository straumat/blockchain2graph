package com.oakinvest.b2g.service;

import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
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
	@Qualifier("BitcoindServiceMock")   // FIXME Find a way to not set this in production.
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
	 * Returns the total nubmer of blocks in the blockchain.
	 *
	 * @return total block count
	 */
	@Override
	public long getTotalBlockCount() {
		return bds.getBlockCount().getResult();
	}

	/**
	 * Returns the number of the last block integrated.
	 *
	 * @return block number.
	 */
	@Override
	public long getLastBlockIntegrated() {
		return bbr.count();
	}

	/**
	 * Returns the last log message.
	 *
	 * @return last log message.
	 */
	@Override
	public String getLastLogMessage() {
		return lastLogMessage;
	}

	/**
	 * Add a log message.
	 *
	 * @param newLogMessage log message
	 */
	@Override
	public void addLogMessage(final String newLogMessage) {
		lastLogMessage = newLogMessage;
	}

	/**
	 * Returns the last error message.
	 *
	 * @return last error message.
	 */
	@Override
	public String getLastErrorMessage() {
		return lastErrorMessage;
	}

	/**
	 * Add an error message.
	 *
	 * @param newErrorMessage error message
	 */
	@Override
	public void addErrorMessage(final String newErrorMessage) {
		lastErrorMessage = newErrorMessage;
	}
}
