package com.oakinvest.b2g.service.bitcoin;

import com.oakinvest.b2g.web.StatusHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

/**
 * Bitcoin statistic service.
 * Created by straumat on 25/03/17.
 */
@Service
public class BitcoinStatisticServiceImplementation implements BitcoinStatisticService {

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
	private final LinkedList<Float> blockImportDurations = new LinkedList<>();

	/**
	 * Executed time statistic.
	 */
	private float averageBlockImportDuration = 0;

	/**
	 * Status handler.
	 */
	@Autowired
	private StatusHandler statusHandler;

	/**
	 * Add an execution time statistics and return the execution mean.
	 *
	 * @param newDuration new execution time.
	 * @return mean time
	 */
	@Override
	public final float addBlockImportDuration(final float newDuration) {
		// If we reach the maximum number of execution times, we remove the first one.
		while (blockImportDurations.size() >= MAX_NUMBER_OF_BLOCKS_FOR_EXECUTION_TIME_STATISTICS) {
			blockImportDurations.removeFirst();
		}

		// We add the statistics.
		blockImportDurations.add(newDuration);

		// Calculate the mean.
		if (blockImportDurations.size() > 0) {
			int n;
			float totalAmountOfTime = 0;
			for (n = 0; n < blockImportDurations.size(); n++) {
				totalAmountOfTime += blockImportDurations.get(n);
			}
			averageBlockImportDuration = (float) Math.round((totalAmountOfTime / n) * ROUND_DIGITS) / ROUND_DIGITS;
			statusHandler.updateAverageBlockImportDuration(averageBlockImportDuration);
			return averageBlockImportDuration;
		} else {
			// Nothing to make a statistic, we return 0.
			return 0;
		}
	}

	/**
	 * Return execution time mean.
	 *
	 * @return execution time mean
	 */
	@Override
	public final float getAverageBlockImportDuration() {
		return averageBlockImportDuration;
	}

}
