package com.oakinvest.b2g.batch.bitcoin.step4.fix;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import static com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState.EMPTY_TRANSACTIONS_FIXED;

/**
 * Bitcoin batch fixing empty transactions.
 * Created by straumat on 08/05/17.
 */
@Component
public class BitcoinBatchEmptyTransaction extends BitcoinBatchTemplate {

	/**
	 * Constructor.
	 *
	 * @param newBlockRepository       blockRepository
	 * @param newAddressRepository     addressRepository
	 * @param newTransactionRepository transactionRepository
	 * @param newBitcoindService       bitcoindService
	 * @param newStatus                status
	 */
	public BitcoinBatchEmptyTransaction(final BitcoinBlockRepository newBlockRepository, final BitcoinAddressRepository newAddressRepository, final BitcoinTransactionRepository newTransactionRepository, final BitcoindService newBitcoindService, final StatusService newStatus) {
		super(newBlockRepository, newAddressRepository, newTransactionRepository, newBitcoindService, newStatus);
	}

	/**
	 * Returns the logger prefix to display in each logger.
	 *
	 * @return logger prefix
	 */
	@Override
	protected final String getLogPrefix() {
		return "";
	}

	/**
	 * Return the block to process.
	 *
	 * @return block to process.
	 */
	@Override
	protected final Long getBlockHeightToProcess() {
		BitcoinBlock blockToTreat = getBlockRepository().findFirstBlockByState(BitcoinBlockState.TRANSACTIONS_IMPORTED);
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
	 * @return the block processed
	 */
	@Override
	protected final BitcoinBlock processBlock(final long blockHeight) {
		final BitcoinBlock blockToTreat = getBlockRepository().findByHeight(blockHeight);
		blockToTreat.getTransactions()
				.stream()
				.filter(t -> t.getInputs().size() == 0 || t.getOutputs().size() == 0)
				.forEach(t -> fixEmptyTransaction(t.getTxId()));
		return blockToTreat;
	}

	/**
	 * Return the state to set to the block that has been processed.
	 *
	 * @return state to set of the block that has been processed.
	 */
	@Override
	protected final BitcoinBlockState getNewStateOfProcessedBlock() {
		return EMPTY_TRANSACTIONS_FIXED;
	}

	/**
	 * Fix a random bug. Sometimes, transactions are saved without vin & vout.
	 *
	 * @param txid transaction id
	 */
	private void fixEmptyTransaction(final String txid) {
		addError("fixEmptyTransaction on transaction " + txid);
		BitcoinTransaction originTransaction = getTransactionRepository().findByTxId(txid);

		// We retrieve the original transaction data from bitcoind.
		GetRawTransactionResult getRawTransactionResult = getBitcoindService().getRawTransaction(txid).getResult();

		// Treating all vin.
		getRawTransactionResult.getVin()
				.forEach(vin -> {
					BitcoinTransactionInput bti = getMapper().rawTransactionVIn(vin);
					originTransaction.getInputs().add(bti);
				});

		// Treating all vout.
		getRawTransactionResult.getVout()
				.forEach(vout -> {
					BitcoinTransactionOutput bto = getMapper().rawTransactionVout(vout);
					originTransaction.getOutputs().add(bto);
				});

		// We save.
		getTransactionRepository().save(originTransaction);
	}

}
