package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.service.bitcoin.BitcoinStatisticService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Statistic service test.
 * Created by straumat on 25/03/17.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

public class StatisticServiceTest {

	/**
	 * Bitcoin statistic service.
	 */
	@Autowired
	private BitcoinStatisticService bitcoinStatisticService;

	/**
	 * Test for addBlockImportDuration().
	 */
	@Test
	public final void getStatisticsTest() {
		// Simple test with two values
		assertThat(bitcoinStatisticService.addBlockImportDuration(1000L))
				.as("First statistic")
				.isEqualTo(1);

		assertThat(bitcoinStatisticService.addBlockImportDuration(2000L))
				.as("Second statistic")
				.isEqualTo(1.5F);

		// Adding 100 values to see if the two previous number are disappearing.
		for (int i = 0; i < 100; i++) {
			bitcoinStatisticService.addBlockImportDuration(4000);
		}
		assertThat(bitcoinStatisticService.addBlockImportDuration(4000L))
				.as("After 100 new statistics")
				.isEqualTo(4F);

		// Adding another value.
		assertThat(bitcoinStatisticService.addBlockImportDuration(104000L))
				.as("Another one")
				.isEqualTo(5F);

		assertThat(bitcoinStatisticService.getAverageBlockImportDuration())
				.as("Just getting value")
				.isEqualTo(5F);
	}

}
