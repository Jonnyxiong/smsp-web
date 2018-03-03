/**    
 * @Title: AccountDao.java  
 * @Package: com.ucpaas.smsp.dao.account  
 * @Description: TODO
 * @author: Niu.T    
 * @date: 2016年9月23日 下午3:14:38  
 * @version: V1.0    
 */
package com.ucpaas.smsp.dao.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ucpaas.smsp.dao.MasterDao;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.model.SmsOauthPic;

/**
 * @ClassName: AccountDao
 * @Description: TODO
 * @author: Niu.T
 * @date: 2016年9月23日 下午3:14:38
 */
@Repository
public class AccountDao {
	@Autowired
	private MasterDao masterDao;

	/**
	 * 获取用户账户的基本信息(根据clientId)
	 */
	public SmsAccountModel getAccountInfo(String clientId) {
		return masterDao.selectOne("smsAcc.getAccountInfo", clientId);
	}

	/**
	 * 修改账户的基本信息
	 */
	public int updateAccInfo(SmsAccountModel smsAccountModel) {
		return masterDao.update("smsAcc.updateAccountInfo", smsAccountModel);
	}

	/**
	 * 获取用户上传的资质信息(根据clientId)
	 */
	public SmsOauthPic getCerInfo(String clientId) {
		return masterDao.selectOne("smsAcc.getCerInfo", clientId);
	}
	
	/**
	 * 获取用户资质为通过的原因(根据clientId)
	 */
	public String getNotPassRemark(String clientId) {
		return masterDao.selectOne("smsAcc.getNotPassRemark", clientId);
	}

	/**
	 * 添加用户资质信息
	 */
	public int addCerInfo(SmsOauthPic smsOauthPic) {
		return masterDao.insert("smsAcc.addCerInfo", smsOauthPic);
	}

	/**
	 * 修改用户资质信息
	 */
	public int updateCerInfo(SmsOauthPic smsOauthPic) {
		return masterDao.update("smsAcc.updateCerInfo", smsOauthPic);
	}

	/**
	 * 用户信息更改时,更新账户信息
	 */
	public int updateAccWithCer(SmsOauthPic SmsOauthPic) {
		
		return masterDao.update("smsAcc.updateAccountWithCer", SmsOauthPic);
	}

	/**
	 * 验证客户的密码(clientId,password)
	 */
	public String checkPassword(SmsAccountModel smsAccountModel) {
		return masterDao.selectOne("smsAcc.checkPassword", smsAccountModel);
	}

	/**
	 * 修改用户账户密码
	 */
	public int updatePassword(SmsAccountModel smsAccountModel) {
		return masterDao.update("smsAcc.updatePassword", smsAccountModel);
	}
}
