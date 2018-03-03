package com.ucpaas.smsp.service.common;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ucpaas.smsp.constant.Constant.DbTablePrefix;

public interface CommonService {
	
	List<String> getExistTable(DbTablePrefix dbTablePrefix, String startDate, String endDate) throws Exception;
	
	List<String> getExistTable(DbTablePrefix dbTablePrefix, String startDate, String endDate,String clientid) throws Exception;
	
//	List<SplitTableTime> getSplitTableTime(String start_time, String end_time) throws Exception;
	
	List<String> getExistTableForSplitTable(DbTablePrefix dbTablePrefix, String startDate, String endDate,String clientid) throws Exception;
	
	void randCheckCodePic(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	boolean randCodeCheck(HttpServletRequest request) throws Exception;
	
	Map<String, Object> clientAccountStatusCheck(String clientId) throws Exception;
	
	Map<String,Object> isExistOauthPicInfo(String client_id);
	
}

