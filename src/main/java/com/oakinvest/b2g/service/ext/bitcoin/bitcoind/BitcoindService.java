package com.oakinvest.b2g.service.ext.bitcoin.bitcoind;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResponse;

/**
 * Provides an easy access to bitcoind server data.
 * Created by straumat on 25/08/16.
 */
public interface BitcoindService {

	/**
	 * Returns the block data from bitcoind.
	 * Util method - not in bitcoind.
	 *
	 * @param blockNumber block number
	 * @return block data or null if a problem occurred.
	 */
	BitcoindBlockData getBlockData(final long blockNumber);

	/**
	 * The getblockcount RPC returns the number of blocks in the local best block chain.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"jsonrpc":"1.0","method":"getblockcount","params":[]}' -H 'content-type:text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @return the number of blocks in the local best block chain.
	 * @see <a href="https://chainquery.com/bitcoin-api/getblockcount">https://chainquery.com/bitcoin-api/getblockcount</a>
	 */
	GetBlockCountResponse getBlockCount();

	/**
	 * The getblockhash RPC returns the header hash of a block at the given height in the local best block chain.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"method": "getblockhash", "params": [427707] }' -H 'content-type: text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @param blockHeight block height.
	 * @return the block header hash.
	 * @see <a href="https://chainquery.com/bitcoin-api/getblockhash/427707">https://chainquery.com/bitcoin-api/getblockhash</a>
	 */
	GetBlockHashResponse getBlockHash(long blockHeight);

	/**
	 * The getblock RPC gets a block with a particular header hash from the local block database either as a JSON object or as a serialized block.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"method": "getblock", "params": ["000000000000000003536b07a8663ea1f10c891ccdb06e3a57c825041551df6a"] }' -H 'content-type: text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @param blockHash block hash.
	 * @return a JSON block.
	 * @see <a href="https://chainquery.com/bitcoin-api/getblock/000000000000000003536b07a8663ea1f10c891ccdb06e3a57c825041551df6a/true">https://chainquery.com/bitcoin-api/getblock</a>
	 */
	GetBlockResponse getBlock(String blockHash);

	/**
	 * The getrawtransaction RPC gets a hex-encoded serialized transaction or a JSON object describing the transaction.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"method": "getrawtransaction", "params": ["5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8322", 1] }' -H 'content-type: text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @param transactionHash transaction hash.
	 * @return getrawtransaction response.
	 * @see <a href="https://chainquery.com/bitcoin-api/getrawtransaction/5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8322/1">https://chainquery.com/bitcoin-api/getrawtransaction</a>
	 */
	GetRawTransactionResponse getRawTransaction(String transactionHash);

}
