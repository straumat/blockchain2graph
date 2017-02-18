package com.oakinvest.b2g.service.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.util.bitcoin.BitcoindToDomainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Integrates blockchain data into the database.
 * Created by straumat on 04/09/16.
 */
@SuppressWarnings("ALL")
@Service
public class BitcoinIntegrationServiceImplementation implements BitcoinIntegrationService {

	/**
	 * How many milli seconds in one second.
	 */
	public static final float MILLISECONDS_IN_SECONDS = 1000F;

	/**
	 * Number of blocks to load in cache.
	 */
	public static final int NUMBER_OF_BLOCKS_TO_LOAD_IN_CACHE = 10;

	/**
	 * Pause between calls for checking if all transactions ar done.
	 */
	public static final int PAUSE_BETWEEN_TRANSACTIONS_THREADS_CHECK = 1000;

	/**
	 * Genesis transaction hash.
	 */
	private static final String GENESIS_BLOCK_TRANSACTION_HASH_1 = "0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098";

	/**
	 * Genesis transaction hash.
	 */
	private static final String GENESIS_BLOCK_TRANSACTION_HASH_2 = "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b";

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(BitcoinIntegrationService.class);

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bds;

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinBlockRepository bbr;

	/**
	 * Bitcoin transaction repository.
	 */
	@Autowired
	private BitcoinTransactionRepository btr;

	/**
	 * Bitcoin address repository.
	 */
	@Autowired
	private BitcoinAddressRepository bar;

	/**
	 * Mapper.
	 */
	@Autowired
	private BitcoindToDomainMapper mapper;

	/**
	 * Transaction task.
	 */
	@Autowired
	private BitcoinTransactionIntegrationTask transactionTask;

