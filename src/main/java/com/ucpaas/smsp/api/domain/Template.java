package com.ucpaas.smsp.api.domain;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucpaas.smsp.common.entity.ResultVO;

/**
 * 模版bean
 *
 * @author huangwenjie
 * @Date 2017-01-03 17:46
 */
public class Template{

	private Integer templateId;
	/**
	 * 模板类型，0：通知模板，4：验证码模板，5：营销模板，7：USSD模板，8：闪信模板，9：挂机短信模板
	 */
	private Integer type;
	private String typeName;

	/**
	 * 模板短信类型，0：通知短信，4：验证码短信，5：营销短信，6：告警短信，7：USSD，8：闪信（type＝9时，运营必填）
	 */
	private Integer smsType;
	private String smsTypeName;
	/**
	 * 内容
	 */
	@NotNull(message = "模版内容不能为空")
	@NotEmpty(message = "模版内容不能为空")
//	@Size(max=1000,min=1,message="模版内容不能超过1000个字符")
	private String content;
	/**
	 * 模板签名
	 */
	@NotNull(message = "模版签名不能为空")
	@NotEmpty(message = "模版签名不能为空")
//	@Size(max = 8, min = 2, message = "模版签名长度范围应该2-8个字符")
	private String sign;
	/**
	 * 归属客户id
	 */
	private String clientId;
	/**
	 * 状态，0：待审核，1：审核通过，2：转审，3：审核不通过
	 */
	private Integer checkStatus;
	private String checkStatusName;
	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	/**
	 * 提交类型，0：客户提交，1：代理商提交
	 */
	private Integer submitType;
	/**
	 * 更新时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Integer getSmsType() {
		return smsType;
	}

	public void setSmsType(Integer smsType) {
		this.smsType = smsType;
	}

	public String getSmsTypeName() {
		return smsTypeName;
	}

	public void setSmsTypeName(String smsTypeName) {
		this.smsTypeName = smsTypeName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientid) {
		this.clientId = clientid;
	}

	public Integer getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(Integer checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getCheckStatusName() {
		return checkStatusName;
	}

	public void setCheckStatusName(String checkStatusName) {
		this.checkStatusName = checkStatusName;
	}

	public Integer getSubmitType() {
		return submitType;
	}

	public void setSubmitType(Integer submitType) {
		this.submitType = submitType;
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

	
	@Override
	public String toString() {
		return "Template [templateId=" + templateId + ", type=" + type + ", typeName=" + typeName + ", smsType="
				+ smsType + ", smsTypeName=" + smsTypeName + ", content=" + content + ", sign=" + sign + ", clientid="
				+ clientId + ", checkStatus=" + checkStatus + ", checkStatusName=" + checkStatusName + ", createTime="
				+ createTime + ", submitType=" + submitType + ", updateTime=" + updateTime + "]";
	}

	public static void main(String[] args) throws JsonProcessingException {
		Template template = new Template();
		template.setCheckStatus(0);
		template.setCheckStatusName("待审核");
		template.setClientId("b000b3");
		template.setContent("您的验证码为：{VerificationCode}");
		template.setCreateTime(new Date());
		template.setSign("云之迅");
		template.setSmsType(0);
		template.setSmsTypeName("通知短信");
		template.setSubmitType(1);
		template.setTemplateId(1001);
		template.setType(0);
		template.setTypeName("通知模板");
		template.setUpdateTime(new Date());
		ResultVO resultVO = new ResultVO();
		resultVO.setData(template);
		resultVO.setMsg("仅支持0-4个参数");
		resultVO.setCode(40001);
		resultVO.setSuccess(false);
		System.out.println(new ObjectMapper().writeValueAsString(resultVO));
	}

}
