package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.IntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * Tests for IntegrationService.
 * Created by straumat on 04/09/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class IntegrationServiceTest {

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private IntegrationService is;

	/**
	 * Bitcoin blcok repository.
	 */
	@Autowired
	private BitcoinBlockRepository bbr;

	/**
	 * Returns a date formated as timestamp
	 *
	 * @param formatedDate date like 2009-01-12 03:30:25
	 * @return timestap
	 */
	private long getDateAsTimestamp(final String formatedDate) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = dateFormat.parse(formatedDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime() / 1000;
	}

	/**
	 * integrateBitcoinBlock test.
	 */
	@Test
	public final void integrateBitcoinBlockTest() {
		// Tests data.
		final int firstBlockToImport = 0;
		final int lastBlockToImport = 170;
		final int expectedAddressesInAllBlocks = 0;

		// Launching integration.
		for (int i = firstBlockToImport; i <= lastBlockToImport; i++) {
			is.integrateBitcoinBlock(i);
		}

		// Testing data of block 170.
		final String expectedHash = "00000000d1145790a8694403d4063f323d499e655c83426834d4ce2f8dd4a2ee";
		final long expectedHeight = 170;
		final long expectedSize = 490;
		final long expectedVersion = 1;
		final String expectedMerkleroot = "7dac2c5666815c17a3b36427de37bb9d2e2c5ccec3f8633eb91a4205cb4c10ff";
		final long expectedTime = getDateAsTimestamp("2009-01-12 04:30:25");
		final long expectedNonce = 1889418792;
		final String expectedBits = "1d00ffff";
		final float expectedDifficulty = 1;
		final String expectedChainwork = "000000000000000000000000000000000000000000000000000000ab00ab00ab";
		final String expectedPreviousblockhash = "000000002a22cfee1f2c846adbd12b3e183d4f97683f85dad08a79780a84bd55";
		final String expectedNextblockhash = "00000000c9ec538cab7f38ef9c67a95742f56ab07b0a37c5be6b02808dbfb4e0";

		BitcoinBlock b = bbr.findByHash(expectedHash);
		assertEquals("Block height is wrong", expectedHeight, b.getHeight());
		assertEquals("Block size is wrong", expectedSize, b.getSize());
		assertEquals("Block version is wrong", expectedVersion, b.getVersion());
		assertEquals("Block merkel root is wrong", expectedMerkleroot, b.getMerkleroot());
		assertEquals("Block time is wrong", expectedTime, b.getTime());
		assertEquals("Block nonce is wrong", expectedNonce, b.getNonce());
		assertEquals("Block difficulty is wrong", expectedDifficulty, b.getDifficulty());
		assertEquals("Block bits is wrong", expectedBits, b.getBits());
		assertEquals("Block chainblock is wrong", expectedChainwork, b.getChainwork());
		assertEquals("Block previous block hash is wrong", expectedPreviousblockhash, b.getPreviousblockhash());
		assertEquals("Block next block hash is wrong", expectedNextblockhash, b.getNextblockhash());
	}

}
