package com.oakinvest.b2g.util.bitcoin.mapper;

import com.oakinvest.b2g.domain.bitcoin.BitcoinBlock;
import com.oakinvest.b2g.dto.bitcoin.core.BitcoindBlockData;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Post mapper.
 * <p>
 * Created by straumat on 29/05/17.
 */
@SuppressWarnings("WeakerAccess")
@Mapper
public abstract class BitcoindToDomainPostMapper {

    /**
     * After mapping for bitcoin block.
     *
     * @param bitcoindBlockData block data from core
     * @param bitcoinBlock      bitcoin block
     */
    @AfterMapping
    @SuppressWarnings({"checkstyle:designforextension", "unused"})
    protected void blockDataToBitcoinBlockAfterMapping(final BitcoindBlockData bitcoindBlockData, @MappingTarget final BitcoinBlock bitcoinBlock) {
        bitcoinBlock.getTransactions().forEach(t -> t.getOutputs().forEach(o -> o.setTxId(t.getTxId())));
    }

}
