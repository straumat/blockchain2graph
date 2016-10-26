package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.IntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Java6Assertions.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * Tests for IntegrationService.
 * Created by straumat on 04/09/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
	 * Bitcoin address repository.
	 */
	@Autowired
	private BitcoinAddressRepository bar;

	/**
	 * Bitcoin transaction repository.
	 */
	@Autowired
	private BitcoinTransactionRepository btr;

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
			fail("problem in date " + e);
		}
		return date.getTime() / 1000;
	}

	/**
	 * integrateBitcoinBlock test.
	 */
	@Test
	public final void integrateBitcoinBlockTest() {
		// Configuration.
		final int firstBlockToImport = 0;
		final int lastBlockToImport = 170;

		// Launching integration.
		for (int i = firstBlockToImport; i <= lastBlockToImport; i++) {
			assertTrue("Block " + i + " intergration failure", is.integrateBitcoinBlock(i));
		}

		// Testing data of block 170.
		final String expectedHash = "00000000d1145790a8694403d4063f323d499e655c83426834d4ce2f8dd4a2ee";
		final long expectedHeight = 170;
		final long expectedSize = 490;
		final long expectedVersion = 1;
		final String expectedMerkleroot = "7dac2c5666815c17a3b36427de37bb9d2e2c5ccec3f8633eb91a4205cb4c10ff";
		//final long expectedTime = getDateAsTimestamp("2009-01-12 04:30:25");
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
		// assertEquals("Block time is wrong", expectedTime, b.getTime()); FIXME There is a problem with time between local and IC
		assertEquals("Block nonce is wrong", expectedNonce, b.getNonce());
		assertEquals("Block difficulty is wrong", expectedDifficulty, b.getDifficulty());
		assertEquals("Block bits is wrong", expectedBits, b.getBits());
		assertEquals("Block chainblock is wrong", expectedChainwork, b.getChainwork());
		assertEquals("Block previous block hash is wrong", expectedPreviousblockhash, b.getPreviousblockhash());
		assertEquals("Block next block hash is wrong", expectedNextblockhash, b.getNextblockhash());

		// Testing the transaction of the block 170
		final String transactionHash = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
		final String expectedTransactionHex = "0100000001c997a5e56e104102fa209c6a852dd90660a20b2d9c352423edce25857fcd3704000000004847304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901ffffffff0200ca9a3b00000000434104ae1a62fe09c5f51b13905f07f06b99a2f7159b2225f374cd378d71302fa28414e7aab37397f554a7df5f142c21c1b7303b8a0626f1baded5c72a704f7e6cd84cac00286bee0000000043410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac00000000";
		final String expectedTransactionTxid = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
		final String expectedTransaxtionHash = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
		final long expectedTransactionSize = 275;
		final long expectedTransactionVSize = 275;
		final long expectedTransactionVersion = 1;
		final long exepctedTransactionLocktime = 0;
		final String expectedBlockHash = "00000000d1145790a8694403d4063f323d499e655c83426834d4ce2f8dd4a2ee";
		final long expectedTransactionTime = 1231731025;
		final long expectedBlockTime = 1231731025;
		BitcoinTransaction t = btr.findByTxid(transactionHash);
		assertNotNull("No transaction found", t);
		assertEquals("Wrong hex", expectedTransactionHex, t.getHex());
		assertEquals("Wrong Tx id", expectedTransactionTxid, t.getTxid());
		assertEquals("Wrong hash", expectedTransaxtionHash, t.getHash());
		assertEquals("Wrong size", expectedTransactionSize, t.getSize());
		assertEquals("Wrong vsize", expectedTransactionVSize, t.getvSize());
		assertEquals("Wrong version", expectedTransactionVersion, t.getVersion());
		assertEquals("Wrong locktime", exepctedTransactionLocktime, t.getLockTime());
		assertEquals("Wrong block hash", expectedBlockHash, t.getBlockHash());
		// assertTrue("Wrong transaction time", expectedTransactionTime < t.getTime());  FIXME There is a problem with time between local and IC
		// assertEquals("Block time is wrong", expectedTime, b.getTime()); FIXME There is a problem with time between local and IC

		// Vin 1.
		BitcoinTransactionInput vin1 = t.getInputs().iterator().next();
		final String vin1Txid = "0437cd7f8525ceed2324359c2d0ba26006d92d856a9c20fa0241106ee5a597c9";
		final long vin1Vout = 0;
		final String vin1ScriptSigAsm = "304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d09[ALL]";
		final String vin1ScriptSigHex = "47304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901";
		final long vin1Sequence = 4294967295L;
		assertEquals("Wrong vin1 tx id", vin1Txid, vin1.getTxid());
		assertEquals("Wrong vin1 voud", vin1Vout, vin1.getVout());
		assertEquals("Wrong vin1 asm", vin1ScriptSigAsm, vin1.getScriptSigAsm());
		assertEquals("Wrong vin1 hex", vin1ScriptSigHex, vin1.getScriptSigHex());
		assertEquals("Wrong vin1 sequence", vin1Sequence, vin1.getSequence());

		// VOut 1.

		// VOut 2.

		// Integrating again the block last block and checking that we did not make a duplicate block.
		long numberOfBlocks = bbr.count();
		is.integrateBitcoinBlock(lastBlockToImport);
		assertEquals("The same block has been saved as two entities", numberOfBlocks, bbr.count());

		// Testing if addresses are integrated
		final String existingBitcoinAdress = "1Q2TWHE3GMdB6BZKafqwxXtWAWgFt5Jvm3";
		final String nonExistingBitcoinAdress = "1Jy1ZoZaTzVs3dsa6LUYif1F5wyRVkLdDv";
		assertNotNull("Bitcoin address not saved", bar.findByAddress(existingBitcoinAdress));
		assertNull("Bitcoin address is saved but does not exists", bar.findByAddress(nonExistingBitcoinAdress));

		// Integrating again the block last block and checking that we did not make a duplicate address.
		long numberOfAddresses = bbr.count();
		is.integrateBitcoinBlock(lastBlockToImport);
		assertEquals("The same address has been saved as two entities", numberOfAddresses, bbr.count());


	}

}
