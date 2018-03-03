package com.ucpaas.smsp.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ucpaas.smsp.controller.BaseController;

@Controller
@RequestMapping("/app")
public class AppController extends BaseController {


	/**
	 * lightApp 页面
	 */
	@RequestMapping(value = "/index")
	public ModelAndView  homePage(ModelAndView model) throws Exception {
		model.setViewName("lightApp/index");
		return model;
	}


}
