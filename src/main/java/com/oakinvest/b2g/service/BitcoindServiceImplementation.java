package com.oakinvest.b2g.service;

import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
import org.apache.commons.codec.binary.Base64;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
	 * Comment to get getblockhash.
	 */
	private static final String COMMAND_GETBLOCKHASH = "getblockhash";

	/**
	 * Comment to get getblock.
	 */
	private static final String COMMAND_GETBLOCK = "getblock";

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
	 * {@inheritDoc}
	 */
	@Override
	public final GetBlockCountResponse getBlockCount() {
		// FIXME Deal with errors like {"result":null,"error":{"code":-28,"message":"Loading block index..."},"id":null}
		// Configuring the request.
		JSONObject request = new JSONObject();
		try {
			request.put(PARAMETER_METHOD, COMMAND_GETBLOCKCOUNT);
		} catch (JSONException e) {
			log.error("Error while building the request " + e);
			e.printStackTrace();
		}

		// Making the call.
		RestTemplate restTemplate = getRestTemplate();
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
		log.info("Calling getblockCount with " + request);
		return restTemplate.postForObject(getURL(), entity, GetBlockCountResponse.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GetBlockHashResponse getBlockHash(final long blockNumber) {
		JSONObject request = new JSONObject();
		try {
			request.put(PARAMETER_METHOD, COMMAND_GETBLOCKHASH);
			List<Long> params = new ArrayList<>();
			params.add(blockNumber);
			request.put(PARAMETER_PARAMS, params);

		} catch (JSONException e) {
			log.error("Error while building the request " + e);
			e.printStackTrace();
		}

		// Making the call.
		RestTemplate restTemplate = getRestTemplate();
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
		log.info("Calling getblockHash on block " + request);
		System.out.println(restTemplate.exchange(getURL(), HttpMethod.POST, entity, String.class));
		return restTemplate.postForObject(getURL(), entity, GetBlockHashResponse.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final GetBlockResponse getBlock(final String blockHash) {
		JSONObject request = new JSONObject();
		try {
			request.put(PARAMETER_METHOD, COMMAND_GETBLOCK);
			List<String> params = new ArrayList<>();
			params.add(blockHash);
			request.put(PARAMETER_PARAMS, params);

		} catch (JSONException e) {
			log.error("Error while building the request " + e);
			e.printStackTrace();
		}

		// Making the call.
		RestTemplate restTemplate = getRestTemplate();
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
		log.info("Calling getblock on block " + request);
		System.out.println(restTemplate.exchange(getURL(), HttpMethod.POST, entity, String.class));
		return restTemplate.postForObject(getURL(), entity, GetBlockResponse.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final GetRawTransactionResponse getRawTransaction(final String transactionHash) {
		// TODO To implement.
		return null;
	}


	/**
	 * Returns a configured restTemplate.
	 *
	 * @return configured restTemplate
	 */
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new BitcoindResponseErrorHandler());
		return restTemplate;
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
	 * @return requireed headers
	 */
	private HttpHeaders getHeaders() {
		return new HttpHeaders() {
			{
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
	}

}
