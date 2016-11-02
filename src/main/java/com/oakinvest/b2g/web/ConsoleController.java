package com.oakinvest.b2g.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Index.
 * Created by straumat on 02/11/16.
 */
@Controller
public class ConsoleController {

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
	 * @return page to display
	 */
	@RequestMapping(value = "/")
	public final ModelAndView index() {
		return new ModelAndView("console", "url", address + ":" + port);
	}

}
