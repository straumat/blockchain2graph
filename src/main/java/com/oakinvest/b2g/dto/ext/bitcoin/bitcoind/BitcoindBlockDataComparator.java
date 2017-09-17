package com.oakinvest.b2g.dto.ext.bitcoin.bitcoind;

import java.util.Comparator;

/**
 * Comparator for block data - order by height.
 * Created by straumat on 13/05/17.
 */
public class BitcoindBlockDataComparator implements Comparator<BitcoindBlockData> {

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.
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
    public final int compare(final BitcoindBlockData o1, final BitcoindBlockData o2) {
        if (o1 != null && o2 != null) {
            return o1.getBlock().getHeight() - o2.getBlock().getHeight();
        } else {
            return 0;
        }
    }
}
