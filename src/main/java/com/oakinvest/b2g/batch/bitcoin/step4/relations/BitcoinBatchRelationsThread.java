package com.oakinvest.b2g.batch.bitcoin.step4.relations;

import com.oakinvest.b2g.batch.bitcoin.step3.transactions.BitcoinBatchTransactionsThread;
import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.Future;

/**
 * Thread that deals with relations of a transaction.
 * Created by straumat on 08/04/17.
 */
@Service
public class BitcoinBatchRelationsThread {

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(BitcoinBatchTransactionsThread.class);

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinTransactionRepository transactionRepository;

	/**
	 * Bitcoin address repository.
	 */
	@Autowired
	private BitcoinAddressRepository addressRepository;

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

	/**
	 * Treat transaction.
	 *
	 * @param transaction transaction
	 * @return transaction relations created
	 */
	@Async("transactionRelationsPoolTaskExecutor")
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public Future<Boolean> process(final BitcoinTransaction transaction) {
		try {
			// For each Vin.
			for (BitcoinTransactionInput vin : transaction.getInputs()) {
				if (vin.getTxId() != null) {
					// Not coinbase. We retrieve the original transaction.
					BitcoinTransaction originTransaction = transactionRepository.findByTxId(vin.getTxId());
					if (originTransaction == null) {
						// Origin transaction not yet in database.
						status.addError("Origin transaction " + vin.getTxId() + " not found");
						return new AsyncResult<>(false);
					}
					Optional<BitcoinTransactionOutput> originTransactionOutput = originTransaction.getOutputByIndex(vin.getvOut());
					if (originTransactionOutput.isPresent()) {
						// We set the addresses "from" if it's not a coinbase transaction.
						vin.setTransactionOutput(originTransactionOutput.get());

						// We set all the addresses linked to this input
						originTransactionOutput.get().getAddresses()
								.stream()
								.filter(a -> a != null)
								.forEach(a -> {
									BitcoinAddress address = addressRepository.findByAddress(a);
									address.getInputTransactions().add(vin);
									addressRepository.save(address);
								});

						log.info(transaction.getHash() + " - Done treating vin : " + vin);
					} else {
						originTransaction = transactionRepository.findByTxId(vin.getTxId());
						System.out.print("==> " + originTransaction.getInputs().size());
						System.out.print("==> " + originTransaction.getOutputs().size());
						status.addError("Origin transaction output " + vin.getTxId() + "/" + vin.getvOut() + " not found");
						return new AsyncResult<>(false);
					}
				}
			}

			// For each Vout.
			for (BitcoinTransactionOutput vout : transaction.getOutputs()) {
				vout.getAddresses()
						.stream()
						.filter(a -> a != null)
						.forEach(a -> {
							BitcoinAddress address = addressRepository.findByAddress(a);
							address.getOutputTransactions().add(vout);
							addressRepository.save(address);
						});
				log.info(transaction.getHash() + " - Done treating vout : " + vout);
			}

			transactionRepository.save(transaction);
			return new AsyncResult<>(true);
		} catch (Exception e) {
			log.error("Error in BitcoinBatchRelationsThread : " + e);
			return new AsyncResult<>(false);
		}
	}

}
