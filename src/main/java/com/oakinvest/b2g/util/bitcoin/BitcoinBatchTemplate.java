package com.oakinvest.b2g.util.bitcoin;

import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionInputRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionOutputRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Bitcoin import batch - abstract model.
 * Created by straumat on 27/02/17.
 */
@Service
public abstract class BitcoinBatchTemplate {

	/**
	 * Log separator.
	 */
	protected static final String LOG_SEPARATOR = "---";

	/**
	 * How many milli seconds in one second.
	 */
	protected static final float MILLISECONDS_IN_SECONDS = 1000F;

	/**
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(BitcoinBatchTemplate.class);

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bitcoindService;

	/**
	 * Mapper.
	 */
	@Autowired
	private BitcoindToDomainMapper mapper;

	/**
	 * BitcoinBlock repository.
	 */
	@Autowired
	private BitcoinBlockRepository blockRepository;

	/**
	 * BitcoinBlock repository.
	 */
	@Autowired
	private BitcoinAddressRepository addressRepository;

	/**
	 * BitcoinTransactionInputRepository repository.
	 */
	@Autowired
	private BitcoinTransactionInputRepository transactionInputRepository;

	/**
	 * BitcoinTransactionOutputRepository repository.
	 */
	@Autowired
	private BitcoinTransactionOutputRepository transactionOutputRepository;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinTransactionRepository transactionRepository;

	/**
	 * Neo4j session.
	 */
	@Autowired
	private Session session;

	/**
	 * Getter de la propriété session.
	 *
	 * @return session
	 */
	public final Session getSession() {
		return session;
	}

	/**
	 * Returns the block height in a formatted way.
	 *
	 * @param blockHeight block height
	 * @return formatted block height
	 */
	protected final String getFormattedBlock(final long blockHeight) {
		return String.format("%09d", blockHeight);
	}


	/**
	 * Returns the logger prefix to display in each logger.
	 *
	 * @return logger prefix
	 */
	protected abstract String getLogPrefix();

	/**
	 * Import data.
	 */
	public abstract void process();

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
	protected final Logger getLogger() {
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
	 * Getter bitcoindService.
	 *
	 * @return bitcoindService
	 */
	public final BitcoindService getBitcoindService() {
		return bitcoindService;
	}

	/**
	 * Setter bitcoindService.
	 *
	 * @param newBds the bitcoindService to set
	 */
	public final void setBitcoindService(final BitcoindService newBds) {
		bitcoindService = newBds;
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
	 * Getter blockRepository.
	 *
	 * @return blockRepository
	 */
	public final BitcoinBlockRepository getBlockRepository() {
		return blockRepository;
	}

	/**
	 * Setter blockRepository.
	 *
	 * @param newBbr the blockRepository to set
	 */
	public final void setBlockRepository(final BitcoinBlockRepository newBbr) {
		blockRepository = newBbr;
	}

	/**
	 * Getter addressRepository.
	 *
	 * @return addressRepository
	 */
	public final BitcoinAddressRepository getAddressRepository() {
		return addressRepository;
	}

	/**
	 * Setter addressRepository.
	 *
	 * @param newBar the addressRepository to set
	 */
	public final void setAddressRepository(final BitcoinAddressRepository newBar) {
		addressRepository = newBar;
	}

	/**
	 * Getter transactionRepository.
	 *
	 * @return transactionRepository
	 */
	public final BitcoinTransactionRepository getTransactionRepository() {
		return transactionRepository;
	}

	/**
	 * Setter transactionRepository.
	 *
	 * @param newBtr the transactionRepository to set
	 */
	public final void setTransactionRepository(final BitcoinTransactionRepository newBtr) {
		transactionRepository = newBtr;
	}

	/**
	 * Getter transactionInputRepository.
	 *
	 * @return transactionInputRepository
	 */
	public final BitcoinTransactionInputRepository getTransactionInputRepository() {
		return transactionInputRepository;
	}

	/**
	 * Setter transactionInputRepository.
	 *
	 * @param newTransactionInputRepository the transactionInputRepository to set
	 */
	public final void setTransactionInputRepository(final BitcoinTransactionInputRepository newTransactionInputRepository) {
		transactionInputRepository = newTransactionInputRepository;
	}

	/**
	 * Getter transactionOutputRepository.
	 *
	 * @return transactionOutputRepository
	 */
	public final BitcoinTransactionOutputRepository getTransactionOutputRepository() {
		return transactionOutputRepository;
	}

	/**
	 * Setter transactionOutputRepository.
	 *
	 * @param newTransactionOutputRepository the transactionOutputRepository to set
	 */
	public final void setTransactionOutputRepository(final BitcoinTransactionOutputRepository newTransactionOutputRepository) {
		transactionOutputRepository = newTransactionOutputRepository;
	}

}
