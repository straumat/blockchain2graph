package com.oakinvest.b2g.util.bitcoin.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import com.oakinvest.b2g.util.bitcoin.mapper.BitcoindToDomainMapper;
import org.mapstruct.factory.Mappers;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bitcoin import batch - abstract model.
 * Created by straumat on 27/02/17.
 */
public abstract class BitcoinBatchTemplate {

	/**
	 * How many milli seconds in one second.
	 */
	private static final float MILLISECONDS_IN_SECONDS = 1000F;

	/**
	 * Log separator.
	 */
	private static final String LOG_SEPARATOR = "=====";

	/**
	 * Pause to make when there is no block to process.
	 */
	private static final int PAUSE_WHEN_NO_BLOCK_TO_PROCESS = 1000;

	/**
	 * Mapper.
	 */
	private final BitcoindToDomainMapper mapper = Mappers.getMapper(BitcoindToDomainMapper.class);

	/**
	 * Bitcoin block repository.
	 */
	private final BitcoinBlockRepository blockRepository;

	/**
	 * Bitcoin address repository.
	 */
	private final BitcoinAddressRepository addressRepository;

	/**
	 * Bitcoin transaction repository.
	 */
	private final BitcoinTransactionRepository transactionRepository;

	/**
	 * Bitcoind service.
	 */
	private final BitcoindService bitcoindService;

	/**
	 * Status service.
	 */
	private final StatusService status;

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
	 */
	public BitcoinBatchTemplate(final BitcoinBlockRepository newBlockRepository, final BitcoinAddressRepository newAddressRepository, final BitcoinTransactionRepository newTransactionRepository, final BitcoindService newBitcoindService, final StatusService newStatus) {
		this.blockRepository = newBlockRepository;
		this.addressRepository = newAddressRepository;
		this.transactionRepository = newTransactionRepository;
		this.bitcoindService = newBitcoindService;
		this.status = newStatus;
		this.session = new SessionFactory("com.oakinvest.b2g").openSession();
	}

	/**
	 * Returns the elapsed time of the batch.
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
	@Scheduled(fixedDelay = 1)
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
					// If we are in the status "block imported", we add where we are
					if (getNewStateOfProcessedBlock() == BitcoinBlockState.IMPORTED) {
						status.setImportedBlockCount(blockToProcess.getHeight());
					}
				}
			} else {
				// If there is nothing to process.
				addLog("No block to process");
				Thread.sleep(PAUSE_WHEN_NO_BLOCK_TO_PROCESS);
			}
		} catch (Exception e) {
			addError("An error occurred while processing block " + getBlockHeightToProcess() + " : " + e.getMessage(), e);
			session = new SessionFactory("com.oakinvest.b2g").openSession();
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
	 * Getter mapper.
	 *
	 * @return mapper
	 */
	protected final BitcoindToDomainMapper getMapper() {
		return mapper;
	}

	/**
	 * Getter blockRepository.
	 *
	 * @return blockRepository
	 */
	protected final BitcoinBlockRepository getBlockRepository() {
		return blockRepository;
	}

	/**
	 * Getter addressRepository.
	 *
	 * @return addressRepository
	 */
	protected final BitcoinAddressRepository getAddressRepository() {
		return addressRepository;
	}

	/**
	 * Getter transactionRepository.
	 *
	 * @return transactionRepository
	 */
	protected final BitcoinTransactionRepository getTransactionRepository() {
		return transactionRepository;
	}

	/**
	 * Getter bitcoindService.
	 *
	 * @return bitcoindService
	 */
	protected final BitcoindService getBitcoindService() {
		return bitcoindService;
	}

	/**
	 * Getter status.
	 *
	 * @return status
	 */
	protected final StatusService getStatus() {
		return status;
	}

	/**
	 * Getter session.
	 *
	 * @return session
	 */
	protected final Session getSession() {
		return session;
	}

}
