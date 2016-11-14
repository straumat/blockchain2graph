package com.oakinvest.b2g.test.util;

import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Bitcoind mock.
 * Created by straumat on 28/10/16.
 */
@Service
@Qualifier("BitcoindServiceMock")
public class BitcoindServiceMock implements BitcoindService {

	/**
	 * bitcoind directory.
	 */
	private static final String SRC_TEST_RESOURCES_BITCOIND = "src/test/resources/bitcoind";

	/**
	 * getblock directory.
	 */
	private static final String SRC_TEST_RESOURCES_BITCOIND_GETBLOCK = "src/test/resources/bitcoind/getblock";

	/**
	 * getblockcount directory.
	 */
	private static final String SRC_TEST_RESOURCES_BITCOIND_GETBLOCKCOUNT = "src/test/resources/bitcoind/getblockcount";

	/**
	 * getBlockHash directory.
	 */
	private static final String SRC_TEST_RESOURCES_BITCOIND_GET_BLOCK_HASH = "src/test/resources/bitcoind/getBlockHash";

	/**
	 * getRawTransaction directory.
	 */
	private static final String SRC_TEST_RESOURCES_BITCOIND_GET_RAW_TRANSACTION = "src/test/resources/bitcoind/getRawTransaction";

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(BitcoindServiceMock.class);

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bds;

	/**
	 * bitcoind directory.
	 */
	private File bitcoindDirectory = new File(SRC_TEST_RESOURCES_BITCOIND);

	/**
	 * getblock directory.
	 */
	private File getBlockDirectory = new File(SRC_TEST_RESOURCES_BITCOIND_GETBLOCK);

	/**
	 * getblockcount directory.
	 */
	private File getBlockCountDirectory = new File(SRC_TEST_RESOURCES_BITCOIND_GETBLOCKCOUNT);

	/**
	 * getBlockHash directory.
	 */
	private File getBlockHashDirectory = new File(SRC_TEST_RESOURCES_BITCOIND_GET_BLOCK_HASH);

	/**
	 * getRawTransaction directory.
	 */
	private File getRawTransactionDirectory = new File(SRC_TEST_RESOURCES_BITCOIND_GET_RAW_TRANSACTION);

	/**
	 * Check if all directory exists. If no, creates them.
	 */
	private void checkDirectory() {
		if (!bitcoindDirectory.exists()) {
			bitcoindDirectory.mkdir();
		}
		if (!getBlockDirectory.exists()) {
			getBlockDirectory.mkdir();
		}
		if (!getBlockCountDirectory.exists()) {
			getBlockCountDirectory.mkdir();
		}
		if (!getBlockHashDirectory.exists()) {
			getBlockHashDirectory.mkdir();
		}
		if (!getRawTransactionDirectory.exists()) {
			getRawTransactionDirectory.mkdir();
		}
	}

	/**
	 * The getblockcount RPC returns the number of blocks in the local best block chain.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"jsonrpc":"1.0","method":"getblockcount","params":[]}' -H 'content-type:text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @return the number of blocks in the local best block chain.
	 * @see <a href="https://chainquery.com/bitcoin-api/getblockcount">https://chainquery.com/bitcoin-api/getblockcount</a>
	 */
	@Override
	public final GetBlockCountResponse getBlockCount() {
		checkDirectory();
		GetBlockCountResponse gbcr;
		File response = new File(getBlockCountDirectory.getPath() + "/response.ser");
		// if the file doesn't exists, we call the bitcoind server and save the file.
		if (!response.exists()) {
			gbcr = bds.getBlockCount();
			writeObjectToFile(getBlockCountDirectory.getPath(), "response.ser", gbcr);
		} else {
			gbcr = (GetBlockCountResponse) loadObjectFromFile(response);
		}
		return gbcr;
	}

