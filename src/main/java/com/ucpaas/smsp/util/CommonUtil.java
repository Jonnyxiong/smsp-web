package com.ucpaas.smsp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class CommonUtil {

	public static String getClientIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
		return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
	}
	
	/**
	 * 验证手机号码
	 * @param phone
	 * @return
	 */
	public static boolean isMobile(String phone){
		boolean flag = false;
		List<String> regexList = new ArrayList<>();
		regexList.add("^13\\d{9}$");
		regexList.add("^14[5|7|9]\\d{8}$");
		regexList.add("^15[0|1|2|3|5|6|7|8|9]\\d{8}$");
		regexList.add("^18\\d{9}$");
		regexList.add("^170[0|1|2|3|4|5|6|7|8|9]\\d{7}$");
		regexList.add("^17[1|5|6|7|8]\\d{8}$");
		regexList.add("^173\\d{8}$");
		for (String regex : regexList) {
			try {
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(phone);
				flag = m.matches();
				if(flag){
					break ;
				}
			} catch (Exception e) {
				flag = false;
				continue;
			}
		}
		return flag;
	}
	
	/**
	 * 验证海外号码
	 * @param phone
	 * @return
	 */
	public static boolean isOverSeaMobile(String phone){
		if(StringUtils.isEmpty(phone)){
			return false ;
		}
		if(phone.startsWith("00") && phone.length()>10){
			return true ;
		}
		return false ;
	}
	
}
