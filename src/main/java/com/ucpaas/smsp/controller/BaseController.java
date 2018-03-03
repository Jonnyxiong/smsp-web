package com.ucpaas.smsp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class BaseController {
	

	@Autowired
	protected HttpServletRequest request;
	
	@Autowired
	protected HttpServletResponse response;
	
	private Logger logger = LoggerFactory.getLogger(BaseController.class);
      
    @ExceptionHandler  
    public String excption(Exception ex) {  
        logger.error("【系统异常】", ex);
        return "common/error";
    }  
}