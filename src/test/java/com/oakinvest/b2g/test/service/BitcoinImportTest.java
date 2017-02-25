package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.batch.BitcoinImportBatch;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.bitcoin.BitcoinImportService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * Tests for bitcoin blockchain import.
 * Created by straumat on 04/09/16.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class BitcoinImportTest {

	/**
	 * Number of blocs to import.
	 */
	public static final int NUMBERS_OF_BLOCK_TO_IMPORT = 500;

	/**
	 * Integration service.
	 */
	@Autowired
	private BitcoinImportService is;

	/**
	 * Bitcoin block repository.
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
	 * Import batch.
	 */
	@Autowired
	private BitcoinImportBatch batch;

	/**
	 * Importing the data.
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// Launching import.
		while (bbr.count() < NUMBERS_OF_BLOCK_TO_IMPORT + 100) {
			batch.importBlock();
			batch.importBlockAddresses();
			batch.importBlockTransactions();
			batch.importBlockRelations();
		}
	}

	/**
	 * importBlock() test.
	 */
	@Test
	public final void importBlockTest() {
		// Testing data of block 170.
		final String expectedHash = "00000000d1145790a8694403d4063f323d499e655c83426834d4ce2f8dd4a2ee";
		final long expectedHeight = 170;
		final long expectedSize = 490;
		final long expectedVersion = 1;
		final String expectedMerkleroot = "7dac2c5666815c17a3b36427de37bb9d2e2c5ccec3f8633eb91a4205cb4c10ff";
		final long expectedTime = 1231731025;
		final long expectedmedianTime = 1231716245;
		final long expectedNonce = 1889418792;
		final String expectedBits = "1d00ffff";
		final float expectedDifficulty = 1;
		final String expectedChainwork = "000000000000000000000000000000000000000000000000000000ab00ab00ab";
		final String expectedPreviousblockhash = "000000002a22cfee1f2c846adbd12b3e183d4f97683f85dad08a79780a84bd55";
		final String expectedNextblockhash = "00000000c9ec538cab7f38ef9c67a95742f56ab07b0a37c5be6b02808dbfb4e0";
		BitcoinBlock b = bbr.findByHash(expectedHash);
		assertEquals("Wrong Block height", expectedHeight, b.getHeight());
		assertEquals("Wrong Block size ", expectedSize, b.getSize());
		assertEquals("Wrong Block version ", expectedVersion, b.getVersion());
		assertEquals("Wrong Block merkel root ", expectedMerkleroot, b.getMerkleRoot());
		assertEquals("Block time ", expectedTime, b.getTime());
		assertEquals("Block median time ", expectedmedianTime, b.getMedianTime());
		assertEquals("Wrong Block nonce ", expectedNonce, b.getNonce());
		assertEquals("Wrong Block difficulty ", expectedDifficulty, b.getDifficulty());
		assertEquals("Wrong Block bits ", expectedBits, b.getBits());
		assertEquals("Wrong Block chainblock ", expectedChainwork, b.getChainWork());
		assertEquals("Wrong Block previous block hash ", expectedPreviousblockhash, b.getPreviousBlockHash());
		assertEquals("Wrong Block next block hash ", expectedNextblockhash, b.getNextBlockHash());
	}

	/**
	 * importBlockAddresses() test.
	 */
	@Test
	public final void importBlockAddressesTest() {
		// Testing that the address of block 500 is imported and that non existing address does not.
		final String existingAddress = "1C1ENNWdkPMyhZ7xTEM4Kwq1FTUifZNCRd";
		final String nonExistingAddress = "TOTO";
		assertNotNull("The address should exists", bar.findByAddress(existingAddress));
		assertNull("The address should exists", bar.findByAddress(nonExistingAddress));
	}

	/**
	 * importBlockTransactions() test.
	 */

	public final void importBlockTransactionsTest() {
		// Testing the transaction of the block 170
		final String transactionHash = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
		final String expectedTransactionHex = "0100000001c997a5e56e104102fa209c6a852dd90660a20b2d9c352423edce25857fcd3704000000004847304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901ffffffff0200ca9a3b00000000434104ae1a62fe09c5f51b13905f07f06b99a2f7159b2225f374cd378d71302fa28414e7aab37397f554a7df5f142c21c1b7303b8a0626f1baded5c72a704f7e6cd84cac00286bee0000000043410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac00000000";
		final String expectedTransactionTxid = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
		final String expectedTransactionHash = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
		final long expectedTransactionSize = 275;
		final long expectedTransactionVSize = 275;
		final long expectedTransactionVersion = 1;
		final long expectedTransactionLockTime = 0;
		final String expectedBlockHash = "00000000d1145790a8694403d4063f323d499e655c83426834d4ce2f8dd4a2ee";
		final long expectedTransactionTime = 1231731025;
		final long expectedTransactionBlockTime = 1231731025;
		BitcoinTransaction t = btr.findByTxId(transactionHash);
		assertNotNull("No transaction found", t);
		assertEquals("Wrong hex", expectedTransactionHex, t.getHex());
		assertEquals("Wrong Tx id", expectedTransactionTxid, t.getTxId());
		assertEquals("Wrong hash", expectedTransactionHash, t.getHash());
		assertEquals("Wrong size", expectedTransactionSize, t.getSize());
		assertEquals("Wrong vSize", expectedTransactionVSize, t.getvSize());
		assertEquals("Wrong version", expectedTransactionVersion, t.getVersion());
		assertEquals("Wrong lockTime", expectedTransactionLockTime, t.getLockTime());
		assertEquals("Wrong block hash", expectedBlockHash, t.getBlockHash());
		assertEquals("Wrong time", expectedTransactionTime, t.getTime());
		assertEquals("Wrong block time", expectedTransactionBlockTime, t.getBlockTime());

		// Vin 1.
		BitcoinTransactionInput vin1 = t.getInputs().iterator().next();
		final String expectedVin1Txid = "0437cd7f8525ceed2324359c2d0ba26006d92d856a9c20fa0241106ee5a597c9";
		final int expectedVin1Vout = 0;
		final String expectedVin1ScriptSigAsm = "304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d09[ALL]";
		final String expectedVin1ScriptSigHex = "47304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901";
		final long expectedVin1Sequence = 4294967295L;
		assertEquals("Wrong vin1 tx id", expectedVin1Txid, vin1.getTxId());
		assertEquals("Wrong vin1 voud", expectedVin1Vout, vin1.getvOut());
		assertEquals("Wrong vin1 asm", expectedVin1ScriptSigAsm, vin1.getScriptSigAsm());
		assertEquals("Wrong vin1 hex", expectedVin1ScriptSigHex, vin1.getScriptSigHex());
		assertEquals("Wrong vin1 sequence", expectedVin1Sequence, vin1.getSequence());

		// VOut 1.
		BitcoinTransactionOutput vout1 = t.getOutputByIndex(0).get();
		final float expectedVout1Value = 10;
		final int expectedVout1N = 0;
		final String expectedVout1ScriptPubKeyAsm = "04ae1a62fe09c5f51b13905f07f06b99a2f7159b2225f374cd378d71302fa28414e7aab37397f554a7df5f142c21c1b7303b8a0626f1baded5c72a704f7e6cd84c OP_CHECKSIG";
		final String expectedVout1ScriptPubKeyHex = "4104ae1a62fe09c5f51b13905f07f06b99a2f7159b2225f374cd378d71302fa28414e7aab37397f554a7df5f142c21c1b7303b8a0626f1baded5c72a704f7e6cd84cac";
		final long expectedVout1ScriptPubKeyReqSigs = 1L;
		final String expectedVout1ScriptPubKeyType = "pubkey";
		final String expectedVout1ScriptPubKeyAddress = "1Q2TWHE3GMdB6BZKafqwxXtWAWgFt5Jvm3";
		assertEquals("Wrong vout1 value", expectedVout1Value, vout1.getValue());
		assertEquals("Wrong vout1 n", expectedVout1N, vout1.getN());
		assertEquals("Wrong vout1 asm", expectedVout1ScriptPubKeyAsm, vout1.getScriptPubKeyAsm());
		assertEquals("Wrong vout1 hex", expectedVout1ScriptPubKeyHex, vout1.getScriptPubKeyHex());
		assertEquals("wrong vout1 reqSigs", expectedVout1ScriptPubKeyReqSigs, vout1.getScriptPubKeyReqSigs());
		assertEquals("Wrong vout1 type", expectedVout1ScriptPubKeyType, vout1.getScriptPubKeyType());
		assertEquals("Wrong vout1 address", expectedVout1ScriptPubKeyAddress, vout1.getAddresses().iterator().next());

		// VOut 2.
		BitcoinTransactionOutput vout2 = t.getOutputByIndex(1).get();
		final float expectedVout2Value = 40;
		final int expectedVout2N = 1;
		final String expectedVout2ScriptPubKeyAsm = "0411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3 OP_CHECKSIG";
		final String expectedVout2ScriptPubKeyHex = "410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac";
		final long expectedVout2ScriptPubKeyReqSigs = 1L;
		final String expectedVout2ScriptPubKeyType = "pubkey";
		final String expectedVout2ScriptPubKeyAddress = "12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S";
		assertEquals("Wrong vout2 value", expectedVout2Value, vout2.getValue());
		assertEquals("Wrong vout2 n", expectedVout2N, vout2.getN());
		assertEquals("Wrong vout2 asm", expectedVout2ScriptPubKeyAsm, vout2.getScriptPubKeyAsm());
		assertEquals("Wrong vout2 hex", expectedVout2ScriptPubKeyHex, vout2.getScriptPubKeyHex());
		assertEquals("wrong vout2 reqSigs", expectedVout2ScriptPubKeyReqSigs, vout2.getScriptPubKeyReqSigs());
		assertEquals("Wrong vout2 type", expectedVout2ScriptPubKeyType, vout2.getScriptPubKeyType());
		assertEquals("Wrong vout2 address", expectedVout2ScriptPubKeyAddress, vout2.getAddresses().iterator().next());
	}

	/**
	 * importBitcoinBlock test.
	 *
	 * @throws InterruptedException if not able to suspend time.
	 */
	@Test
	public final void integrateBitcoinBlockTest() throws Exception {

		/*
		// testing relationships between blocks and transactions.
		assertEquals("Wrong block for the transaction", expectedBlockHash, t.getBlock().getHash());
		assertEquals("Wrong transactions number for the block", 2, b.getTransactions().size());

		// Testing if an address has correct outputs and inputs.
		// https://blockchain.info/address/12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S
		BitcoinAddress a1 = bar.findByAddress("12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S");
		// Testing withdrawals.
		final int a1NumberOfWithdrawls = 5;
		assertEquals("Wrong number of inputs", a1NumberOfWithdrawls, a1.getWithdrawals().size());
		//a1.getWithdrawals().forEach(i -> System.out.println("=> " + i.getTxId()));
		BitcoinTransactionInput bti1 = a1.getWithdrawals().stream().filter(i -> i.getTransaction().getTxId().equals("f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16")).findFirst().get();
		assertEquals("Wrong transaction value", 50.0f, bti1.getTransactionOutput().getValue());
		BitcoinTransactionInput bti2 = a1.getWithdrawals().stream().filter(i -> i.getTransaction().getTxId().equals("a16f3ce4dd5deb92d98ef5cf8afeaf0775ebca408f708b2146c4fb42b41e14be")).findFirst().get();
		assertEquals("Wrong transaction value", 40.0f, bti2.getTransactionOutput().getValue());
		BitcoinTransactionInput bti3 = a1.getWithdrawals().stream().filter(i -> i.getTransaction().getTxId().equals("591e91f809d716912ca1d4a9295e70c3e78bab077683f79350f101da64588073")).findFirst().get();
		assertEquals("Wrong transaction value", 30.0f, bti3.getTransactionOutput().getValue());
		BitcoinTransactionInput bti4 = a1.getWithdrawals().stream().filter(i -> i.getTransaction().getTxId().equals("12b5633bad1f9c167d523ad1aa1947b2732a865bf5414eab2f9e5ae5d5c191ba")).findFirst().get();
		assertEquals("Wrong transaction value", 29.0f, bti4.getTransactionOutput().getValue());
		BitcoinTransactionInput bti5 = a1.getWithdrawals().stream().filter(i -> i.getTransaction().getTxId().equals("828ef3b079f9c23829c56fe86e85b4a69d9e06e5b54ea597eef5fb3ffef509fe")).findFirst().get();
		assertEquals("Wrong transaction value", 28.0f, bti5.getTransactionOutput().getValue());
		// Testing deposits.
		final int a1NumberOfDeposits = 6;
		assertEquals("Wrong number of output", a1NumberOfDeposits, a1.getDeposits().size());
		BitcoinTransactionOutput bto1 = a1.getDeposits().stream().filter(o -> o.getTransaction().getTxId().equals("0437cd7f8525ceed2324359c2d0ba26006d92d856a9c20fa0241106ee5a597c9")).findFirst().get();
		assertEquals("Wrong transaction value", 50.0f, bto1.getValue());
		BitcoinTransactionOutput bto2 = a1.getDeposits().stream().filter(o -> o.getTransaction().getTxId().equals("f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16")).findFirst().get();
		assertEquals("Wrong transaction value", 40.0f, bto2.getValue());
		BitcoinTransactionOutput bto3 = a1.getDeposits().stream().filter(o -> o.getTransaction().getTxId().equals("a16f3ce4dd5deb92d98ef5cf8afeaf0775ebca408f708b2146c4fb42b41e14be")).findFirst().get();
		assertEquals("Wrong transaction value", 30.0f, bto3.getValue());
		BitcoinTransactionOutput bto4 = a1.getDeposits().stream().filter(o -> o.getTransaction().getTxId().equals("591e91f809d716912ca1d4a9295e70c3e78bab077683f79350f101da64588073")).findFirst().get();
		assertEquals("Wrong transaction value", 29.0f, bto4.getValue());
		BitcoinTransactionOutput bto5 = a1.getDeposits().stream().filter(o -> o.getTransaction().getTxId().equals("12b5633bad1f9c167d523ad1aa1947b2732a865bf5414eab2f9e5ae5d5c191ba")).findFirst().get();
		assertEquals("Wrong transaction value", 28.0f, bto5.getValue());
		BitcoinTransactionOutput bto6 = a1.getDeposits().stream().filter(o -> o.getTransaction().getTxId().equals("828ef3b079f9c23829c56fe86e85b4a69d9e06e5b54ea597eef5fb3ffef509fe")).findFirst().get();
		assertEquals("Wrong transaction value", 18.0f, bto6.getValue());

		// Another test on another address.
		// https://blockchain.info/address/1ByLSV2gLRcuqUmfdYcpPQH8Npm8cccsFg
		BitcoinAddress a2 = bar.findByAddress("1ByLSV2gLRcuqUmfdYcpPQH8Npm8cccsFg");
		// Testing withdrawals.
		BitcoinTransactionInput a2Bti1 = a2.getWithdrawals().stream().filter(i -> i.getTransaction().getTxId().equals("a3b0e9e7cddbbe78270fa4182a7675ff00b92872d8df7d14265a2b1e379a9d33")).findFirst().get();
		assertEquals("Wrong to address", "1BBz9Z15YpELQ4QP5sEKb1SwxkcmPb5TMs", a2Bti1.getTransaction().getOutputs().iterator().next().getAddresses().iterator().next());
		assertEquals("Wrong from address", "1ByLSV2gLRcuqUmfdYcpPQH8Npm8cccsFg", a2Bti1.getTransactionOutput().getAddresses().iterator().next());
		assertEquals("Wrong transaction value", 10.0f, a2Bti1.getTransactionOutput().getValue());
		// Testing deposits.
		BitcoinTransactionOutput a2bto1 = a2.getDeposits().stream().filter(o -> o.getTransaction().getTxId().equals("828ef3b079f9c23829c56fe86e85b4a69d9e06e5b54ea597eef5fb3ffef509fe")).findFirst().get();
		assertEquals("Wrong to address", "1ByLSV2gLRcuqUmfdYcpPQH8Npm8cccsFg", a2bto1.getAddresses().iterator().next());
		assertEquals("Wrong from address", "12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S", a2bto1.getTransaction().getInputs().iterator().next().getTransactionOutput().getAddresses().iterator().next());
		assertEquals("Wrong transaction value", 10.0f, a2bto1.getValue());

		// Test to check that coin creation is taken into account.
		// https://blockchain.info/fr/tx/ec2ba1a3784dacd6962d53e9266d08d6cca40cce60240954bb3448c6acdf568f
		BitcoinAddress a3 = bar.findByAddress("1562oGAGjMnQU5VsppQ8R2Hs4ab6WaeGBW");
		assertEquals("No coinbase transaction found", 1, a3.getDeposits().size());
		assertEquals("Wrong coinbase about", 50f, a3.getDeposits().stream().findFirst().get().getValue());
*/
	}

}
