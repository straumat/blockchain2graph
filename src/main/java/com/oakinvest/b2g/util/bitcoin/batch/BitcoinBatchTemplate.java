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
import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired
	private BitcoinBlockRepository blockRepository;

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
	 * BitcoinBlock repository.
	 */
	@Autowired
	private BitcoinAddressRepository addressRepository;

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
	 * time of the start of the batch.
	 */
	private long batchStartTime;

	/**
	 * Getter session.
	 *
	 * @return session
	 */
	protected final Session getSession() {
		return session;
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
			// We retrieve the block to treat.
			Long blockHeightToTreat = getBlockToTreat();

			// If there is a block to treat.
			if (blockHeightToTreat != null) {
				addLog("Starting to treat block " + getFormattedBlock(blockHeightToTreat));
				BitcoinBlock blockToTreat = treatBlock(blockHeightToTreat);
				if (blockToTreat != null) {
					// If the block has been well treated, we change the state.
					blockToTreat.setState(getNewStateOfTreatedBlock());
					blockRepository.save(blockToTreat);
					addLog("Block " + blockToTreat.getFormattedHeight() + " treated in " + getElapsedTime() + " secs");
				}
			} else {
				// If there is nothing to treat.
				addLog("No block to treat");
			}
		} catch (Exception e) {
			addError("An error occurred while treating block : " + e.getMessage(), e);
		} finally {
			getSession().clear();
		}
	}

	/**
	 * Return the block to treat.
	 *
	 * @return block to treat.
	 */
	protected abstract Long getBlockToTreat();

	/**
	 * Treat block.
	 *
	 * @param blockHeight block number to treat.
	 * @return the block treated
	 */
	protected abstract BitcoinBlock treatBlock(long blockHeight);

	/**
	 * Return the state to set to the block that has been treated.
	 *
	 * @return state to set of the block that has been treated.
	 */
	protected abstract BitcoinBlockState getNewStateOfTreatedBlock();

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
	 * Getter blockRepository.
	 *
	 * @return blockRepository
	 */
	protected final BitcoinBlockRepository getBlockRepository() {
		return blockRepository;
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
	 * Getter bitcoindService.
	 *
	 * @return bitcoindService
	 */
	protected final BitcoindService getBitcoindService() {
		return bitcoindService;
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

}
