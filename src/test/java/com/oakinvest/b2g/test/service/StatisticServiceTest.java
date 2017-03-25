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

import static org.junit.Assert.assertEquals;

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
		// Simple test with twi values
		assertEquals(1f, bitcoinStatisticService.addBlockImportDuration(1f), 0f);
		assertEquals(1.5f, bitcoinStatisticService.addBlockImportDuration(2f), 0f);

		// Adding 100 values to see if the two previous number are disappearing.
		for (int i = 0; i < 100; i++) {
			bitcoinStatisticService.addBlockImportDuration(4);
		}
		assertEquals(4f, bitcoinStatisticService.addBlockImportDuration(4f), 0f);

		// Adding another value.
		assertEquals(5f, bitcoinStatisticService.addBlockImportDuration(104f), 0f);
	}

}
