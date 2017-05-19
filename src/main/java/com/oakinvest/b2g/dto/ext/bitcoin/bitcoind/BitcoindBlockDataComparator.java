package com.oakinvest.b2g.dto.ext.bitcoin.bitcoind;

import java.util.Comparator;

/**
 * Comparator for block data - order by height.
 * Created by straumat on 13/05/17.
 */
public class BitcoindBlockDataComparator implements Comparator {

	/**
	 * Compares two block data.
	 *
	 * @param o1 the first object to be compared.
	 * @param o2 the second object to be compared.
	 * @return a negative integer, zero, or a positive integer as the
	 * first argument is less than, equal to, or greater than the
	 * second.
	 * @throws NullPointerException if an argument is null and this
	 *                              comparator does not permit null arguments
	 * @throws ClassCastException   if the arguments' types prevent them from
	 *                              being compared by this comparator.
	 */
	@Override
	public final int compare(final Object o1, final Object o2) {
		BitcoindBlockData blockData1 = (BitcoindBlockData) o1;
		BitcoindBlockData blockData2 = (BitcoindBlockData) o2;
		return (int) (blockData1.getBlock().getHeight() - blockData2.getBlock().getHeight());
	}
}
