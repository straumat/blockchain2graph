package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
import org.neo4j.graphdb.ConstraintViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.Future;

/**
 * Task treating addresses.
 * Created by straumat on 25/02/17.
 */
@Component
public class BitcoinImportBatchAddressesTask extends BitcoinImportBatchTask {

	/**
	 * Create all the addresses used in the vout of a transaction.
	 *
	 * @param transactionHash transaction
	 * @return address list
	 */
	@Async
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public Future<Boolean> importAddresses(final String transactionHash) {
		GetRawTransactionResponse transactionResponse = getBds().getRawTransaction(transactionHash);
		if (transactionResponse.getError() == null) {
			// Retrieving all address in all the vout of the transaction.
			final LinkedHashSet<String> addresses = new LinkedHashSet<>();
			transactionResponse.getResult()
					.getVout().stream().filter(v -> v != null)
					.forEach(v -> v.getScriptPubKey()
							.getAddresses().stream()
							.filter(a -> a != null)
							.forEach(a -> addresses.add(a)));
			// Creating all addresses.
			for (Iterator<String> it = addresses.iterator(); it.hasNext(); ) {
				String address = it.next();
				BitcoinAddress a = getBar().findByAddress(address);
				if (a == null) {
					// Address creation.
					try {
						a = new BitcoinAddress(address);
						getBar().save(a);
					} catch (ConstraintViolationException e) {
						a = getBar().findByAddress(address);
					}
					getStatus().addLog("importBlockAddresses : Address " + address + " created  with id " + a.getId());
				} else {
					getStatus().addLog("importBlockAddresses : Address " + address + " already exists with id " + a.getId());
				}
			}
			// Returns the list of addresses.
			return new AsyncResult<>(true);
		} else {
			getStatus().addError("importBlockAddresses : Impossible to retrieve transaction " + transactionHash + " : " + transactionResponse.getError());
			return new AsyncResult<>(false);
		}
	}

}
