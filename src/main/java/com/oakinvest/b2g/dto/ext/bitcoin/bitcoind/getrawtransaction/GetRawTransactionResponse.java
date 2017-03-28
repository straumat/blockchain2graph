package com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.util.BitcoindResponse;

/**
 * getrawtransaction response.
 * Created by straumat on 30/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetRawTransactionResponse extends BitcoindResponse {

	/**
	 * Result.
	 */
	private GetRawTransactionResult result;

	/**
	 * Getter of result.
	 *
	 * @return result
	 */
	public final GetRawTransactionResult getResult() {
		return result;
	}

	/**
	 * Setter of result.
	 *
	 * @param newResult the result to set
	 */
	public final void setResult(final GetRawTransactionResult newResult) {
		result = newResult;
	}

}
