package com.ucpaas.smsp.service.common.impl;

import com.jsmsframework.user.entity.JsmsClientInfoExt;
import com.jsmsframework.user.service.JsmsClientInfoExtService;
import com.ucpaas.smsp.common.entity.AjaxResult;
import com.ucpaas.smsp.constant.LoginResContent;
import com.ucpaas.smsp.dao.common.SmsAccountDao;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.model.po.SmsAccountModelPo;
import com.ucpaas.smsp.service.common.LoginService;
import com.ucpaas.smsp.util.encrypt.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class LoginServiceImpl implements LoginService{
	
	private Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
	
	@Autowired
	private SmsAccountDao smsAccountDao;

	@Autowired
	private JsmsClientInfoExtService clientInfoExtService;
	
	@Override
	public AjaxResult loginValidate(String loginAccount, String loginPassword) {
		
		AjaxResult result = new AjaxResult();
		
//		loginPassword = MD5.md5(loginPassword);
		
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("loginAccount", loginAccount);
		SmsAccountModelPo model = smsAccountDao.getAccountInfoByLoginAccount(sqlParams);
		
		if(model == null){
			result.setIsSuccess(false);
			result.setMessage(LoginResContent.账号不存在.toString());
			return result;
		}

		// Add by lpjLiu 20170926 v2.2.2 v5.14.0 增加帐号扩展属性
		JsmsClientInfoExt clientInfoExt = clientInfoExtService.getByClientId(model.getClientId());
		model.setClientInfoExt(clientInfoExt);
		if(model.getClientInfoExt() == null){
			result.setIsSuccess(false);
			result.setMessage(LoginResContent.账号不存在.toString());
			return result;
		}
		
		int status = model.getStatus();
		if(status == 0){
			result.setIsSuccess(false);
			result.setMessage(LoginResContent.账号未激活.toString());
			return result;
		}
		
		if(status == 5){
			result.setIsSuccess(false);
			result.setMessage(LoginResContent.账号已冻结.toString());
			return result;
		}
		
		if(status == 6){
			result.setIsSuccess(false);
			result.setMessage(LoginResContent.账号已注销.toString());
			return result;
		}
		
		if(status == 7){
			result.setIsSuccess(false);
			result.setMessage(LoginResContent.账号已锁定.toString());
			return result;
		}

		Integer agent_id=model.getAgentId();
		Map<String ,Object> sqlparam=new HashMap<String,Object>();
		sqlparam.put("agent_id",agent_id);
		//为空or小于20为直客
		if(null!=agent_id && agent_id>20){
			Map<String,Object> result1=smsAccountDao.getAgentInfoByAgentId(sqlparam);
			if("5".equals(result1.get("agent_type").toString())){
				result.setIsSuccess(false);
				result.setMessage("OEM客户不能登录品牌客戶系统，请联系代理商");
				return result;
			}
		}

		// Mod by lpjLiu 20170926 v2.2.2 v5.14.0 增加帐号扩展属性
//		String password = model.getPassword() == null?"":model.getPassword();
		String password = model.getClientInfoExt().getWebPassword() == null?"":model.getClientInfoExt().getWebPassword();

//		password = MD5.md5(EncryptUtils.encodeMd5(password));
		password = EncryptUtils.encodeMd5(password);
		
		if(!password.equals(loginPassword)){
			result.setIsSuccess(false);
			result.setMessage(LoginResContent.密码错误.toString());
			return result;
		}
		
		result.setIsSuccess(true);
		result.setMessage(LoginResContent.登录成功.toString());
		result.setData(model);
		System.out.println(System.currentTimeMillis());
		return result;
	}

	@Override
	public SmsAccountModel getLoginClientInfo(String clientId) {
//		AjaxResult result = new AjaxResult();
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("clientId", clientId);
		SmsAccountModel model = smsAccountDao.getAccountInfoByClientId(sqlParams);
//		result.setData(model);;
		return model;
	}

	@Override
	public SmsAccountModel getApiClientInfo(String clientid) {
		return smsAccountDao.getApiClientInfo(clientid);
	}
	
	

}
