package com.ucpaas.smsp.model.param;

import java.io.Serializable;
import java.util.List;

import com.ucpaas.smsp.model.po.SmsProductInfoPo;

public class PlaceOrderParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1988918968597053473L;

	private String clientid;
	
	//代理商id
	private int agentId;
	
	private List<SmsProductInfoPo> productInfoPoList;

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	
	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public List<SmsProductInfoPo> getProductInfoPoList() {
		return productInfoPoList;
	}

	public void setProductInfoPoList(List<SmsProductInfoPo> productInfoPoList) {
		this.productInfoPoList = productInfoPoList;
	}
	
	

}
