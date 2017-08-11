package com.oakinvest.b2g.test.batch;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.batch.bitcoin.BitcoinBatchBlocks;
import com.oakinvest.b2g.batch.bitcoin.BitcoinBatchBlocksAddresses;
import com.oakinvest.b2g.batch.bitcoin.BitcoinBatchBlocksRelations;
import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutputType;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionInputRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionOutputRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.util.bitcoin.mock.BitcoindMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static com.oakinvest.b2g.domain.bitcoin.BitcoinBlockState.IMPORTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

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
	private static final int NUMBERS_OF_BLOCK_TO_IMPORT = 500;

	/**
	 * setup is done.
	 */
	private static boolean databaseReseted = false;

	/**
	 * Spring context.
	 */
	@Autowired
	private ApplicationContext ctx;

    /**
     * Import batch.
     */
    @Autowired
    private BitcoinBatchBlocks batchBlocks;

    /**
     * Import batch.
     */
    @Autowired
    private BitcoinBatchBlocksAddresses batchAddresses;

    /**
     * Import batch.
     */
    @Autowired
    private BitcoinBatchBlocksRelations batchRelations;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinBlockRepository blockRepository;

	/**
	 * Bitcoin address repository.
	 */
	@Autowired
	private BitcoinAddressRepository addressRepository;

	/**
	 * Bitcoin transaction repository.
	 */
	@Autowired
	private BitcoinTransactionRepository transactionRepository;

    /**
     * Transaction import repository.
     */
    @Autowired
    private BitcoinTransactionInputRepository transactionInputRepository;

    /**
	 * Transaction output repository.
	 */
	@Autowired
	private BitcoinTransactionOutputRepository transactionOutputRepository;

	/**
	 * Bitcoind mock.
	 */
	@Autowired
	private BitcoindMock bitcoindMock;

	/**
	 * Importing the data.
	 *
	 * @throws Exception exception
	 */
	@Before
	public void setUp() throws Exception {
		// Reset the database.
		if (!databaseReseted) {
			Map<String, GraphRepository> graphRepositories = ctx.getBeansOfType(GraphRepository.class);
			for (GraphRepository graphRepository : graphRepositories.values()) {
				graphRepository.deleteAll();
			}
			databaseReseted = true;
		}

		// Reset errors.
		bitcoindMock.resetErrorCounters();

		// Launch block importation.
		int iterations = 0;
		final int maxIteration = 1000;
		while (blockRepository.countBlockByState(IMPORTED) < NUMBERS_OF_BLOCK_TO_IMPORT) {
			try {
				batchBlocks.execute();
				batchAddresses.execute();
				batchRelations.execute();
				iterations++;
				if (iterations >= maxIteration) {
					fail("Persistent problem to get blocks");
				}
			} catch (Exception e) {
				fail("Error while importing : " + e.getMessage());
			}
		}
	}

	/**
	 * Test for no empty block.
	 */
	@Test
	public void testEmptyRelations() {
		// Test all blocks.
		blockRepository.findAll().forEach(b -> assertThat(b.getTransactions()).as("Block %s's transactions", b.getHeight()).isNotEmpty());

		// Test all transactions.
		transactionRepository.findAll().forEach(t -> {
			assertThat(t.getInputs()).as("Transaction %s inputs", t.getTxId()).isNotEmpty();
			assertThat(t.getOutputs()).as("Transaction %s outputs", t.getTxId()).isNotEmpty();
		});
	}

	/**
	 * Test for recovery after crash.
	 */
	@Test
	public final void testRecoveryAfterCrash() {
		final long blockForTest = NUMBERS_OF_BLOCK_TO_IMPORT - 1;

		// We set the last block as not at all imported
		BitcoinBlock b = blockRepository.findByHeight(blockForTest);
		b.setState(BitcoinBlockState.BLOCK_IMPORTED);
		blockRepository.save(b);

		// Then, we import it.
		try {
			batchAddresses.execute();
			batchRelations.execute();
		} catch (Exception e) {
			fail("Recovery after crash did not work " + e.getMessage());
		}

		// we check that everything as been imported again on that block
		assertThat(blockRepository.countBlockByState(IMPORTED)).as("Number of blocks imported", NUMBERS_OF_BLOCK_TO_IMPORT);
		assertThat(blockRepository.findByHeight(blockForTest).getState()).as("Block state").isEqualTo(IMPORTED);
	}

	/**
	 * importBlock() test.
	 */
	@Test
	public final void blocksDataTest() {
		// Expected values.
		final String expectedHash = "00000000d1145790a8694403d4063f323d499e655c83426834d4ce2f8dd4a2ee";
		final long expectedHeight = 170;
		final long expectedSize = 490;
		final long expectedVersion = 1;
		final String expectedMerkleroot = "7dac2c5666815c17a3b36427de37bb9d2e2c5ccec3f8633eb91a4205cb4c10ff";
		final long expectedTime = 1231731025;
		final long expectedMedianTime = 1231716245;
		final long expectedNonce = 1889418792;
		final String expectedBits = "1d00ffff";
		final float expectedDifficulty = 1;
		final String expectedChainwork = "000000000000000000000000000000000000000000000000000000ab00ab00ab";
		final String expectedPreviousblockhash = "000000002a22cfee1f2c846adbd12b3e183d4f97683f85dad08a79780a84bd55";
		final String expectedNextblockhash = "00000000c9ec538cab7f38ef9c67a95742f56ab07b0a37c5be6b02808dbfb4e0";
		final int expectedTxSize = 2;

		// Test.
		BitcoinBlock b = blockRepository.findByHash(expectedHash);
		assertThat(b).as("Block").isNotNull();
		assertThat(b.getHash()).as("Hash").isEqualTo(expectedHash);
		assertThat(b.getHeight()).as("Height").isEqualTo(expectedHeight);
		assertThat(b.getSize()).as("Size").isEqualTo(expectedSize);
		assertThat(b.getVersion()).as("Version").isEqualTo(expectedVersion);
		assertThat(b.getMerkleRoot()).as("Merkel root").isEqualTo(expectedMerkleroot);
		assertThat(b.getTime()).as("Time").isEqualTo(expectedTime);
		assertThat(b.getMedianTime()).as("Median time").isEqualTo(expectedMedianTime);
		assertThat(b.getNonce()).as("Nonce").isEqualTo(expectedNonce);
		assertThat(b.getDifficulty()).as("Difficulty").isEqualTo(expectedDifficulty);
		assertThat(b.getBits()).as("Bits").isEqualTo(expectedBits);
		assertThat(b.getChainWork()).as("Chain work").isEqualTo(expectedChainwork);
		assertThat(b.getPreviousBlockHash()).as("Previous block hash").isEqualTo(expectedPreviousblockhash);
		assertThat(b.getPreviousBlock()).as("Previous block").isNotNull();
		assertThat(b.getNextBlockHash()).as("Next block hash").isEqualTo(expectedNextblockhash);
		assertThat(b.getNextBlock()).as("Next block").isNotNull();
		assertThat(b.getTx()).as("Transaction size").hasSize(expectedTxSize);

        assertThat(b.getTransactions()).as("Block transactions").hasSize(expectedTxSize);

        // Test relations between blocks (previous block & next block).
        assertThat(blockRepository.findByHeight(1L))
                .as("Previous & next block")
                .extracting("previousBlock", "nextBlock.height")
                .contains(null, 2L);

        assertThat(blockRepository.findByHeight(6L))
                .as("Previous & next block")
                .extracting("previousBlock.height", "nextBlock.height")
                .contains(5L, 7L);

        assertThat(blockRepository.findByHeight(NUMBERS_OF_BLOCK_TO_IMPORT))
                .as("Previous & next block")
                .extracting("nextBlock", "previousBlock.height")
                .contains(null, 499L);
    }

	/**
	 * importBlockAddresses() test.
	 */
	@Test
	public final void addressesDataTest() {
		// Testing that the address of block 500 is imported and that non existing address does not.
		final String existingAddress = "1C1ENNWdkPMyhZ7xTEM4Kwq1FTUifZNCRd";
		final String nonExistingAddress = "TOTO";

		// Existing address.
		assertThat(addressRepository.findByAddress(existingAddress))
				.as("Address exists")
				.isNotNull()
				.as("Address value")
				.extracting("address")
				.contains(existingAddress);

		// Non existing address.
		assertThat(addressRepository.findByAddress(nonExistingAddress))
				.as("Address does not exists")
				.isNull();
	}

	/**
	 * importBlockTransactions() test.
	 */
	@Test
	public final void transactionsDataTest() {
		// Expected value for the transaction of the block 170.
		// Transaction.
		final String transactionHash = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
		final String expectedTransactionHex = "0100000001c997a5e56e104102fa209c6a852dd90660a20b2d9c352423edce25857fcd3704000000004847304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901ffffffff0200ca9a3b00000000434104ae1a62fe09c5f51b13905f07f06b99a2f7159b2225f374cd378d71302fa28414e7aab37397f554a7df5f142c21c1b7303b8a0626f1baded5c72a704f7e6cd84cac00286bee0000000043410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac00000000";
		final String expectedTransactionTxID = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
		final String expectedTransactionHash = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
		final long expectedTransactionSize = 275;
		final long expectedTransactionVSize = 275;
		final long expectedTransactionVersion = 1;
		final long expectedTransactionLockTime = 0;
		final String expectedBlockHash = "00000000d1145790a8694403d4063f323d499e655c83426834d4ce2f8dd4a2ee";
		final long expectedTransactionTime = 1231731025;
		final long expectedTransactionBlockTime = 1231731025;
		// Vin 1.
		final String expectedVin1Txid = "0437cd7f8525ceed2324359c2d0ba26006d92d856a9c20fa0241106ee5a597c9";
		final int expectedVin1Vout = 0;
		final String expectedVin1ScriptSigAsm = "304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d09[ALL]";
		final String expectedVin1ScriptSigHex = "47304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901";
		final long expectedVin1Sequence = 4294967295L;
		// Vout 1.
		final float expectedVout1Value = 10;
		final int expectedVout1N = 0;
		final String expectedVout1ScriptPubKeyAsm = "04ae1a62fe09c5f51b13905f07f06b99a2f7159b2225f374cd378d71302fa28414e7aab37397f554a7df5f142c21c1b7303b8a0626f1baded5c72a704f7e6cd84c OP_CHECKSIG";
		final String expectedVout1ScriptPubKeyHex = "4104ae1a62fe09c5f51b13905f07f06b99a2f7159b2225f374cd378d71302fa28414e7aab37397f554a7df5f142c21c1b7303b8a0626f1baded5c72a704f7e6cd84cac";
		final long expectedVout1ScriptPubKeyReqSigs = 1L;
		final BitcoinTransactionOutputType expectedVout1ScriptPubKeyType = BitcoinTransactionOutputType.pubkey;
		final String expectedVout1ScriptPubKeyAddress = "1Q2TWHE3GMdB6BZKafqwxXtWAWgFt5Jvm3";
		// Vout 2.
		final float expectedVout2Value = 40;
		final int expectedVout2N = 1;
		final String expectedVout2ScriptPubKeyAsm = "0411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3 OP_CHECKSIG";
		final String expectedVout2ScriptPubKeyHex = "410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac";
		final long expectedVout2ScriptPubKeyReqSigs = 1L;
		final BitcoinTransactionOutputType expectedVout2ScriptPubKeyType = BitcoinTransactionOutputType.pubkey;
		final String expectedVout2ScriptPubKeyAddress = "12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S";

		// Test.
		// Transaction.
		BitcoinTransaction transaction = transactionRepository.findByTxId(transactionHash);
		assertThat(transaction).as("Transaction").isNotNull();
		assertThat(transaction.getTxId()).as("Txid").isEqualTo(expectedTransactionTxID);
		assertThat(transaction.getHex()).as("Hex").isEqualTo(expectedTransactionHex);
		assertThat(transaction.getHash()).as("Hash").isEqualTo(expectedTransactionHash);
		assertThat(transaction.getSize()).as("Size").isEqualTo(expectedTransactionSize);
		assertThat(transaction.getvSize()).as("Vsize").isEqualTo(expectedTransactionVSize);
		assertThat(transaction.getVersion()).as("Version").isEqualTo(expectedTransactionVersion);
		assertThat(transaction.getLockTime()).as("Lock time").isEqualTo(expectedTransactionLockTime);
		assertThat(transaction.getBlockHash()).as("Hash").isEqualTo(expectedBlockHash);
		assertThat(transaction.getTime()).as("Time").isEqualTo(expectedTransactionTime);
		assertThat(transaction.getBlockTime()).as("Block time").isEqualTo(expectedTransactionBlockTime);

		// Vin 1.
		BitcoinTransactionInput vin1 = transaction.getInputs().iterator().next();
		assertThat(vin1.getTxId()).as("Txid").isEqualTo(expectedVin1Txid);
		assertThat(vin1.getvOut()).as("Vout").isEqualTo(expectedVin1Vout);
		assertThat(vin1.getScriptSigAsm()).as("Asm").isEqualTo(expectedVin1ScriptSigAsm);
		assertThat(vin1.getScriptSigHex()).as("Hex").isEqualTo(expectedVin1ScriptSigHex);
		assertThat(vin1.getSequence()).as("Sequence").isEqualTo(expectedVin1Sequence);
		assertThat(vin1.getCoinbase()).as("Coinbase").isNull();
		assertThat(vin1.isCoinbase()).as("Is coinbase").isFalse();

		// VOut 1.
		assertThat(transaction.getOutputByIndex(0).isPresent()).as("Vout 1").isTrue();
		BitcoinTransactionOutput vout1 = transaction.getOutputByIndex(0).get();
		assertThat(vout1.getValue()).as("Value").isEqualTo(expectedVout1Value);
		assertThat(vout1.getN()).as("N").isEqualTo(expectedVout1N);
		assertThat(vout1.getScriptPubKeyAsm()).as("Asm").isEqualTo(expectedVout1ScriptPubKeyAsm);
		assertThat(vout1.getScriptPubKeyHex()).as("Hex").isEqualTo(expectedVout1ScriptPubKeyHex);
		assertThat(vout1.getScriptPubKeyReqSigs()).as("reqSigs").isEqualTo(expectedVout1ScriptPubKeyReqSigs);
		assertThat(vout1.getScriptPubKeyType()).as("Type").isEqualTo(expectedVout1ScriptPubKeyType);
		assertThat(vout1.getAddresses()).as("Address").contains(expectedVout1ScriptPubKeyAddress);

		// VOut 2.
		assertThat(transaction.getOutputByIndex(1).isPresent()).as("Vout 2").isTrue();
		BitcoinTransactionOutput vout2 = transaction.getOutputByIndex(1).get();
		assertThat(vout2.getValue()).as("Value").isEqualTo(expectedVout2Value);
		assertThat(vout2.getN()).as("N").isEqualTo(expectedVout2N);
		assertThat(vout2.getScriptPubKeyAsm()).as("Asm").isEqualTo(expectedVout2ScriptPubKeyAsm);
		assertThat(vout2.getScriptPubKeyHex()).as("Hex").isEqualTo(expectedVout2ScriptPubKeyHex);
		assertThat(vout2.getScriptPubKeyReqSigs()).as("reqSigs").isEqualTo(expectedVout2ScriptPubKeyReqSigs);
		assertThat(vout2.getScriptPubKeyType()).as("Type").isEqualTo(expectedVout2ScriptPubKeyType);
		assertThat(vout2.getAddresses()).as("Address").contains(expectedVout2ScriptPubKeyAddress);
	}

	/**
	 * importBitcoinBlock test.
	 *
	 * @throws Exception if not able to suspend time.
	 */
	@Test
	public final void transactionsChainTest() throws Exception {
		// Data to test.
		final String address = "12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S";
		final BitcoinAddress bitcoinAddress = addressRepository.findByAddress(address);

        //  Transaction 0437cd7f8525ceed2324359c2d0ba26006d92d856a9c20fa0241106ee5a597c9.
        //  Coinbase                            =>  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (50)
        final String transaction1Hash = "0437cd7f8525ceed2324359c2d0ba26006d92d856a9c20fa0241106ee5a597c9";
        assertThat(getTransactionInput(transaction1Hash, 0).isCoinbase())
                .as("Transaction 1 input 1 - coinbase")
                .isTrue();
        assertThat(getTransactionOutput(transaction1Hash, 0).getBitcoinAddress().getAddress())
                .as("Transaction 1 output 1 - address")
                .isEqualTo(bitcoinAddress.getAddress());
        assertThat(getTransactionOutput(transaction1Hash, 0).getValue())
                .as("Transaction 1 output 1 - value")
                .isEqualTo(50f);

        //  Transaction f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16.
        //  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  =>  1Q2TWHE3GMdB6BZKafqwxXtWAWgFt5Jvm3  (10)
        //                                      =>  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (40)
        final String transaction2Hash = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
        // Input 1.
        assertThat(getTransactionInput(transaction2Hash, 0).isCoinbase())
                .as("Transaction 2 input 1 - not coinbase")
                .isFalse();
        assertThat(getTransactionInput(transaction2Hash, 0).getBitcoinAddress().getAddress())
                .as("Transaction 2 input 1 - bitcoin address")
                .isEqualTo(bitcoinAddress.getAddress());
        assertThat(getTransactionInput(transaction2Hash, 0).getTransactionOutput().getValue())
                .as("Transaction 2 input 1 - bitcoin address")
                .isEqualTo(50f);
        // Output 1.
        assertThat(getTransactionOutput(transaction2Hash, 0).getBitcoinAddress().getAddress())
                .as("Transaction 2 output 1 - bitcoin address")
                .isEqualTo("1Q2TWHE3GMdB6BZKafqwxXtWAWgFt5Jvm3");
        assertThat(getTransactionOutput(transaction2Hash, 0).getValue())
                .as("Transaction 2 output 1 - bitcoin address")
                .isEqualTo(10f);
        // Output 2.
        assertThat(getTransactionOutput(transaction2Hash, 1).getBitcoinAddress().getAddress())
                .as("Transaction 2 output 2 - bitcoin address")
                .isEqualTo(bitcoinAddress.getAddress());
        assertThat(getTransactionOutput(transaction2Hash, 1).getValue())
                .as("Transaction 2 output 2 - bitcoin address")
                .isEqualTo(40f);

        //  Transaction a16f3ce4dd5deb92d98ef5cf8afeaf0775ebca408f708b2146c4fb42b41e14be.
        //  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  =>  1DUDsfc23Dv9sPMEk5RsrtfzCw5ofi5sVW  (10)
        //                                      =>  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (30)
        final String transaction3Hash = "a16f3ce4dd5deb92d98ef5cf8afeaf0775ebca408f708b2146c4fb42b41e14be";
        // Input 1.
        assertThat(getTransactionInput(transaction3Hash, 0).isCoinbase())
                .as("Transaction 3 input 1 - not coinbase")
                .isFalse();
        assertThat(getTransactionInput(transaction3Hash, 0).getBitcoinAddress().getAddress())
                .as("Transaction 3 input 1 - bitcoin address")
                .isEqualTo(bitcoinAddress.getAddress());
        assertThat(getTransactionInput(transaction3Hash, 0).getTransactionOutput().getValue())
                .as("Transaction 3 input 1 - bitcoin address")
                .isEqualTo(40f);
        // Output 1.
        assertThat(getTransactionOutput(transaction3Hash, 0).getBitcoinAddress().getAddress())
                .as("Transaction 3 output 1 - bitcoin address")
                .isEqualTo("1DUDsfc23Dv9sPMEk5RsrtfzCw5ofi5sVW");
        assertThat(getTransactionOutput(transaction3Hash, 0).getValue())
                .as("Transaction 3 output 1 - bitcoin address")
                .isEqualTo(10f);
        // Output 2.
        assertThat(getTransactionOutput(transaction3Hash, 1).getBitcoinAddress().getAddress())
                .as("Transaction 3 output 2 - bitcoin address")
                .isEqualTo(bitcoinAddress.getAddress());
        assertThat(getTransactionOutput(transaction3Hash, 1).getValue())
                .as("Transaction 3 output 2 - bitcoin address")
                .isEqualTo(30f);

        //  Transaction 591e91f809d716912ca1d4a9295e70c3e78bab077683f79350f101da64588073.
        //  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (30)    =>  1LzBzVqEeuQyjD2mRWHes3dgWrT9titxvq  (1)
        //                                                  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (29)
        final String transaction4Hash = "591e91f809d716912ca1d4a9295e70c3e78bab077683f79350f101da64588073";
        // Input 1.
        assertThat(getTransactionInput(transaction4Hash, 0).isCoinbase())
                .as("Transaction 4 input 1 - not coinbase")
                .isFalse();
        assertThat(getTransactionInput(transaction4Hash, 0).getBitcoinAddress().getAddress())
                .as("Transaction 4 input 1 - bitcoin address")
                .isEqualTo(bitcoinAddress.getAddress());
        assertThat(getTransactionInput(transaction4Hash, 0).getTransactionOutput().getValue())
                .as("Transaction 4 input 1 - bitcoin address")
                .isEqualTo(30f);
        // Output 1.
        assertThat(getTransactionOutput(transaction4Hash, 0).getBitcoinAddress().getAddress())
                .as("Transaction 4 output 1 - bitcoin address")
                .isEqualTo("1LzBzVqEeuQyjD2mRWHes3dgWrT9titxvq");
        assertThat(getTransactionOutput(transaction4Hash, 0).getValue())
                .as("Transaction 4 output 1 - bitcoin address")
                .isEqualTo(1);
        // Output 2.
        assertThat(getTransactionOutput(transaction4Hash, 1).getBitcoinAddress().getAddress())
                .as("Transaction 4 output 2 - bitcoin address")
                .isEqualTo(bitcoinAddress.getAddress());
        assertThat(getTransactionOutput(transaction4Hash, 1).getValue())
                .as("Transaction 4 output 2 - bitcoin address")
                .isEqualTo(29f);

        //  Transaction 12b5633bad1f9c167d523ad1aa1947b2732a865bf5414eab2f9e5ae5d5c191ba.
        //  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (29)    =>  13HtsYzne8xVPdGDnmJX8gHgBZerAfJGEf  (1)
        //                                                  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (28)
        final String transaction5Hash = "12b5633bad1f9c167d523ad1aa1947b2732a865bf5414eab2f9e5ae5d5c191ba";
        // Input 1.
        assertThat(getTransactionInput(transaction5Hash, 0).isCoinbase())
                .as("Transaction 5 input 1 - not coinbase")
                .isFalse();
        assertThat(getTransactionInput(transaction5Hash, 0).getBitcoinAddress().getAddress())
                .as("Transaction 5 input 1 - bitcoin address")
                .isEqualTo(bitcoinAddress.getAddress());
        assertThat(getTransactionInput(transaction5Hash, 0).getTransactionOutput().getValue())
                .as("Transaction 5 input 1 - bitcoin address")
                .isEqualTo(29f);
        // Output 1.
        assertThat(getTransactionOutput(transaction5Hash, 0).getBitcoinAddress().getAddress())
                .as("Transaction 5 output 1 - bitcoin address")
                .isEqualTo("13HtsYzne8xVPdGDnmJX8gHgBZerAfJGEf");
        assertThat(getTransactionOutput(transaction5Hash, 0).getValue())
                .as("Transaction 5 output 1 - bitcoin address")
                .isEqualTo(1);
        // Output 2.
        assertThat(getTransactionOutput(transaction5Hash, 1).getBitcoinAddress().getAddress())
                .as("Transaction 5 output 2 - bitcoin address")
                .isEqualTo(bitcoinAddress.getAddress());
        assertThat(getTransactionOutput(transaction5Hash, 1).getValue())
                .as("Transaction 5 output 2 - bitcoin address")
                .isEqualTo(28f);

        //  Transaction 828ef3b079f9c23829c56fe86e85b4a69d9e06e5b54ea597eef5fb3ffef509fe.
        //  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (20)    =>  1ByLSV2gLRcuqUmfdYcpPQH8Npm8cccsFg  (10)
        //                                                  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (18)
        final String transaction6Hash = "828ef3b079f9c23829c56fe86e85b4a69d9e06e5b54ea597eef5fb3ffef509fe";
        // Input 1.
        assertThat(getTransactionInput(transaction6Hash, 0).isCoinbase())
                .as("Transaction 6 input 1 - not coinbase")
                .isFalse();
        assertThat(getTransactionInput(transaction6Hash, 0).getBitcoinAddress().getAddress())
                .as("Transaction 6 input 1 - bitcoin address")
                .isEqualTo(bitcoinAddress.getAddress());
        assertThat(getTransactionInput(transaction6Hash, 0).getTransactionOutput().getValue())
                .as("Transaction 6 input 1 - bitcoin address")
                .isEqualTo(28f);
        // Output 1.
        assertThat(getTransactionOutput(transaction6Hash, 0).getBitcoinAddress().getAddress())
                .as("Transaction 6 output 1 - bitcoin address")
                .isEqualTo("1ByLSV2gLRcuqUmfdYcpPQH8Npm8cccsFg");
        assertThat(getTransactionOutput(transaction6Hash, 0).getValue())
                .as("Transaction 6 output 1 - bitcoin address")
                .isEqualTo(10);
        // Output 2.
        assertThat(getTransactionOutput(transaction6Hash, 1).getBitcoinAddress().getAddress())
                .as("Transaction 6 output 2 - bitcoin address")
                .isEqualTo(bitcoinAddress.getAddress());
        assertThat(getTransactionOutput(transaction6Hash, 1).getValue())
                .as("Transaction 6 output 2 - bitcoin address")
                .isEqualTo(18f);
	}

    /**
     * Return a specified transaction input.
     * @param txId transaction id
     * @param index transaction input id
     * @return transaction input
     */
    private BitcoinTransactionInput getTransactionInput(final String txId, final int index) {
        final BitcoinTransaction transaction = transactionRepository.findByTxId(txId);
        int i = 0;
        for (BitcoinTransactionInput input : transaction.getInputs()) {
            if (i == index) {
                return transactionInputRepository.findOne(input.getId());
            }
            i++;
        }
        return null;
    }

    /**
     * Return a specified transaction output.
     * @param txId transaction id
     * @param index transaction input id
     * @return transaction input
     */
    private BitcoinTransactionOutput getTransactionOutput(final String txId, final int index) {
        final BitcoinTransaction transaction = transactionRepository.findByTxId(txId);
        return transactionOutputRepository.findOne(transaction.getOutputByIndex(index).get().getId());
    }

}
