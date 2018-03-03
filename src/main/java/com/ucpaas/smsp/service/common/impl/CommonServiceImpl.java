package com.ucpaas.smsp.service.common.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucpaas.smsp.constant.Constant.DbTablePrefix;
import com.ucpaas.smsp.dao.MasterDao;
import com.ucpaas.smsp.dao.StatDao;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.model.po.SmsClientIdentifyLogPo;
import com.ucpaas.smsp.service.common.CommonService;
import com.ucpaas.smsp.util.CheckCodeUtil;

@Service
@Transactional
public class CommonServiceImpl implements CommonService {
	
	@Autowired
	private MasterDao masterDao;
	
	@Autowired
	private StatDao statDao;
	
	private Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);
	
	@Override
	public List<String> getExistTable(DbTablePrefix dbTablePrefix, String startDate, String endDate) {
		List<String> data = new ArrayList<String>();
		String tablePrefixStr = dbTablePrefix.getTablePrefix();

		if (StringUtils.isBlank(tablePrefixStr) || StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
			return data;
		}
		DateTime d1 = null;
		DateTime d2 = null;
		try {
			d1 = new DateTime(DateUtils.parseDateStrictly(startDate.substring(0, 10), "yyyy-MM-dd"));
			d2 = new DateTime(DateUtils.parseDateStrictly(endDate.substring(0, 10), "yyyy-MM-dd"));
		} catch (Throwable e) {
			logger.error("时间转换【失败】：startDate=" + startDate + ", endDate=" + endDate, e);
			return data;
		}
		if (d1.isAfter(d2)) {
			return data;
		}
		int between = Days.daysBetween(d1, d2).getDays();
		List<String> tableList = new ArrayList<String>();
		for (int i = 0; i <= between; i++) {
			tableList.add(tablePrefixStr + d1.plusDays(i).toString("yyyyMMdd"));
		}

		List<Map<String, Object>> existList = statDao.getSearchList("common.getExistTable", tableList);// 查询在日期范围内存在的语音表、短信表、即时消息表
		for (Map<String, Object> map : existList) {
			data.add(map.get("table_name").toString());
		}
		return data;
	}
	
	
	
	@Override
	public List<String> getExistTable(DbTablePrefix dbTablePrefix, String startDate, String endDate,String clientid)
			throws Exception {
		List<String> data = new ArrayList<String>();
		
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("clientId", clientid);
		SmsAccountModel smsAccountModel = masterDao.selectOne("smsAccount.getAccountInfoByClientId",sqlParams);
		int identify = smsAccountModel.getIdentify();
		
		String tablePrefixStr = dbTablePrefix.getTablePrefix();

		if (StringUtils.isBlank(tablePrefixStr) || StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
			return data;
		}
		DateTime d1 = null;
		DateTime d2 = null;
		try {
			d1 = new DateTime(DateUtils.parseDateStrictly(startDate.substring(0, 10), "yyyy-MM-dd"));
			d2 = new DateTime(DateUtils.parseDateStrictly(endDate.substring(0, 10), "yyyy-MM-dd"));
		} catch (Throwable e) {
			logger.error("时间转换【失败】：startDate=" + startDate + ", endDate=" + endDate, e);
			return data;
		}
		if (d1.isAfter(d2)) {
			return data;
		}
		int between = Days.daysBetween(d1, d2).getDays();
		List<String> tableList = new ArrayList<String>();
		for (int i = 0; i <= between; i++) {
			tableList.add(tablePrefixStr +identify+"_"+ d1.plusDays(i).toString("yyyyMMdd"));
		}
		System.out.println("tableList:---------------->"+tableList.toString());

		List<Map<String, Object>> existList = statDao.getSearchList("common.getExistTable", tableList);// 查询在日期范围内存在的语音表、短信表、即时消息表
		for (Map<String, Object> map : existList) {
			data.add(map.get("table_name").toString());
		}
		return data;
	}
	

