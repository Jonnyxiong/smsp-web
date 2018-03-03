package com.ucpaas.smsp.aop;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;
import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.model.po.SmsAccountModelPo;
import com.ucpaas.smsp.util.CommonUtil;

@Component
@Aspect
public class LogAspect {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String requestPath = null; // 请求地址
	private String userName = null; // 用户名
	private String clientid = null; // 用户id
	private String ip = null; // ip
	private Map<?, ?> inputParamMap = null; // 传入参数
	private Map<String, Object> outputParamMap = null; // 存放输出结果
	private long startTimeMillis = 0; // 开始时间
	private long endTimeMillis = 0; // 结束时间

	/**
	 * 
	 * @Title：doBeforeInServiceLayer
	 * @Description: 方法调用前触发 记录开始时间
	 * @param joinPoint
	 */
	@Before("execution(* *..controller..*.*(..))")
	public void doBeforeInServiceLayer(JoinPoint joinPoint) {
		startTimeMillis = System.currentTimeMillis(); // 记录方法开始执行的时间
	}

	/**
	 * 
	 * @Title：doAfterInServiceLayer
	 * @Description: 方法调用后触发 记录结束时间
	 * @param joinPoint
	 */
	@After("execution(* *..controller..*.*(..))")
	public void doAfterInServiceLayer(JoinPoint joinPoint) {
		try {
			endTimeMillis = System.currentTimeMillis(); // 记录方法执行完成的时间
			this.printOptLog();
		} catch (Exception e) {
			logger.error("日志组件出错", e);
		}
	}

	/**
	 * 
	 * @Title：doAround
	 * @Description: 环绕触发
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* *..controller..*.*(..))")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		/**
		 * 1.获取request信息 2.根据request获取session 3.从session中取出登录用户信息
		 */
		try {
			RequestAttributes ra = RequestContextHolder.getRequestAttributes();
			ServletRequestAttributes sra = (ServletRequestAttributes) ra;
			HttpServletRequest request = sra.getRequest();
			HttpSession session = request.getSession();
			// 从session中获取用户信息
			SmsAccountModelPo accountModel = (SmsAccountModelPo) session.getAttribute(Constant.LOGIN_USER_INFO);
			if (accountModel != null) {
				userName = accountModel.getName();
				clientid = accountModel.getClientId();
			} else {
				userName = "用户未登录";
				clientid = "";
			}
			ip = CommonUtil.getClientIP(request);
			// 获取输入参数
			inputParamMap = request.getParameterMap();
			// 获取请求地址
			requestPath = request.getRequestURI();

			// 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
			outputParamMap = new HashMap<String, Object>();
		} catch (Exception e) {
			logger.error("日志组件出错", e);
		}
		Object result = pjp.proceed();// result的值就是被拦截方法的返回值

		try {
			outputParamMap.put("result", result);
		} catch (Exception e) {
			logger.error("日志组件出错", e);
		}

		return result;
	}

	/**
	 * 
	 * @Title：printOptLog
	 * @Description: 输出日志
	 */
	private void printOptLog() {
		Gson gson = new Gson(); // 需要用到google的gson解析包
		String optTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTimeMillis);
		logger.info("clientid={},username={},ip={},url={},beginTime={},elapsedTime={},requestParam={},reponse={}",
				clientid, userName, ip, requestPath, optTime, (endTimeMillis - startTimeMillis) + "ms",
				gson.toJson(inputParamMap), gson.toJson(outputParamMap));
		// logger.info("\n user：" + userName + " url：" + requestPath + ";
		// op_time：" + optTime + " pro_time："
		// + (endTimeMillis - startTimeMillis) + "ms ;" + " param：" +
		// gson.toJson(inputParamMap) + ";"
		// + "\n result：" + gson.toJson(outputParamMap));
	}
}
