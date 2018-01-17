package com.oakinvest.b2g.repository.bitcoin;

import org.springframework.stereotype.Component;

/**
 * Bitcoin repositories.
 * Created by straumat on 11/06/17.
 */
@SuppressWarnings("unused")
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
     * Bitcoin transaction input repository.
     */
    private final BitcoinTransactionInputRepository bitcoinTransactionInputRepository;

    /**
     * Bitcoin transaction output repository.
     */
    private final BitcoinTransactionOutputRepository bitcoinTransactionOutputRepository;

    /**
     * Constructor.
     * @param newBitcoinAddressRepository Bitcoin address repository
     * @param newBitcoinBlockRepository Bitcoin block repository
     * @param newBitcoinTransactionRepository Bitcoin transaction repository
     * @param newBitcoinTransactionInputRepository Bitcoin transaction input repository
     * @param newBitcoinTransactionOutputRepository Bitcoin transaction output repository
     */
    public BitcoinRepositories(final BitcoinAddressRepository newBitcoinAddressRepository, final BitcoinBlockRepository newBitcoinBlockRepository, final BitcoinTransactionRepository newBitcoinTransactionRepository, final BitcoinTransactionInputRepository newBitcoinTransactionInputRepository, final BitcoinTransactionOutputRepository newBitcoinTransactionOutputRepository) {
        this.bitcoinAddressRepository = newBitcoinAddressRepository;
        this.bitcoinBlockRepository = newBitcoinBlockRepository;
        this.bitcoinTransactionRepository = newBitcoinTransactionRepository;
        this.bitcoinTransactionInputRepository = newBitcoinTransactionInputRepository;
        this.bitcoinTransactionOutputRepository = newBitcoinTransactionOutputRepository;
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

    /**
     * Getter.
     * @return bitcoin transaction input repository
     */
    public final BitcoinTransactionInputRepository getBitcoinTransactionInputRepository() {
        return bitcoinTransactionInputRepository;
    }

    /**
     * Getter.
     * @return Bitcoin transaction output repository
     */
    public final BitcoinTransactionOutputRepository getBitcoinTransactionOutputRepository() {
        return bitcoinTransactionOutputRepository;
    }

}
