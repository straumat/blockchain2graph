package com.oakinvest.b2g.bitcoin;

import com.oakinvest.b2g.bitcoin.util.benchmark.BenchmarkLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Application launcher.
 *
 * @author straumat
 */
@SpringBootApplication
public class Application implements ApplicationRunner {

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * Benchmark launcher parameter.
     */
    private static final String BENCHMARK_PARAMETER = "benchmark";

    /**
     * Benchmark launcher.
     */
    @Autowired
    private final BenchmarkLauncher benchmarkLauncher = null;

    /**
     * Application launcher.
     *
     * @param args parameters.
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public final void run(final ApplicationArguments args) {
        // If it's a benchmark.
        if (args.containsOption(BENCHMARK_PARAMETER)) {
            final ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(benchmarkLauncher);
        }
    }

}
