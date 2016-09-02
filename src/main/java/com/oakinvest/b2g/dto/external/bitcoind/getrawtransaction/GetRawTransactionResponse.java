package com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oakinvest.b2g.dto.external.bitcoind.util.BitcoindResponse;

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
	 * Getter de la propriété result.
	 *
	 * @return result
	 */
	public final GetRawTransactionResult getResult() {
		return result;
	}

	/**
	 * Setter de la propriété result.
	 *
	 * @param newResult the result to set
	 */
	public final void setResult(final GetRawTransactionResult newResult) {
		result = newResult;
	}

}
