package com.ucpaas.smsp.model.po;

import com.ucpaas.smsp.model.SmsClientIdentifyLog;

public class SmsClientIdentifyLogPo extends SmsClientIdentifyLog{
	
	//偏移量
	private int offsetNum = 1;

	public int getOffsetNum() {
		return offsetNum;
	}

	public void setOffsetNum(int offsetNum) {
		this.offsetNum = offsetNum;
	}
	

}
