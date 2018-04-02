package com.oakinvest.b2g.dto.bitcoin.bitcoind.getblock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.util.BitcoindResponse;

/**
 * getblock response.
 * Created by straumat on 30/08/16.
 */
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetBlockResponse extends BitcoindResponse {

	/**
	 * Result field.
	 */
	private GetBlockResult result;

	/**
	 * Getter of result.
	 *
	 * @return result
	 */
	public final GetBlockResult getResult() {
		return result;
	}

	/**
	 * Setter of result.
	 *
	 * @param newResult the result to set
	 */
    public final void setResult(final GetBlockResult newResult) {
		result = newResult;
	}

}
