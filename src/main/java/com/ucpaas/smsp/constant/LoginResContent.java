package com.ucpaas.smsp.constant;

public enum LoginResContent {
	
	登录成功,
	账号未激活,
	账号已锁定,
	账号已注销,
	账号已冻结,
	账号不存在,
	密码错误
	
//	选择("",""),
//	账号未激活("1","账号未激活"),
//	账号已锁定("2","账号已锁定"),
//	账号已注销("3","账号已注销"),
//	账号已冻结("4","账号已冻结"),
//	账号不存在("5","账号不存在"),
//	密码错误("6","密码错误");
//	
//	
//	private String type;
//	private String typeMessage;
//	
//	private LoginResContent(String type, String typeMessage) {
//		this.type = type;
//		this.typeMessage = typeMessage;
//	}
//
//	public String getType() {
//		return type;
//	}
//
//	public String getTypeMessage(String type) {
//		for(LoginResContent lc : LoginResContent.values()){
//			if(lc.type.equals(type)){
//				return lc.typeMessage;
//			}
//		}
//		return "";
//	}
	
	
}
