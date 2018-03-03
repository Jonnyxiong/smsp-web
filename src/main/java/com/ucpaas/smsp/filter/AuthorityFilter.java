package com.ucpaas.smsp.filter;
import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.dao.common.SmsAccountDao;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.util.ConfigUtils;
import com.ucpaas.smsp.util.SpringUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthorityFilter implements Filter {
	
	private static List<String> whiteList = new ArrayList<String>();
	
	{
		whiteList.add("/index.html");
		whiteList.add("/experience.html");
		whiteList.add("/sms_experience");
		whiteList.add("/randCheckCodePic");
		whiteList.add("/randCodeCheck");
		whiteList.add("/agentApply");

		whiteList.add("/app/index");
		
//		whiteList.add("/api/template");
		
		whiteList.add("/console/login");
		whiteList.add("/console/lockscreen");
		whiteList.add("/console/loginValidate");

		whiteList.add("/");
		whiteList.add("/register");
		whiteList.add("/error"); 
		whiteList.add("/error/404"); 
		//swagger不拦截begin
		whiteList.add("/swagger-ui.html"); 
		whiteList.add("/v2/api-docs"); 
		whiteList.add("/configuration/ui");  
		whiteList.add("/swagger-resources");  
		//swagger不拦截end
	}
	
	private static List<String> swaggerList = new ArrayList<String>();
	
	{ 
		//swagger不拦截begin
		swaggerList.add("/swagger-ui.html"); 
		swaggerList.add("/v2/api-docs"); 
		swaggerList.add("/configuration/ui");  
		swaggerList.add("/swagger-resources");  
		//swagger不拦截end
	}
	
	private boolean isSwagger(String reqURI){
		if(swaggerList.contains(reqURI)){
			return true;
		}

		//swagger不拦截 begin
		if(reqURI!=null && reqURI.contains("springfox-swagger-ui")){
			
			return true;
		}
		
		return false;
	}
	
	private boolean isNeedAuth(String reqURI){
		// 验证路径是否是白名单
		if(whiteList.contains(reqURI)){
			return false;
		}
		
		// 验证是否是对外接口
		if(reqURI.startsWith("/api/")){
			return false;
		}
		 
		//swagger不拦截 end
		
		//如果是静态资源，直接让过
		if(reqURI.indexOf("js/")>=0 || reqURI.indexOf("css/")>=0 || reqURI.indexOf("font/")>=0 || reqURI.indexOf("img/")>=0 || reqURI.indexOf("swf")>=0){
			return false;
		}
		
		return true;
	}

	@Override
	public void init(FilterConfig paramFilterConfig) throws ServletException {
		ServletContext servletContext = paramFilterConfig.getServletContext();
		// 初始化设置应用上下文
		servletContext.setAttribute("ctx", servletContext.getContextPath());
	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		// 去掉项目名
		String reqURI = request.getRequestURI();
		reqURI = reqURI.replace("/smsp-web", "");
		if(reqURI.contains(";")){ //  index.html;jsessionid=5D2E7A76FB03359B80368090E8229742 
			reqURI = reqURI.substring(0, reqURI.indexOf(";"));
		}
		
		if(isSwagger(reqURI)){ //swagger网页
			if("true".equals(ConfigUtils.swagger_switch)){ //开发环境和测试环境开启
				filterChain.doFilter(request, response);
			}else{
				response.sendRedirect(request.getContextPath() + "/");
			}
			return;
		}

		if(isLogin(request)){//已登录
			if("/console/login".equals(reqURI))
				response.sendRedirect(request.getContextPath() + "/console");
			else
				filterChain.doFilter(request, response);
		}else{//未登录
			if(isNeedAuth(reqURI)){//需授权的页面，且未登陆，则返回登陆界面
				//判断是否是ajax
				if(request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")){
					response.setHeader("CONTEXTPATH", request.getContextPath() + "/console/lockscreen");
					response.setHeader("sessionstatus", "TIMEOUT");
				}else{
					response.sendRedirect(request.getContextPath() + "/console/login");
				}
			}else{
				filterChain.doFilter(request, response);
			}
		}
		
	}

	/**
	 * 用户是否登录
	 * @param request
	 * @return
	 */
	public boolean isLogin(HttpServletRequest request){
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession().getAttribute(Constant.LOGIN_USER_INFO);
		String webId = (String) request.getSession().getAttribute(Constant.WEB_ID);
		if(accountModel == null||StringUtils.isEmpty(webId)){
			return false;
		}
		if(accountModel!=null&&!StringUtils.isEmpty(webId)){
			if(webId.equals(ConfigUtils.web_id)){
				Map<String, Object> sqlParams = new HashMap<>();
				sqlParams.put("clientId", accountModel.getClientId());
				SmsAccountDao smsAccountDao =(SmsAccountDao) SpringUtil.getBean("smsAccountDao");
				SmsAccountModel dbAccount = smsAccountDao.getAccountInfoByClientId(sqlParams );
				if(dbAccount.getStatus()!=1){
					request.getSession().setAttribute(Constant.LOGIN_USER_INFO, null);
					request.getSession().setAttribute(Constant.WEB_ID, null);
					return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void destroy() {
	}

}
