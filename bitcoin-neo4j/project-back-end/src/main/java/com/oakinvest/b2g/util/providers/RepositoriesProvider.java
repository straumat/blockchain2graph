package com.oakinvest.b2g.util.providers;

import com.oakinvest.b2g.repository.AddressRepository;
import com.oakinvest.b2g.repository.BlockRepository;
import com.oakinvest.b2g.repository.TransactionInputRepository;
import com.oakinvest.b2g.repository.TransactionOutputRepository;
import com.oakinvest.b2g.repository.TransactionRepository;
import org.springframework.stereotype.Component;

/**
 * Bitcoin repositories.
 *
 * Created by straumat on 11/06/17.
 */
@SuppressWarnings("unused")
@Component
public class RepositoriesProvider {

    /**
     * Bitcoin address repository.
     */
    private final AddressRepository addressRepository;

    /**
     * Bitcoin block repository.
     */
    private final BlockRepository blockRepository;

    /**
     * Bitcoin transaction repository.
     */
    private final TransactionRepository bitcoinTransactionRepository;

    /**
     * Bitcoin transaction input repository.
     */
    private final TransactionInputRepository bitcoinTransactionInputRepository;

    /**
     * Bitcoin transaction output repository.
     */
    private final TransactionOutputRepository bitcoinTransactionOutputRepository;

    /**
     * Constructor.
     * @param newAddressRepository Bitcoin address repository
     * @param newBlockRepository Bitcoin block repository
     * @param newBitcoinTransactionRepository Bitcoin transaction repository
     * @param newBitcoinTransactionInputRepository Bitcoin transaction input repository
     * @param newBitcoinTransactionOutputRepository Bitcoin transaction output repository
     */
    public RepositoriesProvider(final AddressRepository newAddressRepository, final BlockRepository newBlockRepository, final TransactionRepository newBitcoinTransactionRepository, final TransactionInputRepository newBitcoinTransactionInputRepository, final TransactionOutputRepository newBitcoinTransactionOutputRepository) {
        this.addressRepository = newAddressRepository;
        this.blockRepository = newBlockRepository;
        this.bitcoinTransactionRepository = newBitcoinTransactionRepository;
        this.bitcoinTransactionInputRepository = newBitcoinTransactionInputRepository;
        this.bitcoinTransactionOutputRepository = newBitcoinTransactionOutputRepository;
    }

    /**
     * Getter.
     * @return bitcoin address repository
     */
    public final AddressRepository getAddressRepository() {
        return addressRepository;
    }

    /**
     * Getter.
     * @return bitcoin block repository
     */
    public final BlockRepository getBlockRepository() {
        return blockRepository;
    }

    /**
     * Getter.
     * @return bitcoin transaction repository
     */
    public final TransactionRepository getBitcoinTransactionRepository() {
        return bitcoinTransactionRepository;
    }

    /**
     * Getter.
     * @return bitcoin transaction input repository
     */
    public final TransactionInputRepository getBitcoinTransactionInputRepository() {
        return bitcoinTransactionInputRepository;
    }

    /**
     * Getter.
     * @return Bitcoin transaction output repository
     */
    public final TransactionOutputRepository getBitcoinTransactionOutputRepository() {
        return bitcoinTransactionOutputRepository;
    }

}
