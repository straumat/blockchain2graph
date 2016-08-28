package com.oakinvest.b2g.service;

import com.oakinvest.b2g.dto.external.bitcoind.BlockCountResponse;
import org.apache.commons.codec.binary.Base64;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * Default implementation of bitcoind call.
 * Created by straumat on 26/08/16.
 */
@Service
public class BitcoindServiceImplementation implements BitcoindService {

	/**
	 * Command to get blockcount.
	 */
	public static final String COMMAND_GETBLOCKCOUNT = "getblockcount";

	/**
	 * Method parameter.
	 */
	public static final String PARAMETER_METHOD = "method";

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
	 * Calling getblockcount on bitcoind server.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"jsonrpc":"1.0","method":"getblockcount","params":[]}' -H 'content-type:text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @return blockcount.
	 */
	@Override
	public final BlockCountResponse getBlockCount() {
		// FIXME Deal with errors like {"result":null,"error":{"code":-28,"message":"Loading block index..."},"id":null}
		log.info("Calling getBlockCount");

		// Configuring the request.
		JSONObject request = new JSONObject();
		try {
			request.put(PARAMETER_METHOD, COMMAND_GETBLOCKCOUNT);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Making the call.
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(request.toString(), getHeaders());
		return restTemplate.postForObject(getURL(), entity, BlockCountResponse.class);
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
