package com.ucpaas.smsp.model.param;

import java.io.Serializable;

public class GetOrderParam implements Serializable{
	
	
	private static final long serialVersionUID = 3213355177888613631L;

	private String orderCode_productName;
	
	private String status;
	
	private String startTime;
	
	private String endTime;
	
	private String clientId;

	public String getOrderCode_productName() {
		return orderCode_productName;
	}

	public void setOrderCode_productName(String orderCode_productName) {
		this.orderCode_productName = orderCode_productName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public String toString() {
		return "GetOrderParam [orderCode_productName=" + orderCode_productName
				+ ", status=" + status + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", clientId=" + clientId + "]";
	}

}
