package com.ucpaas.smsp.common.entity;

/**
 * ajax响应实体类
 * @author huangzz
 *
 */
public class AjaxResult {
	
	/**
	 * 响应成功状态 
	 */
	public static final boolean SUCCESS = Boolean.TRUE;
	
	/**
	 * 响应失败状态
	 */
	public static final boolean ERROR = Boolean.FALSE;
	
	/**
	 * 响应是否成功状态
	 */
	private Boolean isSuccess;
	
	/**
	 * 提示语
	 */
	private String message;
	
	/**
	 * 数据
	 */
	private Object data;

	public AjaxResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AjaxResult(Boolean isSuccess, String message) {
		super();
		this.isSuccess = isSuccess;
		this.message = message;
	}

	public AjaxResult(Boolean isSuccess, Object data) {
		super();
		this.isSuccess = isSuccess;
		this.data = data;
	}

	public AjaxResult(Boolean isSuccess, String message, Object data) {
		super();
		this.isSuccess = isSuccess;
		this.message = message;
		this.data = data;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
