package com.ucpaas.smsp.model.temp;

public class SplitTableTime {
	
	//开始时间
	private String start_time;
	
	//结束时间
	private String end_time;
	
	//标识：使用原来的数据库还是新的数据库
	private String flag;

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	

}
