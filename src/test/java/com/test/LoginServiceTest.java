package com.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ucpaas.smsp.common.entity.AjaxResult;
import com.ucpaas.smsp.service.common.LoginService;
import com.ucpaas.smsp.util.encrypt.EncryptUtils;

public class LoginServiceTest extends SupperTest{
	
	@Autowired
	private LoginService loginService;
	
	
	@Test
	public void loginValidateTest(){
		String loginAccount = "hzz";
		String loginPassword = EncryptUtils.encodeMd5("123456");
		AjaxResult result = loginService.loginValidate(loginAccount, loginPassword);
		System.out.println("flag:--------->"+result.getIsSuccess());
		System.out.println("结果:---------->"+result.getMessage());
	}

}
