package com.oakinvest.b2g.dto.external.bitcoind.getblock;

import java.util.ArrayList;

/**
 * result inside the getblock response.
 * Created by straumat on 31/08/16.
 */
public class GetBlockResult {

	/**
	 * Transactions
	 */
	private ArrayList<String> tx;

	/**
	 * Getter de la propriété tx.
	 *
	 * @return tx
	 */
	public final ArrayList<String> getTx() {
		return tx;
	}

	/**
	 * Setter de la propriété tx.
	 *
	 * @param newTx the tx to set
	 */
	public final void setTx(final ArrayList<String> newTx) {
		tx = newTx;
	}

}
