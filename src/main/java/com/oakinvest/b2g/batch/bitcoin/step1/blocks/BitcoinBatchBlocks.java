package com.oakinvest.b2g.batch.bitcoin.step1.blocks;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockcount.GetBlockCountResponse;
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
	 * Returns the log prefix to display in each log.
	 */
	@Override
	public final String getLogPrefix() {
		return PREFIX;
	}

	/**
	 * Return the block to treat.
	 *
	 * @return block to treat.
	 */
	@Override
	protected final Long getBlockToTreat() {
		// We retrieve the next block to treat according to the database.
		Long blockToTreat = getBlockRepository().count() + 1;

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

				// We return the block to treat.
				if (blockToTreat <= totalBlockCount) {
					// If there is still block after this one, we continue.
					return blockToTreat;
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
	 * Treat block.
	 *
	 * @param blockNumber block number to treat.
	 */
	@Override
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	protected final BitcoinBlock treatBlock(final long blockNumber) {
		BitcoindBlockData blockData = getBitcoindService().getBlockData(blockNumber);
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
			addError("No response from bitcoind for block n°" + getFormattedBlock(blockNumber));
			return null;
		}
	}

	/**
	 * Return the state to set to the block that has been treated.
	 *
	 * @return state to set of the block that has been treated.
	 */
	@Override
	protected final BitcoinBlockState getNewStateOfTreatedBlock() {
		return BitcoinBlockState.BLOCK_IMPORTED;
	}

}
