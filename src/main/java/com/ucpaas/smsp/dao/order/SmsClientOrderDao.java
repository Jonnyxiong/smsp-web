package com.ucpaas.smsp.dao.order;

import com.ucpaas.smsp.dao.MasterDao;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.model.po.SmsClientOrderPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SmsClientOrderDao {
	
	
	@Autowired
	private MasterDao masterDao;
	
	public void insertOrderBatch(Map<String,Object> sqlParams){
		masterDao.insert("order.SmsClientOrderMapper.insertOrderBatch", sqlParams);
	}
	
	public List<SmsClientOrderPo> getOrderListInfo(Map<String,Object> sqlParams){
		return masterDao.selectList("order.SmsClientOrderMapper.getOrderListInfo", sqlParams);
	}

	public List<Map<String,Object>> getOrderListInfo4export(Map<String,Object> sqlParams){
		return masterDao.selectList("order.SmsClientOrderMapper.getOrderListInfo4export", sqlParams);
	}
	
	public Integer getOrderListInfoCount(Map<String,Object> sqlParams){
		return masterDao.selectOne("order.SmsClientOrderMapper.getOrderListInfoCount", sqlParams);
	}
	
	public String getMostNum(Map<String,Object> sqlParams){
		return masterDao.selectOne("order.SmsClientOrderMapper.getMostNum", sqlParams);
	}
	
	public SmsClientOrderPo getOrderDetailBySubId(Map<String,Object> sqlParams){
		return masterDao.selectOne("order.SmsClientOrderMapper.getOrderDetailBySubId", sqlParams);
	}
	
	public void updateOrderStatus(Map<String,Object> sqlParams){
		masterDao.update("order.SmsClientOrderMapper.updateOrderStatus", sqlParams);
	}
	
	public void cancelOrder(Map<String,Object> sqlParams){
		masterDao.update("order.SmsClientOrderMapper.cancelOrder", sqlParams);
	}
	

	public List<SmsClientOrderPo> getOrderListByProductType(Map<String,Object> sqlParams){
		return masterDao.selectList("order.SmsClientOrderMapper.getOrderListByProductType", sqlParams);
	}
	public Integer getOrderListByProductTypeCount(Map<String,Object> sqlParams){
		return masterDao.selectOne("order.SmsClientOrderMapper.getOrderListByProductTypeCount", sqlParams);
	}

	public List<Map> listRemain(Map<String, Object> sqlParams) {
		return masterDao.selectList("order.SmsClientOrderMapper.listRemain", sqlParams);
	}
	public List<Map> listRemain1(Map<String, Object> sqlParams) {
		return masterDao.selectList("order.SmsClientOrderMapper.listRemain1", sqlParams);
	}


	public int listRemainCount(Map<String, Object> sqlParams) {
		return masterDao.selectOne("order.SmsClientOrderMapper.listRemainCount", sqlParams);
	}

	public List<Integer> getProductTypes(Map<String, Object> orderParam) {
		return masterDao.selectList("order.SmsClientOrderMapper.getProductTypes", orderParam);
	}
	public List<Map> getOrderRemainQuantity(SmsAccountModel account){
		return masterDao.selectList("order.SmsClientOrderMapper.getOrderRemainQuantity", account);
	}

}
