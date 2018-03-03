package com.ucpaas.smsp.model;

import java.math.BigDecimal;
import java.util.Date;

public class SmsClientOrder {
    private Long subId;

    private Long orderId;

    private String clientId;

    private Integer agentId;

    private Integer productId;

    private Integer productType;
    
    private String productName;
    
    private BigDecimal productNumber;

    private Integer orderType;

    private Integer status;

    private Integer activePeriod;

    private BigDecimal quantity;

    private BigDecimal remainQuantity;

    private BigDecimal salePrice;

    private BigDecimal productCost;

    private Date endTime;

    private Date createTime;

    private Date effectiveTime;

    private Date updateTime;

    private Integer auditType;

    private Integer auditor;

    private Long adminId;


    private Integer operator_code;

    private Integer area_code;

    private BigDecimal unit_price;


    public Long getSubId() {
        return subId;
    }

    public void setSubId(Long subId) {
        this.subId = subId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId == null ? null : clientId.trim();
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getActivePeriod() {
        return activePeriod;
    }

    public void setActivePeriod(Integer activePeriod) {
        this.activePeriod = activePeriod;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getRemainQuantity() {
        return remainQuantity;
    }

    public void setRemainQuantity(BigDecimal remainQuantity) {
        this.remainQuantity = remainQuantity;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getProductCost() {
        return productCost;
    }

    public void setProductCost(BigDecimal productCost) {
        this.productCost = productCost;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getAuditType() {
        return auditType;
    }

    public void setAuditType(Integer auditType) {
        this.auditType = auditType;
    }

    public Integer getAuditor() {
        return auditor;
    }

    public void setAuditor(Integer auditor) {
        this.auditor = auditor;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	

	public BigDecimal getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(BigDecimal productNumber) {
		this.productNumber = productNumber;
	}

    public Integer getOperator_code() {
        return operator_code;
    }

    public void setOperator_code(Integer operator_code) {
        this.operator_code = operator_code;
    }

    public Integer getArea_code() {
        return area_code;
    }

    public void setArea_code(Integer area_code) {
        this.area_code = area_code;
    }

    public BigDecimal getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(BigDecimal unit_price) {
        this.unit_price = unit_price;
    }

    @Override
	public String toString() {
		return "SmsClientOrder [subId=" + subId + ", orderId=" + orderId
				+ ", clientId=" + clientId + ", agentId=" + agentId
				+ ", productId=" + productId + ", productType=" + productType
				+ ", productName=" + productName + ", productNumber="
				+ productNumber + ", orderType=" + orderType + ", status="
				+ status + ", activePeriod=" + activePeriod + ", quantity="
				+ quantity +  ", operator_code="
                + operator_code + ", area_code=" + area_code + ", unit_price="
                + unit_price +", remainQuantity=" + remainQuantity
				+ ", salePrice=" + salePrice + ", productCost=" + productCost
				+ ", endTime=" + endTime + ", createTime=" + createTime
				+ ", effectiveTime=" + effectiveTime + ", updateTime="
				+ updateTime + ", auditType=" + auditType + ", auditor="
				+ auditor + ", adminId=" + adminId + "]";
	}


}