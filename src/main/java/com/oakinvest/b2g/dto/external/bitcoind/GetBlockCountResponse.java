package com.oakinvest.b2g.dto.external.bitcoind;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
	public static final int BLOCK_COUNT_ERROR = -1;

	/**
	 * Returns the number of blocks.
	 *
	 * @return number of blocks.
	 */
	public int getCount() {
		if (NumberUtils.isNumber(getResult())) {
			return Integer.parseInt(getResult());
		} else {
			return BLOCK_COUNT_ERROR;
		}
	}

}
