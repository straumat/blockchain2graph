package com.oakinvest.b2g.bitcoin.batch;

import com.oakinvest.b2g.bitcoin.domain.BitcoinAddress;
import com.oakinvest.b2g.bitcoin.domain.BitcoinBlock;
import com.oakinvest.b2g.bitcoin.domain.BitcoinTransaction;
import com.oakinvest.b2g.bitcoin.domain.BitcoinTransactionOutput;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.BitcoinCoreBlockData;
import com.oakinvest.b2g.bitcoin.util.exception.OriginTransactionNotFoundException;
import com.oakinvest.b2g.bitcoin.util.mapper.BitcoinCoreToDomainMapper;
import com.oakinvest.b2g.bitcoin.util.providers.RepositoriesProvider;
import com.oakinvest.b2g.bitcoin.util.providers.ServicesProvider;
import com.oakinvest.b2g.bitcoin.util.status.ApplicationStatus;
import com.oakinvest.b2g.bitcoin.util.status.CurrentBlockStatusProcessStep;
import org.mapstruct.factory.Mappers;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.oakinvest.b2g.bitcoin.configuration.ApplicationConfiguration.LOG_SEPARATOR;
import static com.oakinvest.b2g.bitcoin.configuration.ApplicationConfiguration.PAUSE_BEFORE_STARTING_APPLICATION;
import static com.oakinvest.b2g.bitcoin.configuration.ApplicationConfiguration.BLOCK_GENERATION_DELAY;
import static com.oakinvest.b2g.bitcoin.util.status.CurrentBlockStatusProcessStep.LOADING_TRANSACTIONS_FROM_BITCOIN_CORE;

/**
 * Batch importing bitcoin blocks.
 *
 * Created by straumat on 27/02/17.
 */
@Component
public class ImportBatch {

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(ImportBatch.class);

    /**
     * Neo4j session.
     */
    private final Session session;

    /**
     * Repositories.
     */
    private final RepositoriesProvider repositories;

    /**
     * Services.
     */
    private final ServicesProvider services;

    /**
     * Status component.
     */
    private final ApplicationStatus status;

    /**
     * Mapper.
     */
    private final BitcoinCoreToDomainMapper mapper = Mappers.getMapper(BitcoinCoreToDomainMapper.class);

    /**
     * Constructor.
     * @param newSessionFactory session factory
     * @param newRepositories repositories
     * @param newServices services
     * @param newApplicationStatus application status
     */
    public ImportBatch(final SessionFactory newSessionFactory, final RepositoriesProvider newRepositories, final ServicesProvider newServices, final ApplicationStatus newApplicationStatus) {
        this.session = newSessionFactory.openSession();
        this.repositories = newRepositories;
        this.services = newServices;
        this.status = newApplicationStatus;
    }

    /**
     * Execute the batch.
     */
    @Transactional
    @Scheduled(fixedDelay = 1, initialDelay = PAUSE_BEFORE_STARTING_APPLICATION)
    @SuppressWarnings("checkstyle:designforextension")
    public void execute() {
        Instant batchStartTime = Instant.now();
        log.info(LOG_SEPARATOR);
        try {
            // We retrieve the block to process.
            Optional<Integer> blockHeightToProcess = getBlockHeightToProcess();

            // If there is a block to process.
            if (blockHeightToProcess.isPresent()) {
                // Process the block.
                log.info("Starting to process block " + getFormattedBlockHeight(blockHeightToProcess.get()));
                status.getCurrentBlockStatus().setBlockHeight(blockHeightToProcess.get());
                Optional<BitcoinBlock> blockToProcess = processBlock(blockHeightToProcess.get());

                // If the process ended well.
                blockToProcess.ifPresent((BitcoinBlock bitcoinBlock) -> {
                    // Before saving it, we start to load the next block in the buffer.
                    services.getBitcoinDataServiceBufferLoader().loadBlockInBuffer(blockHeightToProcess.get() + 1);

                    // If the block has been well processed, we change the state and we save it.
                    log.info("Saving block data");
                    status.getCurrentBlockStatus().setProcessStep(CurrentBlockStatusProcessStep.SAVING_BLOCK);
                    repositories.getBlockRepository().save(bitcoinBlock);

                    // We calculate time.
                    Duration batchDuration = Duration.between(batchStartTime, Instant.now());
                    long secondsDuration = batchDuration.getSeconds();
                    // TODO Improve when there won't be anymore this error : java.time.temporal.UnsupportedTemporalTypeException: Unsupported unit: Millis
                    long millisecondsDuration = TimeUnit.NANOSECONDS.toMillis(batchDuration.minusSeconds(secondsDuration).getNano());
                    log.info("Block " + bitcoinBlock.getFormattedHeight() + " processed in " + secondsDuration + "."  + millisecondsDuration + " secs");
                    // TODO Improve when JDK8 won't have this error anymore : java.time.temporal.UnsupportedTemporalTypeException: Unsupported unit: Millis
                    status.setLastBlockProcessDuration(TimeUnit.NANOSECONDS.toMillis(batchDuration.getNano()));

                    // We set status.
                    status.getCurrentBlockStatus().setProcessStep(CurrentBlockStatusProcessStep.BLOCK_SAVED);
                    status.setBlockCountInNeo4j(bitcoinBlock.getHeight());
                });
            } else {
                // If there is nothing to process.
                log.info("No block to process");
                status.getCurrentBlockStatus().setProcessStep(CurrentBlockStatusProcessStep.NO_BLOCK_TO_PROCESS);
                Thread.sleep(BLOCK_GENERATION_DELAY);
            }
        } catch (Exception e) {
            status.setLastErrorMessage("An error occurred while processing block : " + e.getMessage());
            log.error("An error occurred while processing block : " + e.getMessage(), e);
        } finally {
            session.clear();
        }
    }

