package com.oakinvest.b2g.service;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import com.oakinvest.b2g.util.bitcoin.BitcoindToDomainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Integrates blockchain data into the database.
 * Created by straumat on 04/09/16.
 */
@Service
public class IntegrationServiceImplementation implements IntegrationService {

	/**
	 * How many milli seconds in one second.
	 */
	public static final float MILLISECONDS_IN_SECONDS = 1000F;

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
	private final Logger log = LoggerFactory.getLogger(IntegrationService.class);

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
	 * Integrate a bitcoin block into the database.
	 *
	 * @param blockHeight block number
	 * @return true is integrated successfully
	 */
	@Override
	@Transactional
	public final boolean integrateBitcoinBlock(final long blockHeight) {
		boolean allDataAvailable = true;

		// Starting.
		final long start = System.currentTimeMillis();
		status.addLogMessage("Integrating bitcoin block number " + String.format("%09d", blockHeight));

		// Data.
		String blockHash;
		GetBlockResponse blockData = null;
		ArrayList<GetRawTransactionResult> transactionsData = new ArrayList<>();
		ArrayList<String> addressesData = new ArrayList<>();

		// Getting the block hash.
		blockHash = getBlockHash(blockHeight);
		if (blockHash != null) {
			status.addLogMessage("Getting block hash of block number " + String.format("%09d", blockHeight) + " : " + blockHash);
		} else {
			status.addErrorMessage("Error getting the hash of block number " + String.format("%09d", blockHeight));
			allDataAvailable = false;
		}

		// Getting the block data.
		if (blockHash != null) {
			blockData = bds.getBlock(blockHash);
			// Checking that there is no error.
			if (blockData.getError() != null) {
				status.addErrorMessage("Error in calling getBlock " + blockData.getError());
				allDataAvailable = false;
			}
		}

		// Getting all transactions.
		if (blockHash != null) {
			transactionsData = getBlockTransactions(blockHash);
			// Checking that there is no error.
			if (transactionsData == null) {
				status.addErrorMessage("Error getting transactions data from block " + blockHeight);
				allDataAvailable = false;
			}
		}

		// Getting the address list.
		addressesData = getBlockAddresses(transactionsData);

		// If the block is already in the database, we stop.
		if (bbr.findByHash(blockHash) != null) {
			status.addErrorMessage("Block " + blockHeight + " already registered");
			allDataAvailable = false;
		}

		// Persisting data.
		if (allDataAvailable) {
			Map bitcoinAddresses = new HashMap<String, BitcoinAddress>();

			// Saving the bitcoin addresses.
			Iterator<String> itAddresses = addressesData.iterator();
			while (itAddresses.hasNext()) {
				String address = itAddresses.next();
				BitcoinAddress bAddress = bar.findByAddress(address);
				if (bAddress == null) {
					bAddress = bar.save(new BitcoinAddress(address));
					status.addLogMessage("Address " + address + " created");
				} else {
					status.addLogMessage("Address " + address + " already exists");
				}
				bitcoinAddresses.put(address, bAddress);
			}

			// Saving the block.
			BitcoinBlock b = mapper.blockResultToBitcoinBlock(blockData.getResult());
			bbr.save(b);
			status.addLogMessage("Block " + b.getHash() + " created");

			// Saving the transactions
			Iterator<GetRawTransactionResult> itTransactions = transactionsData.iterator();
			while (itTransactions.hasNext()) {
				GetRawTransactionResult t = itTransactions.next();
				BitcoinTransaction bt = mapper.rawTransactionResultToBitcoinTransaction(t);

				// Registering the block.
				bt.setBlock(b);

				// For each vout.
				Iterator<BitcoinTransactionOutput> outputsIterator = bt.getOutputs().iterator();
				while (outputsIterator.hasNext()) {
					BitcoinTransactionOutput o = outputsIterator.next();
					o.setTransaction(bt);
					o.getAddresses().forEach(a -> o.getBitcoinAddresses().add((BitcoinAddress) bitcoinAddresses.get(a)));
					o.getAddresses().forEach(a -> ((BitcoinAddress) bitcoinAddresses.get(a)).getDeposits().add(o));
				}

				// For each vin.
				Iterator<BitcoinTransactionInput> inputsIterator = bt.getInputs().iterator();
				while (inputsIterator.hasNext()) {
					BitcoinTransactionInput i = inputsIterator.next();
					i.setTransaction(bt);

					if (i.getTxId() != null) {
						// We retrieve the original transaction.
						BitcoinTransactionOutput originTransactionOutput = btr.findByTxId(i.getTxId()).getOutputByIndex(i.getvOut());
						i.setTransactionOutput(originTransactionOutput);

						// We set the addresses "from" if it's not a coinbase transaction.
						if (i.getCoinbase() == null) {
							// We retrieve all the addresses used in the transaction.
							originTransactionOutput.getAddresses().forEach(a -> bitcoinAddresses.put(a, bar.findByAddress(a)));
							// We add the input.
							originTransactionOutput.getAddresses().forEach(a -> ((BitcoinAddress) bitcoinAddresses.get(a)).getWithdrawals().add(i));
						}
					}
				}

				// Save the transaction.
				btr.save(bt);
				status.addLogMessage("Transaction " + t.getTxid() + " created");
			}

		}
		final long elapsedTime = System.currentTimeMillis() - start;
		status.addLogMessage("Integration of bitcoin block number " + String.format("%09d", blockHeight) + " done in " + elapsedTime / MILLISECONDS_IN_SECONDS + " secs");
		return allDataAvailable;
	}

