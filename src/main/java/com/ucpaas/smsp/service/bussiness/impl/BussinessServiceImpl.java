package com.ucpaas.smsp.service.bussiness.impl;

import java.math.BigDecimal;
import java.util.*;

import com.ucpaas.smsp.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucpaas.smsp.constant.Constant.DbTablePrefix;
import com.ucpaas.smsp.dao.MasterDao;
import com.ucpaas.smsp.dao.StatDao;
import com.ucpaas.smsp.dao.bussiness.BussinessDao;
import com.ucpaas.smsp.model.PageBean;
import com.ucpaas.smsp.model.PageContainer;
import com.ucpaas.smsp.model.SmsClientTariff;
import com.ucpaas.smsp.model.param.PageParam;
import com.ucpaas.smsp.service.bussiness.BussinessService;
import com.ucpaas.smsp.service.common.CommonService;

@Service
@Transactional
public class BussinessServiceImpl implements BussinessService {
	
	@Autowired
	private CommonService commonService;

	@Autowired
	private BussinessDao bussinessDao;
	@Autowired
	private MasterDao masterDao;

	@Autowired
	private StatDao statDao;
	
	@Override
	public PageContainer queryDayStat(Map<String, Object> params) throws Exception {
		return bussinessDao.queryDayStat(params);
	}

	@Override
	public PageContainer queryMonthStat(Map<String, Object> params) throws Exception {
		return bussinessDao.queryMonthStat(params);
	}

	@Override
	public PageContainer querySmsSendRecord(Map<String, Object> params) throws Exception {
		
		//==============================分表需求-开始==================================
//		List<String> tableList = new ArrayList<>();
//		List<SplitTableTime> splitTableTimeList = commonService.getSplitTableTime(params.get("start_time"), params.get("end_time"));
//		for(SplitTableTime st : splitTableTimeList){
//			String flag = st.getFlag();
//			String start_time = st.getStart_time();
//			String end_time = st.getEnd_time();
//			
//			List<String> tableListTemp = null;
//			if("old".equals(flag)){
//				tableListTemp = commonService.getExistTable(DbTablePrefix.T_SMS_ACCESS_YYYYMMDD, start_time,end_time);
//			}else{
//				tableListTemp = commonService.getExistTable(DbTablePrefix.T_SMS_ACCESS_YYYYMMDD, start_time,end_time,params.get("clientid"));
//			}
//			tableList.addAll(tableListTemp);
//		}
		
		String start_time = (String) params.get("start_time");
		String end_time = (String) params.get("end_time");
		List<String> tableList = new ArrayList<>();
		
		List<String> tableListTemp_old = commonService.getExistTable(DbTablePrefix.T_SMS_ACCESS_YYYYMMDD, start_time,end_time);
		List<String> tableListTemp_new = commonService.getExistTable(DbTablePrefix.T_SMS_ACCESS_YYYYMMDD, start_time,end_time, (String) params.get("clientid"));
		tableList.addAll(tableListTemp_old);
		tableList.addAll(tableListTemp_new);
		
		
		//==============================分表需求-结束==================================
		
		if (tableList.size() > 0) {
			Map<String, Object> p = new HashMap<String, Object>();
			p.putAll(params);
			p.put("table_list", tableList);
			return bussinessDao.querySmsRecordPage(p);
		}
		
		return new PageContainer();
	}
	
	

