package com.oakinvest.b2g.dto.bitcoin.core.getblockcount;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oakinvest.b2g.dto.bitcoin.core.util.BitcoinCoreResponse;

/**
 * getblockcount response.
 * Created by straumat on 26/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetBlockCountResponse extends BitcoinCoreResponse {

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
