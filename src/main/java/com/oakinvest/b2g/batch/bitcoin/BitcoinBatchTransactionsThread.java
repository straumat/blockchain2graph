package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.util.bitcoin.BitcoindToDomainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Future;

/**
 * Thread that deal with a transaction.
 * Created by straumat on 29/03/17.
 */
@Service
public class BitcoinBatchTransactionsThread {

	/**
	 * Logger.
	 */
	private Logger log = LoggerFactory.getLogger(BitcoinBatchTransactionsThread.class);

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
	 * Mapper.
	 */
	@Autowired
	private BitcoindToDomainMapper mapper;

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

	/**
	 * Treat transaction.
	 *
	 * @param transactionData transaction data
	 * @return BitcoinTransaction created
	 */
	@Async
	@Transactional
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public Future<Boolean> process(final GetRawTransactionResult transactionData) {
		BitcoinTransaction transaction = transactionRepository.findByTxId(transactionData.getHash());
		if (transaction != null) {
			// If the transaction already exists in the database, we return it.
			log.info("Transaction " + transactionData.getHash() + " is already in the database");
			return new AsyncResult<>(true);
		} else {
			try {
				// If the transaction is not in the database, we put it there.
				log.info("Transaction " + transactionData.getHash() + " is not in the database. Creating it");
				transaction = mapper.rawTransactionResultToBitcoinTransaction(transactionData);

				// For each Vin.
				int i = 1;
				int vinsSize = transaction.getInputs().size();
				Iterator<BitcoinTransactionInput> vins = transaction.getInputs().iterator();
				while (vins.hasNext()) {
					BitcoinTransactionInput vin = vins.next();
					transaction.getInputs().add(vin);
					vin.setTransaction(transaction);
					log.info(transactionData.getHash() + " - Done treating vin : " + vin + " (" + i + "/" + vinsSize + ")");
				}
				i++;


				// For each Vout.
				int j = 1;
				int voutsSize = transaction.getInputs().size();
				Iterator<BitcoinTransactionOutput> vouts = transaction.getOutputs().iterator();
				while (vouts.hasNext()) {
					BitcoinTransactionOutput vout = vouts.next();
					transaction.getOutputs().add(vout);
					vout.setTransaction(transaction);
					vout.getAddresses().stream()
							.filter(a -> a != null)
							.forEach(a -> {
								BitcoinAddress address = addressRepository.findByAddress(a);
								address.getOutputTransactions().add(vout);
								addressRepository.save(address);
							});
					log.info(transactionData.getHash() + " - Done treating vout : " + vout + " (" + j + "/" + voutsSize + ")");
					j++;
				}

				// Saving the transaction.
				transactionRepository.save(transaction);
				log.info(" - Transaction " + transactionData.getHash() + " saved (id=" + transaction.getId() + ")");
				return new AsyncResult<>(true);
			} catch (Exception e) {
				log.error("Error treating transaction " + transactionData.getHash() + " : " + Arrays.toString(e.getStackTrace()));
				return new AsyncResult<>(false);
			}
		}
	}

}
