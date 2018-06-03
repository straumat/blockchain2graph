package com.oakinvest.b2g.bitcoin.test.service;

import com.oakinvest.b2g.bitcoin.test.util.junit.BaseTest;
import com.oakinvest.b2g.bitcoin.service.StatisticService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Statistic service test.
 * Created by straumat on 25/03/17.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StatisticServiceTest extends BaseTest {

    /**
     * Bitcoin statistic service.
     */
    @Autowired
    private StatisticService statisticService;

    @Before
    public void setUp() {
        statisticService.reset();
    }

    /**
     * Test for addBlockImportDuration().
     */
    @Test
    public final void getStatisticsTest() {
        // Simple test with two values
        assertThat(statisticService.addBlockImportDuration(1000L))
                .as("First statistic")
                .isEqualTo(1);

        assertThat(statisticService.addBlockImportDuration(2000L))
                .as("Second statistic")
                .isEqualTo(1.5F);

        // Adding 100 values to see if the two previous number are disappearing.
        for (int i = 0; i < 100; i++) {
            statisticService.addBlockImportDuration(4000);
        }
        assertThat(statisticService.addBlockImportDuration(4000L))
                .as("After 100 new statistics")
                .isEqualTo(4F);

        // Adding another value.
        assertThat(statisticService.addBlockImportDuration(104000L))
                .as("Another one")
                .isEqualTo(5F);

        assertThat(statisticService.getAverageBlockImportDuration())
                .as("Just getting value")
                .isEqualTo(5F);
    }

}
