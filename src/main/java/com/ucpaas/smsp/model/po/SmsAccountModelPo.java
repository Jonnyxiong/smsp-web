package com.ucpaas.smsp.model.po;

import com.jsmsframework.user.entity.JsmsClientInfoExt;
import com.ucpaas.smsp.model.SmsAccountModel;

public class SmsAccountModelPo extends SmsAccountModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4618717145981544732L;

	private String agentName;
	
	private String agentMobile;

	private JsmsClientInfoExt clientInfoExt;

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentMobile() {
		return agentMobile;
	}

	public void setAgentMobile(String agentMobile) {
		this.agentMobile = agentMobile;
	}

	public JsmsClientInfoExt getClientInfoExt() {
		return clientInfoExt;
	}

	public void setClientInfoExt(JsmsClientInfoExt clientInfoExt) {
		this.clientInfoExt = clientInfoExt;
	}
}
