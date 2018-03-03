package com.ucpaas.smsp.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 接口结果传递类
 *
 * @author huangwenjie
 * @Date 2017-01-04 10:27
 */
public class ResultVO {
	private boolean success;

	@JsonIgnore
	private boolean fail;
	/**
	 * 携带的数据，可能是列表、分页对象等
	 */
	private Object data;
	/**
	 * 携带的消息，包括提示、日志之类的
	 */
	private String msg;

	/**
	 * 请求状态码，包括：0、40001、40002、40003 0：请求成功 40001：模版内容里参数不合法
	 * 40002：短信内容超过长度限制 40003：请求参数缺失
	 * 
	 */
	private Integer code;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * 初始化时，默认是失败
	 */
	public ResultVO() {
		setFail(true);
	}

	public String getMsg() {
		return msg;
	}

	public ResultVO setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public boolean isSuccess() {
		return success;
	}

	public ResultVO setSuccess(boolean success) {
		this.success = success;
		if (success)
			fail = false;
		else
			fail = true;
		return this;
	}

	public boolean isFail() {
		return fail;
	}

	public void setFail(boolean fail) {
		this.fail = fail;
		if (fail)
			success = false;
		else
			success = true;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ResultVO [success=" + success + ", fail=" + fail + ", data=" + data + ", msg=" + msg + ", code=" + code
				+ "]";
	}

}