	/**
	 * Gets block hash.
	 *
	 * @param blockHeight block height.
	 * @return block hash
	 */
	private String getBlockHash(final long blockHeight) {
		GetBlockHashResponse blockHashResponse = bds.getBlockHash(blockHeight);
		if (blockHashResponse.getError() == null) {
			// In case of success.
			status.addLogMessage("getBlockHash on block " + blockHeight + " returns " + blockHashResponse.getResult());
			return blockHashResponse.getResult();
		} else {
			// In case of error.
			status.addErrorMessage("Error in calling getBlockHash " + blockHashResponse.getError());
			return null;
		}
	}

	/**
	 * Returns transactions inside a block.
	 *
	 * @param blockHash block hash.
	 * @return transactions.
	 */
	private ArrayList<GetRawTransactionResult> getBlockTransactions(final String blockHash) {
		ArrayList<GetRawTransactionResult> transactions = new ArrayList<GetRawTransactionResult>();
		status.addLogMessage("Treating " + transactions.size() + " transactions for block " + blockHash);
		Iterator<String> it = bds.getBlock(blockHash).getResult().getTx().iterator();
		while (it.hasNext()) {
			String transactionHash = it.next();
			// We don't treat the genesis block transaction.
			status.addLogMessage("Treating transaction " + transactionHash + " of block " + blockHash);
			if (!transactionHash.equals(GENESIS_BLOCK_TRANSACTION_HASH_1) && !transactionHash.equals(GENESIS_BLOCK_TRANSACTION_HASH_2)) {
				GetRawTransactionResponse transaction = bds.getRawTransaction(transactionHash);
				if (transaction.getError() == null) {
					transactions.add(transaction.getResult());
					status.addLogMessage("Transaction " + transactionHash + " added");
				} else {
					status.addErrorMessage("Error in calling getRawTransaction " + transaction.getError());
					return null;
				}
			}
		}
		return transactions;
	}

	/**
	 * Returns all the addresses in the block.
	 *
	 * @param transactions transactions.
	 * @return addresses.
	 */
	private ArrayList<String> getBlockAddresses(final ArrayList<GetRawTransactionResult> transactions) {
		ArrayList<String> addresses = new ArrayList<>();
		transactions.stream().forEach(t -> {
			t.getVout().forEach(s -> s.getScriptPubKey().getAddresses().forEach(a -> {
				status.addLogMessage("Retrieving address " + a);
				addresses.add(a);
			}));
		});
		return addresses;
	}

}
