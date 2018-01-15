package com.oakinvest.b2g.domain.bitcoin;

/**
 * Bitcoin transaction output type.
 *
 * @see <a href="https://github.com/bitcoin/bitcoin/blob/57b34599b2deb179ff1bd97ffeab91ec9f904d85/src/script/standard.cpp">https://github.com/bitcoin/bitcoin/blob/57b34599b2deb179ff1bd97ffeab91ec9f904d85/src/script/standard.cpp</a>
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
