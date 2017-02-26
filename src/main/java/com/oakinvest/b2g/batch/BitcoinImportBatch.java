package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.BitcoindService;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.util.bitcoin.BitcoindToDomainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Batch importing the data from the bitcoin blockchain.
 * Created by straumat on 23/02/17.
 */
@Component
public class BitcoinImportBatch {

	/**
	 * Pause between imports.
	 */
	public static final int PAUSE_BETWEEN_IMPORTS = 10;

	/**
	 * Initial delay before importing a block.
	 */
	public static final int BLOCK_IMPORT_INITIAL_DELAY = 1000;

	/**
	 * Initial delay before importing a block addresses.
	 */
	public static final int BLOCK_ADDRESSES_IMPORT_INITIAL_DELAY = 2000;

	/**
	 * Initial delay before importing a block transactions.
	 */
	public static final int BLOCK_TRANSACTIONS_IMPORT_INITIAL_DELAY = 3000;

	/**
	 * Initial delay before importing a block relations.
	 */
	public static final int BLOCK_RELATIONS_IMPORT_INITIAL_DELAY = 4000;

	/**
	 * How many milli seconds in one second.
	 */
	public static final float MILLISECONDS_IN_SECONDS = 1000F;

	/**
	 * Pause between calls for checking if all transactions ar done.
	 */
	public static final int PAUSE_BETWEEN_THREADS_CHECK = 5000;

	/**
	 * Number of seconds before displaying threads statistics.
	 */
	public static final int PAUSE_BEFORE_DISPLAYING_STATISTICS = 5;


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
	private final Logger log = LoggerFactory.getLogger(BitcoinImportBatch.class);

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
	 * Import addresses task.
	 */
	@Autowired
	private BitcoinImportBatchAddressesTask importAddressesTask;

	/**
	 * Import transactions task.
	 */
	@Autowired
	private BitcoinImportBatchTransactionTask importTransactionTask;

	/**
	 * Import a block on the database.
	 */
	@Scheduled(initialDelay = BLOCK_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	public final void importBlock() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final long blockToTreat = bbr.count() + 1;

		// We retrieve the total number of blocks in bitcoind.
		GetBlockCountResponse blockCountResponse = bds.getBlockCount();
		if (blockCountResponse.getError() == null) {
			// ---------------------------------------------------------------------------------------------------------
			// If there are still blocks to import...
			final long totalBlockCount = bds.getBlockCount().getResult();
			if (blockToTreat <= totalBlockCount) {
				// -----------------------------------------------------------------------------------------------------
				// We retrieve the block hash...
				GetBlockHashResponse blockHashResponse = bds.getBlockHash(blockToTreat);
				if (blockHashResponse.getError() == null) {
					// -------------------------------------------------------------------------------------------------
					// Then we retrieve the block data...
					String blockHash = blockHashResponse.getResult();
					status.addLog("importBlock : Starting to import block n°" + blockToTreat + " (" + blockHash + ")");
					GetBlockResponse blockResponse = bds.getBlock(blockHash);
					if (blockResponse.getError() == null) {
						// ---------------------------------------------------------------------------------------------
						// Then, if the block doesn't exists, we save it.
						BitcoinBlock block = bbr.findByHash(blockHash);
						if (block == null) {
							block = mapper.blockResultToBitcoinBlock(blockResponse.getResult());
							bbr.save(block);
							status.addLog("importBlock : Block n°" + blockToTreat + " saved with id " + block.getId());
						} else {
							status.addLog("importBlock : Block n°" + blockToTreat + " already saved with id " + block.getId());
						}
						final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
						status.addLog("importBlock : Block n°" + blockToTreat + " imported in " + elapsedTime + " secs");
					} else {
						// Error while retrieving the block informations.
						status.addError("importBlock : Error getting block n°" + blockToTreat + " informations : " + blockResponse.getError());
					}
				} else {
					// Error while retrieving the block hash.
					status.addError("importBlock : Error getting the hash of block n°" + blockToTreat + " : " + blockHashResponse.getError());
				}
			} else {
				status.addLog("importBlock : All block are imported");
			}
		} else {
			// Error while retrieving the number of blocks in bitcoind.
			status.addError("importBlock : Error getting number of blocks : " + blockCountResponse.getError());
		}
	}

