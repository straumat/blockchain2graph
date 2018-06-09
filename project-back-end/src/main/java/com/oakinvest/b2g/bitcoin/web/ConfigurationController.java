package com.oakinvest.b2g.bitcoin.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Console controller.
 *
 * Created by straumat on 02/11/16.
 */
@Controller
public class ConfigurationController {

    /**
     * Console view.
     */
    private static final String CONFIGURATION_VIEW = "configuration";

    /**
     * Server address.
     */
    @Value("${blockchain2graph.address}")
    private String address;

    /**
     * Server port.
     */
    @Value("${blockchain2graph.port}")
    private String port;

    /**
     * Page to be display.
     *
     * @param model model.
     * @return page to display.
     */
    @RequestMapping(value = "/configuration")
    public final String getConfigurationView(final Model model) {
        model.addAttribute("url", address + ":" + port);
        return CONFIGURATION_VIEW;
    }

}
