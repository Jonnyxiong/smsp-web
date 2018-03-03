package com.ucpaas.smsp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/error")
public class ErrorController {

	@ApiOperation(value = "404页面")
	@RequestMapping(value = "/404", method = RequestMethod.GET)
	public String noPage(HttpServletRequest request, Model model) {
		return "404"; 
	}
	@ApiOperation(value = "500页面")
	@RequestMapping(value = "/500", method = RequestMethod.GET)
	public String errorPage(HttpServletRequest request, Model model) {
		return "500"; 
	}
}
