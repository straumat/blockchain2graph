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
     * BenchmarkLauncher duration (1 day).
     */
    private static final long BENCHMARK_DURATION = TimeUnit.DAYS.toMillis(1);

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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("benchmark-blockchain2graph@scub.net");
        message.setTo("stephane.traumat@gmail.com");
        message.setSubject("Blockchain2graph benchmark results");
        message.setText("Number of blocks imported : " + blockCount);
        getJavaMailSender().send(message);

        // We stop the application.
        log.info("Benchmark finished - {} blocks imported", blockCount);
        System.exit(-1);
    }

    /**
     * Returns mail send.
     * @return mail sender.
     */
    private JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(System.getenv("SPRING_MAIL_HOST"));
        mailSender.setPort(Integer.parseInt(System.getenv("SPRING_MAIL_PORT")));
        mailSender.setUsername(System.getenv("SPRING_MAIL_USERNAME"));
        mailSender.setPassword(System.getenv("SPRING_MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

}
