package com.oakinvest.b2g.util.bitcoin.mock;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.util.BitcoindResponseError;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Bitcoind mock for tests.
 * Created by straumat on 04/01/17.
 */
@Configuration
@Aspect
@Profile("test")
public class BitcoindMock {

	/**
	 * Number of errors.
	 */
	private static final int NUMBER_OF_ERRORS = 8;

	/**
	 * Block in error.
	 */
	private static final int BLOCK_IN_ERROR_1 = 496;

	/**
	 * Block hash in error.
	 */
	private static final String BLOCK_HASH_IN_ERROR_1 = "00000000b0c5a240b2a61d2e75692224efd4cbecdf6eaf4cc2cf477ca7c270e7";

	/**
	 * Transaction hash in error.
	 */
	private static final String TRANSACTION_HASH_IN_ERROR_1 = "bc15f9dcbe637c187bb94247057b14637316613630126fc396c22e08b89006ea";

	/**
	 * Non existing block.
	 */
	private static final int NON_EXISTING_BLOCK = 1000000;

	/**
	 * Non existing block hash.
	 */
	private static final String NON_EXISTING_BLOCK_HASH = "NON_EXISTING_BLOCK_HASH";

	/**
	 * Non existing transaction hash.
	 */
	private static final String NON_EXISTING_TRANSACTION_HASH = "NON_EXISTING_TRANSACTION_HASH";

	/**
	 * bitcoind directory.
	 */
	private static final String BITCOIND_CACHE_DIRECTORY = "target/cache";

	/**
	 * getblockdata directory.
	 */
	private static final String GET_BLOCK_DATA_CACHE_DIRECTORY = BITCOIND_CACHE_DIRECTORY + "/getblockdata";

	/**
	 * getblock directory.
	 */
	private static final String GET_BLOCK_CACHE_DIRECTORY = BITCOIND_CACHE_DIRECTORY + "/getblock";

	/**
	 * getblockcount directory.
	 */
	private static final String GET_BLOCK_COUNT_CACHE_DIRECTORY = BITCOIND_CACHE_DIRECTORY + "/getblockcount";

	/**
	 * getBlockHash directory.
	 */
	private static final String GET_BLOCK_HASH_CACHE_DIRECTORY = BITCOIND_CACHE_DIRECTORY + "/getBlockHash";

	/**
	 * getRawTransaction directory.
	 */
	private static final String GET_RAW_TRANSACTION_CACHE_DIRECTORY = BITCOIND_CACHE_DIRECTORY + "/getRawTransaction";

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(BitcoindMock.class);

	/**
	 * getblockdata directory.
	 */
	private final File getBlockDataDirectory = new File(GET_BLOCK_DATA_CACHE_DIRECTORY);

	/**
	 * getblock directory.
	 */
	private final File getBlockDirectory = new File(GET_BLOCK_CACHE_DIRECTORY);

	/**
	 * getblockcount directory.
	 */
	private final File getBlockCountDirectory = new File(GET_BLOCK_COUNT_CACHE_DIRECTORY);

	/**
	 * getBlockHash directory.
	 */
	private final File getBlockHashDirectory = new File(GET_BLOCK_HASH_CACHE_DIRECTORY);

	/**
	 * getRawTransaction directory.
	 */
	private final File getRawTransactionDirectory = new File(GET_RAW_TRANSACTION_CACHE_DIRECTORY);

	/**
	 * getBlockHash errors.
	 */
	private int getBlockHashErrors = 0;

	/**
	 * getBlock errors.
	 */
	private int getBlockErrors = 0;

	/**
	 * getRawTransaction errors.
	 */
	private int getRawTransactionErrors = 0;

