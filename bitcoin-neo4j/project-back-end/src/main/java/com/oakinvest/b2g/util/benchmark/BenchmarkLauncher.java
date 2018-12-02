package com.oakinvest.b2g.util.benchmark;

import com.oakinvest.b2g.util.providers.RepositoriesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark Launcher.
 *
 * @author straumat
 */
@Component
public class BenchmarkLauncher implements Runnable {

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(BenchmarkLauncher.class);

    /**
     * Mail server host.
     */
    private static final String SPRING_MAIL_HOST_PARAMETER = "SPRING_MAIL_HOST";

    /**
     * Mail server port.
     */
    private static final String SPRING_MAIL_PORT_PARAMETER = "SPRING_MAIL_PORT";

    /**
     * Mail server username.
     */
    private static final String SPRING_MAIL_USERNAME_PARAMETER = "SPRING_MAIL_USERNAME";

    /**
     * Mail server password.
     */
    private static final String SPRING_MAIL_PASSWORD_PARAMETER = "SPRING_MAIL_PASSWORD";

    /**
     * Build number from continuous integration.
     */
    private static final String BUILD_NUMBER_PARAMETER = "BUILD_NUMBER";

    /**
     * BenchmarkLauncher duration (1 day).
     */
    private static final long BENCHMARK_DURATION = TimeUnit.DAYS.toMillis(1);

    /**
     * Repositories.
     */
    private final RepositoriesProvider repositories;

    /**
     * Application context.
     */
    private final ApplicationContext context;

    /**
     * Constructor.
     *
     * @param newRepositoriesProvider repositories provider.
     * @param newContext              context.
     */
    public BenchmarkLauncher(final RepositoriesProvider newRepositoriesProvider, final ApplicationContext newContext) {
        this.repositories = newRepositoriesProvider;
        this.context = newContext;
    }

    @Override
    public final void run() {
        log.info("24 hours benchmark started");

        // Waiting 24 hours.
        try {
            Thread.sleep(BENCHMARK_DURATION);
        } catch (InterruptedException e) {
            log.error("Benchmark interrupted " + e.getMessage(), e);
        }

        // get build number
        String buildNumber = "Not Available";
        if (System.getenv(BUILD_NUMBER_PARAMETER) != null) {
            buildNumber = System.getenv(BUILD_NUMBER_PARAMETER);
        }

        // Get the number of blocks imported and sending report.
        long blockCount = repositories.getBlockRepository().count();
        log.info("Benchmark finished - {} blocks imported", blockCount);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("benchmark-blockchain2graph@oak-invest.com");
        message.setTo("stephane.traumat@gmail.com");
        message.setSubject("B2G benchmark results for build number : " + buildNumber);
        message.setText("Number of blocks imported : " + blockCount);
        getJavaMailSender().send(message);
        log.info("Benchmark maid sent");

        // We stop the application.
        log.info("Application exit");
        SpringApplication.exit(context, () -> 0);
        System.exit(0);
    }

    /**
     * Returns mail send.
     *
     * @return mail sender.
     */
    private JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(System.getenv(SPRING_MAIL_HOST_PARAMETER));
        mailSender.setPort(Integer.parseInt(System.getenv(SPRING_MAIL_PORT_PARAMETER)));
        mailSender.setUsername(System.getenv(SPRING_MAIL_USERNAME_PARAMETER));
        mailSender.setPassword(System.getenv(SPRING_MAIL_PASSWORD_PARAMETER));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

}
