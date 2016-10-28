package com.oakinvest.b2g.dto.external.bitcoind.getblockcount;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oakinvest.b2g.dto.external.bitcoind.util.BitcoindResponse;

import java.io.Serializable;

/**
 * getblockcount response.
 * Created by straumat on 26/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetBlockCountResponse extends BitcoindResponse implements Serializable {

	/**
	 * The current block count.
	 */
	private long result;

	/**
	 * Getter de la propriété result.
	 *
	 * @return result
	 */
	public final long getResult() {
		return result;
	}

	/**
	 * Setter de la propriété result.
	 *
	 * @param newResult the result to set
	 */
	public final void setResult(final long newResult) {
		result = newResult;
	}

}
