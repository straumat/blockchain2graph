package com.oakinvest.b2g.service.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.util.bitcoin.BitcoindToDomainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Future;

/**
 * Bitcoin transaction integration task.
 * Created by straumat on 17/02/17.
 */
@Component
public class BitcoinTransactionImportTask {

	/**
	 * Logger.
	 */
	private Logger log = LoggerFactory.getLogger(BitcoinTransactionImportTask.class);

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bds;

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

	/**
	 * Bitcoin transaction repository.
	 */
	@Autowired
	private BitcoinTransactionRepository btr;

	/**
	 * Bitcoin address repository.
	 */
	@Autowired
	private BitcoinAddressRepository bar;

	/**
	 * Mapper.
	 */
	@Autowired
	private BitcoindToDomainMapper mapper;

	/**
	 * Set environment.
	 *
	 * @param nLog    log
	 * @param nBds    bds
	 * @param nStatus status
	 * @param nBtr    btr
	 * @param nBar    bar
	 * @param nMapper mapper
	 */
	public final void setEnvironment(final Logger nLog, final BitcoindService nBds, final StatusService nStatus, final BitcoinTransactionRepository nBtr, final BitcoinAddressRepository nBar, final BitcoindToDomainMapper nMapper) {
		log = nLog;
		bds = nBds;
		status = nStatus;
		btr = nBtr;
		bar = nBar;
		mapper = nMapper;
	}

	/**
	 * Create a transaction in the database.
	 *
	 * @param transactionHash transaction hash.
	 * @return transaction.
	 */
	@Async
	@Transactional
	@SuppressWarnings("checkstyle:designforextension")
	public Future<BitcoinTransaction> createTransaction(final String transactionHash) {
		return null;
	}

}
