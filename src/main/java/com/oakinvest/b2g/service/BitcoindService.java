package com.oakinvest.b2g.service;

import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;

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
	 * @see <a href="https://chainquery.com/bitcoin-api/getblockcount">https://chainquery.com/bitcoin-api/getblockcount</a>
	 */
	GetBlockCountResponse getBlockCount();

	/**
	 * Calling getblockhash on bitcoind server.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"method": "getblockhash", "params": [427707] }' -H 'content-type: text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @param blockNumber block number.
	 * @return getblockhash response.
	 * @see <a href="https://chainquery.com/bitcoin-api/getblockhash/427707">https://chainquery.com/bitcoin-api/getblockhash</a>
	 */
	GetBlockHashResponse getBlockHash(long blockNumber);

	/**
	 * Calling getblock on bitcoind server.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"method": "getblock", "params": ["000000000000000003536b07a8663ea1f10c891ccdb06e3a57c825041551df6a"] }' -H 'content-type: text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @param blockHash block hash.
	 * @return getblock response.
	 * @see <a href="https://chainquery.com/bitcoin-api/getblock/000000000000000003536b07a8663ea1f10c891ccdb06e3a57c825041551df6a/true">https://chainquery.com/bitcoin-api/getblock</a>
	 */
	GetBlockResponse getBlock(String blockHash);

	/**
	 * Calling getrawtransaction on bitcoind server.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"method": "getrawtransaction", "params": ["5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8322", 1] }' -H 'content-type: text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @param transactionHash transaction hash.
	 * @return getrawtransaction response.
	 * @see <a href="https://chainquery.com/bitcoin-api/getrawtransaction/5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8322/1">https://chainquery.com/bitcoin-api/getrawtransaction</a>
	 */
	GetRawTransactionResponse getRawTransaction(String transactionHash);

}
