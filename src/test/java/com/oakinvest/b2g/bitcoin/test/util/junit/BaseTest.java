package com.oakinvest.b2g.bitcoin.test.util.junit;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.batch.bitcoin.BitcoinBatchBlocks;
import com.oakinvest.b2g.batch.bitcoin.BitcoindBufferLoader;
import com.oakinvest.b2g.bitcoin.test.util.mock.BitcoindMock;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionInputRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionOutputRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.bitcoin.BitcoinDataService;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import com.oakinvest.b2g.util.bitcoin.buffer.BitcoinDataServiceBuffer;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;

/**
 * Base for test.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public abstract class BaseTest {

    /**
     * Parameter for live test.
     */
    private static final String PARAMETER_LIVE = "live";

    /**
     * Parameter expected for live test
     */
    private static final String PARAMETER_LIVE_VALUE = "true";

    /**
     * Bitcoin address repository.
     */
    @Autowired
    private BitcoinAddressRepository addressRepository;

    /**
     * Bitcoin block repository.
     */
    @Autowired
    private BitcoinBlockRepository bitcoinBlockRepository;

    /**
     * Bitcoin transaction repository.
     */
    @Autowired
    private BitcoinTransactionRepository transactionRepository;

    /**
     * Transaction import repository.
     */
    @Autowired
    private BitcoinTransactionInputRepository transactionInputRepository;

    /**
     * Transaction output repository.
     */
    @Autowired
    private BitcoinTransactionOutputRepository transactionOutputRepository;

    /**
     * Bitcoind mock.
     */
    @Autowired
    private BitcoindMock bitcoindMock;

    /**
     * Bitcoin data service.
     */
    @Autowired
    private BitcoinDataService bitcoinDataService;

    /**
     * Import batch.
     */
    @Autowired
    private BitcoinBatchBlocks batchBlocks;

    /**
     * Buffer store.
     */
    @Autowired
    private BitcoinDataServiceBuffer buffer;

    /**
     * Buffer loader.
     */
    @Autowired
    private BitcoindBufferLoader bufferLoader;

    /**
     * Bitcoind service.
     */
    @Autowired
    private BitcoindService bitcoindService;

    /**
     * Session factory.
     */
    @Autowired
    private SessionFactory sessionFactory;

    /**
     * If we have the profile "live" active, we delete the cached data from bitcoind.
     */
    @PostConstruct
    private void deleteCache() {
        if (PARAMETER_LIVE_VALUE.equals(System.getProperty(PARAMETER_LIVE))) {
            getBitcoindMock().deleteCache();
        }
    }

    /**
     * Getter de la propriété bds.
     *
     * @return bds
     */
    public final BitcoindService getBitcoindService() {
        return bitcoindService;
    }

    /**
     * Getter bitcoinDataService.
     *
     * @return bitcoinDataService
     */
    public final BitcoinDataService getBitcoinDataService() {
        return bitcoinDataService;
    }

    /**
     * Getter batchBlocks.
     *
     * @return batchBlocks
     */
    public final BitcoinBatchBlocks getBatchBlocks() {
        return batchBlocks;
    }

    /**
     * Getter buffer.
     *
     * @return buffer
     */
    public final BitcoinDataServiceBuffer getBuffer() {
        return buffer;
    }

    /**
     * Getter bufferLoader.
     *
     * @return bufferLoader
     */
    public final BitcoindBufferLoader getBufferLoader() {
        return bufferLoader;
    }

    /**
     * Getter sessionFactory.
     *
     * @return sessionFactory
     */
    public final SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Getter addressRepository.
     *
     * @return addressRepository
     */
    public final BitcoinAddressRepository getAddressRepository() {
        return addressRepository;
    }

    /**
     * Getter transactionRepository.
     *
     * @return transactionRepository
     */
    public final BitcoinTransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    /**
     * Getter transactionInputRepository.
     *
     * @return transactionInputRepository
     */
    public final BitcoinTransactionInputRepository getTransactionInputRepository() {
        return transactionInputRepository;
    }

    /**
     * Getter transactionOutputRepository.
     *
     * @return transactionOutputRepository
     */
    public final BitcoinTransactionOutputRepository getTransactionOutputRepository() {
        return transactionOutputRepository;
    }

    /**
     * Getter bitcoindMock.
     *
     * @return bitcoindMock
     */
    public final BitcoindMock getBitcoindMock() {
        return bitcoindMock;
    }

    /**
     * Getter de la propriété bitcoinBlockRepository.
     *
     * @return bitcoinBlockRepository
     */
    public final BitcoinBlockRepository getBitcoinBlockRepository() {
        return bitcoinBlockRepository;
    }
}
