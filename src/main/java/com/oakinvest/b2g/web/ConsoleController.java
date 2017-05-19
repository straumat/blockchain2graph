package com.oakinvest.b2g.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Console controller.
 * Created by straumat on 02/11/16.
 */
@Controller
public class ConsoleController {

	/**
	 * Console view.
	 */
	private static final String CONSOLE_VIEW = "console";

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
	@RequestMapping(value = "/")
	public final String index(final Model model) {
		model.addAttribute("url", address + ":" + port);
		return CONSOLE_VIEW;
	}

}
