package com.oakinvest.b2g.batch.bitcoin.step3.transactions;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.repository.bitcoin.BitcoinRepositories;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
     * @param newBitcoinRepositories    bitcoin repositories
     * @param newBitcoinDataService     bitcoin data service
     * @param newStatus                 status
     */
    public BitcoinBatchTransactions(final BitcoinRepositories newBitcoinRepositories, final BitcoinDataService newBitcoinDataService, final StatusService newStatus) {
        super(newBitcoinRepositories, newBitcoinDataService, newStatus);
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
		BitcoinBlock block = getBlockRepository().findByHeight(blockHeight);
		Optional<BitcoindBlockData> blockData = getBitcoinDataService().getBlockData(blockHeight);
		// -------------------------------------------------------------------------------------------------------------
		// If we have the data
		if (blockData.isPresent()) {
			// ---------------------------------------------------------------------------------------------------------
			// Creating all the transactions.
			try {
				blockData.get().getTransactions()
						.stream()
						// Only if the transaction is not already in the database.
						.filter(t -> !getTransactionRepository().exists(t.getTxid()))
						// We save it in the database.
						.forEach(t -> {
							BitcoinTransaction transaction = getMapper().rawTransactionResultToBitcoinTransaction(t);
							//getTransactionRepository().save(transaction);
	//						addLog(" - Transaction " + transaction.getTxId() + " added");
						});
			} catch (Exception e) {
				addError("Error treating transaction : " + e.getMessage(), e);
				return null;
			}

			// ---------------------------------------------------------------------------------------------------------
			// We return the block.
			return block;
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
