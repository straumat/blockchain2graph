package com.oakinvest.b2g.bitcoin.util.mapper;

import com.oakinvest.b2g.bitcoin.domain.BitcoinAddress;
import com.oakinvest.b2g.bitcoin.domain.BitcoinBlock;
import com.oakinvest.b2g.bitcoin.domain.BitcoinTransaction;
import com.oakinvest.b2g.bitcoin.domain.BitcoinTransactionInput;
import com.oakinvest.b2g.bitcoin.domain.BitcoinTransactionOutput;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.BitcoinCoreBlockData;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getblock.GetBlockResult;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getrawtransaction.vin.GetRawTransactionVIn;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.getrawtransaction.vout.GetRawTransactionVOut;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper from core to domain.
 * Created by straumat on 09/09/16.
 */
@SuppressWarnings("unused")
@Mapper(uses = BitcoinCoreToDomainPostMapper.class)
public interface BitcoinCoreToDomainMapper {

    /**
     * Maps block data to block.
     *log.debug(
     * @param bitcoinCoreBlockData core block data
     * @return domain block
     */
    @Mappings({
            @Mapping(source = "block.hash", target = "hash"),
            @Mapping(source = "block.size", target = "size"),
            @Mapping(source = "block.height", target = "height"),
            @Mapping(source = "block.version", target = "version"),
            @Mapping(source = "block.merkleroot", target = "merkleRoot"),
            @Mapping(source = "block.time", target = "time"),
            @Mapping(source = "block.mediantime", target = "medianTime"),
            @Mapping(source = "block.nonce", target = "nonce"),
            @Mapping(source = "block.bits", target = "bits"),
            @Mapping(source = "block.tx", target = "tx"),
            @Mapping(source = "block.difficulty", target = "difficulty"),
            @Mapping(source = "block.chainwork", target = "chainWork"),
            @Mapping(source = "block.previousblockhash", target = "previousBlockHash"),
            @Mapping(source = "block.nextblockhash", target = "nextBlockHash"),
            @Mapping(source = "transactions", target = "transactions")
    })
    BitcoinBlock blockDataToBitcoinBlock(BitcoinCoreBlockData bitcoinCoreBlockData);

	/**
	 * Maps block result to block.
	 *
	 * @param getBlockResult getblock result
	 * @return domain block
	 */
	@Mappings({
			@Mapping(source = "hash", target = "hash"),
			@Mapping(source = "size", target = "size"),
			@Mapping(source = "height", target = "height"),
			@Mapping(source = "version", target = "version"),
			@Mapping(source = "merkleroot", target = "merkleRoot"),
			@Mapping(source = "time", target = "time"),
			@Mapping(source = "mediantime", target = "medianTime"),
			@Mapping(source = "nonce", target = "nonce"),
			@Mapping(source = "bits", target = "bits"),
			@Mapping(source = "tx", target = "tx"),
			@Mapping(source = "difficulty", target = "difficulty"),
			@Mapping(source = "chainwork", target = "chainWork"),
			@Mapping(source = "previousblockhash", target = "previousBlockHash"),
			@Mapping(source = "nextblockhash", target = "nextBlockHash")
	})
	BitcoinBlock blockResultToBitcoinBlock(GetBlockResult getBlockResult);

	/**
	 * Maps a raw transaction to a transaction.
	 *
	 * @param getRawTransactionResult getRawTransactionResult result
	 * @return domain transaction
	 */
	@Mappings({
			@Mapping(source = "hex", target = "hex"),
			@Mapping(source = "txid", target = "txId"),
			@Mapping(source = "hash", target = "hash"),
			@Mapping(source = "size", target = "size"),
			@Mapping(source = "vsize", target = "vSize"),
			@Mapping(source = "version", target = "version"),
			@Mapping(source = "locktime", target = "lockTime"),
			@Mapping(source = "blockhash", target = "blockHash"),
			@Mapping(source = "time", target = "time"),
			@Mapping(source = "blocktime", target = "blockTime"),
			@Mapping(source = "vin", target = "inputs"),
			@Mapping(source = "vout", target = "outputs")
	})
	BitcoinTransaction rawTransactionResultToBitcoinTransaction(GetRawTransactionResult getRawTransactionResult);

	/**
	 * maps a vin.
	 *
	 * @param getRawTransactionVIn vin
	 * @return domain transaction input
	 */
	@Mappings({
			@Mapping(source = "txid", target = "txId"),
			@Mapping(source = "coinbase", target = "coinbase"),
			@Mapping(source = "vout", target = "vOut"),
			@Mapping(source = "scriptSig.asm", target = "scriptSigAsm"),
			@Mapping(source = "scriptSig.hex", target = "scriptSigHex"),
			@Mapping(source = "sequence", target = "sequence")
	})
	BitcoinTransactionInput rawTransactionVIn(GetRawTransactionVIn getRawTransactionVIn);

	/**
	 * Maps a vout.
	 *
	 * @param getRawTransactionVOut vout
	 * @return domain transaction output
	 */
	@Mappings({
			@Mapping(source = "value", target = "value"),
			@Mapping(source = "n", target = "n"),
			@Mapping(source = "scriptPubKey.asm", target = "scriptPubKeyAsm"),
			@Mapping(source = "scriptPubKey.hex", target = "scriptPubKeyHex"),
			@Mapping(source = "scriptPubKey.reqSigs", target = "scriptPubKeyReqSigs"),
			@Mapping(source = "scriptPubKey.type", target = "scriptPubKeyType"),
			@Mapping(source = "scriptPubKey.addresses", target = "addresses")
	})
	BitcoinTransactionOutput rawTransactionVout(GetRawTransactionVOut getRawTransactionVOut);

	/**
	 * Maps a string to a bitcoin address.
	 *
	 * @param address address in string
	 * @return bitcoin address
	 */
	@Mappings({
			@Mapping(source = "address", target = "address")
	})
	BitcoinAddress addressToBitcoinAddress(String address);

}
