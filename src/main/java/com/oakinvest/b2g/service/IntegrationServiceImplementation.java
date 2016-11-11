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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
	@Qualifier("BitcoindServiceImplementation") // FIXME Find a way to not set this in production.
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
	public final boolean integrateBitcoinBlock(final long blockHeight) {
		log.info("Integrating bitcoin block number " + String.format("%09d", blockHeight));
		status.addLogMessage("Integrating bitcoin block number " + String.format("%09d", blockHeight));

		// Variables.
		boolean success = true;
		GetBlockHashResponse blockHash;
		GetBlockResponse block = new GetBlockResponse();
		ArrayList<GetRawTransactionResult> transactions = new ArrayList<>();
		ArrayList<String> addresses = new ArrayList<>();

		// Getting block hash & the block informations.
		blockHash = bds.getBlockHash(blockHeight);
		if (blockHash.getError() != null) {
			log.error("Error in calling getBlockHash " + blockHash.getError());
			status.addErrorMessage("Error in calling getBlockHash " + blockHash.getError());
			success = false;
		} else {
			// Getting block informations.
			block = bds.getBlock(blockHash.getResult());
			if (block.getError() != null) {
				log.error("Error in calling getBlock " + block.getError());
				status.addErrorMessage("Error in calling getBlock " + block.getError());
				success = false;
			}
		}

		// If the block is already in the datbase, we stop.
		if (bbr.findByHash(blockHash.getResult()) != null) {
			log.error("Block " + blockHeight + " already registred");
			status.addErrorMessage("Error in calling getBlock " + block.getError());
			success = false;
		}

		// Getting all transactions.
		if (success) {
			Iterator<String> it = block.getResult().getTx().iterator();
			while (it.hasNext() && success) {
				String transactionHash = it.next();
				// We don't treat the genesis block transaction.
				if (!transactionHash.equals(GENESIS_BLOCK_TRANSACTION_HASH_1) && !transactionHash.equals(GENESIS_BLOCK_TRANSACTION_HASH_2)) {
					GetRawTransactionResponse transaction = bds.getRawTransaction(transactionHash);

//					if (transaction.getResult().getVin().size() >= 4) {
//						System.out.println("=> " + transaction.getResult().getTxId());
//						System.exit(-1);
//					}

					if (transaction.getError() == null) {
						transactions.add(transaction.getResult());
					} else {
						log.error("Error in calling getRawTransaction " + transaction.getError());
						status.addErrorMessage("Error in calling getRawTransaction " + transaction.getError());
						success = false;
					}
				}
			}
		}

		// Getting all bitcoin addresses.
		if (success) {
			Iterator<GetRawTransactionResult> it = transactions.iterator();
			while (it.hasNext()) {
				it.next().getVout().stream().forEach(s -> s.getScriptPubKey().getAddresses().stream().forEach(a -> addresses.add(a)));
			}
		}

		// Persisting data.
		if (success) {
			Map bitcoinAddresses = new HashMap<String, BitcoinAddress>();

			// Saving the bitcoin addresses.
			Iterator<String> itAddresses = addresses.iterator();
			while (itAddresses.hasNext()) {
				String address = itAddresses.next();
				BitcoinAddress bAddress = bar.findByAddress(address);
				if (bAddress == null) {
					bAddress = bar.save(new BitcoinAddress(address));
					log.info("Address " + address + " created");
					status.addLogMessage("Address " + address + " created");
				} else {
					log.info("Address " + address + " already exists");
				}
				bitcoinAddresses.put(address, bAddress);
			}

			// Saving the block.
			BitcoinBlock b = mapper.blockResultToBitcoinBlock(block.getResult());
			bbr.save(b);
			log.info("Block " + b.getHash() + " created");
			status.addLogMessage("Block " + b.getHash() + " created");

			// Saving the transactions
			Iterator<GetRawTransactionResult> itTransactions = transactions.iterator();
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
					o.getAddresses().stream().forEach(a -> o.getBitcoinAddresses().add((BitcoinAddress) bitcoinAddresses.get(a)));
					o.getAddresses().stream().forEach(a -> ((BitcoinAddress) bitcoinAddresses.get(a)).getDeposits().add(o));
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
							originTransactionOutput.getAddresses().stream().forEach(a -> bitcoinAddresses.put(a, bar.findByAddress(a)));
							// We add the input.
							originTransactionOutput.getAddresses().stream().forEach(a -> ((BitcoinAddress) bitcoinAddresses.get(a)).getWithdrawals().add(i));
						}
					}
				}

				// Save the transaction.
				btr.save(bt);
				log.info("Transaction " + t.getTxid() + " created");
				status.addLogMessage("Transaction " + t.getTxid() + " created");
			}

		}

		log.info("Integration of bitcoin block number " + String.format("%09d", blockHeight) + " done");
		status.addLogMessage("Integration of bitcoin block number " + String.format("%09d", blockHeight) + " done");
		return success;
	}

}
