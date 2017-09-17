package com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockcount;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.util.BitcoindResponse;

/**
 * getblockcount response.
 * Created by straumat on 26/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetBlockCountResponse extends BitcoindResponse {

	/**
	 * The current block count.
	 */
	private int result;

	/**
	 * Getter of result.
	 *
	 * @return result
	 */
	public final int getResult() {
		return result;
	}

	/**
	 * Setter of result.
	 *
	 * @param newResult the result to set
	 */
	public final void setResult(final int newResult) {
		result = newResult;
	}

}
