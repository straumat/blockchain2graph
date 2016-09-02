package com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction;

import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vin.GetRawTransactionVin;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vout.GetRawTransactionVout;

import java.util.ArrayList;

/**
 * Result inside the GetRawTransaction response.
 * Created by straumat on 01/09/16.
 */
public class GetRawTransactionResult {


	/**
	 * Vin.
	 */
	private ArrayList<GetRawTransactionVin> vin;


	/**
	 * Vout.
	 */
	private ArrayList<GetRawTransactionVout> vout;

	/**
	 * Getter de la propriété vin.
	 *
	 * @return vin
	 */
	public final ArrayList<GetRawTransactionVin> getVin() {
		return vin;
	}

	/**
	 * Setter de la propriété vin.
	 *
	 * @param newVin the vin to set
	 */
	public final void setVin(final ArrayList<GetRawTransactionVin> newVin) {
		vin = newVin;
	}

	/**
	 * Getter de la propriété vout.
	 *
	 * @return vout
	 */
	public final ArrayList<GetRawTransactionVout> getVout() {
		return vout;
	}

	/**
	 * Setter de la propriété vout.
	 *
	 * @param newVout the vout to set
	 */
	public final void setVout(final ArrayList<GetRawTransactionVout> newVout) {
		vout = newVout;
	}

}
