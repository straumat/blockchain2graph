package com.oakinvest.b2g.dto.external.bitcoind.getblockcount;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oakinvest.b2g.dto.external.bitcoind.util.BitcoindResponse;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * getblockcount response.
 * Created by straumat on 26/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetBlockCountResponse extends BitcoindResponse {

	/**
	 * Number returned in case of errors.
	 */
	public static final long BLOCK_COUNT_ERROR_VALUE = -1;

	/**
	 * Result field.
	 */
	private String result;

	/**
	 * Returns the number of blocks.
	 *
	 * @return number of blocks.
	 */
	public long getCount() {
		if (NumberUtils.isNumber(getResult())) {
			return Integer.parseInt(getResult());
		} else {
			return BLOCK_COUNT_ERROR_VALUE;
		}
	}


	/**
	 * Getter de la propriété result.
	 *
	 * @return result
	 */
	public final String getResult() {
		return result;
	}

	/**
	 * Setter de la propriété result.
	 *
	 * @param newResult the result to set
	 */
	public final void setResult(final String newResult) {
		result = newResult;
	}

}
