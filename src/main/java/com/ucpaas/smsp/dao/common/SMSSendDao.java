package com.ucpaas.smsp.dao.common;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ucpaas.smsp.dao.MasterDao;
import com.ucpaas.smsp.dao.StatDao;

@Repository
public class SMSSendDao {
	
	@Autowired
	private MasterDao masterDao;
	@Autowired
	private StatDao statDao;
	
	public Map<String, Object> smsExperienceCheck(Map<String, String> params){
		return statDao.selectOne("smsSend.smsExperienceCheck", params);
	}
	
	public int insertExpRecord(Map<String, String> params){
		return statDao.insert("smsSend.insertExpRecord", params);
	}
	
	public int smsAccUsableCheck(Map<String, String> params){
		return masterDao.selectOne("smsSend.smsAccUsableCheck", params);
	}
	
	public int getClientPaytype(String clientId){
		return masterDao.selectOne("smsSend.getClientPaytype", clientId);
	}
	
	public Map<String, Object> getClientAccountStatus(String clientId){
		return masterDao.selectOne("smsSend.getClientAccountStatus", clientId);
	}
	

}
