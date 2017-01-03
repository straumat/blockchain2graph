package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.service.StatusService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
		ss.addLogMessage("Hi !");
		assertTrue("Wrong last log message after setting it", ss.getLastLogMessage().contains("Hi !"));
	}

	/**
	 * Test for getLastErrorMessage().
	 */
	@Test
	public final void getLastErrorMessageTest() {
		ss.addErrorMessage("Error !");
		assertTrue("Wrong last error message after setting it", ss.getLastErrorMessage().contains("Error !"));
	}

}