	/**
	 * The getblockhash RPC returns the header hash of a block at the given height in the local best block chain.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"method": "getblockhash", "params": [427707] }' -H 'content-type: text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @param blockHeight block height.
	 * @return the block header hash.
	 * @see <a href="https://chainquery.com/bitcoin-api/getblockhash/427707">https://chainquery.com/bitcoin-api/getblockhash</a>
	 */
	@Override
	public final GetBlockHashResponse getBlockHash(final long blockHeight) {
		checkDirectory();
		GetBlockHashResponse gbhr;
		File response = new File(getBlockHashDirectory.getPath() + "/response-" + blockHeight + ".ser");
		// if the file doesn't exists, we call the bitcoind server and save the file.
		if (!response.exists()) {
			gbhr = bds.getBlockHash(blockHeight);
			writeObjectToFile(getBlockHashDirectory.getPath(), "response-" + blockHeight + ".ser", gbhr);
		} else {
			gbhr = (GetBlockHashResponse) loadObjectFromFile(response);
		}
		return gbhr;
	}

	/**
	 * The getblock RPC gets a block with a particular header hash from the local block database either as a JSON object or as a serialized block.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"method": "getblock", "params": ["000000000000000003536b07a8663ea1f10c891ccdb06e3a57c825041551df6a"] }' -H 'content-type: text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @param blockHash block hash.
	 * @return a JSON block.
	 * @see <a href="https://chainquery.com/bitcoin-api/getblock/000000000000000003536b07a8663ea1f10c891ccdb06e3a57c825041551df6a/true">https://chainquery.com/bitcoin-api/getblock</a>
	 */
	@Override
	public final GetBlockResponse getBlock(final String blockHash) {
		checkDirectory();
		GetBlockResponse gbr;
		File response = new File(getBlockDirectory.getPath() + "/response-" + blockHash + ".ser");
		// if the file doesn't exists, we call the bitcoind server and save the file.
		if (!response.exists()) {
			gbr = bds.getBlock(blockHash);
			writeObjectToFile(getBlockDirectory.getPath(), "response-" + blockHash + ".ser", gbr);
		} else {
			gbr = (GetBlockResponse) loadObjectFromFile(response);
		}
		return gbr;
	}

	/**
	 * The getrawtransaction RPC gets a hex-encoded serialized transaction or a JSON object describing the transaction.
	 * curl --user bitcoinrpc:JRkDy3tgCYdmCEqY1VdfdfhTswiRva --data-binary '{"method": "getrawtransaction", "params": ["5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8322", 1] }' -H 'content-type: text/plain;' -X POST http://5.196.65.205:8332
	 *
	 * @param transactionHash transaction hash.
	 * @return getrawtransaction response.
	 * @see <a href="https://chainquery.com/bitcoin-api/getrawtransaction/5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8322/1">https://chainquery.com/bitcoin-api/getrawtransaction</a>
	 */
	@Override
	public final GetRawTransactionResponse getRawTransaction(final String transactionHash) {
		checkDirectory();
		GetRawTransactionResponse grtr;
		File response = new File(getRawTransactionDirectory.getPath(), "reponse-" + transactionHash + ".ser");
		// if the file doesn't exists, we call the bitcoind server and save the file.
		if (!response.exists()) {
			grtr = bds.getRawTransaction(transactionHash);
			writeObjectToFile(getRawTransactionDirectory.getPath(), "reponse-" + transactionHash + ".ser", grtr);
		} else {
			grtr = (GetRawTransactionResponse) loadObjectFromFile(response);
		}
		return grtr;
	}

	/**
	 * Write an object to a file (serialize).
	 *
	 * @param directory directory
	 * @param fileName  file name
	 * @param o         object
	 */
	private void writeObjectToFile(final String directory, final String fileName, final Object o) {
		try {
			FileOutputStream fileOut = new FileOutputStream(directory + File.separator + fileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(o);
			out.close();
			fileOut.close();
		} catch (IOException e) {
			log.error("Error : " + e);
		}
	}

	/**
	 * Load an object from a file (deserialize).
	 *
	 * @param file file
	 * @return the object
	 */
	private Object loadObjectFromFile(final File file) {
		Object o = null;
		try {
			FileInputStream fileIn = new FileInputStream(file.getPath());
			ObjectInputStream in = new ObjectInputStream(fileIn);
			o = in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException e) {
			log.error("Error : " + e);
		} catch (ClassNotFoundException c) {
			log.error("Error : " + c);
		}
		return o;
	}

}
