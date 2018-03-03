package com.ucpaas.smsp.service.home.impl;

import com.ucpaas.smsp.dao.HomeDao;
import com.ucpaas.smsp.model.AgentApplyVO;
import com.ucpaas.smsp.service.common.SMSSendService;
import com.ucpaas.smsp.service.home.HomeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class HomeServiceImpl implements HomeService {
	
	@Autowired
	SMSSendService smsSendService;
	@Autowired
	HomeDao homeDao;
	
	Logger logger = LoggerFactory.getLogger(HomeServiceImpl.class);

	@Override
	public Map<String, Object> smsExperience(String mobile, String ip) throws Exception {
		return smsSendService.smsExperience(mobile, ip);
	}

	@Override
	public Map<String, Object> agentApply(AgentApplyVO agentApplyVO) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> checkResult = new HashMap<String, Object>();
		
		Map<String, Object> checkInApplyTable = homeDao.agentApplyCheckInApply(agentApplyVO);
		checkResult = checkEmailAndMobileEnable(checkInApplyTable);
		if(checkResult != null){
			return checkResult;
		}

		Map<String, Object> checkInUserTable = homeDao.agentApplyCheckInUser(agentApplyVO);
		checkResult = checkEmailAndMobileEnable(checkInUserTable);
		if(checkResult != null){
			return checkResult;
		}
		
		int insertNum = homeDao.agentApply(agentApplyVO);
		if(insertNum == 1){
			result.put("success", true);
			result.put("msg", "申请代理商成功");
		}else{
			result.put("success", false);
			result.put("msg", "申请代理商失败,请联系客服");
		}
		return result;
	}
	
	private Map<String, Object> checkEmailAndMobileEnable(Map<String, Object> map){
		if(null != map){
			Map<String, Object> result = new HashMap<String, Object>();
			String email = Objects.toString(map.get("email"), "");
			String mobile = Objects.toString(map.get("mobile"), "");
			if(StringUtils.isNoneBlank(email)){
				result.put("success", false);
				result.put("errorcode", 1);
				result.put("msg", "邮箱已经被注册");
				return result;
			}
			if(StringUtils.isNoneBlank(mobile)){
				result.put("success", false);
				result.put("errorcode", 2);
				result.put("msg", "手机已经被注册");
				return result;
			}
			
		}
		
		return null;
	}

}
