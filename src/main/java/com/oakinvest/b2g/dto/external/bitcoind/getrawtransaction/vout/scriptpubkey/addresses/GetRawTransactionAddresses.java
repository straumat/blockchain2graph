package com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vout.scriptpubkey.addresses;

/**
 * Addresses.
 * Created by straumat on 01/09/16.
 */
public class GetRawTransactionAddresses {

	/**
	 * Bitcoin addresses.
	 */
	private String bitcoinaddress;

	/**
	 * Getter de la propriété bitcoinaddress.
	 *
	 * @return bitcoinaddress
	 */
	public final String getBitcoinaddress() {
		return bitcoinaddress;
	}

	/**
	 * Setter de la propriété bitcoinaddress.
	 *
	 * @param newBitcoinaddress the bitcoinaddress to set
	 */
	public final void setBitcoinaddress(final String newBitcoinaddress) {
		bitcoinaddress = newBitcoinaddress;
	}

}
