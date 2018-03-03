package com.ucpaas.smsp.model.po;

import java.math.BigDecimal;

import com.ucpaas.smsp.model.SmsProductInfo;

public class SmsProductInfoPo extends SmsProductInfo{
	
	private String operatorName;
	
	private String productTypeName;
	
	private String clientid;
	
	private BigDecimal productNum;
	
	private String createTimeStr;
	
	private String activePeriodStr;
	
	private String quantityStr;
	
	private String productPriceStr;
	
	private String totalPrice;

	private String areaCodeName;

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getProductTypeName() {
		return productTypeName;
	}

	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public BigDecimal getProductNum() {
		return productNum;
	}

	public void setProductNum(BigDecimal productNum) {
		this.productNum = productNum;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getActivePeriodStr() {
		return activePeriodStr;
	}

	public void setActivePeriodStr(String activePeriodStr) {
		this.activePeriodStr = activePeriodStr;
	}

	public String getQuantityStr() {
		return quantityStr;
	}

	public void setQuantityStr(String quantityStr) {
		this.quantityStr = quantityStr;
	}

	public String getProductPriceStr() {
		return productPriceStr;
	}

	public void setProductPriceStr(String productPriceStr) {
		this.productPriceStr = productPriceStr;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getAreaCodeName() {
		return areaCodeName;
	}

	public void setAreaCodeName(String areaCodeName) {
		this.areaCodeName = areaCodeName;
	}
}
