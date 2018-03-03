package com.ucpaas.smsp.dao.bussiness;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ucpaas.smsp.dao.MasterDao;
import com.ucpaas.smsp.dao.StatDao;
import com.ucpaas.smsp.model.PageContainer;
import com.ucpaas.smsp.model.SmsClientTariff;

@Repository
public class BussinessDao {

	@Autowired
	private MasterDao masterDao;
	
	@Autowired
	private StatDao statDao;
	
	public PageContainer queryDayStat(Map<String, Object> params) throws Exception {
		return statDao.getSearchPage("bussiness.queryDayStat", "bussiness.queryDayStatCount", params);
	}

	public PageContainer queryMonthStat(Map<String, Object> params) throws Exception {
		return statDao.getSearchPage("bussiness.queryMonthStat", "bussiness.queryMonthStatCount", params);
	}
	
	public PageContainer querySmsRecordPage(Map<String, Object> params) throws Exception {
		return statDao.getSearchPage("bussiness.querySmsSendRecord", "bussiness.querySmsSendRecordCount", params);
	}
	
	public List<Map<String, Object>> querySmsSendRecordCount(Map<String, Object> params) throws Exception{
		return statDao.selectList("bussiness.querySmsSendRecordCount", params);
	}
	
	public List<Map<String, Object>> querySmsRecord4Excel(Map<String, Object> params) throws Exception {
		return statDao.getSearchList("bussiness.querySmsSendRecord4Excel", params);
	}
	
	public List<SmsClientTariff> querySmsClientTariffList(Map<String,Object> sqlParams){
		return masterDao.selectList("smsClientTariffMapper.querySmsClientTariffList", sqlParams);
	}
	
	public Integer querySmsClientTariffListCount(Map<String,Object> sqlParams){
		return masterDao.selectOne("smsClientTariffMapper.querySmsClientTariffListCount", sqlParams);
	}
	public PageContainer queryStatistic(Map<String, Object> params) {
		return statDao.getSearchPage("bussiness.queryStatistic", "bussiness.queryStatisticCount", params);
	}

	public PageContainer queryStatistic1(Map<String, Object> params) {
		return statDao.getSearchPage("bussiness.queryStatistic1", "bussiness.queryStatisticCount1", params);
	}

	public Map queryStatisticTotal(Map<String, Object> formData) {
		return statDao.selectOne("bussiness.queryStatisticTotal", formData);
	}


}