//	@Override
//	public List<SplitTableTime> getSplitTableTime(String start_time, String end_time) throws Exception {
//		
//		List<SplitTableTime> splitTableTimeList = new ArrayList<>();
//		
//		String online_time = ConfigUtils.online_time;
//		
//		SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		DateTime dt_start_time = new DateTime(sdf.parse(start_time));
//		DateTime dt_end_time = new DateTime(sdf.parse(end_time));
//		DateTime dt_online_time = new DateTime(sdf.parse(online_time));
//		
//		if(dt_end_time.isBefore(dt_online_time) || dt_end_time.isEqual(dt_online_time)){
//			//时间都在上线之前（使用老数据）
//			SplitTableTime st = new SplitTableTime();
//			st.setStart_time(start_time);
//			st.setEnd_time(end_time);
//			st.setFlag("old");
//			splitTableTimeList.add(st);
//			
//			
//		}else if(dt_start_time.isAfter(dt_online_time) || dt_start_time.isEqual(dt_online_time)){
//			//时间都在上线之后（使用新数据）
//			SplitTableTime st = new SplitTableTime();
//			st.setStart_time(start_time);
//			st.setEnd_time(end_time);
//			st.setFlag("new");
//			splitTableTimeList.add(st);
//		}else{
//			//开始时间在上线之前，结束时间在上线之后（使用老数据和新数据）
//			
//			SplitTableTime st_old = new SplitTableTime();
//			st_old.setStart_time(start_time);
//			st_old.setEnd_time(online_time);
//			st_old.setFlag("old");
//			splitTableTimeList.add(st_old);
//			
//			SplitTableTime st_new = new SplitTableTime();
//			st_new.setStart_time(online_time);
//			st_new.setEnd_time(end_time);
//			st_new.setFlag("new");
//			splitTableTimeList.add(st_new);
//		}
//		
//		return splitTableTimeList;
//	}



	/**
	 * 分表需求：获取表名
	 */
	@Override
	public List<String> getExistTableForSplitTable(DbTablePrefix dbTablePrefix, String startDate, String endDate,String clientid)
			throws Exception {
		
		List<String> data = new ArrayList<String>();
		
		//表名前缀
		String tablePrefixStr = dbTablePrefix.getTablePrefix();
		
		if (StringUtils.isBlank(tablePrefixStr) || StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
			return data;
		}
		
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("clientid", clientid);
		sqlParams.put("startDate", startDate);
		sqlParams.put("endDate", endDate);
		
		
		List<String> tableNameList = new ArrayList<>();
		
		List<SmsClientIdentifyLogPo> smsClientIdentifyLogPoList = statDao.selectList("common.getSmsClientIdentifyLogForAll", sqlParams);
		if(smsClientIdentifyLogPoList == null || smsClientIdentifyLogPoList.size() == 0){
			return data;
		}
		
		SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
		
		DateTime begin = new DateTime(sdf.parse(startDate));  
		DateTime end = new DateTime(sdf.parse(endDate));
		Period p = new Period(begin, end, PeriodType.days());
		int days = p.getDays();
		
		for(int i=0; i<=days; i++){
			DateTime dataTimeTemp1 = begin.plusDays(i);
			String tableName = null;
			
			for(int j=0; j<smsClientIdentifyLogPoList.size();j++){
				SmsClientIdentifyLogPo po = smsClientIdentifyLogPoList.get(j);
				DateTime dataTimeTemp2 = new DateTime(po.getDate());
				String formatDateStr = dataTimeTemp1.toString("yyyyMMdd");
				if(dataTimeTemp1.isEqual(dataTimeTemp2) ||  dataTimeTemp1.isAfter(dataTimeTemp2)){
					tableName = tablePrefixStr + po.getIdentify()+"_"+formatDateStr;
				}else{
					if(tableName != null){
						tableNameList.add(tableName);
						break;
					}
				}
				
				if(j == smsClientIdentifyLogPoList.size()-1){
					if(tableName != null){
						tableNameList.add(tableName);
						break;
					}
				}
			}
		}
		
		if(tableNameList.size() == 0){
			return data;
			
		}
		System.out.println("tableNameList:--------------->"+tableNameList.toString());
		
		if(tableNameList.size() > 0){
			List<Map<String, Object>> existList = statDao.getSearchList("common.getExistTable", tableNameList);// 查询在日期范围内存在的语音表、短信表、即时消息表
			for (Map<String, Object> map : existList) {
				data.add(map.get("table_name").toString());
			}
		}
		
		return data;
		
	}


	@Override
	public void randCheckCodePic(HttpServletRequest request, HttpServletResponse response){
		try {
			CheckCodeUtil.makeCheckCode(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
			logger.error("-----------------获取图片验证码失败------------------");
			logger.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("-----------------获取图片验证码失败------------------");
			logger.error(e.getMessage());
		}
	}

	@Override
	public boolean randCodeCheck(HttpServletRequest request) throws Exception {
		return CheckCodeUtil.check(request);
	}
	
	@Override
	public Map<String, Object> clientAccountStatusCheck(String clientId) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		Map<String, Object> clientAccountStatus = masterDao.selectOne("smsSend.getClientAccountStatus", clientId);
		
		if(String.valueOf(clientAccountStatus.get("oauthStatus")).equals("3")){
			result.put("oauthStatus", true);
		}else{
			result.put("oauthStatus", false);
		}
		
		// 1：注册完成，5：冻结，6：注销，7：锁定
		result.put("accountStatus", clientAccountStatus.get("accountStatus"));
		
		return result;
	}

	@Override
	public Map<String, Object> isExistOauthPicInfo(String client_id) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String,Object> picNumMap = masterDao.selectOne("smsSend.checkIsExistOauthPicInfo", client_id);
		
		Long picNum = (Long) picNumMap.get("picNum");
		if(picNum > 0){
			result.put("isExist", true);
		}else{
			result.put("isExist", false);
		}
		
		return result;
	}
	
	
	
	
	
}
