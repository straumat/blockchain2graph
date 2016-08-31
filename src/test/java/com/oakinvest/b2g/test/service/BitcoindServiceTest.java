package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.dto.external.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.service.BitcoindService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for BitcoindService.
 * Created by straumat on 29/08/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class BitcoindServiceTest {

	/**
	 * Block number used for getblockhash test.
	 */
	private static final int BLOCK_NUMBER = 427707;

	/**
	 * Block hash number used for getblockhash test.
	 */
	private static final String BLOCK_HASH = "000000000000000003536b07a8663ea1f10c891ccdb06e3a57c825041551df6a";

	/**
	 * Numver of transactions in the block.
	 */
	private static final int BLOCK_SIZE = 323;

	/**
	 * Existing transaction in the block.
	 */
	private static final String BLOCK_EXISTING_TRANSACTION_HASH = "5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8322";

	/**
	 * Nnn existing transaction in the block.
	 */
	private static final String BLOCK_NON_EXISTING_TRANSACTION_HASH = "5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8333";


	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bds;

	/**
	 * getBlockCount test.
	 */
	@Test
	public final void testGetBlockCount() {
		GetBlockCountResponse r = bds.getBlockCount();
		assertTrue("getblockcount failed", r.getCount() > 0);
	}

	/**
	 * getBlockHash test.
	 */
	@Test
	public final void testGestblockhash() {
		GetBlockHashResponse r = bds.getBlockHash(BLOCK_NUMBER);
		assertEquals("getblockhash did not give the right answer", BLOCK_HASH, r.getResult());
	}

	/**
	 * getBlock test.
	 */
	@Test
	public final void testGetBlock() {
		List<String> transactions = bds.getBlock(BLOCK_HASH).getTransactions();
		assertEquals("getblock doesn't have the good number of transactions", BLOCK_SIZE, transactions.size());
		assertTrue("get block is missing a transaction", transactions.stream().anyMatch(s -> s.trim().equals(BLOCK_EXISTING_TRANSACTION_HASH)));
		assertFalse("get block is having a non existing a transaction", transactions.stream().anyMatch(s -> s.trim().equals(BLOCK_NON_EXISTING_TRANSACTION_HASH)));
	}

	/**
	 * getRawTransaction test.
	 */
	@Test
	public final void getRawTransaction() {
		// TODO To implement.
	}

}
