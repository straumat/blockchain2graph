package com.oakinvest.b2g.bitcoin.util.mapper;

import com.oakinvest.b2g.bitcoin.domain.BitcoinBlock;
import com.oakinvest.b2g.bitcoin.dto.bitcoin.core.BitcoinCoreBlockData;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Post mapper.
 *
 * Created by straumat on 29/05/17.
 */
@SuppressWarnings("WeakerAccess")
@Mapper
public abstract class BitcoinCoreToDomainPostMapper {

    /**
     * After mapping for bitcoin block.
     *
     * @param bitcoinCoreBlockData block data from core
     * @param bitcoinBlock      bitcoin block
     */
    @AfterMapping
    @SuppressWarnings({"checkstyle:designforextension", "unused"})
    protected void blockDataToBitcoinBlockAfterMapping(final BitcoinCoreBlockData bitcoinCoreBlockData, @MappingTarget final BitcoinBlock bitcoinBlock) {
        bitcoinBlock.getTransactions().forEach(t -> t.getOutputs().forEach(o -> o.setTxId(t.getTxId())));
    }

}