    /**
     * Return the block to process.
     *
     * @return block to process.
     */
    private Optional<Integer> getBlockHeightToProcess() {
        // We retrieve the next block to process according to the database.
        int blockToProcess = (int) (repositories.getBlockRepository().count() + 1);
        final Optional<Integer> totalBlockCount = services.getBitcoinDataService().getBlockCount();

        // We check if that next block exists by retrieving the block count.
        if (totalBlockCount.isPresent()) {
            // We update the global status of blockcount (if needed).
            if (totalBlockCount.get() != status.getBlockCountInBitcoinCore()) {
                status.setBlockCountInBitcoinCore(totalBlockCount.get());
            }
            // We return the block to process.
            if (blockToProcess <= totalBlockCount.get()) {
                return Optional.of(blockToProcess);
            } else {
                return Optional.empty();
            }
        } else {
            // Error while retrieving the number of blocks in core.
            return Optional.empty();
        }
    }

    /**
     * Process block.
     *
     * @param blockHeight block height to process.
     * @return block processed
     */
    private Optional<BitcoinBlock> processBlock(final int blockHeight) {
        status.getCurrentBlockStatus().setProcessStep(LOADING_TRANSACTIONS_FROM_BITCOIN_CORE);
        log.info("Loading block data from Bitcoin core");
        Optional<BitcoinCoreBlockData> blockData = services.getBitcoinDataService().getBlockData(blockHeight);

        // -------------------------------------------------------------------------------------------------------------
        // If we have the data.
        if (blockData.isPresent()) {

            // ---------------------------------------------------------------------------------------------------------
            // We create the block to save. We retrieve the data from core and map it.
            status.getCurrentBlockStatus().setTransactionCount(blockData.get().getTransactions().size());
            status.getCurrentBlockStatus().setAddressCount(blockData.get().getAddresses().size());
            final BitcoinBlock block = mapper.blockDataToBitcoinBlock(blockData.get());

            // ---------------------------------------------------------------------------------------------------------
            // We get all the addresses.
            status.getCurrentBlockStatus().setProcessStep(CurrentBlockStatusProcessStep.PROCESSING_ADDRESSES);
            final AtomicInteger addressesCounter = new AtomicInteger(0);
            final Map<String, BitcoinAddress> addressesCache = new ConcurrentHashMap<>();
            log.info("Treating " + blockData.get().getAddresses().size() + " address(es)");
            blockData.get().getAddresses()
                    .parallelStream() // In parallel.
                    .filter(Objects::nonNull) // If the address is not null.
                    .forEach(a -> {
                        Optional<BitcoinAddress> addressInRepository = repositories.getAddressRepository().findByAddressWithoutDepth(a);
                        if (addressInRepository.isPresent()) {
                            addressesCache.put(a, addressInRepository.get());
                            log.info("- Address " + a + " already exists");
                        } else {
                            addressesCache.put(a, new BitcoinAddress(a));
                            log.info("- Creating address " + a);
                        }
                        status.getCurrentBlockStatus().setAddressCount(addressesCounter.incrementAndGet());
                    });

            // ---------------------------------------------------------------------------------------------------------
            // We link the addresses to the input and the origin transaction.
            status.getCurrentBlockStatus().setProcessStep(CurrentBlockStatusProcessStep.PROCESSING_TRANSACTIONS);
            final AtomicInteger transactionCounter = new AtomicInteger(0);
            final int txSize = block.getTx().size();
            log.info("Treating " + txSize + " transaction(s)");
            block.getTransactions()
                    .parallelStream()
                    .forEach(
                            t -> {
                                // -------------------------------------------------------------------------------------
                                // For each Vin.
                                t.getInputs()
                                        .stream()
                                        .filter(vin -> !vin.isCoinbase()) // If it's NOT a coinbase transaction.
                                        .forEach(vin -> {
                                            // -------------------------------------------------------------------------
                                            // We retrieve the original transaction.
                                            Optional<BitcoinTransactionOutput> originTransactionOutput = repositories.getBitcoinTransactionOutputRepository().findByTxIdAndN(vin.getTxId(), vin.getvOut());

                                            // if we don't find in the database, this transaction must be in the block.
                                            if (!originTransactionOutput.isPresent()) {
                                                Optional<BitcoinTransaction> missingTransaction = block.getTransactions()
                                                        .stream()
                                                        .filter(o -> o.getTxId().equals(vin.getTxId()))
                                                        .findFirst();
                                                if (missingTransaction.isPresent() && missingTransaction.get().getOutputByIndex(vin.getvOut()).isPresent()) {
                                                    originTransactionOutput = Optional.of(missingTransaction.get().getOutputByIndex(vin.getvOut()).get());
                                                }
                                            }

                                            if (originTransactionOutput.isPresent()) {
                                                // -------------------------------------------------------------------------
                                                // We create the link.
                                                vin.setTransactionOutput(originTransactionOutput.get());

                                                // -------------------------------------------------------------------------
                                                // We set all the addresses linked to this input.
                                                originTransactionOutput.get().getAddresses()
                                                        .stream()
                                                        .filter(Objects::nonNull)
                                                        .forEach(a -> vin.setBitcoinAddress(addressesCache.get(a)));
                                            } else {
                                                throw new OriginTransactionNotFoundException("Origin transaction not found " + vin.getTxId() + " / " + vin.getvOut());
                                            }
                                        });

                                // -------------------------------------------------------------------------------------
                                // For each Vout.
                                t.getOutputs()
                                        .forEach(vout -> {
                                            // -------------------------------------------------------------------------
                                            // We set all the addresses linked to this output.
                                            vout.getAddresses()
                                                    .stream()
                                                    .filter(Objects::nonNull)
                                                    .forEach(a -> vout.setBitcoinAddress(addressesCache.get(a)));
                                        });

                                // -------------------------------------------------------------------------------------
                                // Logging.
                                status.getCurrentBlockStatus().setTransactionCount(transactionCounter.incrementAndGet());
                                log.info("- Transaction " + transactionCounter.get() + "/" + txSize + " created (" + t.getTxId() + " : " + t.getInputs().size() + " vin(s) & " + t.getOutputs().size() + " vout(s))");
                            });

            // ---------------------------------------------------------------------------------------------------------
            // We set the previous and the next block.
            Optional<BitcoinBlock> previousBlock = repositories.getBlockRepository().findByHeightWithoutDepth(block.getHeight() - 1);
            previousBlock.ifPresent(previous -> {
                log.info("Linking this block to the previous one");
                block.setPreviousBlock(previous);
                previous.setNextBlock(block);
            });

            // ---------------------------------------------------------------------------------------------------------
            // We return the block.
            return Optional.of(block);

        } else {
            // Or nothing if we did not retrieve the data.
            status.setLastErrorMessage("No response from core for block n°" + getFormattedBlockHeight(blockHeight));
            log.error("No response from core for block n°" + getFormattedBlockHeight(blockHeight));
            return Optional.empty();
        }
    }

    /**
     * Returns the block height in a formatted way.
     *
     * @param blockHeight block height
     * @return formatted block height
     */
    private String getFormattedBlockHeight(final int blockHeight) {
        return String.format("%09d", blockHeight);
    }

}