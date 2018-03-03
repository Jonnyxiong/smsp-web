package com.ucpaas.smsp.dao.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ucpaas.smsp.dao.MasterDao;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.model.po.SmsAccountModelPo;

@Repository
public class SmsAccountDao {

	@Autowired
	private MasterDao masterDao;
	
	public Map<String, Object> getAccountInfo(String loginAccount) {
		Map<String, Object> sqlParams = new HashMap<>();
		sqlParams.put("loginAccount", loginAccount);
		return masterDao.selectOne("smsAccount.getAccountInfo", sqlParams);
	}

	public SmsAccountModelPo getAccountInfoByLoginAccount(
			Map<String, Object> sqlParams) {
		SmsAccountModelPo po =  masterDao.selectOne("smsAccount.getAccountInfoByLoginAccount",
				sqlParams);
		return po;
	}

	public SmsAccountModel getAccountInfoByClientId(
			Map<String, Object> sqlParams) {
		return masterDao.selectOne("smsAccount.getAccountInfoByClientId",
				sqlParams);
	}

	public SmsAccountModel getApiClientInfo(
			String clientid) {
		return masterDao.selectOne("smsAccount.getApiClientInfo",
				clientid);
	}

	public Map<String,Object> getAgentInfoByAgentId(Map<String,Object> sqlParams){
		return masterDao.selectOne("smsAccount.getAgentInfoByAgentId",
				sqlParams);
	}

//	// 查询用户基本资料
//	public SmsAccountModel queryMetod(Map<String, Object> sqlParams)
//			throws Exception {
//		return masterDao.selectOne("smsAccount.getAccountInformation",
//				sqlParams);
//
//	}
//
//	// 查询用户证件资料
//	public SmsOauthPic getSmsOauthPicInfo(Map<String, Object> sqlParams)
//			throws Exception {
//		return masterDao.selectOne("smsOauthpic.getSmsOauthPicInfo", sqlParams);
//	}
//
//	// 修改用户基本信息
//	public void updateAccountInfo(SmsAccountModel smsAccountModel)
//			throws Exception {
//		masterDao.update("smsAccount.updateAccountInfo", smsAccountModel);
//	}
//
//	// 账户安全：修改登录密码
//	public void updateClientPassword(SmsAccountModel smsAccountModel)
//			throws Exception {
//		masterDao.update("smsAccount.updateClientPassword", smsAccountModel);
//	}
//
//	// 上传证件照图片
//	public void updateClientPhoto(SmsOauthPic smsOauthPic) throws Exception {
//		masterDao.update("smsOauthpic.updateClientPhoto", smsOauthPic);
//	}
	
	
	
//
//	// 通过客户ID查询客户登录密码
//	public String getPasswordByClientId(Map<String, Object> sqlParams) {
//		return masterDao.selectOne("smsAccount.getPasswordByClientId",sqlParams);
//	}
//
//	// 插入用户资质信息
//	public void insertSmsOauthPicInfo(SmsOauthPic smsOauthPic) throws Exception {
//		masterDao.insert("smsOauthpic.insertSmsOauthPicInfo", smsOauthPic);
//	}
//
//	// 通过ID更新账户真实姓名
//	public void updateClientRealname(SmsOauthPic smsOauthPic) throws Exception {
//		masterDao.update("smsAccount.updateClientRealname", smsOauthPic);
//	}
//
//	// 更新个人用户的资质信息
//	public void updatePersonalPhoto(SmsOauthPic smsOauthPic) throws Exception {
//		masterDao.update("smsOauthpic.updatePersonalPhoto", smsOauthPic);
//	}
//
//	// 更新公司客户的资质信息
//	public void updateCompanyPhoto(SmsOauthPic smsOauthPic) throws Exception {
//		masterDao.update("smsOauthpic.updateCompanyPhoto", smsOauthPic);
//	}
//
//	
//	// 插入个人客户资质信息
//	public void insertClientPhoto(SmsOauthPic smsOauthPic) throws Exception {
//		masterDao.insert("smsOauthpic.insertClientPhoto", smsOauthPic);
//	}
//	
//	//通过客户ID在用户信息表里查询认证状态
//	public SmsAccountModel getOauthStatusByClientId(Map<String, Object> sqlParams) throws Exception{
//		return masterDao.selectOne("smsAccount.getOauthStatusByClientId", sqlParams); 
//	}
//	
//	//通过客户ID查询客户审核资料不通过的原因
//	public SmsOauthAuditLog getOauthErrorMessage(Map<String, Object> sqlParams) throws Exception{
//		return masterDao.selectOne("smsOauthAuditLogMapper.getErrorMessage", sqlParams);
//	}
//	
//	//当客户重新提交审核资质，对客户表里的审核状态进行修改
//	public void updateClientOauthStatus(Map<String, Object> sqlParams) throws Exception{
//		masterDao.update("smsAccount.updateClientOauthStatus", sqlParams);
//	}
	
}
