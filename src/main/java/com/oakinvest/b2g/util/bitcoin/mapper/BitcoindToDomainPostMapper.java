package com.oakinvest.b2g.util.bitcoin.mapper;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.BitcoindBlockData;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Post mapper.
 * Created by straumat on 29/05/17.
 */
@Mapper
public abstract class BitcoindToDomainPostMapper {

    /**
     * After mapping for bitcoin block.
     * @param bitcoindBlockData block data from bitcoind
     * @param bitcoinBlock bitcoin block
     */
    @AfterMapping
    @SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
    protected final void blockDataToBitcoinBlockAfterMapping(final BitcoindBlockData bitcoindBlockData, @MappingTarget final BitcoinBlock bitcoinBlock) {
        bitcoinBlock.getTransactions().forEach(t -> t.setBlock(bitcoinBlock));
    }

}
