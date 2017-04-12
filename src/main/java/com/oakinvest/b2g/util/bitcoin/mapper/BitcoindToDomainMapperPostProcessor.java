package com.oakinvest.b2g.util.bitcoin.mapper;

import com.oakinvest.b2g.domain.bitcoin.BitcoinTransaction;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionInput;
import com.oakinvest.b2g.domain.bitcoin.BitcoinTransactionOutput;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Manage the link between transaction and vin / vout.
 * Created by straumat on 08/04/17.
 */
@Mapper
public abstract class BitcoindToDomainMapperPostProcessor {

	/**
	 * Link vin & vout to transaction.
	 *
	 * @param grtr        GetRawTransactionResult
	 * @param transaction BitcoinTransaction
	 */
	@AfterMapping
	@SuppressWarnings({ "checkstyle:designforextension" })
	protected void linkVinsAndVoutsToTransaction(final GetRawTransactionResult grtr, @MappingTarget final BitcoinTransaction transaction) {
		//System.out.println("===>");
		// For each Vin.
		for (BitcoinTransactionInput vin : transaction.getInputs()) {
			vin.setTransaction(transaction);
		}

		// For each Vout.
		for (BitcoinTransactionOutput vout : transaction.getOutputs()) {
			vout.setTransaction(transaction);
		}
	}

}