	/**
	 * Default constructor.
	 */
	public BitcoindMock() {
		final File bitcoindDirectory = new File(BITCOIND_CACHE_DIRECTORY);
		if (!bitcoindDirectory.exists() && !bitcoindDirectory.mkdir()) {
			log.error("Impossible to create " + bitcoindDirectory.getAbsolutePath());
		}
		if (!getBlockDataDirectory.exists() && !getBlockDataDirectory.mkdir()) {
			log.error("Impossible to create " + getBlockDataDirectory.getAbsolutePath());
		}
		if (!getBlockDirectory.exists() && !getBlockDirectory.mkdir()) {
			log.error("Impossible to create " + getBlockDirectory.getAbsolutePath());
		}
		if (!getBlockCountDirectory.exists() && !getBlockCountDirectory.mkdir()) {
			log.error("Impossible to create " + getBlockCountDirectory.getAbsolutePath());
		}
		if (!getBlockHashDirectory.exists() && !getBlockHashDirectory.mkdir()) {
			log.error("Impossible to create " + getBlockHashDirectory.getAbsolutePath());
		}
		if (!getRawTransactionDirectory.exists() && !getRawTransactionDirectory.mkdir()) {
			log.error("Impossible to create " + getRawTransactionDirectory.getAbsolutePath());
		}
	}

	/**
	 * Reset errors counter.
	 */
	public final void resetErrorCounters() {
		getBlockHashErrors = 0;
		getBlockErrors = 0;
		getRawTransactionErrors = 0;
	}

	/**
	 * getBlockData() advice.
	 *
	 * @param pjp         loadInCache.
	 * @param blockHeight block height.
	 * @return value.
	 * @throws Throwable exception.
	 */
	@Around("execution(* com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService.getBlockData(..)) && args(blockHeight)")
	public final Object getBlockData(final ProceedingJoinPoint pjp, final long blockHeight) throws Throwable {
		log.debug("Using cache for getBlockData()");
		BitcoindBlockData blockData;

		// if the file doesn't exists, we call the bitcoind server and save the file.
		File response = new File(getBlockDataDirectory.getPath() + "/response-" + blockHeight + ".ser");
		if (!response.exists()) {
			blockData = (BitcoindBlockData) pjp.proceed(new Object[]{ blockHeight });
			if (blockData != null) {
				writeObjectToFile(getBlockDataDirectory.getPath(), "response-" + blockHeight + ".ser", blockData);
			}
		} else {
			blockData = (BitcoindBlockData) loadObjectFromFile(response);
		}

		return blockData;
	}

	/**
	 * getBlockCount() advice.
	 *
	 * @param pjp loadInCache.
	 * @return value.
	 * @throws Throwable exception.
	 */
	@Around("execution(* com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService.getBlockCount())")
	public final Object getBlockCount(final ProceedingJoinPoint pjp) throws Throwable {
		log.debug("Using cache for getBlockCount()");
		GetBlockCountResponse getBlockCountResponse;
		File response = new File(getBlockCountDirectory.getPath() + "/response.ser");

		// if the file doesn't exists, we call the bitcoind server and save the file.
		if (!response.exists()) {
			getBlockCountResponse = (GetBlockCountResponse) pjp.proceed(new Object[]{ });
			writeObjectToFile(getBlockCountDirectory.getPath(), "response.ser", getBlockCountResponse);
		} else {
			getBlockCountResponse = (GetBlockCountResponse) loadObjectFromFile(response);
		}

		// We will generate an error on a random basis.
		final int randomStart = 0;
		final int randomEnd = 100;
		int randomNumber = ThreadLocalRandom.current().nextInt(randomStart, randomEnd);
		if (randomNumber == 1) {
			// We create an error.
			BitcoindResponseError error = new BitcoindResponseError();
			error.setCode(0);
			error.setMessage("Mock error on getBlockCount");
			getBlockCountResponse.setResult(0);
			getBlockCountResponse.setError(error);
		}

		return getBlockCountResponse;
	}

