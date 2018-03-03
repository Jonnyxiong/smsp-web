package com.ucpaas.smsp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class StringUtil {

	/**
	 * 数字装成字符串，不足补0
	 * @param num
	 * @param strLength 长度
	 * @param flag 0左边补0,1右边补0
	 * @return 
	 */
	public static String addZeroForNum(int num, int strLength, String flag) {
		String str = num + "";
		int strLen = str.length();
		if (strLen < strLength) {
			if ("0".equals(flag)) {
				while (strLen < strLength) {
					StringBuffer sb = new StringBuffer();
					sb.append("0").append(str);// 左补0
					str = sb.toString();
					strLen = str.length();
				}
			} else {
				if ("0".equals(flag)) {
					while (strLen < strLength) {
						StringBuffer sb = new StringBuffer();
						sb.append(str).append("0");// 右补0
						str = sb.toString();
						strLen = str.length();
					}
				}
			}
		}

		return str;
	}
	
	/**
	 * 验证手机号码
	 * @param phone
	 * @return
	 */
	public static boolean isMobile(String phone){
		boolean flag = false;
		List<String> regexList = new ArrayList<>();
		regexList.add("^13[4|5|6|7|8|9]\\d{8}$$");
		regexList.add("^15[0|1|2|7|8|9]\\d{8}$");
		regexList.add("^18[2|3|4|7|8]\\d{8}$");
		regexList.add("^147\\d{8}$");
		regexList.add("^1705\\d{7}$");
		regexList.add("^178\\d{8}$");
		regexList.add("^13[0|1|2]\\d{8}$");
		regexList.add("^15[5|6]\\d{8}$");
		regexList.add("^18[5|6]\\d{8}$");
		regexList.add("^145\\d{8}$");
		regexList.add("176\\d{8}$");
		regexList.add("^133\\d{8}$");
		regexList.add("^153\\d{8}$");
		regexList.add("^18[0|1|9]\\d{8}$");
		regexList.add("^1700\\d{7}$");
		regexList.add("^177\\d{8}$");
		regexList.add("^170[4|7|8|9]\\d{7}$");
		regexList.add("^171\\d{8}$");
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
	
	public static String getHostFromURL(String url){
		String host = "";
		Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");  
        Matcher matcher = p.matcher(url);  
        if (matcher.find()) {  
            host = matcher.group();  
        }  
		return host; 
	}
	/**
	 * @Description: 判断字符串是不是全是数字
	 * @author: Niu.T 
	 * @date: 2017年2月9日    下午12:23:03
	 * @param str
	 * @return boolean
	 */
	public static boolean isNumeric(String str){ 
		Pattern pattern = Pattern.compile("[0-9]*"); 
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
			return false; 
		} 
		return true; 
	}

	public static void main(String[] args) {
		System.out.println(isNumeric("555552s55"));
	}

}
