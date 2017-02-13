package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.service.StatusService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the status service.
 * Created by straumat on 28/10/16.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StatusServiceTest {

	/**
	 * Status service.
	 */
	@Autowired
	private StatusService ss;

	/**
	 * Test for getTotalBlockCount().
	 */
	@Test
	public final void getTotalBlockCountTest() {
		final long expectedTotalBlockCount = 150;
		ss.setTotalBlockCount(expectedTotalBlockCount);
		assertEquals("Wrong total block count", expectedTotalBlockCount, ss.getTotalBlockCount());
	}

	/**
	 * Test for getImportedBlockCount().
	 */
	@Test
	public final void getLastBlockIntegratedTest() {
		final long expectedImportedBlockCount = 140;
		ss.setImportedBlockCount(expectedImportedBlockCount);
		assertEquals("Wrong last block integrated", expectedImportedBlockCount, ss.getImportedBlockCount());
	}

	/**
	 * Test for getLastLogMessage().
	 */
	@Test
	public final void getLastLogMessageTest() {
		ss.addLog("Hi !");
		assertTrue("Wrong last log message after setting it", ss.getLastLogMessage().contains("Hi !"));
	}

	/**
	 * Test for getLastErrorMessage().
	 */
	@Test
	public final void getLastErrorMessageTest() {
		ss.addError("Error !");
		assertTrue("Wrong last error message after setting it", ss.getLastErrorMessage().contains("Error !"));
	}

	/**
	 * Test for addExecutionTimeStatistic().
	 */
	@Test
	public final void getStatisticsTest() {
		// Simple test with twi values
		assertEquals(1f, ss.addExecutionTimeStatistic(1f), 0f);
		assertEquals(1.5f, ss.addExecutionTimeStatistic(2f), 0f);

		// Adding 100 values to see if the two previous number are disappearing.
		for (int i = 0; i < 100; i++) {
			ss.addExecutionTimeStatistic(4);
		}
		assertEquals(4f, ss.addExecutionTimeStatistic(4f), 0f);

		// Adding another value.
		assertEquals(5f, ss.addExecutionTimeStatistic(104f), 0f);
	}

}
