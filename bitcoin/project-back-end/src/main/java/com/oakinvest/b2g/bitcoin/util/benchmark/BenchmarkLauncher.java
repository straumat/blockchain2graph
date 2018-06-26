package com.oakinvest.b2g.bitcoin.util.benchmark;

import com.oakinvest.b2g.bitcoin.util.providers.RepositoriesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final long BENCHMARK_DURATION = TimeUnit.MINUTES.toMillis(5);

    /**
     * Repositories.
     */
    private final RepositoriesProvider repositories;

    /**
     * Constructor.
     *
     * @param newRepositoriesProvider repositories provider.
     */
    public BenchmarkLauncher(final RepositoriesProvider newRepositoriesProvider) {
        this.repositories = newRepositoriesProvider;
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

        // Get the number of blocks imported and sending report.
        long blockCount = repositories.getBlockRepository().count();
        log.info("Benchmark finished - {} blocks imported", blockCount);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("benchmark-blockchain2graph@scub.net");
        message.setTo("stephane.traumat@gmail.com");
        message.setSubject("B2G benchmark results - build " + System.getenv(BUILD_NUMBER_PARAMETER));
        message.setText("Number of blocks imported : " + blockCount);
        getJavaMailSender().send(message);


        // We stop the application.
        // TODO Exit in a more clean way.
        System.exit(-1);
    }

    /**
     * Returns mail send.
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