	/**
	 * Import the addresses of a block in the database.
	 */
	@Scheduled(initialDelay = BLOCK_ADDRESSES_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings("checkstyle:emptyforiteratorpad")
	public final void importBlockAddresses() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final BitcoinBlock blockToTreat = bbr.findFirstBlockWithoutAddresses();
		ArrayList<String> transactionsHashs = new ArrayList<>();
		HashMap<String, Future<Boolean>> threads = new HashMap<>();

		// -------------------------------------------------------------------------------------------------------------
		// If there is a block to work on.
		if (blockToTreat != null) {
			status.addLog("importBlockAddresses : Starting to import addresses from block n°" + blockToTreat.getHeight());

			// -----------------------------------------------------------------------------------------------------
			// Retrieving all the transaction hashs.
			blockToTreat.getTx().stream()
					.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_1))
					.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_2))
					.forEach(t -> transactionsHashs.add(t));

			// -----------------------------------------------------------------------------------------------------
			// Launching a thread to treat every addresses in transactions.
			importAddressesTask.setEnvironment(log, bds, status, btr, bar); // TODO Why autowired doesn't work ?
			int counter = 1;
			for (Iterator<String> it = transactionsHashs.iterator(); it.hasNext(); ) {
				// We run a an task for every transaction hashs in the block.
				String transactionHash = it.next();
				status.addLog("importBlockAddresses : Starting thread " + counter + " for addresses in transaction : " + transactionHash);
				threads.put(transactionHash, importAddressesTask.importAddresses(transactionHash));
				counter++;
			}

			// ---------------------------------------------------------------------------------------------------------
			// Waiting for all the threads to be done.
			boolean allThreadsDone = false;
			while (!allThreadsDone) {
				// Statistics.
				int threadsWithoutError = 0;
				int threadsWithErrors = 0;
				int threadsNotYetDone = 0;

				// We see if we have all the results we expected.
				for (Map.Entry<String, Future<Boolean>> t : threads.entrySet()) {
					if (t.getValue().isDone()) {
						// Work is done. Is the result ok ?
						Boolean executionResult = false;
						try {
							executionResult = t.getValue().get();
						} catch (Exception e) {
							log.error("importBlock : Error in getting result from thread : " + e.getMessage());
							e.printStackTrace();
							executionResult = false;
						}
						// If the result is ok.
						if (executionResult) {
							threadsWithoutError++;
						} else {
							// If it's done and it's null, an error occured.
							threadsWithErrors++;
							status.addLog("importBlockAddresses : Thread for transaction " + t.getKey() + " had an error. Re launch");
							// We launch again a thread task on this transaction hash.
							threads.put(t.getKey(), importAddressesTask.importAddresses(t.getKey()));
						}
					} else {
						// If the transaction work is not yet done.
						threadsNotYetDone++;
					}
				}

				// Everything is imported if all the transactions are imported without errors.
				allThreadsDone = (threadsWithoutError == transactionsHashs.size());

				// If not has been imported, we log statics if we are already running for 2 secs.
				if (!allThreadsDone & ((System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS) > PAUSE_BEFORE_DISPLAYING_STATISTICS) {
					String message = "importBlockAddresses : Block n°" + blockToTreat.getHeight() + " statistics on threads :";
					message += threadsWithoutError + " ok / ";
					message += threadsWithErrors + " not ok / ";
					message += threadsNotYetDone + " not done";
					status.addLog(message);

					// And we wait a bit to let time for the threads to finish before testing again.
					try {
						Thread.sleep(PAUSE_BETWEEN_THREADS_CHECK);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			// We update the block to say everything went fine.
			blockToTreat.setAddressesImported(true);
			bbr.save(blockToTreat);
			final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
			status.addLog("importBlockAddresses : Block n°" + blockToTreat.getHeight() + " treated in " + elapsedTime + " secs");
		}
	}

	/**
	 * Import the transactions of a block in the database.
	 */
	@Scheduled(initialDelay = BLOCK_TRANSACTIONS_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings("checkstyle:emptyforiteratorpad")
	public final void importBlockTransactions() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final BitcoinBlock blockToTreat = bbr.findFirstBlockWithoutTransactions();
		ArrayList<String> transactionsHashs = new ArrayList<>();
		HashMap<String, Future<Boolean>> threads = new HashMap<>();

		// -------------------------------------------------------------------------------------------------------------
		// If there is a block to work on.
		if (blockToTreat != null) {
			status.addLog("importBlockTransactions : Starting to import transactions from block n°" + blockToTreat.getHeight());

			// -----------------------------------------------------------------------------------------------------
			// Retrieving all the transaction hashs.
			blockToTreat.getTx().stream()
					.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_1))
					.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_2))
					.forEach(t -> transactionsHashs.add(t));

			// -----------------------------------------------------------------------------------------------------
			// Launching a thread to treat every addresses in transactions.
			importTransactionTask.setEnvironment(log, bds, status, btr, bar, mapper); // TODO Why autowired doesn't work ?
			int counter = 1;
			for (Iterator<String> it = transactionsHashs.iterator(); it.hasNext(); ) {
				// We run a an task for every transaction hashs in the block.
				String transactionHash = it.next();
				status.addLog("importBlockTransactions : Starting thread " + counter + " to treat transaction " + transactionHash);
				threads.put(transactionHash, importTransactionTask.importTransaction(transactionHash));
				counter++;
			}

			// ---------------------------------------------------------------------------------------------------------
			// Waiting for all the threads to be done.
			boolean allThreadsDone = false;
			while (!allThreadsDone) {
				// Statistics.
				int threadsWithoutError = 0;
				int threadsWithErrors = 0;
				int threadsNotYetDone = 0;

				// We see if we have all the results we expected.
				for (Map.Entry<String, Future<Boolean>> t : threads.entrySet()) {
					if (t.getValue().isDone()) {
						// Work is done. Is the result ok ?
						Boolean executionResult = false;
						try {
							executionResult = t.getValue().get();
						} catch (Exception e) {
							log.error("importBlockTransactions : error in getting result from thread " + e.getMessage());
							e.printStackTrace();
							executionResult = false;
						}
						// If the result is ok.
						if (executionResult) {
							threadsWithoutError++;
						} else {
							// If it's done and it's null, an error occured so we restart it.
							threadsWithErrors++;
							status.addLog("importBlockTransactions : Thread for transaction " + t.getKey() + " had an error");
							// We launch again a thread task on this transaction hash.
							threads.put(t.getKey(), importTransactionTask.importTransaction(t.getKey()));
						}
					} else {
						// If the transaction work is not yet done.
						threadsNotYetDone++;
					}
				}

				// Everything is imported if all the transactions are imported without errors.
				allThreadsDone = (threadsWithoutError == transactionsHashs.size());

				// If not has been imported, we log statics if we are already running for 2 secs.
				if (!allThreadsDone & ((System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS) > PAUSE_BEFORE_DISPLAYING_STATISTICS) {
					String message = "importBlockTransactions : Block n°" + blockToTreat.getHeight() + " statistics on threads :";
					message += threadsWithoutError + " ok / ";
					message += threadsWithErrors + " not ok / ";
					message += threadsNotYetDone + " not done";
					status.addLog(message);

					// And we wait a bit to let time for the threads to finish before testing again.
					try {
						Thread.sleep(PAUSE_BETWEEN_THREADS_CHECK);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			// We update the block to say everything went fine.
			blockToTreat.setTransactionsImported(true);
			bbr.save(blockToTreat);
			final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
			status.addLog("importBlockTransactions : Block n°" + blockToTreat.getHeight() + " treated in " + elapsedTime + " secs");
		}
	}

	/**
	 * Import the relations of a block in the datbase.
	 */
	@Scheduled(initialDelay = BLOCK_RELATIONS_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	public final void importBlockRelations() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final BitcoinBlock blockToTreat = bbr.findFirstBlockWithoutRelations();

		// -------------------------------------------------------------------------------------------------------------
		// If there is a block to work on.
		if (blockToTreat != null) {
			// ---------------------------------------------------------------------------------------------------------
			// Getting the block informations.
			status.addLog("importBlockRelations : Starting to import relations from block n°" + blockToTreat.getHeight());
			// -----------------------------------------------------------------------------------------------------
			// Setting the relationship between blocks and transactions.
			blockToTreat.getTx().stream()
					.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_1))
					.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_2))
					.forEach(t -> {
						BitcoinTransaction bt = btr.findByTxId(t);
						bt.setBlock(blockToTreat);
						blockToTreat.getTransactions().add(bt);
					});

			// We update the block to say everything went fine.
			blockToTreat.setRelationsImported(true);
			blockToTreat.setImported(true);
			bbr.save(blockToTreat);
			final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
			status.addLog("importBlockRelations : Block n°" + blockToTreat.getHeight() + " treated in " + elapsedTime + " secs");
		}
	}

}