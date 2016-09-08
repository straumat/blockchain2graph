package com.oakinvest.b2g.test.service;

import com.oakinvest.b2g.Application;
import com.oakinvest.b2g.service.IntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for IntegrationService.
 * Created by straumat on 04/09/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class IntegrationServiceTest {

	/**
	 * Bitcoind service.
	 */
	@Autowired
	private IntegrationService is;

	/**
	 * integrateBitcoinBlock test.
	 */
	@Test
	public final void integrateBitcoinBlockTest() {
		// Tests data.
		final int firstBlockToImport = 0;
		final int lastBlockToImport = 10;
		final int expectedAddressesInAllBlocks = 0;

		// Launching integration.
		for (int i = firstBlockToImport; i <= lastBlockToImport; i++) {
			is.integrateBitcoinBlock(i);
		}

		// Testing results.
	}

}
