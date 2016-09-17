package com.oakinvest.b2g.service;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.util.BitcoindToDomainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;

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
	private BitcoindService bds;

	/**
	 * Bitcoin blcok repository.
	 */
	@Autowired
	private BitcoinBlockRepository bbr;

	/**
	 * Bitcoin address repository.
	 */
	@Autowired
	private BitcoinAddressRepository bar;

	/**
	 * Integrate a bitcoin block into the database.
	 *
	 * @param blockHeight block number
	 * @return true is integrated successfully
	 */
	@Override
	public boolean integrateBitcoinBlock(final long blockHeight) {
		log.info("Integrating bitcoin block number " + String.format("%09d", blockHeight));

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
			success = false;
		} else {
			// Getting block informations.
			block = bds.getBlock(blockHash.getResult());
			if (block.getError() != null) {
				log.error("Error in calling getBlock " + block.getError());
				success = false;
			}
		}

		// If the block is already in the datbase, we stop.
		if (bbr.findByHash(blockHash.getResult()) != null) {
			log.error("Block " + blockHeight + " alreeady registred");
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
					if (transaction.getError() == null) {
						transactions.add(transaction.getResult());
					} else {
						log.error("Error in calling getRawTransaction " + transaction.getError());
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
			// Saving all bitcoin addresses.
			Iterator<String> it = addresses.iterator();
			while (it.hasNext()) {
				String address = it.next();
				if (bar.findByAddress(address) == null) {
					bar.save(new BitcoinAddress(address));
				}
			}

			// Saving the block.
			BitcoinBlock b = BitcoindToDomainMapper.INSTANCE.blockResultToBitcoinBlock(block.getResult());
			bbr.save(b);
		}

		return success;
	}

}
