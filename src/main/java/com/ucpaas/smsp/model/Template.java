package com.ucpaas.smsp.model;

import java.util.Arrays;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 系统模版bean
 *
 * @author huangwenjie
 * @Date 2017-01-03 17:46
 */
public class Template {

    private Integer template_id;
    /**
     * 模板类型，0：通知模板，4：验证码模板，5：营销模板，7：USSD模板，8：闪信模板，9：挂机短信模板
     */
    @NotNull(message="请选择模版类型")
    private Integer type;
    private String type_name;

    /**
     * 模板短信类型，0：通知短信，4：验证码短信，5：营销短信，6：告警短信，7：USSD，8：闪信（type＝9时，运营必填）
     */
    private Integer sms_type;
    private String sms_type_name;
    /**
     * 内容
     */
    @NotNull(message="请输入模版内容")
    @NotEmpty(message="请输入模版内容")
//    @Size(max=1000,min=1,message="模版内容不能超过1000个字符")
    private String content;
    /**
     * 模板签名
     */
    @NotNull(message="请输入模版签名")
    @NotEmpty(message="请输入模版签名") 
    @Size(max=8,min=2,message="模版签名长度范围应该2-8个字符")
    private String sign;
    /**
     * 归属客户id
     */
    private String client_id;
    /**
     * 归属代理商id
     */
    private Integer agent_id;
    /**
     * 状态，0：待审核，1：审核通过，2：转审，3：审核不通过
     */
    private Integer check_status;
    private String check_status_name;
    private Long admin_id;
    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date create_time;
    /**
     * 提交类型，0：客户提交，1：代理商提交
     */
    private Integer submit_type;
    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date update_time;
    private Date old_update_time;
    /**
     * 1：短信调度系统，2：代理商平台，3：运营平台，4：OEM代理商平台，5：客户平台，6：oem客户平台
     */
    private Integer update_type;
    
    
    private String[] opera;
    
    


    public String[] getOpera() {
		return opera;
	}

	public void setOpera(String[] opera) {
		this.opera = opera;
	}

	public Integer getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(Integer template_id) {
        this.template_id = template_id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public Integer getSms_type() {
        return sms_type;
    }

    public void setSms_type(Integer sms_type) {
        this.sms_type = sms_type;
    }

    public String getSms_type_name() {
        return sms_type_name;
    }

    public void setSms_type_name(String sms_type_name) {
        this.sms_type_name = sms_type_name;
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

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public Integer getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(Integer agent_id) {
        this.agent_id = agent_id;
    }


    public Long getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(Long admin_id) {
        this.admin_id = admin_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Integer getSubmit_type() {
        return submit_type;
    }

    public void setSubmit_type(Integer submit_type) {
        this.submit_type = submit_type;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getUpdate_type() {
        return update_type;
    }

    public void setUpdate_type(Integer update_type) {
        this.update_type = update_type;
    }

    public Integer getCheck_status() {
        return check_status;
    }

    public void setCheck_status(Integer check_status) {
        this.check_status = check_status;
    }

    public String getCheck_status_name() {
        return check_status_name;
    }

    public void setCheck_status_name(String check_status_name) {
        this.check_status_name = check_status_name;
    }

    public Date getOld_update_time() {
        return old_update_time;
    }

    public void setOld_update_time(Date old_update_time) {
        this.old_update_time = old_update_time;
    }

	@Override
	public String toString() {
		return "Template [template_id=" + template_id + ", type=" + type + ", type_name=" + type_name + ", sms_type="
				+ sms_type + ", sms_type_name=" + sms_type_name + ", content=" + content + ", sign=" + sign
				+ ", client_id=" + client_id + ", agent_id=" + agent_id + ", check_status=" + check_status
				+ ", check_status_name=" + check_status_name + ", admin_id=" + admin_id + ", create_time=" + create_time
				+ ", submit_type=" + submit_type + ", update_time=" + update_time + ", old_update_time="
				+ old_update_time + ", update_type=" + update_type + ", opera=" + Arrays.toString(opera) + "]";
	}
    
    
}
