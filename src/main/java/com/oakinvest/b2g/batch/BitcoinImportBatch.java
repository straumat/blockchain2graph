package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import com.oakinvest.b2g.util.bitcoin.BitcoindToDomainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Batch importing the data from the bitcoin blockchain.
 * Created by straumat on 23/02/17.
 */
@Component
public class BitcoinImportBatch {

	/**
	 * Pause between imports.
	 */
	public static final int PAUSE_BETWEEN_IMPORTS = 10;

	/**
	 * Initial delay before importing a block.
	 */
	public static final int BLOCK_IMPORT_INITIAL_DELAY = 1000;

	/**
	 * Initial delay before importing a block addresses.
	 */
	public static final int BLOCK_ADDRESSES_IMPORT_INITIAL_DELAY = 2000;

	/**
	 * Initial delay before importing a block transactions.
	 */
	public static final int BLOCK_TRANSACTIONS_IMPORT_INITIAL_DELAY = 3000;

	/**
	 * Initial delay before importing a block relations.
	 */
	public static final int BLOCK_RELATIONS_IMPORT_INITIAL_DELAY = 4000;

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(BitcoinImportBatch.class);

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bds;

	/**
	 * Mapper.
	 */
	@Autowired
	private BitcoindToDomainMapper mapper;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinBlockRepository bbr;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinAddressRepository bar;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinTransactionRepository btr;

	/**
	 * Import a block on the database.
	 */
	@Scheduled(initialDelay = BLOCK_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	public final void importBlock() {
		// Block to import.
		final long blockToImport = bbr.count() + 1;

		// We retrieve the total number of blocks in bitcoind.
		GetBlockCountResponse blockCountResponse = bds.getBlockCount();
		if (blockCountResponse.getError() == null) {
			// ---------------------------------------------------------------------------------------------------------
			// If there are still blocks to import...
			final long totalBlockCount = bds.getBlockCount().getResult();
			if (blockToImport <= totalBlockCount) {
				// -----------------------------------------------------------------------------------------------------
				// We retrieve the block hash...
				GetBlockHashResponse blockHashResponse = bds.getBlockHash(blockToImport);
				if (blockHashResponse.getError() == null) {
					// -------------------------------------------------------------------------------------------------
					// Then we retrieve the block data...
					String blockHash = blockHashResponse.getResult();
					status.addLog("importBlock : Importing block n°" + blockToImport + " (" + blockHash + ")");
					GetBlockResponse blockResponse = bds.getBlock(blockHash);
					if (blockResponse.getError() == null) {
						// ---------------------------------------------------------------------------------------------
						// Then, if the block doesn't exists, we save it.
						BitcoinBlock block = bbr.findByHash(blockHash);
						if (block == null) {
							block = mapper.blockResultToBitcoinBlock(blockResponse.getResult());
							bbr.save(block);
							status.addLog("importBlock : Block n°" + blockToImport + " saved with id " + block.getId());
						} else {
							status.addLog("importBlock : Block n°" + blockToImport + " already saved with id " + block.getId());
						}
					} else {
						// Error while retrieving the block informations.
						status.addError("importBlock : Error getting block n°" + blockToImport + " informations : " + blockResponse.getError());
					}
				} else {
					// Error while retrieving the block hash.
					status.addError("importBlock : Error getting the hash of block n°" + blockToImport + " : " + blockHashResponse.getError());
				}
			} else {
				status.addLog("importBlock : all block are imported");
			}
		} else {
			// Error while retrieving the number of blocks in bitcoind.
			status.addError("importBlock : Error getting number of blocks in bitcoind : " + blockCountResponse.getError());
		}
	}

	/**
	 * Import the addresses of a block in the database.
	 */
	@Scheduled(initialDelay = BLOCK_ADDRESSES_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	public void importBlockAddresses() {

	}

	/**
	 * Import the transactions of a block in the database.
	 */
	@Scheduled(initialDelay = BLOCK_TRANSACTIONS_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	public void importBlockTransactions() {

	}

	/**
	 * Import the relations of a block in the datbase.
	 */
	@Scheduled(initialDelay = BLOCK_RELATIONS_IMPORT_INITIAL_DELAY, fixedDelay = PAUSE_BETWEEN_IMPORTS)
	public void importBlockRelations() {

	}

}
