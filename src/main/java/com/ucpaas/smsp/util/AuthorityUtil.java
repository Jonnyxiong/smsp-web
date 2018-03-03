package com.ucpaas.smsp.util;

import javax.servlet.http.HttpServletRequest;

import com.ucpaas.smsp.model.po.SmsAccountModelPo;

public class AuthorityUtil {

	public static SmsAccountModelPo getLoginUserInfo(HttpServletRequest request){
		
		SmsAccountModelPo LOGIN_USER_INFO = (SmsAccountModelPo)request.getSession().getAttribute("LOGIN_USER_INFO");
		
		return LOGIN_USER_INFO;
	}
	
	
}
