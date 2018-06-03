package com.oakinvest.b2g.bitcoin.domain;

/**
 * Bitcoin transaction output type.
 * <p>
 * Created by straumat on 06/11/16.
 */
@SuppressWarnings("unused")
public enum BitcoinTransactionOutputType {

    /**
     * nonstandard.
     */
    nonstandard,

    /**
     * pubkey.
     */
    pubkey,

    /**
     * pubkeyhash.
     */
    pubkeyhash,

    /**
     * scripthash.
     */
    scripthash,

    /**
     * multisig.
     */
    multisig,

    /**
     * nulldata.
     */
    nulldata,

    /**
     * witness_v0_keyhash.
     */
    witness_v0_keyhash,

    /**
     * witness_v0_scripthash.
     */
    witness_v0_scripthash

}
