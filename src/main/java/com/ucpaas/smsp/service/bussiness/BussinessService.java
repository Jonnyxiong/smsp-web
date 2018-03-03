package com.ucpaas.smsp.service.bussiness;

import java.util.List;
import java.util.Map;

import com.ucpaas.smsp.model.PageBean;
import com.ucpaas.smsp.model.PageContainer;
import com.ucpaas.smsp.model.param.PageParam;

public interface BussinessService {
	
	PageContainer queryDayStat(Map<String, Object> params) throws Exception;
	
	PageContainer queryMonthStat(Map<String, Object> params) throws Exception;
	
	PageContainer querySmsSendRecord(Map<String, Object> params) throws Exception;
	
	List<Map<String, Object>> querySmsSendRecordCount(Map<String, Object> params) throws Exception;
	
	List<Map<String, Object>> querySmsRecord4Excel(Map<String, Object> params) throws Exception;
	
	PageBean queryInterShortMessage(PageParam pageParam,String countryInfo);

    List<Map<String,Object>> exportBussinessData(Map<String, Object> queryParams);

	List<Map<String,Object>> exportMonthStat(Map params);

    int exportBussinessDataCount(Map<String, Object> queryParams);

	PageContainer queryStatistic(Map<String, Object> params) throws Exception;

	Map queryStatisticTotal(Map<String, Object> formData) throws Exception;

	List<Map<String, Object>> exportStatistics(Map<String, Object> formData) throws Exception;
}
