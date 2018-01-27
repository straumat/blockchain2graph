package com.oakinvest.b2g.batch.bitcoin;

import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.bitcoin.BitcoinDataService;
import com.oakinvest.b2g.util.bitcoin.buffer.BitcoinDataServiceBuffer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Bitcoind buffer loader.
 */
@Component
public class BitcoindBufferLoader {

    /**
     * Bitcoin data service.
     */
    private final BitcoinDataService bitcoinDataService;

    /**
     * Bitcoin block repository.
     */
    private final BitcoinBlockRepository blockRepository;

    /**
     * Buffer.
     */
    private final BitcoinDataServiceBuffer buffer;

    /**
     * Buffer size.
     */
    @Value("${bitcoind.buffer.size}")
    private int bufferSize;

    /**
     * Constructor.
     *
     * @param newBitcoinDataService bitcoind data service
     * @param newBlockRepository    block repository
     * @param newBuffer             buffer
     */
    public BitcoindBufferLoader(final BitcoinDataService newBitcoinDataService, final BitcoinBlockRepository newBlockRepository, final BitcoinDataServiceBuffer newBuffer) {
        this.bitcoinDataService = newBitcoinDataService;
        this.blockRepository = newBlockRepository;
        this.buffer = newBuffer;
    }

    /**
     * Execute the batch.
     */
    @Scheduled(fixedDelay = 1)
    @SuppressWarnings("checkstyle:designforextension")
    public void loadInBuffer() {
        // We get the required data.
        // Total block count in bitcoind.
        Optional<Integer> blockCount = bitcoinDataService.getBlockCount();
        // The last block being inserted.
        int currentBlock = (int) blockRepository.count();
        // The block to load in the buffer.
        int blockToLoadInBuffer = currentBlock + bufferSize;


        // if the block to load in buffer exists in bitcoind.
        if (blockCount.isPresent() && blockToLoadInBuffer < blockCount.get() && !buffer.isBLockInBuffer(blockToLoadInBuffer)) {
            // We load current block + ${bitcoind.buffer.size} blocks ahead.
            bitcoinDataService.addBlockInBuffer(blockToLoadInBuffer);
        }

        // We remove the useless block in buffer.
        buffer.purge(currentBlock);
    }

}
