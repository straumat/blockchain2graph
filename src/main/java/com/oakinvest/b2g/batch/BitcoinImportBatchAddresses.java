package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
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
	//private static final int BLOCK_ADDRESSES_IMPORT_INITIAL_DELAY = 2000;

	/**
	 * Log prefix.
	 */
	private static final String PREFIX = "Addresses batch";

	/**
	 * Returns the log prefix to display in each log.
	 */
	@Override
	public final String getLogPrefix() {
		return PREFIX;
	}

	/**
	 * Import data.
	 */
	@Override
	//@Scheduled(initialDelay = BLOCK_ADDRESSES_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void importData() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final BitcoinBlock blockToTreat = getBbr().findFirstBlockWithoutAddresses();

		// -------------------------------------------------------------------------------------------------------------
		// If there is a block to work on.
		if (blockToTreat != null) {
			addLog(LOG_SEPARATOR);
			addLog("Starting to import addresses from block n°" + getFormatedBlock(blockToTreat.getHeight()));

			// ---------------------------------------------------------------------------------------------------------
			// Creating all the addresses.
			for (Iterator<String> transactionsHashs = blockToTreat.getTx().iterator(); transactionsHashs.hasNext(); ) {
				String transactionHash = transactionsHashs.next();
				// -----------------------------------------------------------------------------------------------------
				// For every transaction hash, we get all the addresses in vout.
				if (!transactionHash.equals(GENESIS_BLOCK_TRANSACTION)) {
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
												} catch (Exception e) {
													a = getBar().findByAddress(address);
												}
												addLog("Address " + address + " created  with id " + a.getId());
												getLogger().info(getLogPrefix() + " - Address " + address + " created  with id " + a.getId());
											} else {
												addLog("Address " + address + " already exists with id " + a.getId());
												getLogger().info(getLogPrefix() + " - Address " + address + " already exists with id " + a.getId());
											}

										}));
					} else {
						addError("Impossible to get transaction " + transactionHash + " data : " + transactionResponse.getError());
						return;
					}
				}
			}
			blockToTreat.setAddressesImported(true);
			getBbr().save(blockToTreat);
			final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
			addLog("Block n°" + getFormatedBlock(blockToTreat.getHeight()) + " treated in " + elapsedTime + " secs");
			getLogger().info(getLogPrefix() + " - Block n°" + blockToTreat.getHeight() + " treated in " + elapsedTime + " secs");
		} else {
			addLog("Nothing to do");
			try {
				Thread.sleep(PAUSE_BETWEEN_CHECKS);
			} catch (Exception e) {
				addError("Error while waiting : " + e.getMessage());
			}
		}
	}

}
