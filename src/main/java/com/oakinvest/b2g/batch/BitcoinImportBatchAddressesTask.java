package com.oakinvest.b2g.batch;

import com.oakinvest.b2g.domain.bitcoin.BitcoinAddress;
import com.oakinvest.b2g.dto.external.bitcoind.getrawtransaction.GetRawTransactionResponse;
import com.oakinvest.b2g.repository.bitcoin.BitcoinAddressRepository;
import com.oakinvest.b2g.repository.bitcoin.BitcoinTransactionRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import org.neo4j.graphdb.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.Future;

/**
 * Task treating addresses.
 * Created by straumat on 25/02/17.
 */
@Component
public class BitcoinImportBatchAddressesTask {

	/**
	 * Logger.
	 */
	private Logger log = LoggerFactory.getLogger(BitcoinImportBatchAddressesTask.class);

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService status;

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private BitcoindService bds;

	/**
	 * Bitcoin transaction repository.
	 */
	@Autowired
	private BitcoinTransactionRepository btr;

	/**
	 * Bitcoin block repository.
	 */
	@Autowired
	private BitcoinAddressRepository bar;

	/**
	 * Set environment.
	 *
	 * @param newLog    log
	 * @param newBds    bds
	 * @param newStatus status
	 * @param newBtr    btr
	 * @param newBar    bar
	 */
	// TODO why ioc doesn't work ?
	public final void setEnvironment(final Logger newLog, final BitcoindService newBds, final StatusService newStatus, final BitcoinTransactionRepository newBtr, final BitcoinAddressRepository newBar) {
		log = newLog;
		bds = newBds;
		status = newStatus;
		btr = newBtr;
		bar = newBar;
	}

	/**
	 * Create all the addresses used in the vout of a transaction.
	 *
	 * @param transactionHash transaction
	 * @return address list
	 */
	@Async
	@Transactional
	@SuppressWarnings({ "checkstyle:designforextension", "checkstyle:emptyforiteratorpad" })
	public Future<Boolean> importAddrresses(final String transactionHash) {
		GetRawTransactionResponse transactionResponse = bds.getRawTransaction(transactionHash);
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
				BitcoinAddress a = bar.findByAddress(address);
				if (a == null) {
					// Address creation.
					try {
						a = new BitcoinAddress(address);
						bar.save(a);
					} catch (ConstraintViolationException e) {
						a = bar.findByAddress(address);
					}
					status.addLog("importBlockAddresses : Address " + address + " created  with id " + a.getId());
				} else {
					status.addLog("importBlockAddresses : Address " + address + " already exists with id " + a.getId());
				}
			}
			// Returns the list of addresses.
			return new AsyncResult<>(true);
		} else {
			status.addError("importBlockAddresses : Impossible to retrieve transaction " + transactionHash + " : " + transactionResponse.getError());
			return new AsyncResult<>(false);
		}
	}

}
