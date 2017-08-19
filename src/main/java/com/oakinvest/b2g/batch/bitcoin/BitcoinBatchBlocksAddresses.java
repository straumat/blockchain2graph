package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.repository.bitcoin.BitcoinRepositories;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoinDataServiceCacheStore;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState.ADDRESSES_IMPORTED;
import static com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState.BLOCK_IMPORTED;

/**
 * Bitcoin import addresses batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinBatchBlocksAddresses extends BitcoinBatchTemplate {

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
     * @param newCacheStore             cache store
     */
    public BitcoinBatchBlocksAddresses(final BitcoinRepositories newBitcoinRepositories, final BitcoinDataService newBitcoinDataService, final StatusService newStatus, final BitcoinDataServiceCacheStore newCacheStore) {
        super(newBitcoinRepositories, newBitcoinDataService, newStatus, newCacheStore);
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
    protected final Optional<Long> getBlockHeightToProcess() {
		BitcoinBlock blockToTreat = getBlockRepository().findFirstBlockByState(BLOCK_IMPORTED);
		if (blockToTreat != null) {
			return Optional.of(blockToTreat.getHeight());
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Process block.
	 *
	 * @param blockHeight block height to process.
	 */
	@Override
	protected final Optional<BitcoinBlock> processBlock(final long blockHeight) {
        BitcoinBlock blockToProcess = getBlockRepository().findByHeightWithoutDepth(blockHeight);

		// ---------------------------------------------------------------------------------------------------------
		// If we have the data
		if (blockToProcess != null) {

			// -----------------------------------------------------------------------------------------------------
			// We retrieve all the addresses.
            addLog("Listing all addresses from " + blockToProcess.getTx().size() + " transaction(s)");
			final List<String> addresses = Collections.synchronizedList(new ArrayList<String>());
            blockToProcess.getTx()
                    .parallelStream()
                    .forEach(txId ->
                            getTransactionRepository().findByTxId(txId)
                                    .getOutputs()
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .forEach(v -> v.getAddresses()
                                            .stream()
                                            .filter(Objects::nonNull)
                                            .forEach(addresses::add))
                    );

			// -----------------------------------------------------------------------------------------------------
			// We create all the addresses.
			addresses.parallelStream()
                    // We suppress address that exists two times.
					.distinct()
					// If the address doesn't exists
					.filter(address -> !getAddressRepository().exists(address))
					// We save all the addresses.
					.forEach(address -> {
						BitcoinAddress a = getMapper().addressToBitcoinAddress(address);
						getAddressRepository().save(a);
						addLog("- Address " + address + " created with id " + a.getId());
					});

			// ---------------------------------------------------------------------------------------------------------
			// We return the block.
			return Optional.of(blockToProcess);
		} else {
			addError("Impossible to find the block " + getFormattedBlockHeight(blockHeight));
			return Optional.empty();
		}
	}

	/**
	 * Return the state to set to the block that has been processed.
	 *
	 * @return state to set of the block that has been processed.
	 */
	@Override
	protected final BitcoinBlockState getNewStateOfProcessedBlock() {
		return ADDRESSES_IMPORTED;
	}

}
