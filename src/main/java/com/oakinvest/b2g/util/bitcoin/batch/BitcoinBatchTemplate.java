package com.oakinvest.b2g.util.bitcoin.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinRepositories;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionInputRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionOutputRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoinDataServiceCacheStore;
import com.oakinvest.b2g.util.bitcoin.mapper.BitcoindToDomainMapper;
import org.mapstruct.factory.Mappers;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState.BLOCK_IMPORTED;

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
     * Bitcoin transaction input repository.
     */
    private final BitcoinTransactionInputRepository transactionInputRepository;

    /**
     * Bitcoin transaction output repository.
     */
    private final BitcoinTransactionOutputRepository transactionOutputRepository;

    /**
	 * Bitcoin data service.
	 */
	private final BitcoinDataService bitcoinDataService;

	/**
	 * Status service.
	 */
	private final StatusService status;

	/**
	 * Neo4j session.
	 */
	private final Session session;

	/**
	 * time of the start of the batch.
	 */
	private long batchStartTime;

    /**
     * Cache store.
     */
    private final BitcoinDataServiceCacheStore cacheStore;

	/**
	 * Constructor.
     *
	 * @param newBitcoinRepositories    bitcoin repositories
     * @param newBitcoinDataService     bitcoin data service
     * @param newStatus                 status
     * @param newCacheStore             cache store
     */
	public BitcoinBatchTemplate(final BitcoinRepositories newBitcoinRepositories, final BitcoinDataService newBitcoinDataService, final StatusService newStatus, final BitcoinDataServiceCacheStore newCacheStore) {
        this.addressRepository = newBitcoinRepositories.getBitcoinAddressRepository();
	    this.blockRepository = newBitcoinRepositories.getBitcoinBlockRepository();
		this.transactionRepository = newBitcoinRepositories.getBitcoinTransactionRepository();
		this.transactionInputRepository = newBitcoinRepositories.getBitcoinTransactionInputRepository();
		this.transactionOutputRepository = newBitcoinRepositories.getBitcoinTransactionOutputRepository();
        this.bitcoinDataService = newBitcoinDataService;
        this.cacheStore = newCacheStore;
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
	@SuppressWarnings("checkstyle:designforextension")
	public void execute() {
		addLog(LOG_SEPARATOR);
		batchStartTime = System.currentTimeMillis();
		try {
			// We retrieve the block to process.
            Optional<Long> blockHeightToProcess = getBlockHeightToProcess();

			// If there is a block to process.
			if (blockHeightToProcess.isPresent()) {
			    // Process the block.
				addLog("Starting to process block " + getFormattedBlockHeight(blockHeightToProcess.get()));
                Optional<BitcoinBlock> blockToProcess = processBlock(blockHeightToProcess.get());

                // If the process ended well.
                blockToProcess.ifPresent(bitcoinBlock -> {
                    // If the block has been well processed, we change the state and we save it.
                    bitcoinBlock.setState(getNewStateOfProcessedBlock());
                    getBlockRepository().save(bitcoinBlock);
                    addLog("Block " + bitcoinBlock.getFormattedHeight() + " processed in " + getElapsedTime() + " secs");

                    // -------------------------------------------------------------------------------------------------
                    // Temporary fix : sometimes vins & vouts are missing.
                    // We check that the block just created have all the vin/vout.
                    // If not, we delete it to recreate it.
                    if (getNewStateOfProcessedBlock().equals(BLOCK_IMPORTED)) {
                        boolean validBlock = true;

                        // Getting the data from bitcoind.
                        cacheStore.removeBlockData(blockHeightToProcess.get());
                        Optional<BitcoindBlockData> blockData = getBitcoinDataService().getBlockData(blockHeightToProcess.get());

                        // If we did not succeed to get it, we delete anyway.
                        if (!blockData.isPresent()) {
                            validBlock = false;
                        } else {
                            // Checking all transactions.
                            for (String tx : bitcoinBlock.getTx()) {
                                // Getting the data in database & from bitcoind.
                                BitcoinTransaction bitcoinTransaction = getTransactionRepository().findByTxId(tx);
                                Optional<GetRawTransactionResult> bitcoindTransaction = blockData.get().getRawTransactionResult(tx);

                                // Checking vins & vouts.
                                if (bitcoindTransaction.isPresent()) {
                                    if (bitcoinTransaction.getInputs().size() != bitcoindTransaction.get().getVin().size()) {
                                        validBlock = false;
                                    }
                                    if (bitcoinTransaction.getOutputs().size() != bitcoindTransaction.get().getVout().size()) {
                                        validBlock = false;
                                    }
                                } else {
                                    validBlock = false;
                                }
                            }
                        }

                        // If the block is invalid, we delete it.
                        if (!validBlock) {
                            addError("Block " + bitcoinBlock.getHeight() + " is not correct - deleting it");
                            bitcoinBlock.getTransactions().forEach(t -> {
                                        BitcoinTransaction transactionToRemove = getTransactionRepository().findByTxId(t.getTxId());
                                        transactionToRemove.getOutputs().forEach(o -> getTransactionOutputRepository().delete(o));
                                        transactionToRemove.getInputs().forEach(i -> getTransactionInputRepository().delete(i));
                                        transactionToRemove.getOutputs().clear();
                                        transactionToRemove.getInputs().clear();
                                        getTransactionRepository().delete(transactionToRemove);
                                        bitcoinBlock.getTransactions().remove(transactionToRemove);
                                    }
                            );
                            getBlockRepository().delete(bitcoinBlock.getId());
                        }
                    }
                    // -------------------------------------------------------------------------------------------------

                    // -------------------------------------------------------------------------------------------------
                    // If we are in the status "block imported", we update the status of number of blocks imported.
                    if (getNewStateOfProcessedBlock() == BitcoinBlockState.IMPORTED) {
                        status.setImportedBlockCount(bitcoinBlock.getHeight());
                    }
                });
			} else {
				// If there is nothing to process.
				addLog("No block to process");
				Thread.sleep(PAUSE_WHEN_NO_BLOCK_TO_PROCESS);
			}
		} catch (Exception e) {
		    addError("An error occurred while processing block : " + e.getMessage(), e);
        } finally {
			getSession().clear();
		}
	}

    /**
     * Getter session.
     *
     * @return session
     */
    private Session getSession() {
        return session;
    }

    /**
	 * Return the block to process.
	 *
	 * @return block to process.
	 */
	protected abstract Optional<Long> getBlockHeightToProcess();

	/**
	 * Treat block.
	 *
	 * @param blockHeight block height to process.
	 * @return the block processed
	 */
	protected abstract Optional<BitcoinBlock> processBlock(long blockHeight);

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
	private void addError(final String message, final Exception e) {
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
     * Getter.
     * @return transactionInputRepository
     */
    protected final BitcoinTransactionInputRepository getTransactionInputRepository() {
        return transactionInputRepository;
    }

    /**
     * Getter.
     * @return transactionOutputRepository
     */
    protected final BitcoinTransactionOutputRepository getTransactionOutputRepository() {
        return transactionOutputRepository;
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
     * Getter bitcoin data service.
     *
     * @return bitcoin data service
     */
    protected final BitcoinDataService getBitcoinDataService() {
        return bitcoinDataService;
    }

}
