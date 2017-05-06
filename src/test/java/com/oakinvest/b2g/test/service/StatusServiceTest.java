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

import static org.assertj.core.api.Assertions.assertThat;

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
	private StatusService statusService;

	/**
	 * Test for getTotalBlockCount().
	 */
	@Test
	public final void getTotalBlockCountTest() {
		final long expectedTotalBlockCount = 150;
		statusService.setTotalBlockCount(expectedTotalBlockCount);
		// Test.
		assertThat(statusService.getTotalBlockCount())
				.as("Check total block count")
				.isEqualTo(expectedTotalBlockCount);
	}

	/**
	 * Test for getImportedBlockCount().
	 */
	@Test
	public final void getImportedBlockCountTest() {
		final long expectedImportedBlockCount = 140;
		statusService.setImportedBlockCount(expectedImportedBlockCount);
		// Test.
		assertThat(statusService.getImportedBlockCount())
				.as("Check imported block count")
				.isEqualTo(expectedImportedBlockCount);
	}

	/**
	 * Test for getLastLog().
	 */
	@Test
	public final void getLastLogTest() {
		statusService.addLog("Hi !");
		// Test.
		assertThat(statusService.getLastLog())
				.as("Check last log")
				.endsWith("Hi !");
	}

	/**
	 * Test for getLastError().
	 */
	@Test
	public final void getLastErrorTest() {
		statusService.addError("Error !", null);
		// Test.
		assertThat(statusService.getLastError())
				.as("Check last error")
				.endsWith("Error !");
	}

}
