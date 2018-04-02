package com.oakinvest.b2g.dto.bitcoin.bitcoind.getblockhash;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.util.BitcoindResponse;

/**
 * getblockhash response.
 * Created by straumat on 26/08/16.
 */
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetBlockHashResponse extends BitcoindResponse {

	/**
	 * the block header hash.
	 */
	private String result;

	/**
	 * Getter of result.
	 *
	 * @return result
	 */
	public final String getResult() {
		return result;
	}

	/**
	 * Setter of result.
	 *
	 * @param newResult the result to set
	 */
	public final void setResult(final String newResult) {
		result = newResult;
	}

}
