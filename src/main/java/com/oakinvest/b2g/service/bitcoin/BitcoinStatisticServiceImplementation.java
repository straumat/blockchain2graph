package com.oakinvest.b2g.service.bitcoin;

import com.oakinvest.b2g.service.StatisticService;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

/**
 * Bitcoin statistic service.
 * Created by straumat on 25/03/17.
 */
@Service
public class BitcoinStatisticServiceImplementation implements StatisticService {

	/**
	 * How many milli seconds in one second.
	 */
	private static final float MILLISECONDS_IN_SECONDS = 1000F;

	/**
	 * How many digits for the statistics.
	 */
	private static final int ROUND_DIGITS = 100;

	/**
	 * Number of blocks used for blockImportDurations.
	 */
	private static final int MAX_NUMBER_OF_BLOCKS_FOR_EXECUTION_TIME_STATISTICS = 100;

	/**
	 * Execution time statistics.
	 */
	private final LinkedList<Long> blockImportDurations = new LinkedList<>();

	/**
	 * Executed time statistic.
	 */
	private float averageBlockImportDuration = 0;

	/**
	 * Add an execution time statistics and return the average execution time.
	 *
	 * @param newDuration new duration in milliseconds.
	 * @return average time in seconds
	 */
	@Override
	public final float addBlockImportDuration(final long newDuration) {
		// If we reach the maximum number of execution times, we remove the first one.
		while (blockImportDurations.size() >= MAX_NUMBER_OF_BLOCKS_FOR_EXECUTION_TIME_STATISTICS) {
			blockImportDurations.removeFirst();
		}

		// We add the statistics.
		blockImportDurations.add(newDuration);

		// Calculate the average duration.
		if (blockImportDurations.size() > 0) {
			int n;
			float totalAmountOfTime = 0;
			for (n = 0; n < blockImportDurations.size(); n++) {
				totalAmountOfTime += blockImportDurations.get(n);
			}
			averageBlockImportDuration = (float) Math.round(((totalAmountOfTime / n) / MILLISECONDS_IN_SECONDS) * ROUND_DIGITS) / ROUND_DIGITS;
			return averageBlockImportDuration;
		} else {
			// Nothing to make a statistic, we return 0.
			return 0;
		}
	}

	/**
	 * Return average execution time.
	 *
	 * @return average execution
	 */
	@Override
	public final float getAverageBlockImportDuration() {
		return averageBlockImportDuration;
	}

}
