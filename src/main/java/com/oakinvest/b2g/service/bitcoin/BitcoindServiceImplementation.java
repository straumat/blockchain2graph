package com.oakinvest.b2g.service.bitcoin;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockDataComparator;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.util.bitcoin.rest.BitcoindResponseErrorHandler;
import org.apache.commons.codec.binary.Base64;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.oakinvest.b2g.configuration.CacheConfiguration.BITCOIND_BUFFER_SIZE;

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
     * Delay between buffer clean (every 5 minutes).
     */
    private static final int DELAY_BETWEEN_BUFFER_CLEAN = 5 * 60 * 1000;

    /**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(BitcoindService.class);

	/**
	 * Rest template.
	 */
	private final RestTemplate restTemplate;

    /**
     * Get buffer.
     * @return buffer
     */
    @SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
    public ConcurrentSkipListSet<BitcoindBlockData> getBuffer() {
        return buffer;
    }


    /**
     * Buffer content.
     */
    private final ConcurrentSkipListSet<BitcoindBlockData> buffer;

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
        buffer = new ConcurrentSkipListSet<BitcoindBlockData>(new BitcoindBlockDataComparator());
	}

    /**
     * Returns the block data from bitcoind.
     *
     * @param blockHeight block height
     * @return block data or null if a problem occurred.
     */
    @SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
    public Optional<BitcoindBlockData> getBlockData(final long blockHeight) {
        Optional<BitcoindBlockData> result = getBlockDataFromBuffer(blockHeight);
        if (result.isPresent()) {
            // If the block is in the buffer, we return it and remove it.
            buffer.remove(result.get());
            return result;
        } else {
            // Else we get it directly from bitcoind and add it to the buffer.
            Optional<BitcoindBlockData> blockData = getBlockDataFromBitcoind(blockHeight);
            if (blockData.isPresent()) {
                buffer.add(blockData.get());
                return blockData;
            } else {
                return Optional.empty();
            }
        }
    }

    /**
     * Get block data from buffer.
     *
     * @param blockHeight block height
     * @return block data
     */
    @SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
    public Optional<BitcoindBlockData> getBlockDataFromBuffer(final long blockHeight) {
        return buffer.stream().filter(b -> b.getBlock().getHeight() == blockHeight).findFirst();
    }


    /**
     * Get block data from buffer.
     *
     * @param blockHeight block height
     * @return block data
     */
    @SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
    public Optional<BitcoindBlockData> getBlockDataFromBitcoind(final long blockHeight) {
		try {
			// ---------------------------------------------------------------------------------------------------------
			// We retrieve the block hash...
			GetBlockHashResponse blockHashResponse = getBlockHash(blockHeight);
			if (blockHashResponse.getError() == null) {
				// -----------------------------------------------------------------------------------------------------
				// Then we retrieve the block data...
				String blockHash = blockHashResponse.getResult();
				final GetBlockResponse blockResponse = getBlock(blockHash);
				if (blockResponse.getError() == null) {
					// -------------------------------------------------------------------------------------------------
					// Then we retrieve the transactions data...
					final List<GetRawTransactionResult> transactions = new LinkedList<>();
					try {
                        // We use multi thread to retrieve all the transactions informations.
                        final Map<String, GetRawTransactionResult> tempTransactionList = new ConcurrentHashMap<>();
                        blockResponse.getResult().getTx()
                                .parallelStream()
                                .filter(t -> !t.equals(GENESIS_BLOCK_TRANSACTION))
                                .forEach(t -> {
                                    GetRawTransactionResponse r = getRawTransaction(t);
                                    if (r.getError() == null) {
                                        tempTransactionList.put(t, getRawTransaction(t).getResult());
                                    } else {
                                        log.error("Error getting transaction n°" + t + " informations : " + r.getError());
                                        throw new RuntimeException("Error getting transaction n°" + t + " informations : " + r.getError());
                                    }
                                });

                        // Then we add it to the list in the right order.
                        blockResponse.getResult().getTx().stream().forEach(t -> transactions.add(tempTransactionList.get(t)));

					} catch (Exception e) {
						return Optional.empty();
					}
					// -------------------------------------------------------------------------------------------------
					// And we end up returning all the block data all at once.
					return Optional.of(new BitcoindBlockData(blockResponse.getResult(), transactions));
				} else {
					// Error while retrieving the block informations.
					log.error("Error getting block n°" + blockHeight + " informations : " + blockResponse.getError());
					return Optional.empty();
				}
			} else {
				// Error while retrieving the block hash.
				log.error("Error getting the hash of block n°" + blockHeight + " : " + blockHashResponse.getError());
				return Optional.empty();
			}
		} catch (Exception e) {
			log.error("Error getting the block data of block n°" + blockHeight + " : " + e.getMessage(), e);
			return Optional.empty();
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
		log.debug("Calling getblockCount with " + request);
		return restTemplate.postForObject(getURL(), entity, GetBlockCountResponse.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("checkstyle:designforextension")
	public GetBlockHashResponse getBlockHash(final long blockHeight) {
		// Setting parameters
		List<Object> params = new ArrayList<>();
		params.add(blockHeight);
		JSONObject request = getRequest(COMMAND_GETBLOCKHASH, params);

		// Making the call.
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
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
		JSONObject request = getRequest(COMMAND_GETBLOCK, params);

		// Making the call.
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
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
		JSONObject request = getRequest(COMMAND_GETRAWTRANSACTION, params);

		// Making the call.
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), getHeaders());
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
	private JSONObject getRequest(final String command, final List<Object> params) {
		JSONObject request = new JSONObject();
		try {
			request.put(PARAMETER_METHOD, command);
			request.put(PARAMETER_PARAMS, params);
		} catch (JSONException e) {
			log.error("Error while building the request : " + e.getMessage(), e);
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

    /**
     * Buffer cleaner (in case of).
     */
    @Scheduled(fixedDelay = DELAY_BETWEEN_BUFFER_CLEAN)
    @SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
    public void cleanBuffer() {
        // We remove the old entries until we go back to BUFFER_SIZE.
        while (buffer.size() > BITCOIND_BUFFER_SIZE) {
            buffer.pollFirst();
        }
    }

}
