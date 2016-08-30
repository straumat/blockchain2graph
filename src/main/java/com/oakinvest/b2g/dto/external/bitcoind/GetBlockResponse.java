package com.oakinvest.b2g.dto.external.bitcoind;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * getblock response.
 * Created by straumat on 30/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetBlockResponse extends BitcoindResponse {

}
