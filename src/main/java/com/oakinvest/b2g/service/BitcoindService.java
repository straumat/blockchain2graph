package com.oakinvest.b2g.service;

import com.oakinvest.b2g.dto.external.bitcoind.BlockCountResponse;

/**
 * Provides an easy access to bitcoind server data.
 * Created by straumat on 25/08/16.
 */
public interface BitcoindService {

	/**
	 * Calling getblockcount on bitcoind server.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"jsonrpc":"1.0","method":"getblockcount","params":[]}' -H 'content-type:text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @return blockcount.
	 */
	BlockCountResponse getBlockCount();

}
