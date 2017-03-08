package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.configuration.BitcoindMock;
import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResult;
import com.oakinvest.b2g.dto.external.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getblockhash.GetBlockHashResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vin.GetRawTransactionVIn;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.vout.GetRawTransactionVOut;
import com.oakinvest.b2g.service.BitcoindService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for BitcoindService.
 * Created by straumat on 29/08/16.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class BitcoindServiceTest {

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
	 * Number of blocks.
	 */
	private static final int NUMBER_OF_BLOCKS = 500;

	/**
	 * Double delta acceptable for assertEquals.
	 */
	private static final double DOUBLE_DELTA = 0.0000001;

	/**
	 * Block number used for getblockhash test.
	 */
	private static final int BLOCK_NUMBER_VALID = 427707;

	/**
	 * Block hash number used for getblockhash test.
	 */
	private static final String BLOCK_HASH = "000000000000000003536b07a8663ea1f10c891ccdb06e3a57c825041551df6a";

	/**
	 * Block hash with coinbase transaction.
	 */
	private static final String COINBASE_TRANSACTION_HASH = "0437cd7f8525ceed2324359c2d0ba26006d92d856a9c20fa0241106ee5a597c9";

	/**
	 * Existing transaction in the block.
	 */
	private static final String BLOCK_EXISTING_TRANSACTION_HASH = "5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8322";

	/**
	 * Nnn existing transaction in the block.
	 */
	private static final String BLOCK_NON_EXISTING_TRANSACTION_HASH = "5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8333";

	/**
	 * Transaction hash in error.
	 */
	private static final String TRANSACTION_HASH_IN_ERROR_1 = "bc15f9dcbe637c187bb94247057b14637316613630126fc396c22e08b89006ea";

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bds;

	/**
	 * Bitcoind mock.
	 */
	@Autowired
	private BitcoindMock bitcoindMock;

	/**
	 * Setup.
	 *
	 * @throws Exception exception
	 */
	@Before
	public void setUp() throws Exception {
		bitcoindMock.resetErrorCounters();
	}

	/**
	 * getBlockCount test.
	 */
	@Test
	public final void getBlockCountTest() {
		GetBlockCountResponse r = bds.getBlockCount();
		assertNull("An error occurred", r.getError());
		assertTrue("getblockcount failed", r.getResult() >= 1);
	}

	/**
	 * getBlockCountTest test in error.
	 * It happends randomely so this test may fail.
	 */
	@Test
	public final void getBlockCountTestInError() {
		boolean errorFound = false;
		GetBlockCountResponse r = bds.getBlockCount();
		assertNull(r.getError());
		for (int i = 0; i < NUMBER_OF_BLOCKS + 1; i++) {
			if (bds.getBlockCount().getError() != null) {
				errorFound = true;
				break;
			}
		}
		assertTrue("bds.getBlockCount() never raised an error", errorFound);
	}

	/**
	 * getBlockHash test.
	 */
	@Test
	public final void getBlockHashTest() {
		GetBlockHashResponse r = bds.getBlockHash(BLOCK_NUMBER_VALID);
		assertNull("An error occurred", r.getError());
		assertEquals("getblockhash did not give the right answer", BLOCK_HASH, r.getResult());
	}

	/**
	 * getBlockHash test in error.
	 */
	@Test
	public final void getBlockHashTestInError() {
		GetBlockHashResponse r = bds.getBlockHash(BLOCK_IN_ERROR_1);
		assertNotNull("No error occurred", r.getError());
		for (int i = 0; i < NUMBER_OF_ERRORS + 1; i++) {
			r = bds.getBlockHash(BLOCK_IN_ERROR_1);
		}
		assertNull("An error occurred", r.getError());
	}

	/**
	 * getBlock test.
	 */
	@Test
	public final void getBlockTest() {
		// Expected values.
		final int expectedSize = 123489;
		final int expectedNumberOfTransactions = 323;
		final int expectedMinimumOfConfirmations = 300;
		final int expectedVersion = 536870912;
		final int expectedTime = 1472669751;
		final int expectedMedianTime = 1472667998;
		final long expectedNonce = 2771503229L;
		final float difficulty = 220755908330.3723f;
		final int expectedTxSize = 323;

		// Test.
		GetBlockResult r = bds.getBlock(BLOCK_HASH).getResult();
		assertEquals("Wrong hash", BLOCK_HASH, r.getHash());
		assertTrue("Wrong confirmations", r.getConfirmations() > expectedMinimumOfConfirmations);
		assertEquals("Wrong size", expectedSize, r.getSize());
		assertEquals("Wrong version", expectedVersion, r.getVersion());
		assertEquals("Wrong merkle root", "5a9d9f82baf0b9dd947f8e4b019bb7c8c51ac51ecc22aa935346a3faf32c369d", r.getMerkleroot());
		assertEquals("Wrong number of transactions", expectedNumberOfTransactions, r.getTx().size());
		assertTrue("getBlock() is missing a transaction", r.getTx().stream().anyMatch(s -> s.equals(BLOCK_EXISTING_TRANSACTION_HASH)));
		assertFalse("getBlock() is having a non existing a transaction", r.getTx().stream().anyMatch(s -> s.equals(BLOCK_NON_EXISTING_TRANSACTION_HASH)));
		assertEquals("Wrong time", expectedTime, r.getTime());
		assertEquals("Wrong median time", expectedMedianTime, r.getMediantime());
		assertEquals("Wrong nonce", expectedNonce, r.getNonce());
		assertEquals("Wrong bits", "1804fb08", r.getBits());
		assertEquals("Wrong difficulty", difficulty, r.getDifficulty(), 1);
		assertEquals("Wrong chain work", "00000000000000000000000000000000000000000024f53caa84da5b8101b580", r.getChainwork());
		assertEquals("Wrong previous block hash", "0000000000000000034a9b379481e41e935165dd32b39c69cb46591678b7eaa8", r.getPreviousblockhash());
		assertEquals("Wrong next block hash", "0000000000000000029c774f0f83bff2f2a2418040e775b5d3237d23382e2cf0", r.getNextblockhash());
		assertEquals("Wrong tx size", expectedTxSize, r.getTx().size());
	}

	/**
	 * getBlockTest test in error.
	 */
	@Test
	public final void getBlockTestInError() {
		GetBlockResponse r = bds.getBlock(BLOCK_HASH_IN_ERROR_1);
		assertNotNull("No error occurred", r.getError());
		for (int i = 0; i < NUMBER_OF_ERRORS + 1; i++) {
			r = bds.getBlock(BLOCK_HASH_IN_ERROR_1);
		}
		assertNull("An error occurred", r.getError());
	}

	/**
	 * getRawTransactionTest test.
	 */
	@Test
	public final void getRawTransactionTest() {
		// Expected values.
		final String expectedHex = "01000000036cdb9b62cbe05f8a927b9533c516cec7f48ea86f84a74a7a270b565e716c8d03010000006a4730440220658261d55d04fc86bbc8ab24580ec054608efbba4e7ef323f47d7da6115d53a40220555d4a7d7d764af19dab228f58f16897a1ec2df6ebb86d2f2fdec54022c7212d0121020ca90ba28ac971548973887e0ca85ba49a28a6d420d69f234484187939f21440fffffffff654b6e676d5df674cf6243d3cbd43ec1808b693e093592babdcd482efec1d29000000006a4730440220410979edac5c8fdbcadeed4dbf60d4c81a23529fd638a85ee2021ddfa8ccec9d022024a51ed255cc0e155e0242cc6a78424cbd425e3214748eed3abc91477e7d3684012102a7ee7ab483e24923fc07a2da4f04fcb57bb2ae07f9534f13ac0ab66b24f2f231fffffffffd641b0dbee467fc645dca838910f54b71d7215b6f7338e66f65a5d424fcb855000000006a47304402201a1ff43febcfee2fffb1dff37348181a4a0bfb3c3fd018efcf26c27812a1e13f02205a2dc3935c2c315d98f4b6da97f264931ec97234b69ddee3d76ad47989a23b2b012103e4ab3eb6a7f15ca989a16ec4457a0f776ebce42930580abc76f2a2f06939bc27ffffffff02e0d14d000000000017a9146169cadbc390751d0932864719c8ca539e18ad9487924a1a00000000001976a9144e7d959b26448aa47de48c622e6ce23838edbb6788ac00000000";
		final String expectedTxID = "5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8322";
		final String expectedHash = "5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8322";
		final int expectedSize = 517;
		final int expectedVsize = 517;
		final int expectedVersion = 1;
		final int expectedLockTime = 0;
		final int expectedVInSize = 3;
		final int expectedVOutSize = 2;
		// First VIn.
		final String expectedVIn1TxID = "038d6c715e560b277a4aa7846fa88ef4c7ce16c533957b928a5fe0cb629bdb6c";
		final int expectedVIn1VOut = 1;
		final String expectedVIn1scriptSigAsm = "30440220658261d55d04fc86bbc8ab24580ec054608efbba4e7ef323f47d7da6115d53a40220555d4a7d7d764af19dab228f58f16897a1ec2df6ebb86d2f2fdec54022c7212d[ALL] 020ca90ba28ac971548973887e0ca85ba49a28a6d420d69f234484187939f21440";
		final String expectedVIn1scriptSigHex = "4730440220658261d55d04fc86bbc8ab24580ec054608efbba4e7ef323f47d7da6115d53a40220555d4a7d7d764af19dab228f58f16897a1ec2df6ebb86d2f2fdec54022c7212d0121020ca90ba28ac971548973887e0ca85ba49a28a6d420d69f234484187939f21440";
		final long expectedVIn1Sequence = 4294967295L;
		// Second VIn.
		final String expectedVIn2TxID = "291decef82d4dcab2b5993e093b60818ec43bd3c3d24f64c67dfd576e6b654f6";
		final int expectedVIn2VOut = 0;
		final String expectedVIn2scriptSigAsm = "30440220410979edac5c8fdbcadeed4dbf60d4c81a23529fd638a85ee2021ddfa8ccec9d022024a51ed255cc0e155e0242cc6a78424cbd425e3214748eed3abc91477e7d3684[ALL] 02a7ee7ab483e24923fc07a2da4f04fcb57bb2ae07f9534f13ac0ab66b24f2f231";
		final String expectedVIn2scriptSigHex = "4730440220410979edac5c8fdbcadeed4dbf60d4c81a23529fd638a85ee2021ddfa8ccec9d022024a51ed255cc0e155e0242cc6a78424cbd425e3214748eed3abc91477e7d3684012102a7ee7ab483e24923fc07a2da4f04fcb57bb2ae07f9534f13ac0ab66b24f2f231";
		final long expectedVIn2Sequence = 4294967295L;
		// Third VIn.
		final String expectedVIn3TxID = "55b8fc24d4a5656fe638736f5b21d7714bf5108983ca5d64fc67e4be0d1b64fd";
		final int expectedVIn3VOut = 0;
		final String expectedVIn3scriptSigAsm = "304402201a1ff43febcfee2fffb1dff37348181a4a0bfb3c3fd018efcf26c27812a1e13f02205a2dc3935c2c315d98f4b6da97f264931ec97234b69ddee3d76ad47989a23b2b[ALL] 03e4ab3eb6a7f15ca989a16ec4457a0f776ebce42930580abc76f2a2f06939bc27";
		final String expectedVIn3scriptSigHex = "47304402201a1ff43febcfee2fffb1dff37348181a4a0bfb3c3fd018efcf26c27812a1e13f02205a2dc3935c2c315d98f4b6da97f264931ec97234b69ddee3d76ad47989a23b2b012103e4ab3eb6a7f15ca989a16ec4457a0f776ebce42930580abc76f2a2f06939bc27";
		final long expectedVIn3Sequence = 4294967295L;
		// First VOut.
		final float expectedVOut1Value = 0.05100000f;
		final int expectedVOut1N = 0;
		final String expectedVOut1ScriptPubKeyAsm = "OP_HASH160 6169cadbc390751d0932864719c8ca539e18ad94 OP_EQUAL";
		final String expectedVOut1ScriptPubKeyHex = "a9146169cadbc390751d0932864719c8ca539e18ad9487";
		final int expectedVOut1ScriptPubKeyReqSigs = 1;
		final String expectedVOut1ScriptPubKeyType = "scripthash";
		final String expectedVOut1ScriptPubKeyAddresses1 = "3Aa6CjiGJVo6sDvZV9R2DFngdGbbhJjaRr";
		// Second VOut.
		final float expectedVOut2Value = 0.01723026f;
		final int expectedVOut2N = 1;
		final String expectedVOut2ScriptPubKeyAsm = "OP_DUP OP_HASH160 4e7d959b26448aa47de48c622e6ce23838edbb67 OP_EQUALVERIFY OP_CHECKSIG";
		final String expectedVOut2ScriptPubKeyHex = "76a9144e7d959b26448aa47de48c622e6ce23838edbb6788ac";
		final int expectedVOut2ScriptPubKeyReqSigs = 1;
		final String expectedVOut2ScriptPubKeyType = "pubkeyhash";
		final String expectedVOut2ScriptPubKeyAddresses2 = "18A29ikHT3rzYCr1DGfgR7XZSF683Mqw6Z";
		// Last parameters.
		final String expectedBlockhash = "000000000000000003536b07a8663ea1f10c891ccdb06e3a57c825041551df6a";
		final long expectedMinimalConfirmations = 374;
		final long expectedTime = 1472669751L;
		final long expectedBlockTime = 1472669751L;

		// Test.
		GetRawTransactionResult r = bds.getRawTransaction(BLOCK_EXISTING_TRANSACTION_HASH).getResult();
		assertEquals("Wrong hex", expectedHex, r.getHex());
		assertEquals("Wrong tx id", expectedTxID, r.getTxid());
		assertEquals("Wrong hash", expectedHash, r.getHash());
		assertEquals("Wrong size", expectedSize, r.getSize());
		assertEquals("Wrong vsize", expectedVsize, r.getVsize());
		assertEquals("Wrong version", expectedVersion, r.getVersion());
		assertEquals("Wrong lock time", expectedLockTime, r.getLocktime());
		assertEquals("Wrong VIn size", expectedVInSize, r.getVin().size());
		assertEquals("Wrong vOut size", expectedVOutSize, r.getVout().size());

		// VIn 1
		GetRawTransactionVIn vin1 = r.getVin().get(0);
		assertEquals("Wrong VIn 1 Tx ID", expectedVIn1TxID, vin1.getTxid());
		assertEquals("Wrong VIn 1 vOut", expectedVIn1VOut, vin1.getVout());
		assertEquals("Wrong VIn 1 asm", expectedVIn1scriptSigAsm, vin1.getScriptSig().getAsm());
		assertEquals("Wrong VIn 1 hex", expectedVIn1scriptSigHex, vin1.getScriptSig().getHex());
		assertEquals("Wrong VIn 1 sequence", expectedVIn1Sequence, vin1.getSequence());
		assertNull("Vin1 coinbase not null", vin1.getCoinbase());
		// VIn 2
		GetRawTransactionVIn vin2 = r.getVin().get(1);
		assertEquals("Wrong VIn 2 Tx ID", expectedVIn2TxID, vin2.getTxid());
		assertEquals("Wrong VIn 2 vOut", expectedVIn2VOut, vin2.getVout());
		assertEquals("Wrong VIn 2 asm", expectedVIn2scriptSigAsm, vin2.getScriptSig().getAsm());
		assertEquals("Wrong VIn 2 hex", expectedVIn2scriptSigHex, vin2.getScriptSig().getHex());
		assertEquals("Wrong VIn 2 sequence", expectedVIn2Sequence, vin2.getSequence());
		assertNull("Vin2 coinbase not null", vin1.getCoinbase());
		// VIn 3
		GetRawTransactionVIn vin3 = r.getVin().get(2);
		assertEquals("Wrong VIn 3 Tx ID", expectedVIn3TxID, vin3.getTxid());
		assertEquals("Wrong VIn 3 vOut", expectedVIn3VOut, vin3.getVout());
		assertEquals("Wrong VIn 3 asm", expectedVIn3scriptSigAsm, vin3.getScriptSig().getAsm());
		assertEquals("Wrong VIn 3 hex", expectedVIn3scriptSigHex, vin3.getScriptSig().getHex());
		assertEquals("Wrong VIn 3 sequence", expectedVIn3Sequence, vin3.getSequence());

		// VOut 1
		GetRawTransactionVOut vOut1 = r.getVout().get(0);
		assertEquals("Wrong VOut 1 value", expectedVOut1Value, vOut1.getValue(), DOUBLE_DELTA);
		assertEquals("Wrong VOut 1 n", expectedVOut1N, vOut1.getN());
		assertEquals("Wring VOut 1 scriptPubKey asm", expectedVOut1ScriptPubKeyAsm, vOut1.getScriptPubKey().getAsm());
		assertEquals("Wring VOut 1 scriptPubKey hex", expectedVOut1ScriptPubKeyHex, vOut1.getScriptPubKey().getHex());
		assertEquals("Wring VOut 1 scriptPubKey reqSigs", expectedVOut1ScriptPubKeyReqSigs, vOut1.getScriptPubKey().getReqSigs());
		assertEquals("Wring VOut 1 scriptPubKey type", expectedVOut1ScriptPubKeyType, vOut1.getScriptPubKey().getType());
		assertEquals("Wring VOut 1 scriptPubKey addresses", expectedVOut1ScriptPubKeyAddresses1, vOut1.getScriptPubKey().getAddresses().get(0));
		// VOut 2
		GetRawTransactionVOut vOut2 = r.getVout().get(1);
		assertEquals("Wrong VOut 2 value", expectedVOut2Value, vOut2.getValue(), DOUBLE_DELTA);
		assertEquals("Wrong VOut 2 n", expectedVOut2N, vOut2.getN());
		assertEquals("Wring VOut 2 scriptPubKey asm", expectedVOut2ScriptPubKeyAsm, vOut2.getScriptPubKey().getAsm());
		assertEquals("Wring VOut 2 scriptPubKey hex", expectedVOut2ScriptPubKeyHex, vOut2.getScriptPubKey().getHex());
		assertEquals("Wring VOut 2 scriptPubKey reqSigs", expectedVOut2ScriptPubKeyReqSigs, vOut2.getScriptPubKey().getReqSigs());
		assertEquals("Wring VOut 2 scriptPubKey type", expectedVOut2ScriptPubKeyType, vOut2.getScriptPubKey().getType());
		assertEquals("Wring VOut 2 scriptPubKey addresses", expectedVOut2ScriptPubKeyAddresses2, vOut2.getScriptPubKey().getAddresses().get(0));

		// Last fields.
		assertEquals("Wrong block hash", expectedBlockhash, r.getBlockhash());
		assertTrue("Wrong confirmations", expectedMinimalConfirmations < r.getConfirmations());
		assertEquals("Wrong time", expectedTime, r.getTime());
		assertEquals("Wrong block time", expectedBlockTime, r.getBlocktime());

		// Getrawtransaction for coinbase.
		GetRawTransactionResult coinbaseTransaction = bds.getRawTransaction(COINBASE_TRANSACTION_HASH).getResult();
		assertEquals("Wrong vin transaction count", 1, coinbaseTransaction.getVin().size());
		final String expectedCoinbase = "04ffff001d0134";
		final long expectedSequence = 4294967295L;
		assertEquals("Coinbase not set", expectedCoinbase, coinbaseTransaction.getVin().get(0).getCoinbase());
		assertEquals("Sequence not set", expectedSequence, coinbaseTransaction.getVin().get(0).getSequence());
	}

	/**
	 * getRawTransaction test in error.
	 */
	@Test
	public final void getRawTransactionInError() {
		GetRawTransactionResponse r = bds.getRawTransaction(TRANSACTION_HASH_IN_ERROR_1);
		assertNotNull("No error occurred", r.getError());
		for (int i = 0; i < NUMBER_OF_ERRORS + 1; i++) {
			r = bds.getRawTransaction(TRANSACTION_HASH_IN_ERROR_1);
		}
		assertNull("An error occurred", r.getError());
	}

	/**
	 * getRawTransactionTest test.
	 */
	@Test
	public final void getRawTransactionWithEmptyAddressesTest() {
		final String invalidTransaction = "a288fec5559c3f73fd3d93db8e8460562ebfe2fcf04a5114e8d0f2920a6270dc";
		GetRawTransactionResult r = bds.getRawTransaction(invalidTransaction).getResult();
		assertNotNull(r.getVout().get(1).getScriptPubKey().getAddresses());
	}

	/**
	 * Testing error management.
	 */
	@Test
	public final void errorManagementTest() {
		// Expected values.
		final int expectedErrorCode = -8;
		final int nonExistingBlockNumber = 9999999;

		// Test.
		GetBlockHashResponse r = bds.getBlockHash(nonExistingBlockNumber);
		assertNotNull("No error was raised", r.getError());
		assertEquals("Error code was not retrieved", expectedErrorCode, r.getError().getCode());
		assertEquals("Error message was not retrieved", "Block height out of range", r.getError().getMessage());
	}

}
