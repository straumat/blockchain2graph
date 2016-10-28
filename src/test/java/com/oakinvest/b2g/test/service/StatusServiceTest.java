package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.service.IntegrationService;
import com.oakinvest.b2g.service.StatusService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the status service.
 * Created by straumat on 28/10/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StatusServiceTest {

	/**
	 * Integration service.
	 */
	@Autowired
	private IntegrationService is;

	/**
	 * Status service
	 */
	@Autowired
	private StatusService ss;

	/**
	 * Test for getTotalBlockCount().
	 */
	@Test
	public final void getTotalBlockCountTest() {
		assertTrue("Wrong total block count", 435000 < ss.getTotalBlockCount());
	}

	/**
	 * Test for getLastBlockIntegrated().
	 */
	@Test
	public final void getLastBlockIntegratedTest() {
		// Configuration.
		final int firstBlockToImport = 0;
		final int lastBlockToImport = 5;

		// Launching integration.
		for (int i = firstBlockToImport; i <= lastBlockToImport; i++) {
			assertTrue("Block " + i + " integration failure", is.integrateBitcoinBlock(i));
		}

		assertEquals("Wrong last block integrated", 6, ss.getLastBlockIntegrated());
	}


	/**
	 * Test for getLastLogMessage().
	 */
	@Test
	public final void getLastLogMessageTest() {
		assertEquals("Wrong last log message at start", "", ss.getLastLogMessage());
		ss.addLogMessage("Hi !");
		assertEquals("Wrong last log message after setting it", "Hi !", ss.getLastLogMessage());
	}

	/**
	 * Test for getLastErrorMessage().
	 */
	@Test
	public final void getLastErrorMessageTest() {
		assertEquals("Wrong last error message at start", "", ss.getLastErrorMessage());
		ss.addErrorMessage("Error !");
		assertEquals("Wrong last error message after setting it", "Error !", ss.getLastErrorMessage());
	}

}