	/**
	 * getBlockHash() advice.
	 *
	 * @param pjp         loadInCache.
	 * @param blockHeight block height.
	 * @return value.
	 * @throws Throwable exception.
	 */
	@Around("execution(* com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService.getBlockHash(..)) && args(blockHeight)")
	public final Object getBlockHash(final ProceedingJoinPoint pjp, final long blockHeight) throws Throwable {
		log.debug("Using cache for getBlockHash()");
		GetBlockHashResponse getBlockHashResponse;

		// blockHeightValue is the value to get.
		long blockHeightValue = blockHeight;

		// Simulate error on a specific bloc.
		if (blockHeight == BLOCK_IN_ERROR_1 && getBlockHashErrors < NUMBER_OF_ERRORS) {
			blockHeightValue = NON_EXISTING_BLOCK;
			getBlockHashErrors++;
		}

		// if the file doesn't exists, we call the bitcoind server and save the file.
		File response = new File(getBlockHashDirectory.getPath() + "/response-" + blockHeightValue + ".ser");
		if (!response.exists()) {
			getBlockHashResponse = (GetBlockHashResponse) pjp.proceed(new Object[]{ blockHeightValue });
			writeObjectToFile(getBlockHashDirectory.getPath(), "response-" + blockHeightValue + ".ser", getBlockHashResponse);
		} else {
			getBlockHashResponse = (GetBlockHashResponse) loadObjectFromFile(response);
		}

		return getBlockHashResponse;
	}

	/**
	 * getBlock() advice.
	 *
	 * @param pjp       loadInCache.
	 * @param blockHash block hash.
	 * @return value.
	 * @throws Throwable exception.
	 */
	@Around("execution(* com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService.getBlock(..)) && args(blockHash)")
	public final Object getBlock(final ProceedingJoinPoint pjp, final String blockHash) throws Throwable {
		log.debug("Using cache for getBlock()");
		GetBlockResponse getBlockResponse;

		// blockHash is the value to get.
		String blockHashValue = blockHash;

		// Simulate error on a specific bloc.
		if (BLOCK_HASH_IN_ERROR_1.equals(blockHashValue) && getBlockErrors < NUMBER_OF_ERRORS) {
			blockHashValue = NON_EXISTING_BLOCK_HASH;
			getBlockErrors++;
		}

		// if the file doesn't exists, we call the bitcoind server and save the file.
		File response = new File(getBlockDirectory.getPath() + "/response-" + blockHashValue + ".ser");
		if (!response.exists()) {
			getBlockResponse = (GetBlockResponse) pjp.proceed(new Object[]{ blockHashValue });
			writeObjectToFile(getBlockDirectory.getPath(), "response-" + blockHashValue + ".ser", getBlockResponse);
		} else {
			getBlockResponse = (GetBlockResponse) loadObjectFromFile(response);
		}

		return getBlockResponse;
	}

	/**
	 * getRawTransaction() advice.
	 *
	 * @param pjp             loadInCache.
	 * @param transactionHash transaction hash.
	 * @return value.
	 * @throws Throwable exception.
	 */
	@Around("execution(* com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService.getRawTransaction(..)) && args(transactionHash)")
	public final Object getRawTransaction(final ProceedingJoinPoint pjp, final String transactionHash) throws Throwable {
		log.debug("Using cache for getRawTransaction()");
		GetRawTransactionResponse getRawTransactionResponse;

		// blockHash is the value to get.
		String transactionHashValue = transactionHash;

		// Simulate error on a specific bloc.
		if (TRANSACTION_HASH_IN_ERROR_1.equals(transactionHash) && getRawTransactionErrors < NUMBER_OF_ERRORS) {
			transactionHashValue = NON_EXISTING_TRANSACTION_HASH;
			getRawTransactionErrors++;
		}
		File response = new File(getRawTransactionDirectory.getPath(), "response-" + transactionHashValue + ".ser");
		// if the file doesn't exists, we call the bitcoind server and save the file.
		if (!response.exists()) {
			getRawTransactionResponse = (GetRawTransactionResponse) pjp.proceed(new Object[]{ transactionHashValue });
			writeObjectToFile(getRawTransactionDirectory.getPath(), "response-" + transactionHashValue + ".ser", getRawTransactionResponse);
		} else {
			getRawTransactionResponse = (GetRawTransactionResponse) loadObjectFromFile(response);
		}

		return getRawTransactionResponse;
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
			log.error("Error (IOException) : " + e.getMessage(), e);
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
			log.error("Error (IOException) : " + e.getMessage(), e);
		} catch (ClassNotFoundException c) {
			log.error("Error (ClassNotFoundException) : " + c.getMessage(), c);
		}
		return o;
	}

}
