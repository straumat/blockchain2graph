package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * Task treating transactions.
 * Created by straumat on 25/02/17.
 */
@Component
public class BitcoinImportBatchTransactionTask extends BitcoinImportBatchTask {

	/**
	 * Create all the addresses used in the vout of a transaction.
	 *
	 * @param transactionHash transaction
	 * @return address list
	 */
	@Async
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public Future<Boolean> importTransaction(final String transactionHash) {
		BitcoinTransaction transaction = getBtr().findByTxId(transactionHash);
		if (transaction != null) {
			// If the transaction already exists in the database, we return it.
			getLog().info("importBlockTransactions : Transaction " + transactionHash + " already saved with id " + transaction.getId());
			return new AsyncResult<>(true);
		} else {
			// If the transaction is not in the database, we create it.
			GetRawTransactionResponse transactionResponse = getBds().getRawTransaction(transactionHash);
			if (transactionResponse.getError() == null) {
				// Success.
				try {
					// Saving the transaction in the database.
					BitcoinTransaction bt = getMapperBtD().rawTransactionResultToBitcoinTransaction(transactionResponse.getResult());

					// For each Vin.
					Iterator<BitcoinTransactionInput> vins = bt.getInputs().iterator();
					while (vins.hasNext()) {
						BitcoinTransactionInput vin = vins.next();
						vin.setTransaction(bt);
						if (vin.getTxId() != null) {
							// Not coinbase. We retrieve the original transaction.
							Optional<BitcoinTransactionOutput> originTransactionOutput = getBtr().findByTxId(vin.getTxId()).getOutputByIndex(vin.getvOut());
							if (originTransactionOutput.isPresent()) {
								vin.setTransactionOutput(originTransactionOutput.get());
								// We set the addresses "from" if it's not a coinbase transaction.
								originTransactionOutput.get().getAddresses().forEach(a -> (getBar().findByAddress(a)).getWithdrawals().add(vin));
								getLog().info("importBlockTransactions : Done treating vin : " + vin);
							} else {
								getStatus().addError("importBlockTransactions : Impossible to find the original output transaction " + vin.getTxId() + " / " + vin.getvOut());
								// As we did not find a transaction, we will use async to reimport it.
								importTransaction(vin.getTxId());
								return new AsyncResult<>(false);
							}
						}
					}

					// For each Vout.
					Iterator<BitcoinTransactionOutput> vouts = bt.getOutputs().iterator();
					while (vouts.hasNext()) {
						BitcoinTransactionOutput vout = vouts.next();
						vout.setTransaction(bt);
						vout.getAddresses().forEach(a -> (getBar().findByAddress(a)).getDeposits().add(vout));
						getLog().info("importBlockTransactions : Done treating vout : " + vout);
					}

					// Saving the transaction.
					getBtr().save(bt);
					getStatus().addLog("importBlockTransactions : Transaction " + transactionHash + " created with id " + bt.getId());
					return new AsyncResult<>(true);
				} catch (Exception e) {
					getStatus().addError("importBlockTransactions : Error treating transaction " + transactionHash + " : " + e.getMessage());
					getLog().error(e.toString());
					e.printStackTrace();
					return new AsyncResult<>(false);
				}
			} else {
				// Error.
				getStatus().addError("importBlockTransactions : Error in calling getrawtransaction on " + transactionHash + " : " + transactionResponse.getError());
				return new AsyncResult<>(false);
			}
		}
	}

}