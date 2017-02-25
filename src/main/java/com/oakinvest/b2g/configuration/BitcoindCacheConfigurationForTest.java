package com.oakinvest.b2g.configuration;

import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.dto.external.bitcoind.util.BitcoindResponseError;
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
 * Bitcoind cache service for tests.
 * Created by straumat on 04/01/17.
 */
@Configuration
@Aspect
@Profile("test")
public class BitcoindCacheConfigurationForTest {

	/**
	 * Block in error.
	 */
	public static final int BLOCK_IN_ERROR_1 = 496;

	/**
	 * Block hash in error.
	 */
	public static final String BLOCK_HASH_IN_ERROR_1 = "00000000b0c5a240b2a61d2e75692224efd4cbecdf6eaf4cc2cf477ca7c270e7";

	/**
	 * Non existing block.
	 */
	public static final int NON_EXISTING_BLOCK = 1000000;

	/**
	 * Non existing block hash.
	 */
	public static final String NON_EXISTING_BLOCK_HASH = "NON_EXISTING_HASH";

	/**
	 * Number of errors.
	 */
	public static final int NUMBER_OF_ERRORS = 8;

	/**
	 * bitcoind directory.
	 */
	private static final String BITCOIND_CACHE_DIRECTORY = "target/cache";

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
	private final Logger log = LoggerFactory.getLogger(BitcoindCacheConfigurationForTest.class);

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
	public BitcoindCacheConfigurationForTest() {
		final File bitcoindDirectory = new File(BITCOIND_CACHE_DIRECTORY);
		if (!bitcoindDirectory.exists()) {
			if (!bitcoindDirectory.mkdir()) {
				log.error("Impossible to create " + bitcoindDirectory.getAbsolutePath());
			}
		}
		if (!getBlockDirectory.exists()) {
			if (!getBlockDirectory.mkdir()) {
				log.error("Impossible to create " + getBlockDirectory.getAbsolutePath());
			}
		}
		if (!getBlockCountDirectory.exists()) {
			if (!getBlockCountDirectory.mkdir()) {
				log.error("Impossible to create " + getBlockCountDirectory.getAbsolutePath());
			}
		}
		if (!getBlockHashDirectory.exists()) {
			if (!getBlockHashDirectory.mkdir()) {
				log.error("Impossible to create " + getBlockHashDirectory.getAbsolutePath());
			}
		}
		if (!getRawTransactionDirectory.exists()) {
			if (!getRawTransactionDirectory.mkdir()) {
				log.error("Impossible to create " + getRawTransactionDirectory.getAbsolutePath());
			}
		}
	}

	/**
	 * getBlockCount() advice.
	 *
	 * @param pjp process.
	 * @return value.
	 * @throws Throwable exception.
	 */
	@Around("execution(* com.oakinvest.b2g.service.bitcoin.BitcoindService.getBlockCount())")
	public final Object getBlockCount(final ProceedingJoinPoint pjp) throws Throwable {
		log.debug("Using cache for getBlockCount()");
		GetBlockCountResponse gbcr;
		File response = new File(getBlockCountDirectory.getPath() + "/response.ser");
		// if the file doesn't exists, we call the bitcoind server and save the file.
		if (!response.exists()) {
			gbcr = (GetBlockCountResponse) pjp.proceed(new Object[]{ });
			writeObjectToFile(getBlockCountDirectory.getPath(), "response.ser", gbcr);
		} else {
			gbcr = (GetBlockCountResponse) loadObjectFromFile(response);
		}

/*		// Generate an error
		if (gbcr.getResult() == BLOCK_COUNT_IN_ERROR_1 && getBlockCountErrors < NUMBER_OF_ERRORS) {
			BitcoindResponseError error = new BitcoindResponseError();
			error.setCode(0);
			error.setMessage("Mock error on getBlockCount");
			gbcr.setResult(0);
			gbcr.setError(error);
			getBlockCountErrors++;
			response.delete();
		}*/

		// We will generate an error on a random basis.
		final int randomStart = 0;
		final int randomEnd = 100;
		int randomNumber = ThreadLocalRandom.current().nextInt(randomStart, randomEnd);
		if (randomNumber == 1) {
			// We create an error.
			BitcoindResponseError error = new BitcoindResponseError();
			error.setCode(0);
			error.setMessage("Mock error on getBlockCount");
			gbcr.setResult(0);
			gbcr.setError(error);
		}

		return gbcr;
	}

