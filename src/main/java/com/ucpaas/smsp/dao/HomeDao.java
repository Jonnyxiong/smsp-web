package com.ucpaas.smsp.dao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ucpaas.smsp.model.AgentApplyVO;

@Repository
public class HomeDao {
	
	@Autowired
	private MasterDao masterDao;
	
	public int agentApply(AgentApplyVO agentApplyVO) throws Exception {
		return masterDao.insert("home.insertAgentApply", agentApplyVO);
	}
	
	public Map<String, Object> agentApplyCheckInApply(AgentApplyVO agentApplyVO) throws Exception {
		return masterDao.selectOne("home.agentApplyCheckInApply", agentApplyVO);
	}
	
	public Map<String, Object> agentApplyCheckInUser(AgentApplyVO agentApplyVO) throws Exception {
		return masterDao.selectOne("home.agentApplyCheckInUser", agentApplyVO);
	}
	
}
