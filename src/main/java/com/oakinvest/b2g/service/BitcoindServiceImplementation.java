package com.oakinvest.b2g.service;

import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
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
import java.util.List;

/**
 * Default implementation of bitcoind call.
 * Created by straumat on 26/08/16.
 */
@Service
public class BitcoindServiceImplementation implements BitcoindService {

	/**
	 * Command to get blockcount.
	 */
	private static final String COMMAND_GETBLOCKCOUNT = "getblockcount";

	/**
	 * Command to get getblockhash.
	 */
	private static final String COMMAND_GETBLOCKHASH = "getblockhash";

	/**
	 * Command to get getblock.
	 */
	private static final String COMMAND_GETBLOCK = "getblock";

	/**
	 * Command to get getrawtransaction.
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
	 * {@inheritDoc}
	 */
	@Override
	public final GetBlockCountResponse getBlockCount() {
		// Setting parameters
		List<Object> params = new ArrayList<>();
		JSONObject request = getRequest(COMMAND_GETBLOCKCOUNT, params);

		// Making the call.
		//RestTemplate restTemplate = getRestTemplate();
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
		log.info("Calling getblockCount with " + request);
		return restTemplate.postForObject(getURL(), entity, GetBlockCountResponse.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Cacheable("blockHashs")
	@SuppressWarnings("checkstyle:designforextension")
	public GetBlockHashResponse getBlockHash(final long blockHeight) {
		// Setting parameters
		List<Object> params = new ArrayList<>();
		params.add(blockHeight);
		JSONObject request = getRequest(COMMAND_GETBLOCKHASH, params);

		// Making the call.
		//RestTemplate restTemplate = getRestTemplate();
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
		log.info("Calling getblockHash on block " + request);
		return restTemplate.postForObject(getURL(), entity, GetBlockHashResponse.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Cacheable("blocks")
	@SuppressWarnings("checkstyle:designforextension")
	public GetBlockResponse getBlock(final String blockHash) {
		// Setting parameters
		List<Object> params = new ArrayList<>();
		params.add(blockHash);
		JSONObject request = getRequest(COMMAND_GETBLOCK, params);

		// Making the call.
		//RestTemplate restTemplate = getRestTemplate();
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
		log.info("Calling getblock on block " + request);
		return restTemplate.postForObject(getURL(), entity, GetBlockResponse.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Cacheable("rawTransactions")
	@SuppressWarnings("checkstyle:designforextension")
	public GetRawTransactionResponse getRawTransaction(final String transactionHash) {
		// Setting parameters
		List<Object> params = new ArrayList<>();
		params.add(transactionHash);
		params.add(1);
		JSONObject request = getRequest(COMMAND_GETRAWTRANSACTION, params);

		// Making the call.
		//RestTemplate restTemplate = getRestTemplate();
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
		}
		return request;
	}

	/**
	 * Getting the URL to call.
	 *
	 * @return bitcoind serveur url
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
