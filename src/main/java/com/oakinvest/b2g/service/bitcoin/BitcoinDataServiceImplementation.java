package com.oakinvest.b2g.service.bitcoin;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockDataComparator;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.service.BitcoinDataService;
import com.oakinvest.b2g.service.BitcoindService;
import com.oakinvest.b2g.service.StatusService;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.oakinvest.b2g.configuration.ParametersConfiguration.BITCOIND_BUFFER_SIZE;
import static com.oakinvest.b2g.configuration.ParametersConfiguration.BITCOIN_BLOCK_GENERATION_DELAY;

/**
 * Bitcoin data service implementation.
 * Created by straumat on 11/06/17.
 */
@Service
public class BitcoinDataServiceImplementation implements BitcoinDataService {

    /**
     * Genesis transaction hash.
     */
    private static final String GENESIS_BLOCK_TRANSACTION = "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b";

    /**
     * How many milli seconds in 1 minute.
     */
    private static final float MILLISECONDS_IN_ONE_MINUTE = 60F * 1000F;

    /**
     * Status service.
     */
    private final StatusService status;

    /**
     * Bitcoind service.
     */
    private final BitcoindService bitcoindService;

    /**
     * Buffer content.
     */
    private final ConcurrentSkipListSet<BitcoindBlockData> buffer;

    /**
     * Last block count value.
     */
    private long lastBlockCountValue = -1;

    /**
     * Last block count access.
     */
    private long lastBlockCountAccess = -1;

    /**
     * Constructor.
     *
     * @param newBitcoindService bitcoind service
     * @param newStatusService status service
     */
    public BitcoinDataServiceImplementation(final BitcoindService newBitcoindService, final StatusService newStatusService) {
        this.status = newStatusService;
        this.bitcoindService = newBitcoindService;
        buffer = new ConcurrentSkipListSet<>(new BitcoindBlockDataComparator());
    }

    /**
     * Return getblockcount (The result stays in cache for 10 minutes).
     *
     * @return the number of blocks in the block chain & -1 if error.
     */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public long getBlockCount() {
        // Getting the time elapsed since last call to getblockcount.
        float elapsedMinutesSinceLastCall = (System.currentTimeMillis() - lastBlockCountAccess) / MILLISECONDS_IN_ONE_MINUTE;

        if (elapsedMinutesSinceLastCall < BITCOIN_BLOCK_GENERATION_DELAY && lastBlockCountAccess != -1) {
            // If the last call to getblockcount was made less than 10 minutes ago, we return the result in cache.
            return lastBlockCountValue;
        } else {
            // Clean buffer.
            while (buffer.size() >= BITCOIND_BUFFER_SIZE) {
                buffer.pollFirst();
            }

            // Else we get it from the bitcoind server.
            try {
                GetBlockCountResponse blockCountResponse = bitcoindService.getBlockCount();
                if (blockCountResponse.getError() == null) {
                    lastBlockCountValue = blockCountResponse.getResult();
                    lastBlockCountAccess = System.currentTimeMillis();
                    return lastBlockCountValue;
                }  else {
                    // Error while retrieving the number of blocks in bitcoind.
                    status.addError("Error getting the number of blocks : " + blockCountResponse.getError(), null);
                    lastBlockCountAccess = -1;
                    return -1;
                }
            } catch (Exception e) {
                // Error while retrieving the number of blocks in bitcoind.
                status.addError("Error getting the number of blocks : " + e.getMessage(), e);
                lastBlockCountAccess = -1;
                return -1;
            }
        }
    }

    /**
     * Get block data from buffer.
     *
     * @param blockHeight block height
     * @return block data
     */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public Optional<BitcoindBlockData> getBlockData(final long blockHeight) {
        // Getting result
        Optional<BitcoindBlockData> result = getBlockDataFromBuffer(blockHeight);
        if (result.isPresent()) {
            // If the block is in the buffer, we return it and remove it.
            buffer.remove(result.get());
            return result;
        } else {
            // Else we get it directly from bitcoind and add it to the buffer.
            Optional<BitcoindBlockData> blockData = getBlockDataFromBitcoind(blockHeight);
            if (blockData.isPresent()) {
                buffer.add(blockData.get());
                return blockData;
            } else {
                return Optional.empty();
            }
        }
    }

    /**
     * Get the buffer.
     *
     * @return buffer
     */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public ConcurrentSkipListSet<BitcoindBlockData> getBuffer() {
        return buffer;
    }

    /**
     * Get block data from buffer.
     *
     * @param blockHeight block height
     * @return block data
     */
    private Optional<BitcoindBlockData> getBlockDataFromBuffer(final long blockHeight) {
        return buffer.stream().filter(b -> b.getBlock().getHeight() == blockHeight).findFirst();
    }

    /**
     * Get block data from bitcoind.
     *
     * @param blockHeight block height
     * @return block data
     */
    private Optional<BitcoindBlockData> getBlockDataFromBitcoind(final long blockHeight) {
        try {
            // ---------------------------------------------------------------------------------------------------------
            // We retrieve the block hash...
            GetBlockHashResponse blockHashResponse = bitcoindService.getBlockHash(blockHeight);
            if (blockHashResponse.getError() == null) {
                // -----------------------------------------------------------------------------------------------------
                // Then we retrieve the block data...
                String blockHash = blockHashResponse.getResult();
                final GetBlockResponse blockResponse = bitcoindService.getBlock(blockHash);
                if (blockResponse.getError() == null) {
                    // -------------------------------------------------------------------------------------------------
                    // Then we retrieve the transactions data...
                    final List<GetRawTransactionResult> transactions = new LinkedList<>();
                    try {
                        // We use multi thread to retrieve all the transactions information.
                        final Map<String, GetRawTransactionResult> tempTransactionList = new ConcurrentHashMap<>();
                        blockResponse.getResult().getTx()
                                .parallelStream()
                                .filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION))
                                .forEach(t -> {
                                    GetRawTransactionResponse r = bitcoindService.getRawTransaction(t);
                                    if (r.getError() == null) {
                                        tempTransactionList.put(t, bitcoindService.getRawTransaction(t).getResult());
                                    } else {
                                        throw new RuntimeException("Error getting transaction n°" + t + " informations : " + r.getError());
                                    }
                                });

                        // Then we add it to the list in the right order.
                        blockResponse.getResult().getTx().forEach(t -> transactions.add(tempTransactionList.get(t)));

                    } catch (Exception e) {
                        status.addError("Error retrieving the block : " + e.getMessage(), e);
                        return Optional.empty();
                    }
                    // -------------------------------------------------------------------------------------------------
                    // And we end up returning all the block data all at once.
                    return Optional.of(new BitcoindBlockData(blockResponse.getResult(), transactions));
                } else {
                    // Error while retrieving the block informations.
                    status.addError("Error retrieving the block : " + blockResponse.getError(), null);
                    return Optional.empty();
                }
            } else {
                // Error while retrieving the block hash.
                status.addError("Error getting the hash of block n°" + blockHeight + " : " + blockHashResponse.getError(), null);
                return Optional.empty();
            }
        } catch (Exception e) {
            status.addError("Error getting the block data of block n°" + blockHeight + " : " + e.getMessage(), e);
            return Optional.empty();
        }
    }

}
