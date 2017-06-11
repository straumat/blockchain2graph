package com.oakinvest.b2g.batch.bitcoin.step2.addresses;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.repository.bitcoin.BitcoinRepositories;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
     * Constructor.
     *
     * @param newBitcoinRepositories    bitcoin repositories
     * @param newBitcoinDataService     bitcoin data service
     * @param newStatus                 status
     */
    public BitcoinBatchAddresses(final BitcoinRepositories newBitcoinRepositories, final BitcoinDataService newBitcoinDataService, final StatusService newStatus) {
        super(newBitcoinRepositories, newBitcoinDataService, newStatus);
    }

    /**
	 * Returns the log prefix to display in each log.
	 */
	@Override
	public final String getLogPrefix() {
		return PREFIX;
	}

	/**
	 * Return the block to process.
	 *
	 * @return block to process.
	 */
	@Override
    protected final Long getBlockHeightToProcess() {
		BitcoinBlock blockToTreat = getBlockRepository().findFirstBlockByState(BitcoinBlockState.BLOCK_IMPORTED);
		if (blockToTreat != null) {
			return blockToTreat.getHeight();
		} else {
			return null;
		}
	}

	/**
	 * Process block.
	 *
	 * @param blockHeight block height to process.
	 */
	@Override
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	protected final BitcoinBlock processBlock(final long blockHeight) {
		Optional<BitcoindBlockData> blockData = getBitcoinDataService().getBlockData(blockHeight);
		// ---------------------------------------------------------------------------------------------------------
		// If we have the data
		if (blockData.isPresent()) {
			// -----------------------------------------------------------------------------------------------------
			// We retrieve all the addresses.
			final List<String> addresses = Collections.synchronizedList(new ArrayList<String>());
			blockData.get().getTransactions()
					.forEach(grt -> grt.getVout()
							.stream()
							.filter(Objects::nonNull)
							.forEach(v -> v.getScriptPubKey()
									.getAddresses().stream()
									.filter(Objects::nonNull)
									.forEach(addresses::add)));

			// -----------------------------------------------------------------------------------------------------
			// We create all the addresses.
			addresses.stream()
					.distinct()
					// If the address doesn't exists
					.filter(address -> !getAddressRepository().exists(address))
					// We save all the addresses.
					.forEach(address -> {
						BitcoinAddress a = getMapper().toBitcoinAddress(address);
						getAddressRepository().save(a);
						addLog("- Address " + address + " created with id " + a.getId());
					});

			// ---------------------------------------------------------------------------------------------------------
			// We return the block.
			return getBlockRepository().findByHeight(blockHeight);
		} else {
			addError("No response from bitcoind for block n°" + getFormattedBlockHeight(blockHeight));
			return null;
		}
	}

	/**
	 * Return the state to set to the block that has been processed.
	 *
	 * @return state to set of the block that has been processed.
	 */
	@Override
	protected final BitcoinBlockState getNewStateOfProcessedBlock() {
		return BitcoinBlockState.ADDRESSES_IMPORTED;
	}

}