	/**
	 * Load a block in cache.
	 *
	 * @param blockHeight block number
	 */
	@Override
	@Async
	public final void loadBlockInCache(final long blockHeight) {
		try {
			// We don't care if this method returns any error.
			String blockHash = bds.getBlockHash(blockHeight).getResult();
			if (blockHash != null) {
				GetBlockResponse blockResponse = bds.getBlock(blockHash);
				blockResponse.getResult().getTx().stream()
						.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_1))
						.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_2))
						.forEach(t -> bds.getRawTransaction(t).getResult().getVout()
								.stream().forEach(v -> v.getScriptPubKey().getAddresses()
										.stream().filter(a -> a != null).forEach(a -> createOrGetAddress(a)))
						);
			}
		} catch (Exception e) {
			log.info("Error while loading in cache the block n°" + blockHeight);
		}
	}

	/**
	 * Integrate a bitcoin block into the database.
	 *
	 * @param blockHeight block number
	 */
	@Override
	@SuppressWarnings("checkstyle:emptyforiteratorpad")
	public final void importBitcoinBlock(final long blockHeight) throws InterruptedException, ExecutionException {
		final long start = System.currentTimeMillis();
		final String formatedBlockHeight = String.format("%08d", blockHeight);
		status.addLog("--------------------------------------------------------------------------------");
		status.addLog("Importing data of bitcoin block n°" + formatedBlockHeight);

		// -------------------------------------------------------------------------------------------------------------
		// Retrieve the block hash.
		String blockHash = bds.getBlockHash(blockHeight).getResult();
		if (blockHash != null) {
			// Success.
			status.addLog("Block n°" + formatedBlockHeight + " hash  is " + blockHash);
		} else {
			// Error.
			throw new RuntimeException("Error getting the hash of block n°" + formatedBlockHeight);
		}

		// -------------------------------------------------------------------------------------------------------------
		// Retrieve the transaction list and saving the block as non integrated.
		BitcoinBlock block = null;
		ArrayList<String> transactionsHashs = new ArrayList<>();
		if (blockHash != null) {
			GetBlockResponse blockResponse = bds.getBlock(blockHash);
			if (blockResponse.getError() == null) {
				// Success.
				// Retrieving transaction list.
				blockResponse.getResult().getTx().stream()
						.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_1))
						.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_2))
						.forEach(t -> transactionsHashs.add(t));

				// If the block doesn't exists, we save it else we retrieve it.
				block = bbr.findByHash(blockHash);
				if (block == null) {
					block = mapper.blockResultToBitcoinBlock(blockResponse.getResult());
					bbr.save(block);
				}

				// Send a message to set the status.
				status.addLog("Block n°" + formatedBlockHeight + " has " + transactionsHashs.size() + " transaction(s) and its id is " + block.getId());
			} else {
				// Error.
				throw new RuntimeException("Error getting block n°" + formatedBlockHeight + " informations : " + blockResponse.getError());
			}
		}

		// -------------------------------------------------------------------------------------------------------------
		// Creating a thread for every transaction we have in the block (using @async).
		transactionTask.setEnvironnement(log, bds, status, btr, bar, mapper); // TODO Why autowired doesn't work ?
		HashMap<String, Future<BitcoinTransaction>> transactions = new HashMap<>();
		if (block != null) {
			int counter = 1;
			for (Iterator<String> it = transactionsHashs.iterator(); it.hasNext(); ) {
				// We run a an task for every transaction hashs in the block.
				String transactionHash = it.next();
				status.addLog("> Starting a thread for transaction n°" + counter + " : " + transactionHash);
				transactions.put(transactionHash, transactionTask.createTransaction(transactionHash));
				counter++;
			}
		}

		// -------------------------------------------------------------------------------------------------------------
		// Waiting for all the transactions to be imported.
		boolean allTransactionsImported = false;
		while (!allTransactionsImported) {
			int transactionsImportedWithoutError = 0;
			int transactionsImportedWithErrors = 0;
			int transactionsNotYetImported = 0;

			// We see if we have all the results we expected.
			for (Map.Entry<String, Future<BitcoinTransaction>> t : transactions.entrySet()) {
				if (t.getValue().isDone()) {
					// If the transaction work is done.
					if (t.getValue() != null) {
						// If it's done, we cound it as imported.
						transactionsImportedWithoutError++;
						// If it's not null, we set the relation.
						block.getTransactions().add(t.getValue().get());
						t.getValue().get().setBlock(block);
					} else {
						// If it's done and it's null, an error occured.
						transactionsImportedWithErrors++;
						status.addLog("> Thread for transaction " + t.getKey() + " has an error");
					}
				} else {
					// If the transaction work is not yet done.
					transactionsNotYetImported++;
				}
			}

			// Everything is imported if all the transactions are imported without errors.
			allTransactionsImported = (transactionsImportedWithoutError == transactionsHashs.size());

			// If not has been imported, we log statics.
			if (!allTransactionsImported) {
				status.addLog("Block n°" + formatedBlockHeight + " statistics on threads.");
				status.addLog(transactionsImportedWithoutError + " transaction(s) without errors");
				status.addLog(transactionsImportedWithErrors + " transaction(s) with errors");
				status.addLog(transactionsNotYetImported + " transaction(s) without errors");
				// And we wait a bit to let time for the threads to finish before testing again.
				Thread.sleep(PAUSE_BETWEEN_TRANSACTIONS_THREADS_CHECK);
			}
		}
		status.addLog("Block n°" + formatedBlockHeight + " : all threads treating transactions are done.");

		// -------------------------------------------------------------------------------------------------------------
		// Saving block and its relations woth tasks.
		block.setIntegrated(true);
		bbr.save(block);

		// -------------------------------------------------------------------------------------------------------------
		// Sending a message saying that all worked fine and making some statistics.
		final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
		status.addLog("Block " + formatedBlockHeight + " treated in " + elapsedTime + " secs");
		status.addExecutionTimeStatistic(elapsedTime);
	}

	/**
	 * Creates or get a bitcoin address.
	 *
	 * @param address address.
	 * @return bitcoin address in database.
	 */
	@Transactional
	private BitcoinAddress createOrGetAddress(final String address) {
		BitcoinAddress bAddress = bar.findByAddress(address);
		if (bAddress == null) {
			// If it doesn't exists, we create it.
			bAddress = bar.save(new BitcoinAddress(address));
			log.info("Address " + address + " created with id " + bAddress.getId());
		} else {
			// Else we just return the one existing in the database.
			log.info("Address " + address + " already exists with id " + bAddress.getId());
		}
		return bAddress;
	}

}
