package com.oakinvest.b2g.service;

import com.oakinvest.b2g.dto.external.bitcoind.GetBlockCountResponse;
import com.oakinvest.b2g.dto.external.bitcoind.GetBlockHashResponse;
import com.oakinvest.b2g.dto.external.bitcoind.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.GetRawTransactionResponse;

/**
 * Provides an easy access to bitcoind server data.
 * Created by straumat on 25/08/16.
 */
public interface BitcoindService {

	/**
	 * Calling getblockcount on bitcoind server.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"jsonrpc":"1.0","method":"getblockcount","params":[]}' -H 'content-type:text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @return getblockcount response.
	 */
	GetBlockCountResponse getBlockCount();

	/**
	 * Calling getblockhash on bitcoind server.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"method": "getblockhash", "params": [1] }' -H 'content-type: text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @param blockNumber block number.
	 * @return getblockhash response.
	 */
	GetBlockHashResponse getBlockHash(int blockNumber);

	/**
	 * Calling getblock on bitcoind server.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"method": "getblock", "params": ["00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048"] }' -H 'content-type: text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @param blockHash block hash.
	 * @return getblock response.
	 */
	GetBlockResponse getBlock(String blockHash);

	/**
	 * Calling getrawtransaction on bitcoind server.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"method": "getrawtransaction", "params": ["540a7e54fd64478554519f1b2d643ecc888c5030631487f9cfc530b71d281309", 1] }' -H 'content-type: text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @param transactionHash transaction hash.
	 * @return getrawtransaction response.
	 */
	GetRawTransactionResponse getRawTransaction(String transactionHash);

}
