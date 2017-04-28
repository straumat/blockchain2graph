package com.oakinvest.b2g.batch.bitcoin.step3.transactions;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Component;

/**
 * Bitcoin import transactions batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinBatchTransactions extends BitcoinBatchTemplate {

	/**
	 * Log prefix.
	 */
	private static final String PREFIX = "Transactions batch";

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
	public BitcoinBatchTransactions(final BitcoinBlockRepository newBlockRepository, final BitcoinAddressRepository newAddressRepository, final BitcoinTransactionRepository newTransactionRepository, final BitcoindService newBitcoindService, final StatusService newStatus, final Session newSession) {
		super(newBlockRepository, newAddressRepository, newTransactionRepository, newBitcoindService, newStatus, newSession);
	}

	/**
	 * Returns the log prefix to display in each log.
	 */
	@Override
	public final String getLogPrefix() {
		return PREFIX;
	}

	/**
	 * Return the block to process.
	 *
	 * @return block to process.
	 */
	@Override
	protected final Long getBlockHeightToProcess() {
		BitcoinBlock blockToTreat = getBlockRepository().findFirstBlockByState(BitcoinBlockState.ADDRESSES_IMPORTED);
		if (blockToTreat != null) {
			return blockToTreat.getHeight();
		} else {
			return null;
		}
	}

	/**
	 * Treat block.
	 *
	 * @param blockHeight block height to process.
	 */
	@Override
	protected final BitcoinBlock processBlock(final long blockHeight) {
		BitcoindBlockData blockData = getBitcoindService().getBlockData(blockHeight);
		// -------------------------------------------------------------------------------------------------------------
		// If we have the data
		if (blockData != null) {
			// ---------------------------------------------------------------------------------------------------------
			// Creating all the transactions.
			blockData.getTransactions()
					.stream()
					// Only if the transaction is not already in the database.
					.filter(t -> getTransactionRepository().findByTxId(t.getTxid()) == null)
					// We save it in the database.
					.forEach(t -> {
						BitcoinTransaction transaction = getMapper().rawTransactionResultToBitcoinTransaction(t);
						getTransactionRepository().save(transaction);
						addLog(" - Transaction " + transaction.getTxId() + " created with id " + transaction.getId());
					});

			// ---------------------------------------------------------------------------------------------------------
			// We return the block.
			return getBlockRepository().findByHeight(blockHeight);
		} else {
			addError("No response from bitcoind for block n°" + getFormattedBlockHeight(blockHeight));
			return null;
		}
	}

	/**
	 * Return the state to set to the block that has been processed.
	 *
	 * @return state to set of the block that has been 	 * Return the state to set to the block that has been processed.
	 * .
	 */
	@Override
	protected final BitcoinBlockState getNewStateOfProcessedBlock() {
		return BitcoinBlockState.TRANSACTIONS_IMPORTED;
	}

}
