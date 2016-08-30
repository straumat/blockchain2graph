package com.oakinvest.b2g.dto.external.bitcoind;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * getblockhash response.
 * Created by straumat on 26/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetBlockHashResponse extends BitcoindResponse {

}
