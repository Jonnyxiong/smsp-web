package com.ucpaas.smsp.entity.message;

import java.util.Date;

/**
 * @description 预付费客户余额预警信息
 * @author
 * @date 2017-07-21
 */
public class ClientBalanceAlarm {

	// 序号ID,自增长,主键
	private Integer id;
	// 用户账号,关联t_sms_account表中clientid字段
	private String clientid;
	// 接收告警手机号,多个手机号以","分隔
	private String alarmPhone;
	// 告警阀值,单位:条
	private Integer alarmNumber;
	// 告警可提醒次数
	private Integer reminderNumber;
	// 创建时间
	private Date createTime;
	// 更新时间
	private Date updateTime;
	private Date resetTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getAlarmPhone() {
		return alarmPhone;
	}

	public void setAlarmPhone(String alarmPhone) {
		this.alarmPhone = alarmPhone;
	}

	public Integer getAlarmNumber() {
		return alarmNumber;
	}

	public void setAlarmNumber(Integer alarmNumber) {
		this.alarmNumber = alarmNumber;
	}

	public Integer getReminderNumber() {
		return reminderNumber;
	}

	public void setReminderNumber(Integer reminderNumber) {
		this.reminderNumber = reminderNumber;
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

	public Date getResetTime() {
		return resetTime;
	}

	public void setResetTime(Date resetTime) {
		this.resetTime = resetTime;
	}
}