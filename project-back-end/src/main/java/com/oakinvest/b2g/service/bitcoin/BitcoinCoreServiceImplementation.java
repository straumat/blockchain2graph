package com.oakinvest.b2g.service.bitcoin;

import com.google.gson.Gson;
import com.oakinvest.b2g.dto.bitcoin.core.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.bitcoin.core.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.bitcoin.core.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.bitcoin.core.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.util.bitcoin.rest.BitcoindResponseErrorHandler;
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
 * Created by straumat on 26/08/16.
 */
@Service
public class BitcoinCoreServiceImplementation implements BitcoinCoreService {

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
    private final Logger log = LoggerFactory.getLogger(BitcoinCoreService.class);


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
     * Bitcoind URL.
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
        restTemplate.setErrorHandler(new BitcoindResponseErrorHandler());
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
        String request = getRequest(COMMAND_GETBLOCKCOUNT, params);

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
        String request = getRequest(COMMAND_GETBLOCKHASH, params);

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
        String request = getRequest(COMMAND_GETBLOCK, params);

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
        String request = getRequest(COMMAND_GETRAWTRANSACTION, params);

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
        request.put(PARAMETER_METHOD, command);
        request.put(PARAMETER_PARAMS, params);
        return gson.toJson(request);
    }

}
