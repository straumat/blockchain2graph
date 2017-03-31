package com.oakinvest.b2g.service.ext.bitcoin.bitcoind;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.util.bitcoin.BitcoindResponseErrorHandler;
import org.apache.commons.codec.binary.Base64;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Default implementation of bitcoind call.
 * Created by straumat on 26/08/16.
 */
@Service
public class BitcoindServiceImplementation implements BitcoindService {

	/**
	 * Genesis transaction hash.
	 */
	private static final String GENESIS_BLOCK_TRANSACTION = "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b";

	/**
	 * Command to getblockcount.
	 */
	private static final String COMMAND_GETBLOCKCOUNT = "getblockcount";

	/**
	 * Command to getblockhash.
	 */
	private static final String COMMAND_GETBLOCKHASH = "getblockhash";

	/**
	 * Command to getblock.
	 */
	private static final String COMMAND_GETBLOCK = "getblock";

	/**
	 * Command to getrawtransaction.
	 */
	private static final String COMMAND_GETRAWTRANSACTION = "getrawtransaction";

	/**
	 * Method parameter.
	 */
	private static final String PARAMETER_METHOD = "method";

	/**
	 * Params parameter.
	 */
	private static final String PARAMETER_PARAMS = "params";

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(BitcoindService.class);

	/**
	 * Rest template.
	 */
	private final RestTemplate restTemplate;

	/**
	 * Bitcoind hostname.
	 */
	@Value("${bitcoind.hostname}")
	private String hostname;

	/**
	 * Bitcoind port.
	 */
	@Value("${bitcoind.port}")
	private String port;

	/**
	 * Bitcoind username.
	 */
	@Value("${bitcoind.username}")
	private String username;

	/**
	 * Bitcoind password.
	 */
	@Value("${bitcoind.password}")
	private String password;

	/**
	 * Constructor.
	 */
	public BitcoindServiceImplementation() {
		restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
		restTemplate.setErrorHandler(new BitcoindResponseErrorHandler());
	}

	/**
	 * Returns the block data from bitcoind.
	 *
	 * @param blockNumber block number
	 * @return block data or null if a problem occurred.
	 */
	@Cacheable(value = "blockData", unless = "#result == null")
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public BitcoindBlockData getBlockData(final long blockNumber) {
		try {
			// ---------------------------------------------------------------------------------------------------------
			// We retrieve the block hash...
			GetBlockHashResponse blockHashResponse = getBlockHash(blockNumber);
			if (blockHashResponse.getError() == null) {
				// -----------------------------------------------------------------------------------------------------
				// Then we retrieve the block data...
				String blockHash = blockHashResponse.getResult();
				final GetBlockResponse blockResponse = getBlock(blockHash);
				if (blockResponse.getError() == null) {
					// -------------------------------------------------------------------------------------------------
					// Then we retrieve the transactions data...
					final HashMap<String, GetRawTransactionResult> transactions = new LinkedHashMap<>();
					for (Iterator<String> transactionsHashs = blockResponse.getResult().getTx().iterator(); transactionsHashs.hasNext(); ) {
						String t = transactionsHashs.next();
						if (!t.equals(GENESIS_BLOCK_TRANSACTION)) {
							GetRawTransactionResponse r = getRawTransaction(t);
							if (r.getError() == null) {
								transactions.put(t, getRawTransaction(t).getResult());
							} else {
								log.error("Error getting transaction n°" + t + " informations : " + r.getError());
								return null;
							}
						}
					}
					// -------------------------------------------------------------------------------------------------
					// And we end up returning all the block data all at once.
					return new BitcoindBlockData(blockResponse.getResult(), transactions);
				} else {
					// Error while retrieving the block informations.
					log.error("Error getting block n°" + blockNumber + " informations : " + blockResponse.getError());
					return null;
				}
			} else {
				// Error while retrieving the block hash.
				log.error("Error getting the hash of block n°" + blockNumber + " : " + blockHashResponse.getError());
				return null;
			}
		} catch (Exception e) {
			log.error("Error getting the block data of block n°" + blockNumber + " : " + e.getMessage());
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final GetBlockCountResponse getBlockCount() {
		// Setting parameters
		List<Object> params = new ArrayList<>();
		JSONObject request = getRequest(COMMAND_GETBLOCKCOUNT, params);

		// Making the call.
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
		log.info("Calling getblockCount with " + request);
		return restTemplate.postForObject(getURL(), entity, GetBlockCountResponse.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	//@Cacheable(cacheNames = "blockHashs", unless = "#result.getError() != null")
	@SuppressWarnings("checkstyle:designforextension")
	public GetBlockHashResponse getBlockHash(final long blockHeight) {
		// Setting parameters
		List<Object> params = new ArrayList<>();
		params.add(blockHeight);
		JSONObject request = getRequest(COMMAND_GETBLOCKHASH, params);

		// Making the call.
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
		log.info("Calling getblockHash on block " + request);
		return restTemplate.postForObject(getURL(), entity, GetBlockHashResponse.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	//@Cacheable(cacheNames = "blocks", unless = "#result.getError() != null")
	@SuppressWarnings("checkstyle:designforextension")
	public GetBlockResponse getBlock(final String blockHash) {
		// Setting parameters
		List<Object> params = new ArrayList<>();
		params.add(blockHash);
		JSONObject request = getRequest(COMMAND_GETBLOCK, params);

		// Making the call.
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
		log.info("Calling getblock on block " + request);
		return restTemplate.postForObject(getURL(), entity, GetBlockResponse.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	//@Cacheable(cacheNames = "rawTransactions", unless = "#result.getError() != null")
	@SuppressWarnings("checkstyle:designforextension")
	public GetRawTransactionResponse getRawTransaction(final String transactionHash) {
		// Setting parameters
		List<Object> params = new ArrayList<>();
		params.add(transactionHash);
		params.add(1);
		JSONObject request = getRequest(COMMAND_GETRAWTRANSACTION, params);

		// Making the call.
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
		log.info("Calling getrawtransaction on transaction " + request);
		return restTemplate.postForObject(getURL(), entity, GetRawTransactionResponse.class);
	}

	/**
	 * Util method to build the request.
	 *
	 * @param command command t call.
	 * @param params  parameters.
	 * @return json query.
	 */
	private JSONObject getRequest(final String command, final List<Object> params) {
		JSONObject request = new JSONObject();
		try {
			request.put(PARAMETER_METHOD, command);
			request.put(PARAMETER_PARAMS, params);
		} catch (JSONException e) {
			log.error("Error while building the request " + e);
			log.error("Error : " + Arrays.toString(e.getStackTrace()));
		}
		return request;
	}

	/**
	 * Getting the URL to call.
	 *
	 * @return bitcoind server url
	 */
	private String getURL() {
		return "http://" + hostname + ":" + port;
	}

	/**
	 * Manage authentication.
	 *
	 * @return required headers
	 */
	private HttpHeaders getHeaders() {
		String auth = username + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + new String(encodedAuth);
		HttpHeaders h = new HttpHeaders();
		h.set("Authorization", authHeader);
		return h;
	}

}
