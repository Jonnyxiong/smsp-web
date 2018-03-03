package com.ucpaas.smsp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AgentApplyVO extends BaseModel {

	private static final long serialVersionUID = -4929171813548544727L;

	/**
	 * 企业名称
	 */
	private String companyName;

	/**
	 * 联系人
	 */
	private String realName;
	
	/**
	 * 邮箱
	 */
	private String email;
	
	/**
	 * 手机号
	 */
	private String mobile;
	
	/**
	 * 地址
	 */
	private String address;
	
	/**
	 * 城市
	 */
	private String city;
	
	/**
	 * 备注
	 */
	private String remark;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
