package com.oakinvest.b2g.batch.bitcoin.step3.transactions;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
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
	 * Returns the log prefix to display in each log.
	 */
	@Override
	public final String getLogPrefix() {
		return PREFIX;
	}

	/**
	 * Return the block to treat.
	 *
	 * @return block to treat.
	 */
	@Override
	protected final Long getBlockToTreat() {
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
	 * @param blockHeight block number to treat.
	 */
	@Override
	protected final BitcoinBlock treatBlock(final long blockHeight) {
		BitcoindBlockData blockData = getBitcoindService().getBlockData(blockHeight);
		// -------------------------------------------------------------------------------------------------------------
		// If we have the data
		if (blockData != null) {
			// ---------------------------------------------------------------------------------------------------------
			// Creating all the transactions.
			blockData.getTransactions().stream()
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
			addError("No response from bitcoind for block n°" + getFormattedBlock(blockHeight));
			return null;
		}
	}

	/**
	 * Return the state to set to the block that has been treated.
	 *
	 * @return state to set of the block that has been treated.
	 */
	@Override
	protected final BitcoinBlockState getNewStateOfTreatedBlock() {
		return BitcoinBlockState.TRANSACTIONS_IMPORTED;
	}

}
