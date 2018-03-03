package com.ucpaas.smsp.service.common;

import com.ucpaas.smsp.common.entity.AjaxResult;
import com.ucpaas.smsp.model.SmsAccountModel;

public interface LoginService {
	
	AjaxResult loginValidate(String loginAccount,String loginPassword);
	
	SmsAccountModel getLoginClientInfo(String clientId);

	/**
	 * @Description: 根据api接口提供的参数(clientid)获取客户的基本信息
	 * @author: Niu.T 
	 * @date: 2017年2月6日    下午12:10:20
	 * @param clientId
	 * @return SmsAccountModel
	 */
	SmsAccountModel getApiClientInfo(String clientId);
}
