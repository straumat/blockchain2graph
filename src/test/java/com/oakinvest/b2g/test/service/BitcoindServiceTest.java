package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblock.GetBlockResult;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.vin.GetRawTransactionVIn;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.vout.GetRawTransactionVOut;
import com.oakinvest.b2g.service.ext.bitcoin.bitcoind.BitcoindService;
import com.oakinvest.b2g.util.bitcoin.mock.BitcoindMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

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
		assertThat(bds.getBlockCount().getError())
				.as("Check that the api does not return an error")
				.isNull();

		assertThat(bds.getBlockCount().getResult())
				.as("Check getblockcount response")
				.isGreaterThanOrEqualTo(1);
	}

	/**
	 * getBlockCountTest test in error.
	 * Statistically getBlockCount() should fail at least one time every 100 block.
	 */
	@Test
	public final void getBlockCountTestInError() {
		for (int i = 0; i < NUMBER_OF_BLOCKS + 1; i++) {
			if (bds.getBlockCount().getError() != null) {
				return;
			}
		}
		fail("getBlockCountTest never raised an error");
	}

	/**
	 * getBlockHash test.
	 */
	@Test
	public final void getBlockHashTest() {
		assertThat(bds.getBlockHash(BLOCK_NUMBER_VALID).getError())
				.as("Check that the api does not return an error")
				.isNull();

		assertThat(bds.getBlockHash(BLOCK_NUMBER_VALID).getResult())
				.as("Check the block hash")
				.isEqualTo(BLOCK_HASH);
	}

	/**
	 * getBlockHash test in error.
	 */
	@Test
	public final void getBlockHashTestInError() {
		assertThat(bds.getBlockHash(BLOCK_IN_ERROR_1).getError())
				.as("Check that the api does return an error")
				.isNotNull();

		// After NUMBER_OF_ERRORS errors, the service should return to normal.
		for (int i = 0; i < NUMBER_OF_ERRORS; i++) {
			bds.getBlockHash(BLOCK_IN_ERROR_1);
		}

		assertThat(bds.getBlockHash(BLOCK_IN_ERROR_1).getError())
				.as("Check that the api does not return an error")
				.isNull();
	}

	/**
	 * getBlock test.
	 */
	@Test
	public final void getBlockTest() {
		// Expected values.
		final int expectedSize = 123489;
		final int expectedNumberOfTransactions = 323;
		final int expectedConfirmations = 300;
		final int expectedVersion = 536870912;
		final String expectedMerkelRoot = "5a9d9f82baf0b9dd947f8e4b019bb7c8c51ac51ecc22aa935346a3faf32c369d";
		final int expectedTime = 1472669751;
		final int expectedMedianTime = 1472667998;
		final long expectedNonce = 2771503229L;
		final String expectedBits = "1804fb08";
		final float expectedDifficulty = 220755908330.3723f;
		final String expectedChainWorld = "00000000000000000000000000000000000000000024f53caa84da5b8101b580";
		final String expectedPreviousBlockHash = "0000000000000000034a9b379481e41e935165dd32b39c69cb46591678b7eaa8";
		final String expectedNextBlockHash = "0000000000000000029c774f0f83bff2f2a2418040e775b5d3237d23382e2cf0";

		// Test.
		GetBlockResult blockResult = bds.getBlock(BLOCK_HASH).getResult();
		assertThat(blockResult.getHash()).as("Hash").isEqualTo(BLOCK_HASH);
		assertThat(blockResult.getConfirmations()).as("Confirmations").isGreaterThan(expectedConfirmations);
		assertThat(blockResult.getSize()).as("Size").isEqualTo(expectedSize);
		assertThat(blockResult.getVersion()).as("Version").isEqualTo(expectedVersion);
		assertThat(blockResult.getMerkleroot()).as("Merkle root").isEqualTo(expectedMerkelRoot);
		assertThat(blockResult.getTx())
				.as("Number of transactions").hasSize(expectedNumberOfTransactions)
				.as("Existing transaction id").contains(BLOCK_EXISTING_TRANSACTION_HASH)
				.as("Non existing transaction ids").doesNotContain(BLOCK_NON_EXISTING_TRANSACTION_HASH);
		assertThat(blockResult.getTime()).as("Time").isEqualTo(expectedTime);
		assertThat(blockResult.getMediantime()).as("Median time").isEqualTo(expectedMedianTime);
		assertThat(blockResult.getNonce()).as("Nonce").isEqualTo(expectedNonce);
		assertThat(blockResult.getBits()).as("Bits").isEqualTo(expectedBits);
		assertThat(blockResult.getDifficulty()).as("Difficulty").isEqualTo(expectedDifficulty);
		assertThat(blockResult.getChainwork()).as("Chainwork").isEqualTo(expectedChainWorld);
		assertThat(blockResult.getPreviousblockhash()).as("Previous block hash").isEqualTo(expectedPreviousBlockHash);
		assertThat(blockResult.getNextblockhash()).as("Next block hash").isEqualTo(expectedNextBlockHash);
	}

	/**
	 * getBlockTest test in error.
	 */
	@Test
	public final void getBlockTestInError() {
		assertThat(bds.getBlock(BLOCK_HASH_IN_ERROR_1).getError())
				.as("Check that the api does return an error")
				.isNotNull();

		for (int i = 0; i < NUMBER_OF_ERRORS + 1; i++) {
			bds.getBlock(BLOCK_HASH_IN_ERROR_1);
		}

		assertThat(bds.getBlock(BLOCK_HASH_IN_ERROR_1).getError())
				.as("Check that the api does not return an error")
				.isNull();
	}

	/**
	 * getRawTransactionTest test.
	 */
	@Test
	public final void getRawTransactionTest() {
		// Expected values.
		// Transaction.
		final String expectedHex = "01000000036cdb9b62cbe05f8a927b9533c516cec7f48ea86f84a74a7a270b565e716c8d03010000006a4730440220658261d55d04fc86bbc8ab24580ec054608efbba4e7ef323f47d7da6115d53a40220555d4a7d7d764af19dab228f58f16897a1ec2df6ebb86d2f2fdec54022c7212d0121020ca90ba28ac971548973887e0ca85ba49a28a6d420d69f234484187939f21440fffffffff654b6e676d5df674cf6243d3cbd43ec1808b693e093592babdcd482efec1d29000000006a4730440220410979edac5c8fdbcadeed4dbf60d4c81a23529fd638a85ee2021ddfa8ccec9d022024a51ed255cc0e155e0242cc6a78424cbd425e3214748eed3abc91477e7d3684012102a7ee7ab483e24923fc07a2da4f04fcb57bb2ae07f9534f13ac0ab66b24f2f231fffffffffd641b0dbee467fc645dca838910f54b71d7215b6f7338e66f65a5d424fcb855000000006a47304402201a1ff43febcfee2fffb1dff37348181a4a0bfb3c3fd018efcf26c27812a1e13f02205a2dc3935c2c315d98f4b6da97f264931ec97234b69ddee3d76ad47989a23b2b012103e4ab3eb6a7f15ca989a16ec4457a0f776ebce42930580abc76f2a2f06939bc27ffffffff02e0d14d000000000017a9146169cadbc390751d0932864719c8ca539e18ad9487924a1a00000000001976a9144e7d959b26448aa47de48c622e6ce23838edbb6788ac00000000";
		final String expectedTxID = "5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8322";
		final String expectedHash = "5481ccb8fd867ae90ae33793fff2b6bcd93f8881f1c883035f955c59d4fa8322";
		final int expectedSize = 517;
		final int expectedVSize = 517;
		final int expectedVersion = 1;
		final int expectedLockTime = 0;
		final int expectedVInSize = 3;
		final int expectedVOutSize = 2;
		final String expectedBlockhash = "000000000000000003536b07a8663ea1f10c891ccdb06e3a57c825041551df6a";
		final int expectedMinimalConfirmations = 374;
		final long expectedTime = 1472669751L;
		final long expectedBlockTime = 1472669751L;
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

		// Test.
		// Transaction.
		GetRawTransactionResult transactionResult = bds.getRawTransaction(BLOCK_EXISTING_TRANSACTION_HASH).getResult();
		assertThat(transactionResult.getHex()).as("Hex").isEqualTo(expectedHex);
		assertThat(transactionResult.getTxid()).as("Tx id").isEqualTo(expectedTxID);
		assertThat(transactionResult.getHash()).as("Hash").isEqualTo(expectedHash);
		assertThat(transactionResult.getSize()).as("Size").isEqualTo(expectedSize);
		assertThat(transactionResult.getVsize()).as("VSite").isEqualTo(expectedVSize);
		assertThat(transactionResult.getVersion()).as("Version").isEqualTo(expectedVersion);
		assertThat(transactionResult.getLocktime()).as("Lock time").isEqualTo(expectedLockTime);
		assertThat(transactionResult.getVin()).as("Vin size").hasSize(expectedVInSize);
		assertThat(transactionResult.getVout()).as("Vout size").hasSize(expectedVOutSize);
		assertThat(transactionResult.getBlockhash()).as("Block hash").isEqualTo(expectedBlockhash);
		assertThat(transactionResult.getConfirmations()).as("Confirmations").isGreaterThan(expectedMinimalConfirmations);
		assertThat(transactionResult.getTime()).as("Time").isEqualTo(expectedTime);
		assertThat(transactionResult.getBlocktime()).as("Block time").isEqualTo(expectedBlockTime);

		// VIn 1.
		GetRawTransactionVIn vin1 = transactionResult.getVin().get(0);
		assertThat(vin1.getTxid()).as("VIn 1 Tx ID").isEqualTo(expectedVIn1TxID);
		assertThat(vin1.getVout()).as("VIn 1 vOut").isEqualTo(expectedVIn1VOut);
		assertThat(vin1.getScriptSig().getAsm()).as("VIn 1 asm").isEqualTo(expectedVIn1scriptSigAsm);
		assertThat(vin1.getScriptSig().getHex()).as("VIn 1 hex").isEqualTo(expectedVIn1scriptSigHex);
		assertThat(vin1.getSequence()).as("VIn 1 sequence").isEqualTo(expectedVIn1Sequence);
		assertThat(vin1.getCoinbase()).as("VIn 1 coinbase not null").isNull();

		// VIn 2.
		GetRawTransactionVIn vin2 = transactionResult.getVin().get(1);
		assertThat(vin2.getTxid()).as("VIn 2 Tx ID").isEqualTo(expectedVIn2TxID);
		assertThat(vin2.getVout()).as("VIn 2 vOut").isEqualTo(expectedVIn2VOut);
		assertThat(vin2.getScriptSig().getAsm()).as("VIn 2 asm").isEqualTo(expectedVIn2scriptSigAsm);
		assertThat(vin2.getScriptSig().getHex()).as("VIn 2 hex").isEqualTo(expectedVIn2scriptSigHex);
		assertThat(vin2.getSequence()).as("VIn 2 sequence").isEqualTo(expectedVIn2Sequence);
		assertThat(vin2.getCoinbase()).as("VIn 2 coinbase not null").isNull();

		// VIn 3.
		GetRawTransactionVIn vin3 = transactionResult.getVin().get(2);
		assertThat(vin3.getTxid()).as("VIn 3 Tx ID").isEqualTo(expectedVIn3TxID);
		assertThat(vin3.getVout()).as("VIn 3 vOut").isEqualTo(expectedVIn3VOut);
		assertThat(vin3.getScriptSig().getAsm()).as("VIn 3 asm").isEqualTo(expectedVIn3scriptSigAsm);
		assertThat(vin3.getScriptSig().getHex()).as("VIn 3 hex").isEqualTo(expectedVIn3scriptSigHex);
		assertThat(vin3.getSequence()).as("VIn 3 sequence").isEqualTo(expectedVIn3Sequence);
		assertThat(vin3.getCoinbase()).as("VIn 3 coinbase not null").isNull();

		// VOut 1.
		GetRawTransactionVOut vOut1 = transactionResult.getVout().get(0);
		assertThat(vOut1.getValue()).as("VOut 1 value").isEqualTo(expectedVOut1Value);
		assertThat(vOut1.getN()).as("VOut 1 n").isEqualTo(expectedVOut1N);
		assertThat(vOut1.getScriptPubKey().getAsm()).as("VOut 1 scriptPubKey asm").isEqualTo(expectedVOut1ScriptPubKeyAsm);
		assertThat(vOut1.getScriptPubKey().getHex()).as("VOut 1 scriptPubKey hex").isEqualTo(expectedVOut1ScriptPubKeyHex);
		assertThat(vOut1.getScriptPubKey().getReqSigs()).as("VOut 1 scriptPubKey reqSigs").isEqualTo(expectedVOut1ScriptPubKeyReqSigs);
		assertThat(vOut1.getScriptPubKey().getType()).as("VOut 1 scriptPubKey type").isEqualTo(expectedVOut1ScriptPubKeyType);
		assertThat(vOut1.getScriptPubKey().getAddresses()).as("VOut 1 scriptPubKey address").contains(expectedVOut1ScriptPubKeyAddresses1);

		// VOut 2.
		GetRawTransactionVOut vOut2 = transactionResult.getVout().get(1);
		assertThat(vOut2.getValue()).as("VOut 2 value").isEqualTo(expectedVOut2Value);
		assertThat(vOut2.getN()).as("VOut 2 n").isEqualTo(expectedVOut2N);
		assertThat(vOut2.getScriptPubKey().getAsm()).as("VOut 2 scriptPubKey asm").isEqualTo(expectedVOut2ScriptPubKeyAsm);
		assertThat(vOut2.getScriptPubKey().getHex()).as("VOut 2 scriptPubKey hex").isEqualTo(expectedVOut2ScriptPubKeyHex);
		assertThat(vOut2.getScriptPubKey().getReqSigs()).as("VOut 2 scriptPubKey reqSigs").isEqualTo(expectedVOut2ScriptPubKeyReqSigs);
		assertThat(vOut2.getScriptPubKey().getType()).as("VOut 2 scriptPubKey type").isEqualTo(expectedVOut2ScriptPubKeyType);
		assertThat(vOut2.getScriptPubKey().getAddresses()).as("VOut 2 scriptPubKey address").contains(expectedVOut2ScriptPubKeyAddresses2);

		// Coinbase.
		final int expectedTransactionCount = 1;
		final String expectedCoinbase = "04ffff001d0134";
		final long expectedSequence = 4294967295L;
		GetRawTransactionResult coinbaseTransaction = bds.getRawTransaction(COINBASE_TRANSACTION_HASH).getResult();
		assertThat(coinbaseTransaction.getVin()).as("Vin transaction count").hasSize(expectedTransactionCount);
		assertThat(coinbaseTransaction.getVin().get(0))
				.as("Coinbase & sequence")
				.extracting("coinbase", "sequence")
				.contains(expectedCoinbase, expectedSequence);
	}

	/**
	 * getRawTransaction test in error.
	 */
	@Test
	public final void getRawTransactionInError() {
		assertThat(bds.getRawTransaction(TRANSACTION_HASH_IN_ERROR_1).getError())
				.as("Check that the api does return an error")
				.isNotNull();

		// After NUMBER_OF_ERRORS errors, the service should return to normal.
		for (int i = 0; i < NUMBER_OF_ERRORS; i++) {
			bds.getRawTransaction(TRANSACTION_HASH_IN_ERROR_1);
		}

		assertThat(bds.getRawTransaction(TRANSACTION_HASH_IN_ERROR_1).getError())
				.as("Check that the api does not return an error")
				.isNull();
	}

	/**
	 * getRawTransactionTest test.
	 */
	@Test
	public final void getRawTransactionWithEmptyAddressesTest() {
		final String invalidTransaction = "a288fec5559c3f73fd3d93db8e8460562ebfe2fcf04a5114e8d0f2920a6270dc";
		assertThat(bds.getRawTransaction(invalidTransaction).getResult().getVout().get(1).getScriptPubKey().getAddresses())
				.as("Empty address")
				.isEmpty();
	}

	/**
	 * Testing error management.
	 */
	@Test
	public final void errorManagementTest() {
		// Expected values.
		final int nonExistingBlockNumber = 9999999;
		final int expectedErrorCode = -8;
		final String expectedErrorMessage = "Block height out of range";

		// Test.
		assertThat(bds.getBlockHash(nonExistingBlockNumber).getError())
				.as("Error")
				.isNotNull()
				.as("Error code & message")
				.extracting("code", "message")
				.contains(expectedErrorCode, expectedErrorMessage);
	}

}
