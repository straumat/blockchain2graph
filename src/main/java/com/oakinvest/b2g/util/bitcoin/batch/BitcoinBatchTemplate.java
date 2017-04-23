package com.oakinvest.b2g.util.bitcoin.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService;
import com.oakinvest.b2g.util.bitcoin.mapper.BitcoindToDomainMapper;
import org.mapstruct.factory.Mappers;
import org.neo4j.ogm.session.Session;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bitcoin import batch - abstract model.
 * Created by straumat on 27/02/17.
 */
public abstract class BitcoinBatchTemplate {

	/**
	 * Genesis transaction hash.
	 */
	protected static final String GENESIS_BLOCK_TRANSACTION = "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b";

	/**
	 * How many milli seconds in one second.
	 */
	private static final float MILLISECONDS_IN_SECONDS = 1000F;

	/**
	 * Log separator.
	 */
	private static final String LOG_SEPARATOR = "=====";

	/**
	 * Mapper.
	 */
	private final BitcoindToDomainMapper mapper = Mappers.getMapper(BitcoindToDomainMapper.class);

	/**
	 * BitcoinBlock repository.
	 */
	private BitcoinBlockRepository blockRepository;

	/**
	 * BitcoinBlock repository.
	 */
	private BitcoinAddressRepository addressRepository;

	/**
	 * Bitcoin block repository.
	 */
	private BitcoinTransactionRepository transactionRepository;

	/**
	 * Bitcoind service.
	 */
	private BitcoindService bitcoindService;

	/**
	 * Status service.
	 */
	private StatusService status;

	/**
	 * Neo4j session.
	 */
	private Session session;

	/**
	 * time of the start of the batch.
	 */
	private long batchStartTime;

	/**
	 * Constructor.
	 *
	 * @param newBlockRepository       blockRepository
	 * @param newAddressRepository     addressRepository
	 * @param newTransactionRepository transactionRepository
	 * @param newBitcoindService       bitcoindService
	 * @param newStatus                status
	 * @param newSession               session
	 */
	public BitcoinBatchTemplate(final BitcoinBlockRepository newBlockRepository, final BitcoinAddressRepository newAddressRepository, final BitcoinTransactionRepository newTransactionRepository, final BitcoindService newBitcoindService, final StatusService newStatus, final Session newSession) {
		this.blockRepository = newBlockRepository;
		this.addressRepository = newAddressRepository;
		this.transactionRepository = newTransactionRepository;
		this.bitcoindService = newBitcoindService;
		this.status = newStatus;
		this.session = newSession;
	}

	/**
	 * Returns the elapsted time of the batch.
	 *
	 * @return elapsed time of the batch.
	 */
	private float getElapsedTime() {
		return (System.currentTimeMillis() - batchStartTime) / MILLISECONDS_IN_SECONDS;
	}

	/**
	 * Returns the logger prefix to display in each logger.
	 *
	 * @return logger prefix
	 */
	protected abstract String getLogPrefix();

	/**
	 * Execute the batch.
	 */
	@Transactional
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void execute() {
		addLog(LOG_SEPARATOR);
		batchStartTime = System.currentTimeMillis();
		try {
			// We retrieve the block to process.
			Long blockHeightToProcess = getBlockHeightToProcess();

			// If there is a block to process.
			if (blockHeightToProcess != null) {
				addLog("Starting to process block " + getFormattedBlockHeight(blockHeightToProcess));
				BitcoinBlock blockToProcess = processBlock(blockHeightToProcess);
				if (blockToProcess != null) {
					// If the block has been well processed, we change the state.
					blockToProcess.setState(getNewStateOfProcessedBlock());
					blockRepository.save(blockToProcess);
					addLog("Block " + blockToProcess.getFormattedHeight() + " processed in " + getElapsedTime() + " secs");
				}
			} else {
				// If there is nothing to process.
				addLog("No block to process");
			}
		} catch (Exception e) {
			addError("An error occurred while processing block : " + e.getMessage(), e);
		} finally {
			getSession().clear();
		}
	}

	/**
	 * Return the block to process.
	 *
	 * @return block to process.
	 */
	protected abstract Long getBlockHeightToProcess();

	/**
	 * Treat block.
	 *
	 * @param blockHeight block height to process.
	 * @return the block processed
	 */
	protected abstract BitcoinBlock processBlock(long blockHeight);

	/**
	 * Return the state to set to the block that has been processed.
	 *
	 * @return state to set of the block that has been processed.
	 */
	protected abstract BitcoinBlockState getNewStateOfProcessedBlock();

	/**
	 * Returns the block height in a formatted way.
	 *
	 * @param blockHeight block height
	 * @return formatted block height
	 */
	protected final String getFormattedBlockHeight(final long blockHeight) {
		return String.format("%09d", blockHeight);
	}

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
		status.addError(getLogPrefix() + " - " + message, null);
	}

	/**
	 * Add an error to the status and the logs.
	 *
	 * @param message message
	 * @param e       exception raised.
	 */
	protected final void addError(final String message, final Exception e) {
		status.addError(getLogPrefix() + " - " + message, e);
	}

	/**
	 * Getter de la propriété mapper.
	 *
	 * @return mapper
	 */
	protected final BitcoindToDomainMapper getMapper() {
		return mapper;
	}

	/**
	 * Getter de la propriété blockRepository.
	 *
	 * @return blockRepository
	 */
	protected final BitcoinBlockRepository getBlockRepository() {
		return blockRepository;
	}

	/**
	 * Getter de la propriété addressRepository.
	 *
	 * @return addressRepository
	 */
	protected final BitcoinAddressRepository getAddressRepository() {
		return addressRepository;
	}

	/**
	 * Getter de la propriété transactionRepository.
	 *
	 * @return transactionRepository
	 */
	protected final BitcoinTransactionRepository getTransactionRepository() {
		return transactionRepository;
	}

	/**
	 * Getter de la propriété bitcoindService.
	 *
	 * @return bitcoindService
	 */
	protected final BitcoindService getBitcoindService() {
		return bitcoindService;
	}

	/**
	 * Getter de la propriété status.
	 *
	 * @return status
	 */
	protected final StatusService getStatus() {
		return status;
	}

	/**
	 * Getter de la propriété session.
	 *
	 * @return session
	 */
	protected final Session getSession() {
		return session;
	}

}
