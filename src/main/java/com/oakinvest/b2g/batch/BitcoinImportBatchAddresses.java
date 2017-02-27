package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
import org.neo4j.graphdb.ConstraintViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * Bitcoin import addresses batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinImportBatchAddresses extends BitcoinImportBatch {

	/**
	 * Initial delay before importing a block addresses.
	 */
	private static final int BLOCK_ADDRESSES_IMPORT_INITIAL_DELAY = 2000;

	/**
	 * Import data.
	 */
	@Override
	@Scheduled(initialDelay = BLOCK_ADDRESSES_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void importData() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final BitcoinBlock blockToTreat = getBbr().findFirstBlockWithoutAddresses();

		// -------------------------------------------------------------------------------------------------------------
		// If there is a block to work on.
		if (blockToTreat != null) {
			getStatus().addLog("importBlockAddresses : Starting to import addresses from block n°" + blockToTreat.getHeight());

			// ---------------------------------------------------------------------------------------------------------
			// Creating all the addresses.
			for (Iterator<String> transactionsHashs = blockToTreat.getTx().iterator(); transactionsHashs.hasNext(); ) {
				String transactionHash = transactionsHashs.next();
				// -----------------------------------------------------------------------------------------------------
				// For every transaction hash, we get all the addresses in vout.
				if (!transactionHash.equals(GENESIS_BLOCK_TRANSACTION_HASH_1) && !transactionHash.equals(GENESIS_BLOCK_TRANSACTION_HASH_2)) {
					GetRawTransactionResponse transactionResponse = getBds().getRawTransaction(transactionHash);
					if (transactionResponse.getError() == null) {
						transactionResponse.getResult()
								.getVout().stream().filter(v -> v != null)
								.forEach(v -> v.getScriptPubKey()
										.getAddresses().stream()
										.filter(a -> a != null)
										.forEach(address -> {
											BitcoinAddress a = getBar().findByAddress(address);
											if (a == null) {
												// Address creation.
												try {
													a = new BitcoinAddress(address);
													getBar().save(a);
												} catch (ConstraintViolationException e) {
													a = getBar().findByAddress(address);
												}
												getStatus().addLog("importBlockAddresses : Address " + address + " created  with id " + a.getId());
											} else {
												getStatus().addLog("importBlockAddresses : Address " + address + " already exists with id " + a.getId());
											}

										}));
					} else {
						getStatus().addError("importBlockAddresses : Impossible to get transaction " + transactionHash + " data : " + transactionResponse.getError());
						return;
					}
				}
			}
			blockToTreat.setAddressesImported(true);
			getBbr().save(blockToTreat);
			final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
			getStatus().addLog("importBlockAddresses : Block n°" + blockToTreat.getHeight() + " treated in " + elapsedTime + " secs");
		} else {
			getStatus().addLog("importBlockAddresses : Nothing to do");
			try {
				Thread.sleep(PAUSE_BETWEEN_CHECKS);
			} catch (Exception e) {
				getLog().error("importBlockAddresses : Error while waiting : " + e.getMessage());
				getLog().error(e.toString());
			}
		}
	}

}
