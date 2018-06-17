package com.oakinvest.b2g.bitcoin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getblock.GetBlockResponse;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.bitcoin.util.rest.BitcoinCoreResponseErrorHandler;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Default implementation of core call.
 *
 * Created by straumat on 26/08/16.
 */
@Service
public class BitcoinCoreServiceImplementation implements BitcoinCoreService {

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(BitcoinCoreService.class);

    /**
     * getblockcount command.
     */
    private static final String GETBLOCKCOUNT_COMMAND = "getblockcount";

    /**
     * getblockhash command.
     */
    private static final String GETBLOCKHASH_COMMAND = "getblockhash";

    /**
     * getblock command.
     */
    private static final String GETBLOCK_COMMAND = "getblock";

    /**
     * getrawtransaction command.
     */
    private static final String GETRAWTRANSACTION_COMMAND = "getrawtransaction";

    /**
     * Method parameter.
     */
    private static final String METHOD_PARAMETER = "method";

    /**
     * Params parameter.
     */
    private static final String PARAMS_PARAMETER = "params";

    /**
     * Rest template.
     */
    private final RestTemplate restTemplate;

    /**
     * Bitcoin core hostname.
     */
    @Value("${bitcoinCore.hostname}")
    private String hostname;

    /**
     * Bitcoin core port.
     */
    @Value("${bitcoinCore.port}")
    private String port;

    /**
     * Bitcoin core username.
     */
    @Value("${bitcoinCore.username}")
    private String username;

    /**
     * Bitcoin core password.
     */
    @Value("${bitcoinCore.password}")
    private String password;

    /**
     * Bitcoin core URL.
     */
    private String url;

    /**
     * Header to use with core.
     */
    private HttpHeaders headers;

    /**
     * Constructor.
     */
    public BitcoinCoreServiceImplementation() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new BitcoinCoreResponseErrorHandler());
    }

    /**
     * Initialize URL and authentication.
     */
    @PostConstruct
    private void initializeURLAndAuthentication() {
        // Generate url.
        url = "http://" + hostname + ":" + port;
        // Generate headers.
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders h = new HttpHeaders();
        h.set("Authorization", authHeader);
        headers = h;
    }

    /**
     * Getter url.
     *
     * @return url
     */
    private String getUrl() {
        return url;
    }

    /**
     * Getter headers.
     *
     * @return headers
     */
    private HttpHeaders getHeaders() {
        return headers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final GetBlockCountResponse getBlockCount() {
        // Setting parameters
        List<Object> params = new ArrayList<>();
        String request = getRequest(GETBLOCKCOUNT_COMMAND, params);

        // Making the call.
        HttpEntity<String> entity = new HttpEntity<>(request, getHeaders());
        log.debug("Calling getblockCount with " + request);
        return restTemplate.postForObject(getUrl(), entity, GetBlockCountResponse.class);
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
        String request = getRequest(GETBLOCKHASH_COMMAND, params);

        // Making the call.
        HttpEntity<String> entity = new HttpEntity<>(request, getHeaders());
        log.debug("Calling getblockHash on block " + request);
        return restTemplate.postForObject(getUrl(), entity, GetBlockHashResponse.class);
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
        String request = getRequest(GETBLOCK_COMMAND, params);

        // Making the call.
        HttpEntity<String> entity = new HttpEntity<>(request, getHeaders());
        log.debug("Calling getblock on block " + request);
        return restTemplate.postForObject(getUrl(), entity, GetBlockResponse.class);
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
        String request = getRequest(GETRAWTRANSACTION_COMMAND, params);

        // Making the call.
        HttpEntity<String> entity = new HttpEntity<>(request, getHeaders());
        log.debug("Calling getrawtransaction on transaction " + request);
        return restTemplate.postForObject(getUrl(), entity, GetRawTransactionResponse.class);
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
        request.put(METHOD_PARAMETER, command);
        request.put(PARAMS_PARAMETER, params);
        try {
            return new ObjectMapper().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("Error building the request " + e.getMessage());
            return null;
        }
    }

}
