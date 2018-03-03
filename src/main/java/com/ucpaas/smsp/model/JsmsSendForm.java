package com.ucpaas.smsp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Objects;

@JsonInclude(Include.NON_NULL)
public class JsmsSendForm extends AccessSmsBO implements Cloneable,Serializable{

	/**
	 * 协议版本 固定为 ver=2.0
	 */
	private String ver="2.0";
	/**
	 * 密码，8－12位，MD5加密后32位，小写
	 */
	private String password;
	
	/**
	 * 发送手机号码（国内短信不要加前缀，国际短信号码前须带相应的国家区号，如日本：0081），支持多号码，号码之间用英文逗号隔开，最多100个
	 */
	private String mobile;
	
	/**
	 * 短信类型，0：通知短信，4：验证码短信，5：营销短信
	 */
	private String smstype;
	
	/**
	 * 签名 + 短信内容，UTF-8编码（短信内容最长500个字，签名最长10个字）
	 */
	private String content;

	/**
	 * (可选) 自扩展端口，1－4位，只能是数字,可以为空(注：此功能需要通道支持)
	 */
	private String extend;

	/**
	 * (可选) 定时发送时间	 为空表示立即发送，定时发送格式2016-11-11 09:00:00
	 */
	private String sendtime;
	
	/**
	 * (可选) 用户透传ID，随状态报告返回，最长60位
	 */
	private String batchid;

    @Override
    public JsmsSendForm clone(){
        JsmsSendForm newObj = new JsmsSendForm();
        newObj.setVer(this.getVer());
        newObj.setPassword(this.getPassword());
        newObj.setSmstype(this.getSmstype());
        newObj.setContent(this.getContent());
		newObj.setClientid(this.getClientid());
		return newObj;
    }

	public String getVer() {
		return ver;
	}

	private void setVer(String ver) {
		this.ver = ver;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSmstype() {
		return smstype;
	}

	public void setSmstype(String smstype) {
		this.smstype = smstype;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public String getBatchid() {
		return batchid;
	}

	public void setBatchid(String batchid) {
		this.batchid = batchid;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public String toFormString(){
		StringBuilder formStr = new StringBuilder("ver=2.0&password=");
//		ver=2.0,password=pwd,mobile=<null>,smstype=4,content=content,extend=<null>,batchid=<null>
		formStr.append(this.getPassword())
				.append("&mobile=")
				.append(Objects.toString(this.getMobile(),""))
				.append("&smstype=")
				.append(this.getSmstype())
				.append("&content=")
				.append(this.getContent())
				.append("&sendtime=")
				.append(Objects.toString(this.getSendtime(),""))
				.append("&extend=")
				.append(Objects.toString(this.getExtend(),""))
				.append("&batchid=")
				.append(Objects.toString(this.getBatchid(),""));
		return formStr.toString();
	}

}