	/**
	 * getBlockHash() advice.
	 *
	 * @param pjp         process.
	 * @param blockHeight block height.
	 * @return value.
	 * @throws Throwable exception.
	 */
	@Around("execution(* com.oakinvest.b2g.service.bitcoin.BitcoindService.getBlockHash(..)) && args(blockHeight)")
	@SuppressWarnings("checkstyle:finalparameters")
	public final Object getBlockHash(final ProceedingJoinPoint pjp, long blockHeight) throws Throwable {
		log.debug("Using cache for getBlockHash()");
		GetBlockHashResponse gbhr;

		// Simulate error on a specific bloc.
		if (blockHeight == BLOCK_IN_ERROR_1 && getBlockHashErrors < NUMBER_OF_ERRORS) {
			blockHeight = NON_EXISTING_BLOCK;
			getBlockHashErrors++;
		}

		// if the file doesn't exists, we call the bitcoind server and save the file.
		File response = new File(getBlockHashDirectory.getPath() + "/response-" + blockHeight + ".ser");
		if (!response.exists()) {
			gbhr = (GetBlockHashResponse) pjp.proceed(new Object[]{ blockHeight });
			writeObjectToFile(getBlockHashDirectory.getPath(), "response-" + blockHeight + ".ser", gbhr);
		} else {
			gbhr = (GetBlockHashResponse) loadObjectFromFile(response);
		}
		return gbhr;
	}

	/**
	 * getBlock() advice.
	 *
	 * @param pjp       process.
	 * @param blockHash block hash.
	 * @return value.
	 * @throws Throwable exception.
	 */
	@Around("execution(* com.oakinvest.b2g.service.bitcoin.BitcoindService.getBlock(..)) && args(blockHash)")
	@SuppressWarnings("checkstyle:finalparameters")
	public final Object getBlock(final ProceedingJoinPoint pjp, String blockHash) throws Throwable {
		log.debug("Using cache for getBlock()");
		GetBlockResponse gbr;

		// Simulate error on a specific bloc.
		if (blockHash == BLOCK_HASH_IN_ERROR_1 && getBlockErrors < NUMBER_OF_ERRORS) {
			blockHash = NON_EXISTING_BLOCK_HASH;
			getBlockErrors++;
		}

		// if the file doesn't exists, we call the bitcoind server and save the file.
		File response = new File(getBlockDirectory.getPath() + "/response-" + blockHash + ".ser");
		if (!response.exists()) {
			gbr = (GetBlockResponse) pjp.proceed(new Object[]{ blockHash });
			writeObjectToFile(getBlockDirectory.getPath(), "response-" + blockHash + ".ser", gbr);
		} else {
			gbr = (GetBlockResponse) loadObjectFromFile(response);
		}
		return gbr;
	}

	/**
	 * getRawTransaction() advice.
	 *
	 * @param pjp             process.
	 * @param transactionHash transaction hash.
	 * @return value.
	 * @throws Throwable exception.
	 */
	@Around("execution(* com.oakinvest.b2g.service.bitcoin.BitcoindService.getRawTransaction(..)) && args(transactionHash)")
	public final Object getRawTransaction(final ProceedingJoinPoint pjp, final String transactionHash) throws Throwable {
		log.debug("Using cache for getRawTransaction()");
		GetRawTransactionResponse grtr;
		File response = new File(getRawTransactionDirectory.getPath(), "response-" + transactionHash + ".ser");
		// if the file doesn't exists, we call the bitcoind server and save the file.
		if (!response.exists()) {
			grtr = (GetRawTransactionResponse) pjp.proceed(new Object[]{ transactionHash });
			writeObjectToFile(getRawTransactionDirectory.getPath(), "response-" + transactionHash + ".ser", grtr);
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
			log.error("Error (IOException) : " + e);
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
			log.error("Error (IOException) : " + e);
		} catch (ClassNotFoundException c) {
			log.error("Error (ClassNotFoundException) : " + c);
		}
		return o;
	}

}
