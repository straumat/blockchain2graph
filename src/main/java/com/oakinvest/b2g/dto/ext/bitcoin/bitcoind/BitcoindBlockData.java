package com.oakinvest.b2g.dto.ext.bitcoin.bitcoind;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblock.GetBlockResult;
import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Bitcoind block data.
 * Created by straumat on 16/03/17.
 */
public class BitcoindBlockData {

	/**
	 * Bitcoin block.
	 */
	private final GetBlockResult block;

	/**
	 * Bitcoin transactions.
	 */
	private HashMap<String, GetRawTransactionResult> transactions = new LinkedHashMap<>();

	/**
	 * Constructor.
	 *
	 * @param newBlock        block data
	 * @param newTransactions transactions data
	 */
	public BitcoindBlockData(final GetBlockResult newBlock, final HashMap<String, GetRawTransactionResult> newTransactions) {
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
	public final HashMap<String, GetRawTransactionResult> getTransactions() {
		return transactions;
	}

}
