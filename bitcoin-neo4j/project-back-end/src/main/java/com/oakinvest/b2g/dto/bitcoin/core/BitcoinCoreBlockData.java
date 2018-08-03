package com.oakinvest.b2g.dto.bitcoin.core;

import com.oakinvest.b2g.dto.bitcoin.core.getblock.GetBlockResult;
import com.oakinvest.b2g.dto.bitcoin.core.getrawtransaction.GetRawTransactionResult;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Bitcoin core block data - not a core object - created for b2g.
 *
 * Created by straumat on 16/03/17.
 */
public class BitcoinCoreBlockData implements Serializable {

	/**
	 * Bitcoin block.
	 */
	private final GetBlockResult block;

	/**
	 * Bitcoin transactions.
	 */
	private final List<GetRawTransactionResult> transactions;

    /**
     * Bitcoin addresses used in the block.
     */
    private final Set<String> addresses;

	/**
	 * Constructor.
	 *
	 * @param newBlock          block data
	 * @param newTransactions   transactions data
     * @param newAddresses      block addresses
	 */
	public BitcoinCoreBlockData(final GetBlockResult newBlock, final List<GetRawTransactionResult> newTransactions, final Set<String> newAddresses) {
		this.block = newBlock;
		this.transactions = newTransactions;
		this.addresses = newAddresses;
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
     * Getter addresses.
     *
     * @return addresses used in the block.
     */
    public final Set<String> getAddresses() {
        return addresses;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BitcoinCoreBlockData)) {
            return false;
        }

        BitcoinCoreBlockData that = (BitcoinCoreBlockData) o;
        return getBlock().getHeight() == that.getBlock().getHeight();
    }

    @Override
    public final int hashCode() {
        return getBlock().hashCode();
    }

}