	@Override
	public List<Map<String, Object>> querySmsSendRecordCount(Map<String, Object> params) throws Exception {
		
		
		//==============================分表需求-开始==================================
//		List<String> tableList = new ArrayList<>();
//		List<SplitTableTime> splitTableTimeList = commonService.getSplitTableTime(params.get("start_time"), params.get("end_time"));
//		for(SplitTableTime st : splitTableTimeList){
//			String flag = st.getFlag();
//			String start_time = st.getStart_time();
//			String end_time = st.getEnd_time();
//			
//			List<String> tableListTemp = null;
//			if("old".equals(flag)){
//				tableListTemp = commonService.getExistTable(DbTablePrefix.T_SMS_ACCESS_YYYYMMDD, start_time,end_time);
//			}else{
//				tableListTemp = commonService.getExistTable(DbTablePrefix.T_SMS_ACCESS_YYYYMMDD, start_time,end_time,params.get("clientid"));
//			}
//			tableList.addAll(tableListTemp);
//		}
		
		String start_time = (String) params.get("start_time");
		String end_time = (String) params.get("end_time");
		List<String> tableList = new ArrayList<>();
		
		List<String> tableListTemp_old = commonService.getExistTable(DbTablePrefix.T_SMS_ACCESS_YYYYMMDD, start_time,end_time);
		List<String> tableListTemp_new = commonService.getExistTable(DbTablePrefix.T_SMS_ACCESS_YYYYMMDD, start_time,end_time, (String) params.get("clientid"));
		tableList.addAll(tableListTemp_old);
		tableList.addAll(tableListTemp_new);
		
		//==============================分表需求-结束==================================
		
		if (tableList.size() > 0) {
			Map<String, Object> p = new HashMap<String, Object>();
			p.putAll(params);
			p.put("table_list", tableList);
			return bussinessDao.querySmsSendRecordCount(p);
		}
		return new ArrayList<Map<String, Object>>();
	}

	@Override
	public PageBean queryInterShortMessage(PageParam pageParam, String countryInfo) {
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("countryInfo", countryInfo);
		sqlParams.put("startIndex", pageParam.getStartIndex());
		sqlParams.put("pageSize", pageParam.getPageSize());
		List<SmsClientTariff> tariffList = bussinessDao.querySmsClientTariffList(sqlParams);
		int totalRows = bussinessDao.querySmsClientTariffListCount(sqlParams);
		
		PageBean pageBean = new PageBean(pageParam.getPageSize(),pageParam.getGoalPage(),totalRows);
		pageBean.setList(tariffList);
		return pageBean;
	}

	@Override
	public List<Map<String, Object>> querySmsRecord4Excel(Map<String, Object> params) throws Exception {
		
		//==============================分表需求-开始==================================
//		List<String> tableList = new ArrayList<>();
//		List<SplitTableTime> splitTableTimeList = commonService.getSplitTableTime(params.get("start_time"), params.get("end_time"));
//		for(SplitTableTime st : splitTableTimeList){
//			String flag = st.getFlag();
//			String start_time = st.getStart_time();
//			String end_time = st.getEnd_time();
//			
//			List<String> tableListTemp = null;
//			if("old".equals(flag)){
//				tableListTemp = commonService.getExistTable(DbTablePrefix.T_SMS_ACCESS_YYYYMMDD, start_time,end_time);
//			}else{
//				tableListTemp = commonService.getExistTable(DbTablePrefix.T_SMS_ACCESS_YYYYMMDD, start_time,end_time,params.get("clientid"));
//			}
//			tableList.addAll(tableListTemp);
//		}
		
		String start_time = (String) params.get("start_time");
		String end_time = (String) params.get("end_time");
		List<String> tableList = new ArrayList<>();
		
		List<String> tableListTemp_old = commonService.getExistTable(DbTablePrefix.T_SMS_ACCESS_YYYYMMDD, start_time,end_time);
		List<String> tableListTemp_new = commonService.getExistTable(DbTablePrefix.T_SMS_ACCESS_YYYYMMDD, start_time,end_time, (String) params.get("clientid"));
		tableList.addAll(tableListTemp_old);
		tableList.addAll(tableListTemp_new);
		
		//==============================分表需求-结束==================================
		
		if (tableList.size() > 0) {
			Map<String, Object> p = new HashMap<String, Object>();
			p.putAll(params);
			p.put("table_list", tableList);
			return bussinessDao.querySmsRecord4Excel(p);
		}
		return new ArrayList<Map<String, Object>>();
	}

