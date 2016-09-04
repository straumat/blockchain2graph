package com.oakinvest.b2g.dto.external.bitcoind.getblock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oakinvest.b2g.dto.external.bitcoind.util.BitcoindResponse;

/**
 * getblock response.
 * Created by straumat on 30/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetBlockResponse extends BitcoindResponse {

	/**
	 * Result field.
	 */
	private GetBlockResult result;

	/**
	 * Getter de la propriété result.
	 *
	 * @return result
	 */
	public final GetBlockResult getResult() {
		return result;
	}

	/**
	 * Setter de la propriété result.
	 *
	 * @param newResult the result to set
	 */
	public final void setResult(final GetBlockResult newResult) {
		result = newResult;
	}

}
