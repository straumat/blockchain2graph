package com.oakinvest.b2g.batch.bitcoin.step1.blocks;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService;
import com.oakinvest.b2g.util.bitcoin.batch.BitcoinBatchTemplate;
import org.springframework.stereotype.Component;

/**
 * Bitcoin import blocks batch.
 * Created by straumat on 27/02/17.
 */
@Component
public class BitcoinBatchBlocks extends BitcoinBatchTemplate {

	/**
	 * Log prefix.
	 */
	private static final String PREFIX = "Blocks batch";

	/**
	 * Constructor.
	 *
	 * @param newBlockRepository       blockRepository
	 * @param newAddressRepository     addressRepository
	 * @param newTransactionRepository transactionRepository
	 * @param newBitcoindService       bitcoindService
	 * @param newStatus                status
	 */
	public BitcoinBatchBlocks(final BitcoinBlockRepository newBlockRepository, final BitcoinAddressRepository newAddressRepository, final BitcoinTransactionRepository newTransactionRepository, final BitcoindService newBitcoindService, final StatusService newStatus) {
		super(newBlockRepository, newAddressRepository, newTransactionRepository, newBitcoindService, newStatus);
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
		// We retrieve the next block to process according to the database.
		Long blockToProcess = getBlockRepository().count() + 1;

		// -------------------------------------------------------------------------------------------------------------
		// We check if that next block exists by retrieving the block count.
		try {
			GetBlockCountResponse blockCountResponse = getBitcoindService().getBlockCount();
			if (blockCountResponse.getError() == null) {
				final long totalBlockCount = blockCountResponse.getResult();
				// We update the global status.
				if (totalBlockCount != getStatus().getTotalBlockCount()) {
					getStatus().setTotalBlockCount(totalBlockCount);
				}

				// We return the block to process.
				if (blockToProcess <= totalBlockCount) {
					// If there is still block after this one, we continue.
					return blockToProcess;
				} else {
					return null;
				}
			} else {
				// Error while retrieving the number of blocks in bitcoind.
				addError("Error getting the number of blocks : " + blockCountResponse.getError());
				return null;
			}
		} catch (Exception e) {
			// Error while retrieving the number of blocks in bitcoind.
			addError("Error getting the number of blocks : " + e.getMessage(), e);
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
		BitcoindBlockData blockData = getBitcoindService().getBlockData(blockHeight);
		// -------------------------------------------------------------------------------------------------------------
		// If we have the data
		if (blockData != null) {
			// ---------------------------------------------------------------------------------------------------------
			// Then, if the block doesn't exists, we map it to save it.
			BitcoinBlock block = getBlockRepository().findByHash(blockData.getBlock().getHash());
			if (block == null) {
				block = getMapper().blockResultToBitcoinBlock(blockData.getBlock());
			}

			// ---------------------------------------------------------------------------------------------------------
			// We return the block.
			return block;
		} else {
			// Or nothing if we did not retrieve the data.
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
		return BitcoinBlockState.BLOCK_IMPORTED;
	}

}
