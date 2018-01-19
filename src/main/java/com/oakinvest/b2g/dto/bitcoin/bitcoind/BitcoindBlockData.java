package com.oakinvest.b2g.dto.bitcoin.bitcoind;

import com.oakinvest.b2g.dto.bitcoin.bitcoind.getblock.GetBlockResult;
import com.oakinvest.b2g.dto.bitcoin.bitcoind.getrawtransaction.GetRawTransactionResult;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
     * Bitcoin addresses used in the block.
     */
    private Set<String> addresses = new HashSet<>();

	/**
	 * Constructor.
	 *
	 * @param newBlock          block data
	 * @param newTransactions   transactions data
     * @param newAddresses      block addresses
	 */
	public BitcoindBlockData(final GetBlockResult newBlock, final List<GetRawTransactionResult> newTransactions, final Set<String> newAddresses) {
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

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BitcoindBlockData)) {
            return false;
        }

        BitcoindBlockData that = (BitcoindBlockData) o;
        return getBlock().getHeight() == that.getBlock().getHeight();
    }

    @Override
    public final int hashCode() {
        return getBlock().hashCode();
    }

}
