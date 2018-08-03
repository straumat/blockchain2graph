package com.oakinvest.b2g;

import com.oakinvest.b2g.util.benchmark.BenchmarkLauncher;
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
     * Benchmark launcher parameter.
     */
    private static final String BENCHMARK_PARAMETER = "benchmark";

    /**
     * Benchmark launcher.
     */
    private final BenchmarkLauncher benchmarkLauncher;

    /**
     * Constructor.
     * @param newBenchmarkLauncher benchmark launcher.
     */
    public Application(final BenchmarkLauncher newBenchmarkLauncher) {
        this.benchmarkLauncher = newBenchmarkLauncher;
    }

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
