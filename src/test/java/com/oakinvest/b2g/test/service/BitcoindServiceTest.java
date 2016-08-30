package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.service.BitcoindService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
	 * Block hash number used for getblockhash test.
	 */
	public static final String GET_BLOCKHASH_BLOCK_HASH = "0000000082b5015589a3fdf2d4baff403e6f0be035a5d9742c1cae6295464449";

	/**
	 * Block number used for getblockhash test.
	 */
	public static final int GET_BLOCKHASH_BLOCK_NUMBER = 3;

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
		assertTrue("Getblockcount failed", bds.getBlockCount().getResult() > 0);
	}

	/**
	 * gestblockhash test.
	 */
	@Test
	public final void testGestblockhash() {
		assertEquals("Getblockhash did not give a good answer", GET_BLOCKHASH_BLOCK_HASH, bds.getBlockHash(GET_BLOCKHASH_BLOCK_NUMBER).getResult());
	}


}