	/**
	 *
	 *   客户id
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageContainer queryStatistic(Map<String, Object> params) throws Exception {
		int startDate = (int) (params.get("startDate")==null?0:params.get("startDate"));
		int endDate = (int) (params.get("endDate")==null?0:params.get("endDate"));
		if(startDate>endDate){
			return new PageContainer();
		}

		formatStatisticFormData(params, startDate, endDate,true);
		PageContainer page=new PageContainer();
//		if((params.get("paytype").toString()).equals("0")){ //预付费加产品类型维度
//			page= bussinessDao.queryStatistic(params);
//		}else {
//			page= bussinessDao.queryStatistic1(params);
//		}
		page= bussinessDao.queryStatistic1(params);
		return page;
	}

	@Override
	public Map queryStatisticTotal(Map<String, Object> formData) throws Exception {

		int startDate = (int) (formData.get("startDate")==null?0:formData.get("startDate"));
		int endDate = (int) (formData.get("endDate")==null?0:formData.get("endDate"));
		if(startDate>endDate){
			return zeroMap();
		}
		formatStatisticFormData(formData, startDate, endDate,false);

		Map subtotal =  bussinessDao.queryStatisticTotal(formData);



		if(subtotal==null||subtotal.size()==0){
			return zeroMap();
		}
		return subtotal;
	}

	public void formatStatisticFormData(Map<String, Object> formData, int startDate, int endDate,boolean addOneday) throws Exception {
		int before90Int = DateUtil.getDateFromTodayInInt(-90);
		if(startDate<before90Int){
			throw new Exception("开始时间不得超过90天之前");
		}

		if(startDate==0){
			throw new Exception("请输入开始时间");
		}
		if(endDate==0){
			endDate = Integer.valueOf(DateUtil.dateToStr(new Date(), "yyyyMMdd"));
		}

		if(addOneday){
			Calendar endCal = Calendar.getInstance();
			endCal.set(endDate/10000, endDate%10000/100-1, endDate%10000%100);
			endCal.add(Calendar.DATE, 1);
			endDate = Integer.valueOf(DateUtil.dateToStr(endCal.getTime(), "yyyyMMdd"));
			formData.put("startDate", startDate);
			formData.put("endDate", endDate);
		}
	}


	public Map zeroMap(){
		Map subtotal = new HashMap<>();
		BigDecimal num_all_total = BigDecimal.ZERO;
		BigDecimal num_sucs_total = BigDecimal.ZERO;
		BigDecimal num_fail_total = BigDecimal.ZERO;
		BigDecimal num_known_total = BigDecimal.ZERO;
		BigDecimal num_pending_total = BigDecimal.ZERO;
		BigDecimal chargeTotal_total = BigDecimal.ZERO;
		BigDecimal intercept_total = BigDecimal.ZERO;

		subtotal.put("num_all_total",num_all_total);
		subtotal.put("chargeTotal_total",chargeTotal_total);
		subtotal.put("num_sucs_total",num_sucs_total );
		subtotal.put("num_known_total",num_known_total);
		subtotal.put("num_fail_total",num_fail_total);
		subtotal.put("num_pending_total",num_pending_total);
		subtotal.put("num_intercept_total",intercept_total);

		return subtotal;
	}

	@Override
	public List<Map<String, Object>> exportStatistics(Map<String, Object> formData) throws Exception {
		int startDate = (int) (formData.get("startDate")==null?0:formData.get("startDate"));
		int endDate = (int) (formData.get("endDate")==null?0:formData.get("endDate"));
		if(startDate>endDate){
			return new ArrayList<>();
		}
		formatStatisticFormData(formData, startDate, endDate,true);
		List<Map<String, Object>> data =new ArrayList<Map<String, Object>>();
//		if((formData.get("paytype").toString()).equals("0")){ //预付费加产品类型维度
//			data= statDao.getSearchList("bussiness.queryStatistic1", formData);
//		}else {
//			data=statDao.getSearchList("bussiness.queryStatistic", formData);
//		}
		data=statDao.getSearchList("bussiness.queryStatistic", formData);
		return data;
	}

	@Override
	public List<Map<String, Object>> exportBussinessData(Map<String, Object> queryParams) {
		return statDao.selectList("bussiness.queryDayStat",queryParams);
	}

	@Override
	public List<Map<String, Object>> exportMonthStat(Map params) {
		return statDao.selectList("bussiness.queryMonthStat",params);
	}

	@Override
	public int exportBussinessDataCount(Map<String, Object> queryParams) {
		return statDao.selectOne("bussiness.queryDayStatCountInt",queryParams);
	}
}
