package com.oakinvest.b2g.util.bitcoin.mapper;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblock.GetBlockResult;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.vin.GetRawTransactionVIn;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.vout.GetRawTransactionVOut;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper from bitcoind to domain.
 * Created by straumat on 09/09/16.
 */
@Mapper(uses = { BitcoindToDomainMapperPostProcessor.class })
public interface BitcoindToDomainMapper {

	/**
	 * Maps block result to block.
	 *
	 * @param gbr getblock result
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
	BitcoinBlock blockResultToBitcoinBlock(GetBlockResult gbr);

	/**
	 * Maps a raw transaction to a transaction.
	 *
	 * @param grtr getRawTransactionResult result
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
	BitcoinTransaction rawTransactionResultToBitcoinTransaction(GetRawTransactionResult grtr);

	/**
	 * maprs a vin.
	 *
	 * @param grtvin vin
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
	BitcoinTransactionInput rawTransactionVIn(GetRawTransactionVIn grtvin);

	/**
	 * Maps a vout.
	 *
	 * @param grtvout vout
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
	BitcoinTransactionOutput rawTransactionVout(GetRawTransactionVOut grtvout);

	/**
	 * Maps a string to a bitcoin address.
	 *
	 * @param address address in string
	 * @return bitcoin address
	 */
	@Mappings({
			@Mapping(source = "address", target = "address")
	})
	BitcoinAddress toBitcoinAddress(String address);

}
