package com.oakinvest.b2g.service;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
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
import java.util.Iterator;

/**
 * Integrates blockchain data into the database.
 * Created by straumat on 04/09/16.
 */
@SuppressWarnings("ALL")
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
	 */
	@Override
	@Transactional
	public final void integrateBitcoinBlock(final long blockHeight) {
		// Starting.
		final long start = System.currentTimeMillis();
		final String formatedBlockHeight = String.format("%08d", blockHeight);
		status.addLog("-------------------------------------------------------------------------");
		status.addLog("Integrating bitcoin block number " + formatedBlockHeight);

		// Data.
		String blockHash;
		BitcoinBlock block = null;
		ArrayList<String> transactions = new ArrayList<>();

		// -------------------------------------------------------------------------------------------------------------
		// Retrieve the block hash.
		blockHash = bds.getBlockHash(blockHeight).getResult();
		if (blockHash != null) {
			// Success.
			status.addLog("Hash of block " + formatedBlockHeight + " is " + blockHash);
		} else {
			// Error.
			throw new RuntimeException("Error getting the hash of block " + formatedBlockHeight);
		}

		// -------------------------------------------------------------------------------------------------------------
		// Retrieve the transaction list.
		if (blockHash != null) {
			GetBlockResponse blockResponse = bds.getBlock(blockHash);
			if (blockResponse.getError() == null) {
				// Success.

				// Retrieving transaction list.
				blockResponse.getResult().getTx().stream()
						.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_1))
						.filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION_HASH_2))
						.forEach(t -> transactions.add(t));

				// If the block doesn't exists, we save it else we retrieve it.
				block = bbr.findByHash(blockHash);
				if (block == null) {
					block = mapper.blockResultToBitcoinBlock(blockResponse.getResult());
					bbr.save(block);
					status.addLog("Block " + formatedBlockHeight + " has " + transactions.size() + " transaction(s) and is saved with id " + block.getId());
				} else {
					status.addLog("Block " + formatedBlockHeight + " alrerady exists with id " + block.getId());
				}
			} else {
				// Error.
				throw new RuntimeException("Error saving the block and transactions list " + blockResponse.getError());
			}
		}

		// -------------------------------------------------------------------------------------------------------------
		// Saving all transactions if we have the block.
		if (block != null) {
			Iterator<String> it = transactions.iterator();
			int i = 1;
			while (it.hasNext()) {
				// We retrieve the transaction data.
				String transactionHash = it.next();
				status.addLog("> Treating transaction n° " + i + " : " + transactionHash);
				GetRawTransactionResponse transaction = bds.getRawTransaction(transactionHash);
				if (transaction.getError() == null) {
					// Success.
					try {
						// Saving the transaction in the database.
						BitcoinTransaction bt = mapper.rawTransactionResultToBitcoinTransaction(transaction.getResult());
						bt.setBlock(block);

						// For each Vin.
						Iterator<BitcoinTransactionInput> vins = bt.getInputs().iterator();
						while (vins.hasNext()) {
							BitcoinTransactionInput vin = vins.next();
							vin.setTransaction(bt);
							if (vin.getTxId() == null) {
								// Coinbase.
								status.addLog(">> Done treating vin : " + vin);
							} else {
								// Not coinbase. We retrieve the original transaction.
								BitcoinTransactionOutput originTransactionOutput = btr.findByTxId(vin.getTxId()).getOutputByIndex(vin.getvOut());
								vin.setTransactionOutput(originTransactionOutput);

								// We set the addresses "from" if it's not a coinbase transaction.
								if (vin.getCoinbase() == null) {
									// We add the input to the withdrawls of the address.
									originTransactionOutput.getAddresses().forEach(a -> createOrGetAddress(a).getWithdrawals().add(vin));
								}
								status.addLog(">> Done treating vin : " + vin);
							}
						}

						// For each Vout.
						Iterator<BitcoinTransactionOutput> vouts = bt.getOutputs().iterator();
						while (vouts.hasNext()) {
							BitcoinTransactionOutput vout = vouts.next();
							vout.setTransaction(bt);
							vout.getAddresses().forEach(a -> (createOrGetAddress(a)).getDeposits().add(vout));
							status.addLog(">> Done treating vout : " + vout);
						}

						// Saving the transaction.
						btr.save(bt);
						status.addLog("> Transaction " + transactionHash + " created");
					} catch (Exception e) {
						throw new RuntimeException("Error treating transaction " + transactionHash + " : " + e.getMessage());
					}
				} else {
					// Error.
					throw new RuntimeException("Error in calling getRawTransaction " + transaction.getError());
				}
				i++;
			}
		}

		final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
		status.addLog("Block " + formatedBlockHeight + " treated in " + elapsedTime + " secs");
	}

	/**
	 * Creates or get a bitcoin address.
	 *
	 * @param address address.
	 * @return bitcoin address in database.
	 */
	private BitcoinAddress createOrGetAddress(final String address) {
		BitcoinAddress bAddress = bar.findByAddress(address);
		if (bAddress == null) {
			// If it doesn't exists, we create it.
			bAddress = bar.save(new BitcoinAddress(address));
			status.addLog(">> Address " + address + " created with id " + bAddress.getId());
		} else {
			// Else we just return the one existing in the database.
			status.addLog(">> Address " + address + " already exists with id " + bAddress.getId());
		}
		return bAddress;
	}

}
