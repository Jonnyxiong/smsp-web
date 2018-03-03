package com.ucpaas.smsp.model.po;

import com.ucpaas.smsp.model.SmsClientOrder;

public class SmsClientOrderPo extends SmsClientOrder{


	private Integer rownum;

	private String statusName;
	
	private String productTypeName;
	
	private String orderTypeName;
	
	private String endTimeStr;
	
	private String createTimeStr;
	
	private String effectiveTimeStr;
	
	private String activePeriodStr;
	
	private String quantityStr;
	
	private String remainQuantityStr;
	
	private String salePriceStr;



	private String operatorCodeName;

	private  String areaCodeName;

	private  String productCode;
	

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getProductTypeName() {
		return productTypeName;
	}

	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}

	public String getOrderTypeName() {
		return orderTypeName;
	}

	public void setOrderTypeName(String orderTypeName) {
		this.orderTypeName = orderTypeName;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getEffectiveTimeStr() {
		return effectiveTimeStr;
	}

	public void setEffectiveTimeStr(String effectiveTimeStr) {
		this.effectiveTimeStr = effectiveTimeStr;
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

	public String getRemainQuantityStr() {
		return remainQuantityStr;
	}

	public void setRemainQuantityStr(String remainQuantityStr) {
		this.remainQuantityStr = remainQuantityStr;
	}

	public String getSalePriceStr() {
		return salePriceStr;
	}

	public void setSalePriceStr(String salePriceStr) {
		this.salePriceStr = salePriceStr;
	}

	public String getOperatorCodeName() {
		return operatorCodeName;
	}

	public void setOperatorCodeName(String operatorCodeName) {
		this.operatorCodeName = operatorCodeName;
	}

	public String getAreaCodeName() {
		return areaCodeName;
	}

	public void setAreaCodeName(String areaCodeName) {
		this.areaCodeName = areaCodeName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public Integer getRownum() {
		return rownum;
	}

	public void setRownum(Integer rownum) {
		this.rownum = rownum;
	}
}
