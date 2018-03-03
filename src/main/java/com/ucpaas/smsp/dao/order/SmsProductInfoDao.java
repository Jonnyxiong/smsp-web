package com.ucpaas.smsp.dao.order;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ucpaas.smsp.dao.MasterDao;
import com.ucpaas.smsp.model.po.SmsProductInfoPo;


@Repository
public class SmsProductInfoDao {
	
	@Autowired
	private MasterDao masterDao;
	
	
	public List<SmsProductInfoPo> getProductListByClientId(Map<String,Object> sqlParams){
		return masterDao.selectList("order.SmsProductInfoMapper.getProductListByClientId", sqlParams);
	}
	
	public Integer getProductListByClientIdCount(Map<String,Object> sqlParams){
		return masterDao.selectOne("order.SmsProductInfoMapper.getProductListByClientIdCount", sqlParams);
	}
	
	public List<SmsProductInfoPo> getProductListByProductCode(Map<String,Object> sqlParams){
		return masterDao.selectList("order.SmsProductInfoMapper.getProductListByProductCode", sqlParams);
	}
	
	public int queryCountAgentProduct(Map<String,Object> sqlParams){
		return masterDao.selectOne("order.SmsProductInfoMapper.queryCountAgentProduct", sqlParams);
	}
	
	

}
