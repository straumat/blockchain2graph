package com.oakinvest.b2g.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Scheduling configuration.
 * Created by straumat on 03/01/17.
 */
@Configuration
@EnableScheduling
@Profile("!test")
public class SchedulingConfiguration {

}
