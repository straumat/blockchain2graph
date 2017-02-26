package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.BitcoindService;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.util.bitcoin.BitcoindToDomainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class for task.
 * Created by straumat on 26/02/17.
 */
public abstract class BitcoinImportBatchTask {

	/**
	 * Logger.
	 */
	private Logger log = LoggerFactory.getLogger(BitcoinImportBatchTask.class);

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
	 * Bitcoin transaction repository.
	 */
	@Autowired
	private BitcoinTransactionRepository btr;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinAddressRepository bar;

	/**
	 * Mapper.
	 */
	@Autowired
	private BitcoindToDomainMapper mapperBtD;

	/**
	 * Set environment.
	 *
	 * @param newLog       log
	 * @param newBds       bds
	 * @param newStatus    status
	 * @param newBtr       btr
	 * @param newBar       bar
	 * @param newBTDMapper mapper
	 */
	// TODO why ioc doesn't work ?
	public final void setEnvironment(final Logger newLog, final BitcoindService newBds, final StatusService newStatus, final BitcoinTransactionRepository newBtr, final BitcoinAddressRepository newBar, final BitcoindToDomainMapper newBTDMapper) {
		log = newLog;
		bds = newBds;
		status = newStatus;
		btr = newBtr;
		bar = newBar;
		mapperBtD = newBTDMapper;
	}

	/**
	 * Getter de la propriété mapperBtD.
	 *
	 * @return mapperBtD
	 */
	final BitcoindToDomainMapper getMapperBtD() {
		return mapperBtD;
	}

	/**
	 * Getter de la propriété log.
	 *
	 * @return log
	 */
	final Logger getLog() {
		return log;
	}

	/**
	 * Getter de la propriété status.
	 *
	 * @return status
	 */
	final StatusService getStatus() {
		return status;
	}

	/**
	 * Getter de la propriété bds.
	 *
	 * @return bds
	 */
	final BitcoindService getBds() {
		return bds;
	}

	/**
	 * Getter de la propriété btr.
	 *
	 * @return btr
	 */
	final BitcoinTransactionRepository getBtr() {
		return btr;
	}

	/**
	 * Getter de la propriété bar.
	 *
	 * @return bar
	 */
	final BitcoinAddressRepository getBar() {
		return bar;
	}
}
