package com.oakinvest.b2g.util.bitcoin;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Bitcoin import batch - abstract model.
 * Created by straumat on 27/02/17.
 */
public abstract class BitcoinBatchTemplate {

	/**
	 * Log separator.
	 */
	protected static final String LOG_SEPARATOR = "-------------------------------------------------------------------------------------------------------";

	/**
	 * Genesis transaction hash.
	 */
	protected static final String GENESIS_BLOCK_TRANSACTION = "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b";

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
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinBlockRepository blockRepository;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinAddressRepository addressRepository;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinTransactionRepository transactionRepository;

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
	 * Returns the block data from bitcoind.
	 *
	 * @param blockNumber block number
	 * @return block data or null if a problem occurred.
	 */
	@Cacheable(value = "blockData", unless = "#result != null")
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public BitcoindBlockData getBlockDataFromBitcoind(final long blockNumber) {
		try {
			// ---------------------------------------------------------------------------------------------------------
			// We retrieve the block hash...
			GetBlockHashResponse blockHashResponse = bitcoindService.getBlockHash(blockNumber);
			if (blockHashResponse.getError() == null) {
				// -----------------------------------------------------------------------------------------------------
				// Then we retrieve the block data...
				String blockHash = blockHashResponse.getResult();
				final GetBlockResponse blockResponse = bitcoindService.getBlock(blockHash);
				if (blockResponse.getError() == null) {
					// -------------------------------------------------------------------------------------------------
					// Then we retrieve the transactions data...
					final HashMap<String, GetRawTransactionResult> transactions = new LinkedHashMap<>();
					for (Iterator<String> transactionsHashs = blockResponse.getResult().getTx().iterator(); transactionsHashs.hasNext(); ) {
						String t = transactionsHashs.next();
						if (!t.equals(GENESIS_BLOCK_TRANSACTION)) {
							GetRawTransactionResponse r = bitcoindService.getRawTransaction(t);
							if (r.getError() == null) {
								transactions.put(t, bitcoindService.getRawTransaction(t).getResult());
							} else {
								status.addError("Error getting transaction n°" + t + " informations : " + r.getError());
								return null;
							}
						}
					}
					// -------------------------------------------------------------------------------------------------
					// And we end up returning all the block data all at once.
					return new BitcoindBlockData(blockResponse.getResult(), transactions);
				} else {
					// Error while retrieving the block informations.
					status.addError("Error getting block n°" + getFormattedBlock(blockNumber) + " informations : " + blockResponse.getError());
					return null;
				}
			} else {
				// Error while retrieving the block hash.
				status.addError("Error getting the hash of block n°" + getFormattedBlock(blockNumber) + " : " + blockHashResponse.getError());
				return null;
			}
		} catch (Exception e) {
			status.addError("Error getting the block data of block n°" + getFormattedBlock(blockNumber) + " : " + e.getMessage());
			getLogger().error(e.getStackTrace().toString());
			return null;
		}
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

}
