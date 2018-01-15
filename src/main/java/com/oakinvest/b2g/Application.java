package com.oakinvest.b2g;

import com.oakinvest.b2g.dto.ext.bitcoin.bitcoind.getblockcount.GetBlockCountResponse;
import com.oakinvest.b2g.repository.bitcoin.BitcoinBlockRepository;
import com.oakinvest.b2g.service.StatusService;
import com.oakinvest.b2g.service.bitcoin.BitcoindService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Application launcher.
 *
 * @author straumat
 */
@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {

    /**
     * Constructor.
     *
     * @param bitcoindService        Bitcoind service.
     * @param bitcoinBlockRepository Bitcoin block repository.
     * @param statusService          Status service.
     */
    public Application(final BitcoindService bitcoindService, final BitcoinBlockRepository bitcoinBlockRepository, final StatusService statusService) {
        // Update the status of the number of block imported.
        statusService.setImportedBlockCount((int) bitcoinBlockRepository.count());

        // Update the status of the number of block in bitcoind.
        GetBlockCountResponse getBlockCountResponse = bitcoindService.getBlockCount();
        if (getBlockCountResponse.getError() != null) {
            statusService.setTotalBlockCount(getBlockCountResponse.getResult());
        }
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
    protected final SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

}
