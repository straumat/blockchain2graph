package com.oakinvest.b2g.util;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResult;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Mapper from bitcoind to domain.
 * Created by straumat on 09/09/16.
 */
@Mapper
public interface BitcoindToDomainMapper {

	/**
	 * Mapper instance
	 */
	BitcoindToDomainMapper INSTANCE = Mappers.getMapper(BitcoindToDomainMapper.class);

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
			@Mapping(source = "merkleroot", target = "merkleroot"),
			@Mapping(source = "time", target = "time"),
			@Mapping(source = "mediantime", target = "mediantime"),
			@Mapping(source = "nonce", target = "nonce"),
			@Mapping(source = "bits", target = "bits"),
			@Mapping(source = "difficulty", target = "difficulty"),
			@Mapping(source = "chainwork", target = "chainwork"),
			@Mapping(source = "previousblockhash", target = "previousblockhash"),
			@Mapping(source = "nextblockhash", target = "nextblockhash")
	})
	BitcoinBlock blockResultToBitcoinBlock(GetBlockResult gbr);

	/**
	 * Maps a raw transaction to a transaction.
	 *
	 * @param grtr getRawTransactionResult resumt
	 * @return domain transaction
	 */
	@Mappings({
			@Mapping(source = "hex", target = "hex"),
			@Mapping(source = "txid", target = "txid"),
			@Mapping(source = "hash", target = "hash"),
			@Mapping(source = "size", target = "size"),
			@Mapping(source = "vsize", target = "vSize"),
			@Mapping(source = "version", target = "version"),
			@Mapping(source = "locktime", target = "lockTime"),
			@Mapping(source = "blockhash", target = "blockHash"),
			@Mapping(source = "time", target = "time"),
			@Mapping(source = "blocktime", target = "blockTime")

	})
	BitcoinTransaction rawTransactionResultToBitcoinTransaction(GetRawTransactionResult grtr);

}
