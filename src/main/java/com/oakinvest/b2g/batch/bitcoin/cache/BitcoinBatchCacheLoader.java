package com.oakinvest.b2g.batch.bitcoin.cache;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * This batch loads block data from bitcoind ahead of the import loadInCache.
 * Created by straumat on 20/03/17.
 */
@Service
public class BitcoinBatchCacheLoader {

	/**
	 * Number of blocks to cache.
	 */
	private static final long NUMBER_OF_BLOCKS_TO_CACHE = 10;

	/**
	 * Pause between load in cache.
	 */
	private static final int PAUSE_BETWEEN_LOAD_IN_CACHE = 1000;

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(BitcoinBatchCacheLoader.class);

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bitcoindService;

	/**
	 * BitcoinBlock repository.
	 */
	@Autowired
	private BitcoinBlockRepository blockRepository;

	/**
	 * Load a block in cache. Set in cache NUMBER_OF_BLOCKS_TO_CACHE blocks ahead.
	 */
	@Scheduled(fixedDelay = PAUSE_BETWEEN_LOAD_IN_CACHE)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void loadInCache() {
		try {
			long importedBlockCount = getBlockRepository().countBlockByState(BitcoinBlockState.IMPORTED);
			for (int i = 1; i <= NUMBER_OF_BLOCKS_TO_CACHE; i++) {
				getBitcoindService().getBlockData(importedBlockCount + i);
			}
		} catch (Exception e) {
			log.error("Error loading cache " + e.getMessage(), e);
		}
	}

	/**
	 * Getter de la propriété bitcoindService.
	 *
	 * @return bitcoindService
	 */
	public final BitcoindService getBitcoindService() {
		return bitcoindService;
	}

	/**
	 * Setter de la propriété bitcoindService.
	 *
	 * @param newBitcoindService the bitcoindService to set
	 */
	public final void setBitcoindService(final BitcoindService newBitcoindService) {
		bitcoindService = newBitcoindService;
	}

	/**
	 * Getter de la propriété blockRepository.
	 *
	 * @return blockRepository
	 */
	public final BitcoinBlockRepository getBlockRepository() {
		return blockRepository;
	}

	/**
	 * Setter de la propriété blockRepository.
	 *
	 * @param newBlockRepository the blockRepository to set
	 */
	public final void setBlockRepository(final BitcoinBlockRepository newBlockRepository) {
		blockRepository = newBlockRepository;
	}
}
