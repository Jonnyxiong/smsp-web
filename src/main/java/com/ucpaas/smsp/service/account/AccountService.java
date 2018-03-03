/**    
 * @Title: AccountService.java  
 * @Package: com.ucpaas.smsp.service.account  
 * @Description: TODO
 * @author: Niu.T    
 * @date: 2016年9月23日 下午2:24:20  
 * @version: V1.0    
 */
package com.ucpaas.smsp.service.account;

import java.util.Map;

import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.model.SmsOauthPic;

/**  
 * @ClassName: AccountService  
 * @Description: 账户管理Service:(1)账户资料(2)账户安全
 * @author: Niu.T 
 * @date: 2016年9月23日 下午2:24:20  
 */
public interface AccountService {

	/**
	 * @Description: 获取用户账户的基本信息(根据clientId)
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午2:33:24  
	 * @param clientId
	 * @return: SmsAccountModel
	 */
	SmsAccountModel getAccountInfo(String clientId);
	
	/**
	 * @Description: 修改账户的基本信息,返回结果信息(map)
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午2:36:34  
	 * @param smsAccountModel
	 * @return: Map<String,Object>
	 */
	Map<String,Object> updateAccInfo(SmsAccountModel smsAccountModel);
	
	/**
	 * @Description: 获取用户上传的资质信息(根据clientId)
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午2:40:57  
	 * @param clientId
	 * @return: SmsOauthPic
	 */
	SmsOauthPic getCerInfo(String clientId);
	
	/**
	 * @Description: 添加用户资质信息,返回结果信息(map)
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午2:44:46  
	 * @param smsOauthPic
	 * @return: Map<String,Object>
	 */
	Map<String,Object> addCerInfo(SmsOauthPic smsOauthPic) throws Exception;
	
	/**
	 * @Description: 修改用户资质信息,返回结果信息(map)
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午2:45:09  
	 * @param smsOauthpic
	 * @return: Map<String,Object>
	 */
	Map<String,Object> updateCerInfo(SmsOauthPic smsOauthPic) throws Exception;
	
	/**
	 * @Description: 验证客户的密码(根据Id)
	 * @author: Niu.T 
	 * @date: 2016年9月27日 下午4:07:00  
	 * @param clientId
	 * @return: String
	 */
	boolean checkPasswordById(SmsAccountModel accountModel);
	
	/**
	 * @Description: 修改用户账户密码,返回结果信息(map)
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午2:45:17  
	 * @param smsAccountModel
	 * @return: Map<String,Object>
	 */
	Map<String,Object> updatePassword(SmsAccountModel smsAccountModel);
	
}
