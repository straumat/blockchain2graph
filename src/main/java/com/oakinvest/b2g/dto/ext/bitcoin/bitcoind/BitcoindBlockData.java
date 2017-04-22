package com.oakinvest.b2g.dto.ext.bitcoin.bitcoind;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblock.GetBlockResult;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Bitcoind block data - not a bitcoind object - created for b2g.
 * Created by straumat on 16/03/17.
 */
public class BitcoindBlockData implements Serializable {

	/**
	 * Bitcoin block.
	 */
	private final GetBlockResult block;

	/**
	 * Bitcoin transactions.
	 */
	private List<GetRawTransactionResult> transactions = new LinkedList<>();

	/**
	 * Constructor.
	 *
	 * @param newBlock        block data
	 * @param newTransactions transactions data
	 */
	public BitcoindBlockData(final GetBlockResult newBlock, final List<GetRawTransactionResult> newTransactions) {
		this.block = newBlock;
		this.transactions = newTransactions;
	}

	/**
	 * Getter block.
	 *
	 * @return block
	 */
	public final GetBlockResult getBlock() {
		return block;
	}

	/**
	 * Getter transactions.
	 *
	 * @return transactions
	 */
	public final List<GetRawTransactionResult> getTransactions() {
		return transactions;
	}

	/**
	 * Return a particular rawTransactionResult.
	 *
	 * @param txid transaction hash
	 * @return transaction data
	 */
	public final Optional<GetRawTransactionResult> getRawTransactionResult(final String txid) {
		if (txid != null) {
			return transactions.stream()
					.filter(t -> txid.equals(t.getTxid()))
					.findFirst();
		} else {
			return Optional.empty();
		}
	}

}
