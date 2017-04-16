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
	private static final long NUMBER_OF_BLOCKS_TO_LOAD_IN_CACHE = 10;

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
	 * Load a block in cache. Set in cache NUMBER_OF_BLOCKS_TO_LOAD_IN_CACHE blocks ahead.
	 */
	@Scheduled(fixedDelay = 1)
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public void loadInCache() {
		try {
			long importedBlockCount = blockRepository.countBlockByState(BitcoinBlockState.IMPORTED);
			for (int i = 0; i <= NUMBER_OF_BLOCKS_TO_LOAD_IN_CACHE; i++) {
				bitcoindService.getBlockData(importedBlockCount + i);
			}
		} catch (Exception e) {
			log.error("Error loading block data in cache " + e.getMessage(), e);
		}
	}

}
