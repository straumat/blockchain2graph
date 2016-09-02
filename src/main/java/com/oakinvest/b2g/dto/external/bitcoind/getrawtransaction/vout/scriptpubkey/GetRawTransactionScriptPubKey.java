package com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vout.scriptpubkey;

import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vout.scriptpubkey.addresses.GetRawTransactionAddresses;

import java.util.ArrayList;

/**
 * ScriptPubKey.
 * Created by straumat on 01/09/16.
 */
public class GetRawTransactionScriptPubKey {

	/**
	 * Addresses.
	 */
	private ArrayList<GetRawTransactionAddresses> addresses;

	/**
	 * Getter de la propriété addresses.
	 *
	 * @return addresses
	 */
	public final ArrayList<GetRawTransactionAddresses> getAddresses() {
		return addresses;
	}

	/**
	 * Setter de la propriété addresses.
	 *
	 * @param newAddresses the addresses to set
	 */
	public final void setAddresses(final ArrayList<GetRawTransactionAddresses> newAddresses) {
		addresses = newAddresses;
	}
}
