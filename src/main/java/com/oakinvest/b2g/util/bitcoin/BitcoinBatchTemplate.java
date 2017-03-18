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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Bitcoin import batch - abstract model.
 * Created by straumat on 27/02/17.
 */
public abstract class BitcoinBatchTemplate {

	/**
	 * Pause between imports - Not used for the moment because of multi threading problems.
	 */
	protected static final int PAUSE_BETWEEN_IMPORTS = 10;

	/**
	 * Log separator.
	 */
	protected static final String LOG_SEPARATOR = "-------------------------------------------------------------------------------------------------------";

	/**
	 * How many milli seconds in one second.
	 */
	protected static final float MILLISECONDS_IN_SECONDS = 1000F;

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
	private BitcoindService bds;

	/**
	 * Mapper.
	 */
	@Autowired
	private BitcoindToDomainMapper mapper;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinBlockRepository bbr;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinAddressRepository bar;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinTransactionRepository btr;

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
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public BitcoindBlockData getBlockDataFromBitcoind(final long blockNumber) {
		try {
			// ---------------------------------------------------------------------------------------------------------
			// We retrieve the block hash...
			GetBlockHashResponse blockHashResponse = bds.getBlockHash(blockNumber);
			if (blockHashResponse.getError() == null) {
				// -----------------------------------------------------------------------------------------------------
				// Then we retrieve the block data...
				String blockHash = blockHashResponse.getResult();
				final GetBlockResponse blockResponse = bds.getBlock(blockHash);
				if (blockResponse.getError() == null) {
					// -------------------------------------------------------------------------------------------------
					// Then we retrieve the transactions data...
					final HashMap<String, GetRawTransactionResult> transactions = new LinkedHashMap<>();
					for (Iterator<String> transactionsHashs = blockResponse.getResult().getTx().iterator(); transactionsHashs.hasNext(); ) {
						String t = transactionsHashs.next();
						if (!t.equals(GENESIS_BLOCK_TRANSACTION)) {
							GetRawTransactionResponse r = bds.getRawTransaction(t);
							if (r.getError() == null) {
								transactions.put(t, bds.getRawTransaction(t).getResult());
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
	public abstract String getLogPrefix();

	/**
	 * Import data.
	 */
	public abstract void importData();

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
	public final Logger getLogger() {
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
	 * Getter bds.
	 *
	 * @return bds
	 */
	public final BitcoindService getBds() {
		return bds;
	}

	/**
	 * Setter bds.
	 *
	 * @param newBds the bds to set
	 */
	public final void setBds(final BitcoindService newBds) {
		bds = newBds;
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
	 * Getter bbr.
	 *
	 * @return bbr
	 */
	public final BitcoinBlockRepository getBbr() {
		return bbr;
	}

	/**
	 * Setter bbr.
	 *
	 * @param newBbr the bbr to set
	 */
	public final void setBbr(final BitcoinBlockRepository newBbr) {
		bbr = newBbr;
	}

	/**
	 * Getter bar.
	 *
	 * @return bar
	 */
	public final BitcoinAddressRepository getBar() {
		return bar;
	}

	/**
	 * Setter bar.
	 *
	 * @param newBar the bar to set
	 */
	public final void setBar(final BitcoinAddressRepository newBar) {
		bar = newBar;
	}

	/**
	 * Getter btr.
	 *
	 * @return btr
	 */
	public final BitcoinTransactionRepository getBtr() {
		return btr;
	}

	/**
	 * Setter btr.
	 *
	 * @param newBtr the btr to set
	 */
	public final void setBtr(final BitcoinTransactionRepository newBtr) {
		btr = newBtr;
	}

}
