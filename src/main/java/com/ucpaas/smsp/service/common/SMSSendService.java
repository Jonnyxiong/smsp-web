package com.ucpaas.smsp.service.common;

import com.ucpaas.smsp.common.entity.ResultVO;
import com.ucpaas.smsp.enums.HttpProtocolType;
import com.ucpaas.smsp.model.AccessSmsBO;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.Map;

public interface SMSSendService {
	
	Map<String, Object> smsExperience(String mobile, String ip) throws Exception;
	
	ResultVO sendSms(AccessSmsBO smsModel, HttpProtocolType httpProtocolType) throws Exception;
	
	Map<String, Object> importMobile(CommonsMultipartFile excel) throws Exception;
	
	Map<String, Object> clientAccountStatusCheck(String clientId) throws Exception;

}
