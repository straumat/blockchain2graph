package com.oakinvest.b2g.batch.bitcoin.step2.addresses;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bitcoin import addresses batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinBatchAddresses extends BitcoinBatchTemplate {

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
	public void process() {
		final long start = System.currentTimeMillis();
		// Block to import.
		final BitcoinBlock blockToTreat = getBlockRepository().findFirstBlockByState(BitcoinBlockState.BLOCK_IMPORTED);

		// -------------------------------------------------------------------------------------------------------------
		// If there is a block to work on.
		if (blockToTreat != null) {
			addLog(LOG_SEPARATOR);
			addLog("Starting to import addresses from block n°" + getFormattedBlock(blockToTreat.getHeight()));
			BitcoindBlockData blockData = getBitcoindService().getBlockData(blockToTreat.getHeight());

			// ---------------------------------------------------------------------------------------------------------
			// If we have the data
			if (blockData != null) {

				// -----------------------------------------------------------------------------------------------------
				// We retrieve all the addresses.
				final List<String> addresses = Collections.synchronizedList(new ArrayList<String>());
				blockData.getTransactions()
						.parallelStream()
						.forEach(grt -> {
							grt.getVout()
									.parallelStream()
									.filter(v -> v != null)
									.forEach(v -> v.getScriptPubKey()
											.getAddresses().stream()
											.filter(a -> a != null)
											.forEach(address -> addresses.add(address)));
						});

				// -----------------------------------------------------------------------------------------------------
				// We create all the addresses.
				addresses.stream()
						.filter(a -> a != null)
						.distinct()
						.forEach(address -> {
							BitcoinAddress a = getAddressRepository().findByAddress(address);
							if (a == null) {
								// Address creation.
								try {
									a = new BitcoinAddress(address);
									getAddressRepository().save(a);
								} catch (Exception e) {
									return;
								}
								addLog("Address " + address + " created with id " + a.getId());
								getLogger().info(getLogPrefix() + " - Address " + address + " created with id " + a.getId());
							} else {
								addLog("Address " + address + " already exists with id " + a.getId());
								getLogger().info(getLogPrefix() + " - Address " + address + " already exists with id " + a.getId());
							}
						});

				// -----------------------------------------------------------------------------------------------------
				// We save the block state.
				blockToTreat.setState(BitcoinBlockState.ADDRESSES_IMPORTED);
				getBlockRepository().save(blockToTreat);

				// We log.
				final float elapsedTime = (System.currentTimeMillis() - start) / MILLISECONDS_IN_SECONDS;
				addLog("Block n°" + getFormattedBlock(blockToTreat.getHeight()) + " treated in " + elapsedTime + " secs");
				getLogger().info(getLogPrefix() + " - Block n°" + getFormattedBlock(blockToTreat.getHeight()) + " treated in " + elapsedTime + " secs");

				// Clear session
				getSession().clear();
			} else {
				addLog("No response from bitcoind - addresses from block n°" + getFormattedBlock(blockToTreat.getHeight()) + " NOT imported");
			}
		} else {
			addLog("Nothing to do");
		}

	}

}
