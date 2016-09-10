package com.oakinvest.b2g.util;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.dto.external.bitcoind.getblock.GetBlockResult;
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
	BitcoinBlock BlockResultToBitcoinBlock(GetBlockResult gbr);

}
