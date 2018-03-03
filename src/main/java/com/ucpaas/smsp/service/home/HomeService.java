package com.ucpaas.smsp.service.home;

import java.util.Map;

import com.ucpaas.smsp.model.AgentApplyVO;

public interface HomeService {

	Map<String, Object> smsExperience(String mobile, String ip) throws Exception;
	
	Map<String, Object> agentApply(AgentApplyVO agentApplyVO) throws Exception;
}
