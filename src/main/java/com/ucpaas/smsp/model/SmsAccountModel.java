package com.ucpaas.smsp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "账户信息传递对象", description = "账户信息传递对象")
public class SmsAccountModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5201698292604461357L;

	// 主键id,UUID
	@ApiModelProperty(value = "主键id", required = true)
	private String id;

	// 用户帐号（子帐号）
	@ApiModelProperty(value = "用户帐号（子帐号）", required = true)
	private String clientId;

	// 用户密码
	@ApiModelProperty(value = "修改密码时使用", required = false)
	private String password;

	// 用户名称
	@ApiModelProperty(value = "用户名称", required = true)
	private String name;

	// 平台帐号
	@ApiModelProperty(value = "平台帐号", required = false)
	private String sid;

	// 帐户状态，0：注册未激活，1：注册完成，5：锁定，6：注销
	private int status;

	// 代理商id
	private int agentId;

	// 认证状态，2：待认证 ，3：证件已认证(正常)，4：认证不通过
	private int oauthStatus;

	// 认证时间
	private Date oauthDate;

	// 手机号码
	private String mobile;

	// 邮箱地址
	private String email;

	// 个人地址/公司地址
	private String address;

	// 个人姓名/公司名称
	private String realname;

	// 用户等级，1：普通客户（6－8位用户扩展），2：中小企业大型企业（4－5位用户扩展），3：大型企业（2－3位用户扩展）
	private int clientLevel;

	// 用户类型，1：个人用户，2：企业用户
	private int clientType;

	// 是否需要状态报告，0：不需要，1：需要简单状态报告，2：需要透传状态报告
	private int needreport;

	// 是否需要上行，0：不需要，1：需要
	private int needmo;

	// 是否需要审核，0：不需要，1：需要
	private int needaudit;

	// 创建时间
	private Date createtime;

	// 验证IP（可以有多个，用逗号分隔：192.168.0.*，*，192.168.0.0/16
	private String ip;

	//SGIP协议接入客户提供的上行IP
	private String moip;

	//SGIP协议接入客户提供的上行端口
	private String moport;

	//提供给SGIP协议接入客户的节点编码
	private Long nodeid;

	// 状态报告回调地址
	private String deliveryurl;

	// 上行回调地址
	private String mourl;
	
	// 模板审核通知回调地址
	private String noticeurl;

	// 连接节点数
	private int nodenum;

	// 付费类型，0：预付费，1：后付费
	private int paytype;

	// 是否支持自扩展，0：不支持，1：支持
	private int needextend;

	// 是否支持签名对应签名端口，0：不支持，1：支持
	private int signextend;

	// 备注
	private String remarks;

	// 短信类型，0：通知短信，4：验证码短信，5：营销短信（适用于标准协议）
	private int smstype;

	// 短信协议类型，0为rest协议(rest)，1为vmsp协议（vmsp），2为SMPP协议（access），3为CMPP协议（access）,4为SGIP协议（access），5为SMGP协议（access），6为HTTPS协议
	private int smsfrom;

	private int httpProtocolType;

	// 创建时间，默认为系统时间
	private Date createTime;

	// 更新时间
	private Date updateTime;

	// 省
	private String province;

	// 市
	private String city;

	// 区县
	private String area;

	// access流水表id
	private int identify;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public int getOauthStatus() {
		return oauthStatus;
	}

	public void setOauthStatus(int oauthStatus) {
		this.oauthStatus = oauthStatus;
	}

	public Date getOauthDate() {
		return oauthDate;
	}

	public void setOauthDate(Date oauthDate) {
		this.oauthDate = oauthDate;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public int getClientLevel() {
		return clientLevel;
	}

	public void setClientLevel(int clientLevel) {
		this.clientLevel = clientLevel;
	}

	public int getClientType() {
		return clientType;
	}

	public void setClientType(int clientType) {
		this.clientType = clientType;
	}

	public int getNeedreport() {
		return needreport;
	}

	public void setNeedreport(int needreport) {
		this.needreport = needreport;
	}

	public int getNeedmo() {
		return needmo;
	}

	public void setNeedmo(int needmo) {
		this.needmo = needmo;
	}

	public int getNeedaudit() {
		return needaudit;
	}

	public void setNeedaudit(int needaudit) {
		this.needaudit = needaudit;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDeliveryurl() {
		return deliveryurl;
	}

	public void setDeliveryurl(String deliveryurl) {
		this.deliveryurl = deliveryurl;
	}

	public String getMourl() {
		return mourl;
	}

	public void setMourl(String mourl) {
		this.mourl = mourl;
	}

	public String getNoticeurl() {
		return noticeurl;
	}

	public void setNoticeurl(String noticeurl) {
		this.noticeurl = noticeurl;
	}

	public int getNodenum() {
		return nodenum;
	}

	public void setNodenum(int nodenum) {
		this.nodenum = nodenum;
	}

	public int getPaytype() {
		return paytype;
	}

	public void setPaytype(int paytype) {
		this.paytype = paytype;
	}

	public int getNeedextend() {
		return needextend;
	}

	public void setNeedextend(int needextend) {
		this.needextend = needextend;
	}

	public int getSignextend() {
		return signextend;
	}

	public void setSignextend(int signextend) {
		this.signextend = signextend;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getSmstype() {
		return smstype;
	}

	public void setSmstype(int smstype) {
		this.smstype = smstype;
	}

	public int getSmsfrom() {
		return smsfrom;
	}

	public void setSmsfrom(int smsfrom) {
		this.smsfrom = smsfrom;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public int getIdentify() {
		return identify;
	}

	public void setIdentify(int identify) {
		this.identify = identify;
	}

	public String getMoip() {
		return moip;
	}

	public void setMoip(String moip) {
		this.moip = moip;
	}

	public String getMoport() {
		return moport;
	}

	public void setMoport(String moport) {
		this.moport = moport;
	}

	public Long getNodeid() {
		return nodeid;
	}

	public void setNodeid(Long nodeid) {
		this.nodeid = nodeid;
	}

	public int getHttpProtocolType() {
		return httpProtocolType;
	}

	public void setHttpProtocolType(int httpProtocolType) {
		this.httpProtocolType = httpProtocolType;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
