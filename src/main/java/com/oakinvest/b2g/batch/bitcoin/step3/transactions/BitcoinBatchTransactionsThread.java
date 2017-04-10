package com.oakinvest.b2g.batch.bitcoin.step3.transactions;

import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.util.bitcoin.mapper.BitcoindToDomainMapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.Future;

/**
 * Thread that deals with a transaction.
 * Created by straumat on 29/03/17.
 */
@Service
public class BitcoinBatchTransactionsThread {

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(BitcoinBatchTransactionsThread.class);

	/**
	 * Mapper.
	 */
	private final BitcoindToDomainMapper mapper = Mappers.getMapper(BitcoindToDomainMapper.class);

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinTransactionRepository transactionRepository;

	/**
	 * Treat transaction.
	 *
	 * @param transactionData transaction data
	 * @return BitcoinTransaction created
	 */
	@Async("transactionPoolTaskExecutor")
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public Future<Boolean> process(final GetRawTransactionResult transactionData) {
		try {
			BitcoinTransaction transaction = transactionRepository.findByTxId(transactionData.getHash());
			if (transaction != null) {
				// If the transaction already exists in the database, we return it.
				log.info("Transaction " + transactionData.getHash() + " is already in the database");
				return new AsyncResult<>(true);
			} else {
				// If the transaction is not in the database, we create it.
				transaction = mapper.rawTransactionResultToBitcoinTransaction(transactionData);
				transactionRepository.save(transaction);
				log.info("Transaction " + transactionData.getHash() + " saved (id=" + transaction.getId() + ")");
				return new AsyncResult<>(true);
			}
		} catch (Exception e) {
			log.error("Error treating transaction " + transactionData.getHash() + " : " + Arrays.toString(e.getStackTrace()));
			return new AsyncResult<>(false);
		}
	}

}
