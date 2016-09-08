package com.oakinvest.b2g.service;

import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResult;
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
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(IntegrationService.class);

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bds;

	/**
	 * Integrate a bitcoin block into the database.
	 *
	 * @param blockHeight block number
	 * @return true is integrated successfully
	 */
	@Override
	public boolean integrateBitcoinBlock(final long blockHeight) {
		log.info("Integrating bitcoin block number " + String.format("%09d", blockHeight));

		// Variables
		boolean success = true;
		GetBlockHashResponse blockHash;
		GetBlockResponse block;
		ArrayList<GetRawTransactionResult> transactions = new ArrayList<>();

		// Getting block hash
		blockHash = bds.getBlockHash(blockHeight);
		if (blockHash.getError() != null) {
			log.error("Error in calling getBlockHash " + blockHash.getError());
			success = false;
		}

		// Getting all informations
		if (success) {
			// Getting the block informations
			block = bds.getBlock(blockHash.getResult());
			// Getting all the transaction informations
			if (block.getError() == null) {
				Iterator<String> it = block.getResult().getTx().iterator();
				while (it.hasNext() && success) {
					GetRawTransactionResponse transaction = bds.getRawTransaction(it.next());
					if (transaction.getError() == null) {
						// Adding transactions
						transactions.add(transaction.getResult());
					} else {
						log.error("Error in calling getRawTransaction " + transaction.getError());
						success = false;
					}
				}
			} else {
				log.error("Error in calling getBlock " + block.getError());
				success = false;
			}
		}

		// If we have all the informations, then we do the persistance.
		if (success) {

		}

		return success;
	}

}
