package com.oakinvest.b2g.service.bitcoin;

import com.google.gson.Gson;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.util.bitcoin.rest.BitcoindResponseErrorHandler;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Default implementation of bitcoind call.
 * Created by straumat on 26/08/16.
 */
@Service
public class BitcoindServiceImplementation implements BitcoindService {

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
     * Gson.
     */
    private final Gson gson = new Gson();

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
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new BitcoindResponseErrorHandler());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final GetBlockCountResponse getBlockCount() {
        // Setting parameters
        List<Object> params = new ArrayList<>();
        String request = getRequest(COMMAND_GETBLOCKCOUNT, params);

        // Making the call.
        HttpEntity<String> entity = new HttpEntity<>(request, getHeaders());
        log.debug("Calling getblockCount with " + request);
        return restTemplate.postForObject(getURL(), entity, GetBlockCountResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public GetBlockHashResponse getBlockHash(final int blockHeight) {
        // Setting parameters
        List<Object> params = new ArrayList<>();
        params.add(blockHeight);
        String request = getRequest(COMMAND_GETBLOCKHASH, params);

        // Making the call.
        HttpEntity<String> entity = new HttpEntity<>(request, getHeaders());
        log.debug("Calling getblockHash on block " + request);
        return restTemplate.postForObject(getURL(), entity, GetBlockHashResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public GetBlockResponse getBlock(final String blockHash) {
        // Setting parameters
        List<Object> params = new ArrayList<>();
        params.add(blockHash);
        String request = getRequest(COMMAND_GETBLOCK, params);

        // Making the call.
        HttpEntity<String> entity = new HttpEntity<>(request, getHeaders());
        log.debug("Calling getblock on block " + request);
        return restTemplate.postForObject(getURL(), entity, GetBlockResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public GetRawTransactionResponse getRawTransaction(final String transactionHash) {
        // Setting parameters
        List<Object> params = new ArrayList<>();
        params.add(transactionHash);
        params.add(1);
        String request = getRequest(COMMAND_GETRAWTRANSACTION, params);

        // Making the call.
        HttpEntity<String> entity = new HttpEntity<>(request, getHeaders());
        log.debug("Calling getrawtransaction on transaction " + request);
        return restTemplate.postForObject(getURL(), entity, GetRawTransactionResponse.class);
    }

    /**
     * Util method to build the request.
     *
     * @param command command t call.
     * @param params  parameters.
     * @return json query.
     */
    private String getRequest(final String command, final List<Object> params) {
        HashMap<Object, Object> request = new HashMap<>();
        request.put(PARAMETER_METHOD, command);
        request.put(PARAMETER_PARAMS, params);
        return gson.toJson(request);
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
