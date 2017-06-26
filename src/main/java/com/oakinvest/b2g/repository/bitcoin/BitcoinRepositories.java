package com.oakinvest.b2g.repository.bitcoin;

import org.springframework.stereotype.Component;

/**
 * Bitcoin repositories.
 * Created by straumat on 11/06/17.
 */
@Component
public class BitcoinRepositories {

    /**
     * Bitcoin address repository.
     */
    private final BitcoinAddressRepository bitcoinAddressRepository;

    /**
     * Bitcoin block repository.
     */
    private final BitcoinBlockRepository bitcoinBlockRepository;

    /**
     * Bitcoin transaction repository.
     */
    private final BitcoinTransactionRepository bitcoinTransactionRepository;

    /**
     * Constructor.
     * @param newBitcoinAddressRepository Bitcoin address repository
     * @param newBitcoinBlockRepository Bitcoin block repository
     * @param newBitcoinTransactionRepository Bitcoin transaction repository
     */
    public BitcoinRepositories(final BitcoinAddressRepository newBitcoinAddressRepository, final BitcoinBlockRepository newBitcoinBlockRepository, final BitcoinTransactionRepository newBitcoinTransactionRepository) {
        this.bitcoinAddressRepository = newBitcoinAddressRepository;
        this.bitcoinBlockRepository = newBitcoinBlockRepository;
        this.bitcoinTransactionRepository = newBitcoinTransactionRepository;
    }

    /**
     * Getter.
     * @return bitcoin address repository
     */
    public final BitcoinAddressRepository getBitcoinAddressRepository() {
        return bitcoinAddressRepository;
    }

    /**
     * Getter.
     * @return bitcoin block repository
     */
    public final BitcoinBlockRepository getBitcoinBlockRepository() {
        return bitcoinBlockRepository;
    }

    /**
     * Getter.
     * @return bitcoin transaction repository
     */
    public final BitcoinTransactionRepository getBitcoinTransactionRepository() {
        return bitcoinTransactionRepository;
    }

}
