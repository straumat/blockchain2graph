package com.oakinvest.b2g.bitcoin.test.batch;

import com.oakinvest.b2g.bitcoin.test.util.junit.BaseTest;
import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutputType;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

/**
 * Tests for bitcoin blockchain import.
 * Created by straumat on 04/09/16.
 */
public class BitcoinImportTest extends BaseTest {

    /**
     * Number of blocs to import.
     */
    private static final int NUMBERS_OF_BLOCK_TO_IMPORT = 600;

    /**
     * importBlock() test.
     */
    @Test
    public final void blocksDataTeist() {
        // Reset the database.
        getSessionFactory().openSession().purgeDatabase();

        // Reset errors.
        getBitcoindMock().resetErrors();

        // Launch block importation.
        int iterations = 0;
        final int maxIterations = 1000;
        while (getBitcoinBlockRepository().count() < NUMBERS_OF_BLOCK_TO_IMPORT) {
            try {
                getBatchBlocks().execute();
                iterations++;
                if (iterations >= maxIterations) {
                    fail("Persistent problem to get blocks");
                }
            } catch (Exception e) {
                fail("Error while importing : " + e.getMessage());
            }
        }

        // Expected values.
        final String expectedHash = "00000000d1145790a8694403d4063f323d499e655c83426834d4ce2f8dd4a2ee";
        final int expectedHeight = 170;
        final int expectedSize = 490;
        final int expectedVersion = 1;
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
        Optional<BitcoinBlock> b = getBitcoinBlockRepository().findByHash(expectedHash);
        assertThat(b.isPresent()).as("Block").isTrue();
        assertThat(b.get().getHash()).as("Hash").isEqualTo(expectedHash);
        assertThat(b.get().getHeight()).as("Height").isEqualTo(expectedHeight);
        assertThat(b.get().getSize()).as("Size").isEqualTo(expectedSize);
        assertThat(b.get().getVersion()).as("Version").isEqualTo(expectedVersion);
        assertThat(b.get().getMerkleRoot()).as("Merkel root").isEqualTo(expectedMerkleroot);
        assertThat(b.get().getTime()).as("Time").isEqualTo(expectedTime);
        assertThat(b.get().getMedianTime()).as("Median time").isEqualTo(expectedMedianTime);
        assertThat(b.get().getNonce()).as("Nonce").isEqualTo(expectedNonce);
        assertThat(b.get().getDifficulty()).as("Difficulty").isEqualTo(expectedDifficulty);
        assertThat(b.get().getBits()).as("Bits").isEqualTo(expectedBits);
        assertThat(b.get().getChainWork()).as("Chain work").isEqualTo(expectedChainwork);
        assertThat(b.get().getPreviousBlockHash()).as("Previous block hash").isEqualTo(expectedPreviousblockhash);
        assertThat(b.get().getPreviousBlock()).as("Previous block").isNotNull();
        assertThat(b.get().getNextBlockHash()).as("Next block hash").isEqualTo(expectedNextblockhash);
        assertThat(b.get().getNextBlock()).as("Next block").isNotNull();
        assertThat(b.get().getTx()).as("Transaction size").hasSize(expectedTxSize);

        assertThat(b.get().getTransactions()).as("Block transactions").hasSize(expectedTxSize);

        // Test relations between blocks (previous block & next block).
        assertThat(getBitcoinBlockRepository().findByHeight(1).get())
                .as("Previous & next block")
                .extracting("previousBlock", "nextBlock.height")
                .contains(null, 2);

        assertThat(getBitcoinBlockRepository().findByHeight(6).get())
                .as("Previous & next block")
                .extracting("previousBlock.height", "nextBlock.height")
                .contains(5, 7);

        assertThat(getBitcoinBlockRepository().findByHeight(NUMBERS_OF_BLOCK_TO_IMPORT).get())
                .as("Previous & next block")
                .extracting("nextBlock", "previousBlock.height")
                .contains(null, NUMBERS_OF_BLOCK_TO_IMPORT - 1);

        // Testing that the address of block 500 is imported and that non existing address does not.
        final String existingAddress = "1C1ENNWdkPMyhZ7xTEM4Kwq1FTUifZNCRd";
        final String nonExistingAddress = "TOTO";

        // Existing address.
        assertThat(getAddressRepository().findByAddress(existingAddress).get())
                .as("Address exists")
                .isNotNull()
                .as("Address value")
                .extracting("address")
                .contains(existingAddress);

        // Non existing address.
        assertThat(getAddressRepository().findByAddress(nonExistingAddress).isPresent())
                .as("Address does not exists")
                .isFalse();

        // Expected value for the transaction of the block 170.
        // Transaction.
        final String transactionHash = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
        final String expectedTransactionHex = "0100000001c997a5e56e104102fa209c6a852dd90660a20b2d9c352423edce25857fcd3704000000004847304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901ffffffff0200ca9a3b00000000434104ae1a62fe09c5f51b13905f07f06b99a2f7159b2225f374cd378d71302fa28414e7aab37397f554a7df5f142c21c1b7303b8a0626f1baded5c72a704f7e6cd84cac00286bee0000000043410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac00000000";
        final String expectedTransactionTxID = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
        final String expectedTransactionHash = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
        final int expectedTransactionSize = 275;
        final int expectedTransactionVSize = 275;
        final int expectedTransactionVersion = 1;
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
        final int expectedVout1ScriptPubKeyReqSigs = 1;
        final BitcoinTransactionOutputType expectedVout1ScriptPubKeyType = BitcoinTransactionOutputType.pubkey;
        final String expectedVout1ScriptPubKeyAddress = "1Q2TWHE3GMdB6BZKafqwxXtWAWgFt5Jvm3";
        // Vout 2.
        final float expectedVout2Value = 40;
        final int expectedVout2N = 1;
        final String expectedVout2ScriptPubKeyAsm = "0411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3 OP_CHECKSIG";
        final String expectedVout2ScriptPubKeyHex = "410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac";
        final int expectedVout2ScriptPubKeyReqSigs = 1;
        final BitcoinTransactionOutputType expectedVout2ScriptPubKeyType = BitcoinTransactionOutputType.pubkey;
        final String expectedVout2ScriptPubKeyAddress = "12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S";

        // Test.
        // Transaction.
        Optional<BitcoinTransaction> transaction = getTransactionRepository().findByTxId(transactionHash);
        assertThat(transaction.isPresent()).as("Transaction").isTrue();
        assertThat(transaction.get().getTxId()).as("Txid").isEqualTo(expectedTransactionTxID);
        assertThat(transaction.get().getHex()).as("Hex").isEqualTo(expectedTransactionHex);
        assertThat(transaction.get().getHash()).as("Hash").isEqualTo(expectedTransactionHash);
        assertThat(transaction.get().getSize()).as("Size").isEqualTo(expectedTransactionSize);
        assertThat(transaction.get().getvSize()).as("Vsize").isEqualTo(expectedTransactionVSize);
        assertThat(transaction.get().getVersion()).as("Version").isEqualTo(expectedTransactionVersion);
        assertThat(transaction.get().getLockTime()).as("Lock time").isEqualTo(expectedTransactionLockTime);
        assertThat(transaction.get().getBlockHash()).as("Hash").isEqualTo(expectedBlockHash);
        assertThat(transaction.get().getTime()).as("Time").isEqualTo(expectedTransactionTime);
        assertThat(transaction.get().getBlockTime()).as("Block time").isEqualTo(expectedTransactionBlockTime);

        // Vin 1.
        BitcoinTransactionInput vin1 = transaction.get().getInputs().iterator().next();
        assertThat(vin1.getTxId()).as("Txid").isEqualTo(expectedVin1Txid);
        assertThat(vin1.getvOut()).as("Vout").isEqualTo(expectedVin1Vout);
        assertThat(vin1.getScriptSigAsm()).as("Asm").isEqualTo(expectedVin1ScriptSigAsm);
        assertThat(vin1.getScriptSigHex()).as("Hex").isEqualTo(expectedVin1ScriptSigHex);
        assertThat(vin1.getSequence()).as("Sequence").isEqualTo(expectedVin1Sequence);
        assertThat(vin1.getCoinbase()).as("Coinbase").isNull();
        assertThat(vin1.isCoinbase()).as("Is coinbase").isFalse();

        // VOut 1.
        assertThat(transaction.get().getOutputByIndex(0).isPresent()).as("Vout 1").isTrue();
        BitcoinTransactionOutput vout1 = transaction.get().getOutputByIndex(0).get();
        assertThat(vout1.getValue()).as("Value").isEqualTo(expectedVout1Value);
        assertThat(vout1.getN()).as("N").isEqualTo(expectedVout1N);
        assertThat(vout1.getScriptPubKeyAsm()).as("Asm").isEqualTo(expectedVout1ScriptPubKeyAsm);
        assertThat(vout1.getScriptPubKeyHex()).as("Hex").isEqualTo(expectedVout1ScriptPubKeyHex);
        assertThat(vout1.getScriptPubKeyReqSigs()).as("reqSigs").isEqualTo(expectedVout1ScriptPubKeyReqSigs);
        assertThat(vout1.getScriptPubKeyType()).as("Type").isEqualTo(expectedVout1ScriptPubKeyType);
        assertThat(vout1.getAddresses()).as("Address").contains(expectedVout1ScriptPubKeyAddress);

        // VOut 2.
        assertThat(transaction.get().getOutputByIndex(1).isPresent()).as("Vout 2").isTrue();
        BitcoinTransactionOutput vout2 = transaction.get().getOutputByIndex(1).get();
        assertThat(vout2.getValue()).as("Value").isEqualTo(expectedVout2Value);
        assertThat(vout2.getN()).as("N").isEqualTo(expectedVout2N);
        assertThat(vout2.getScriptPubKeyAsm()).as("Asm").isEqualTo(expectedVout2ScriptPubKeyAsm);
        assertThat(vout2.getScriptPubKeyHex()).as("Hex").isEqualTo(expectedVout2ScriptPubKeyHex);
        assertThat(vout2.getScriptPubKeyReqSigs()).as("reqSigs").isEqualTo(expectedVout2ScriptPubKeyReqSigs);
        assertThat(vout2.getScriptPubKeyType()).as("Type").isEqualTo(expectedVout2ScriptPubKeyType);
        assertThat(vout2.getAddresses()).as("Address").contains(expectedVout2ScriptPubKeyAddress);

        // Data to test.
        final String address = "12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S";
        final Optional<BitcoinAddress> bitcoinAddress = getAddressRepository().findByAddress(address);
        assertThat(bitcoinAddress.isPresent()).as("Bitcoin address").isTrue();
        Optional<BitcoinTransactionInput> bti1;
        Optional<BitcoinTransactionOutput> bto1;
        Optional<BitcoinTransactionOutput> bto2;

        // -------------------------------------------------------------------------------------------------------------
        // Transaction 0437cd7f8525ceed2324359c2d0ba26006d92d856a9c20fa0241106ee5a597c9.
        // Coinbase                            =>  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (50)
        final String transaction1Hash = "0437cd7f8525ceed2324359c2d0ba26006d92d856a9c20fa0241106ee5a597c9";
        // Input 1.
        bti1 = getTransactionInput(transaction1Hash, 0);
        if (bti1.isPresent()) {
            assertThat(bti1.get().isCoinbase())
                    .as("Transaction 1 input 1 - coinbase")
                    .isTrue();
        } else {
            fail(transaction1Hash + " input 1 not found");
        }
        // Output 1.
        bto1 = getTransactionOutput(transaction1Hash, 0);
        if (bto1.isPresent()) {
            assertThat(bto1.get().getBitcoinAddress().getAddress())
                    .as("Transaction 1 output 1 - address")
                    .isEqualTo(bitcoinAddress.get().getAddress());
            assertThat(bto1.get().getValue())
                    .as("Transaction 1 output 1 - value")
                    .isEqualTo(50f);
        } else {
            fail(transaction1Hash + " output 1 not found");
        }

        //  Transaction f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16.
        //  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  =>  1Q2TWHE3GMdB6BZKafqwxXtWAWgFt5Jvm3  (10)
        //                                      =>  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (40)
        final String transaction2Hash = "f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16";
        // Input 1.
        bti1 = getTransactionInput(transaction2Hash, 0);
        if (bti1.isPresent()) {
            assertThat(bti1.get().isCoinbase())
                    .as("Transaction 2 input 1 - not coinbase")
                    .isFalse();
            assertThat(bti1.get().getBitcoinAddress().getAddress())
                    .as("Transaction 2 input 1 - bitcoin address")
                    .isEqualTo(bitcoinAddress.get().getAddress());
            assertThat(bti1.get().getTransactionOutput().getValue())
                    .as("Transaction 2 input 1 - bitcoin address")
                    .isEqualTo(50f);
        } else {
            fail(transaction2Hash + " input 1 not found");
        }
        // Output 1.
        bto1 = getTransactionOutput(transaction2Hash, 0);
        if (bto1.isPresent()) {
            assertThat(bto1.get().getBitcoinAddress().getAddress())
                    .as("Transaction 2 output 1 - bitcoin address")
                    .isEqualTo("1Q2TWHE3GMdB6BZKafqwxXtWAWgFt5Jvm3");
            assertThat(bto1.get().getValue())
                    .as("Transaction 2 output 1 - bitcoin address")
                    .isEqualTo(10f);
        } else {
            fail(transaction2Hash + " output 1 not found");
        }
        // Output 2.
        bto2 = getTransactionOutput(transaction2Hash, 1);
        if (bto2.isPresent()) {
            assertThat(bto2.get().getBitcoinAddress().getAddress())
                    .as("Transaction 2 output 2 - bitcoin address")
                    .isEqualTo(bitcoinAddress.get().getAddress());
            assertThat(bto2.get().getValue())
                    .as("Transaction 2 output 2 - bitcoin address")
                    .isEqualTo(40f);
        } else {
            fail(transaction2Hash + " output 2 not found");
        }

        //  Transaction a16f3ce4dd5deb92d98ef5cf8afeaf0775ebca408f708b2146c4fb42b41e14be.
        //  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  =>  1DUDsfc23Dv9sPMEk5RsrtfzCw5ofi5sVW  (10)
        //                                      =>  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (30)
        final String transaction3Hash = "a16f3ce4dd5deb92d98ef5cf8afeaf0775ebca408f708b2146c4fb42b41e14be";
        // Input 1.
        bti1 = getTransactionInput(transaction3Hash, 0);
        if (bti1.isPresent()) {
            assertThat(bti1.get().isCoinbase())
                    .as("Transaction 3 input 1 - not coinbase")
                    .isFalse();
            assertThat(bti1.get().getBitcoinAddress().getAddress())
                    .as("Transaction 3 input 1 - bitcoin address")
                    .isEqualTo(bitcoinAddress.get().getAddress());
            assertThat(bti1.get().getTransactionOutput().getValue())
                    .as("Transaction 3 input 1 - bitcoin address")
                    .isEqualTo(40f);
        } else {
            fail(transaction3Hash + " input 1 not found");
        }
        // Output 1.
        bto1 = getTransactionOutput(transaction3Hash, 0);
        if (bto1.isPresent()) {
            assertThat(bto1.get().getBitcoinAddress().getAddress())
                    .as("Transaction 3 output 1 - bitcoin address")
                    .isEqualTo("1DUDsfc23Dv9sPMEk5RsrtfzCw5ofi5sVW");
            assertThat(bto1.get().getValue())
                    .as("Transaction 3 output 1 - bitcoin address")
                    .isEqualTo(10f);
        } else {
            fail(transaction3Hash + " output 1 not found");
        }
        // Output 2.
        bto2 = getTransactionOutput(transaction3Hash, 1);
        if (bto2.isPresent()) {
            assertThat(bto2.get().getBitcoinAddress().getAddress())
                    .as("Transaction 3 output 2 - bitcoin address")
                    .isEqualTo(bitcoinAddress.get().getAddress());
            assertThat(bto2.get().getValue())
                    .as("Transaction 3 output 2 - bitcoin address")
                    .isEqualTo(30f);
        } else {
            fail(transaction3Hash + " output 2 not found");
        }

        //  Transaction 591e91f809d716912ca1d4a9295e70c3e78bab077683f79350f101da64588073.
        //  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (30)    =>  1LzBzVqEeuQyjD2mRWHes3dgWrT9titxvq  (1)
        //                                                  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (29)
        final String transaction4Hash = "591e91f809d716912ca1d4a9295e70c3e78bab077683f79350f101da64588073";
        // Input 1.
        bti1 = getTransactionInput(transaction4Hash, 0);
        if (bti1.isPresent()) {
            assertThat(bti1.get().isCoinbase())
                    .as("Transaction 4 input 1 - not coinbase")
                    .isFalse();
            assertThat(bti1.get().getBitcoinAddress().getAddress())
                    .as("Transaction 4 input 1 - bitcoin address")
                    .isEqualTo(bitcoinAddress.get().getAddress());
            assertThat(bti1.get().getTransactionOutput().getValue())
                    .as("Transaction 4 input 1 - bitcoin address")
                    .isEqualTo(30f);
        } else {
            fail(transaction4Hash + " input 1 not found");
        }
        // Output 1.
        bto1 = getTransactionOutput(transaction4Hash, 0);
        if (bto1.isPresent()) {
            assertThat(bto1.get().getBitcoinAddress().getAddress())
                    .as("Transaction 4 output 1 - bitcoin address")
                    .isEqualTo("1LzBzVqEeuQyjD2mRWHes3dgWrT9titxvq");
            assertThat(bto1.get().getValue())
                    .as("Transaction 4 output 1 - bitcoin address")
                    .isEqualTo(1);
        } else {
            fail(transaction4Hash + " output 1 not found");
        }
        // Output 2.
        bto2 = getTransactionOutput(transaction4Hash, 1);
        if (bto2.isPresent()) {
            assertThat(bto2.get().getBitcoinAddress().getAddress())
                    .as("Transaction 4 output 2 - bitcoin address")
                    .isEqualTo(bitcoinAddress.get().getAddress());
            assertThat(bto2.get().getValue())
                    .as("Transaction 4 output 2 - bitcoin address")
                    .isEqualTo(29f);
        } else {
            fail(transaction4Hash + " output 2 not found");
        }

        //  Transaction 12b5633bad1f9c167d523ad1aa1947b2732a865bf5414eab2f9e5ae5d5c191ba.
        //  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (29)    =>  13HtsYzne8xVPdGDnmJX8gHgBZerAfJGEf  (1)
        //                                                  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (28)
        final String transaction5Hash = "12b5633bad1f9c167d523ad1aa1947b2732a865bf5414eab2f9e5ae5d5c191ba";
        // Input 1.
        bti1 = getTransactionInput(transaction5Hash, 0);
        if (bti1.isPresent()) {
            assertThat(bti1.get().isCoinbase())
                    .as("Transaction 5 input 1 - not coinbase")
                    .isFalse();
            assertThat(bti1.get().getBitcoinAddress().getAddress())
                    .as("Transaction 5 input 1 - bitcoin address")
                    .isEqualTo(bitcoinAddress.get().getAddress());
            assertThat(bti1.get().getTransactionOutput().getValue())
                    .as("Transaction 5 input 1 - bitcoin address")
                    .isEqualTo(29f);
        } else {
            fail(transaction4Hash + " input 1 not found");
        }
        // Output 1.
        bto1 = getTransactionOutput(transaction5Hash, 0);
        if (bto1.isPresent()) {
            assertThat(bto1.get().getBitcoinAddress().getAddress())
                    .as("Transaction 5 output 1 - bitcoin address")
                    .isEqualTo("13HtsYzne8xVPdGDnmJX8gHgBZerAfJGEf");
            assertThat(bto1.get().getValue())
                    .as("Transaction 5 output 1 - bitcoin address")
                    .isEqualTo(1);
        } else {
            fail(transaction5Hash + " output 1 not found");
        }
        // Output 2.
        bto2 = getTransactionOutput(transaction5Hash, 1);
        if (bto2.isPresent()) {
            assertThat(bto2.get().getBitcoinAddress().getAddress())
                    .as("Transaction 5 output 2 - bitcoin address")
                    .isEqualTo(bitcoinAddress.get().getAddress());
            assertThat(bto2.get().getValue())
                    .as("Transaction 5 output 2 - bitcoin address")
                    .isEqualTo(28f);
        } else {
            fail(transaction5Hash + " output 2 not found");
        }

        //  Transaction 828ef3b079f9c23829c56fe86e85b4a69d9e06e5b54ea597eef5fb3ffef509fe.
        //  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (20)    =>  1ByLSV2gLRcuqUmfdYcpPQH8Npm8cccsFg  (10)
        //                                                  12cbQLTFMXRnSzktFkuoG3eHoMeFtpTu3S  (18)
        final String transaction6Hash = "828ef3b079f9c23829c56fe86e85b4a69d9e06e5b54ea597eef5fb3ffef509fe";
        // Input 1.
        bti1 = getTransactionInput(transaction6Hash, 0);
        if (bti1.isPresent()) {
            assertThat(bti1.get().isCoinbase())
                    .as("Transaction 6 input 1 - not coinbase")
                    .isFalse();
            assertThat(bti1.get().getBitcoinAddress().getAddress())
                    .as("Transaction 6 input 1 - bitcoin address")
                    .isEqualTo(bitcoinAddress.get().getAddress());
            assertThat(bti1.get().getTransactionOutput().getValue())
                    .as("Transaction 6 input 1 - bitcoin address")
                    .isEqualTo(28f);
        } else {
            fail(transaction6Hash + " input 1 not found");
        }
        // Output 1.
        bto1 = getTransactionOutput(transaction6Hash, 0);
        if (bto1.isPresent()) {
            assertThat(bto1.get().getBitcoinAddress().getAddress())
                    .as("Transaction 6 output 1 - bitcoin address")
                    .isEqualTo("1ByLSV2gLRcuqUmfdYcpPQH8Npm8cccsFg");
            assertThat(bto1.get().getValue())
                    .as("Transaction 6 output 1 - bitcoin address")
                    .isEqualTo(10);
        } else {
            fail(transaction6Hash + " output 1 not found");
        }
        // Output 2.
        bto2 = getTransactionOutput(transaction6Hash, 1);
        if (bto2.isPresent()) {
            assertThat(bto2.get().getBitcoinAddress().getAddress())
                    .as("Transaction 6 output 2 - bitcoin address")
                    .isEqualTo(bitcoinAddress.get().getAddress());
            assertThat(bto2.get().getValue())
                    .as("Transaction 6 output 2 - bitcoin address")
                    .isEqualTo(18f);
        } else {
            fail(transaction6Hash + " output 2 not found");
        }
    }

    /**
     * Return a specified transaction input.
     *
     * @param txId  transaction id
     * @param index transaction input id
     * @return transaction input
     */
    private Optional<BitcoinTransactionInput> getTransactionInput(final String txId, final int index) {
        final Optional<BitcoinTransaction> transaction = getTransactionRepository().findByTxId(txId);
        int i = 0;
        if (transaction.isPresent()) {
            for (BitcoinTransactionInput input : transaction.get().getInputs()) {
                if (i == index) {
                    return getTransactionInputRepository().findById(input.getId());
                }
                i++;
            }
        }
        return Optional.empty();
    }

    /**
     * Return a specified transaction output.
     *
     * @param txId  transaction id
     * @param index transaction input id
     * @return transaction input
     */
    private Optional<BitcoinTransactionOutput> getTransactionOutput(final String txId, final int index) {
        return getTransactionOutputRepository().findByTxIdAndN(txId, index);
    }

}
