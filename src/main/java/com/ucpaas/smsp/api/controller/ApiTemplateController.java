package com.ucpaas.smsp.api.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ucpaas.smsp.api.domain.Template;
import com.ucpaas.smsp.common.entity.ResultVO;
import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.constant.Constant.TemplateType;
import com.ucpaas.smsp.controller.BaseController;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.service.common.LoginService;
import com.ucpaas.smsp.service.template.TemplateService;
import com.ucpaas.smsp.util.DateUtil;
import com.ucpaas.smsp.util.StringUtil;
import com.ucpaas.smsp.util.encrypt.EncryptUtils;

/**
 * @ClassName: ExternalInterfaceController
 * @Description: 短信对外接口
 * @author: Niu.T
 * @date: 2017年2月6日  上午9:51:26
 */
@Controller
@RequestMapping("/api")
public class ApiTemplateController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ApiTemplateController.class);


    @Autowired
    private TemplateService templateService;
    
    @Autowired
    private LoginService loginService;
    
    /**
     * @Description: 添加模板 http://sms.ucpaas.com/api/template/{clientid}/create
     * @author: Niu.T 
     * @date: 2017年2月6日    上午9:57:30
     * @param seckillId
     * @return Map<String,Object>
     */

    @RequestMapping(value = "/template/{clientid}/create", 
			method = RequestMethod.POST,
			consumes = "application/json",
			produces = {"application/json;charset=utf-8" })
    @ResponseBody
    public ResultVO add(@PathVariable String clientid,@RequestBody Map<String, String> params) {
    	logger.debug("短信接口:添加模板请求参数:{}",params);
    	ResultVO result = new ResultVO();
    	String password = (String) params.get("password");
        try {
			SmsAccountModel clientInfo = loginService.getApiClientInfo(clientid);
			
			if(password == null || clientInfo == null || !password.equals(EncryptUtils.encodeMd5(clientInfo.getPassword()))){
				result.setSuccess(false);
				result.setMsg("账号不存在或者密码错误");
				result.setCode(40001);
				logger.debug("请求接口:添加短信模板失败  ---> {}",result);
				return result;
			}

			if(clientInfo.getStatus() != 1){
				result.setSuccess(false);
				result.setMsg("账号被冻结、注销或锁定");
				result.setCode(40008);
				logger.debug("请求接口:添加短信模板失败  ---> {}\r\n账号状态 ---> {}",result,clientInfo.getStatus());
				return result;
			}
			
			if (params.get("content") == null || StringUtils.isEmpty(params.get("content").trim())) {
				result.setSuccess(false);
				result.setMsg("content不能为空");
				result.setCode(40005);
				logger.debug("请求接口:添加短信模板失败  ---> {}",result);
				return result;
			}
	        if (params.get("sign") == null || StringUtils.isEmpty(params.get("sign").trim())) {
	        	
	        	result.setSuccess(false);
	        	result.setMsg("sign不能为空");
	        	result.setCode(40006);
	        	logger.debug("请求接口:添加短信模板失败  ---> {}",result);
	        	return result;
	        }
	        String type = params.get("type");
			if(StringUtils.isEmpty(type) || !StringUtil.isNumeric(type) || !checkType(Integer.valueOf(type))){
			    result.setSuccess(false);
			    result.setMsg("type为空或者无效");
			    result.setCode(40004);
			    logger.debug("请求接口:添加短信模板失败  ---> {}\r\ntype ---> {}",result,type);
			    return result;
			}
			
			if(((String) params.get("sign")).length()< 2 || ((String) params.get("sign")).length() > 8){
				result.setSuccess(false);
				result.setMsg("模版签名长度范围应该2-8个字符");
				result.setCode(40007);
				logger.debug("请求接口:添加短信模板失败  ---> {}",result);
				return result;
			}
			
			if((((String) params.get("sign"))+((String) params.get("content"))).length()>498){
			    result.setSuccess(false);
			    result.setMsg("短信签名和短信内容总长度不能超过498个字符");
			    result.setCode(40003);
			    logger.debug("请求接口:添加短信模板失败  ---> {}",result);
			    return result;
			}
			
			params.put("clientid", clientid);
//			template.setSubmitType(0);	//提交类型，0：客户提交，1：代理商提交
			com.ucpaas.smsp.model.Template convert = this.convert(params);
			convert.setAgent_id(clientInfo.getAgentId());
			
			// 添加短信模板
			result = templateService.save(convert);
			
			Map<Object, Object> queryParams = new HashMap<>();
			com.ucpaas.smsp.model.Template sysTemplate = (com.ucpaas.smsp.model.Template)result.getData();
			// 重新到数据库查询一次
			queryParams.put("template_id",sysTemplate.getTemplate_id()); 
			com.ucpaas.smsp.model.Template queryTemporary = templateService.queryTemporary(queryParams);
			if(null != queryTemporary){
				result.setData(this.reversal(queryTemporary));
			}
			logger.debug("请求接口:添加短信模板成功  ---> {}",result);
			return result;
		} catch (Exception e) {
			DateTime dt = DateTime.now();
        	result.setSuccess(false);
        	result.setMsg("系统错误  "+dt.toString("yyyy-MM-dd HH:mm:ss"));
		    result.setCode(10001);
			logger.error("请求接口:添加短信模板失败  ---> {}",e);
			return result;
		}
    }

    private boolean checkType(Integer type) {
		TemplateType[] values = Constant.TemplateType.values();
		for (TemplateType templateType: values) {
			if(type.equals(templateType.getValue()))
			return true;
		}
		return false;
	}
    
    /**
     * @Description: 查看模版  http://sms.ucpaas.com/api/template/{clientid}/getTemplate
     * @author: Niu.T 
     * @date: 2017年2月7日    下午5:18:28
     * @param clientid
     * @param request
     * @return Map<String,Object>
     */
    @RequestMapping(value = "/template/{clientid}/getTemplate", 
			method = RequestMethod.POST,
//			headers = "Accept=application/json",
			consumes = "application/json",
    		produces = { "application/json;charset=utf-8"})
    @ResponseBody
    public Map<String,Object> getTemplate(@PathVariable String clientid,@RequestBody Map<String,String> params,HttpServletRequest request) {
		logger.debug("短信接口/获取短信模板状态:请求参数---> {}",params);
    	Map<String, Object> data = new HashMap<>();
    	String password = params.get("password");
    	String templateId = params.get("templateId");
        try {
			SmsAccountModel clientInfo = loginService.getApiClientInfo(clientid);
			if(password == null || clientInfo == null || !password.equals(EncryptUtils.encodeMd5(clientInfo.getPassword()))){
				data.put("data", null);
				data.put("msg", "账号不存在或者密码错误");
				data.put("code", "40001");
				logger.debug("请求接口:获取短信模板状态失败  ---> {}",data);
				return data;
			}
			if(clientInfo.getStatus() != 1){
				data.put("data", null);
				data.put("msg", "账号被冻结、注销或锁定");
				data.put("code", "40008");
				logger.debug("请求接口:添加短信模板失败  ---> {}\r\n账号状态 ---> {}",data,clientInfo.getStatus());
				return data;
			}
			if(templateId == null){
				data.put("data", null);
				data.put("msg", "templateId为空或无效");
				data.put("code", "40011");
				logger.debug("请求接口:获取短信模板状态失败  ---> {}",data);
				return data;
			}
			
			Map<Object, Object> queryParams = new HashMap<>();
	        queryParams.put("template_id",templateId); //获取模板信息
			com.ucpaas.smsp.model.Template queryTemporary = templateService.queryTemporary(queryParams);
			
			//判断是否查询到模板 或者 模板是否当前客户
			if(queryTemporary == null || !queryTemporary.getClient_id().equals(clientid)){
				data.put("data", null);
				data.put("msg", "templateId为空或无效");
				data.put("code", "40011");
				logger.debug("请求接口:获取短信模板状态失败  ---> {} \r\n	无效templateId或者clientid[{}]与模板所属客户ID不符 ---> {}",data,clientid,queryTemporary);
				return data;
			}
			data.put("data", this.reversal(queryTemporary));
			data.put("msg", "获取模版成功");
			data.put("code", "0");
			logger.debug("请求接口:获取短信模板状态成功  ---> {}",data);
			return data;
		} catch (Exception e) {
			DateTime dt = DateTime.now();
			data.put("data", null);
			data.put("msg", "系统错误		"+dt.toString("yyyy-MM-dd HH:mm:ss"));
			data.put("code", "10001");
			logger.error("请求接口:获取短信模板状态失败  ---> {} \r\n错误信息{}",data,e);
			return data;
		}
    }
    
    
    /**
     * @Description: api模板统一转换成系统模板类
     * @author: Niu.T 
     * @date: 2017年2月6日    下午2:08:30
     * @param apiTemplate
     * @return com.ucpaas.smsp.model.Template
     */
    private com.ucpaas.smsp.model.Template convert(Map<String,String> apiTemplate){
    	com.ucpaas.smsp.model.Template template = new com.ucpaas.smsp.model.Template();
    	if(!StringUtils.isEmpty(apiTemplate.get("templateId"))){
    		template.setTemplate_id(Integer.parseInt(apiTemplate.get("templateId")));
    	}
    	template.setClient_id(apiTemplate.get("clientid"));
    	template.setContent(apiTemplate.get("content"));
    	template.setSign(apiTemplate.get("sign"));
    	template.setType(Integer.parseInt(apiTemplate.get("type")));
    	template.setSms_type(Integer.parseInt(apiTemplate.get("type")));
    	template.setSubmit_type(0);
    	return template;
    }
    
   
//    private com.ucpaas.smsp.model.Template convert(Template apiTemplate){
//    	com.ucpaas.smsp.model.Template template = new com.ucpaas.smsp.model.Template();
//    	template.setCheck_status(apiTemplate.getCheckStatus());
//    	template.setCheck_status_name(apiTemplate.getCheckStatusName());
//    	template.setClient_id(apiTemplate.getClientid());
//    	template.setContent(apiTemplate.getContent());
//    	template.setCreate_time(apiTemplate.getCreateTime());
//    	template.setSign(apiTemplate.getSign());
//    	template.setSms_type(apiTemplate.getSmsType());
//    	template.setSms_type_name(apiTemplate.getSmsTypeName());
//    	template.setSubmit_type(apiTemplate.getSubmitType());
//    	template.setTemplate_id(apiTemplate.getTemplateId());
//    	template.setType(apiTemplate.getType());
//    	template.setType_name(apiTemplate.getTypeName());
//    	template.setUpdate_time(apiTemplate.getUpdateTime());
//    	
//    	return template;
//    }
    
    /**
     * @Description: 系统模板统一转换成api模板类
     * @author: Niu.T 
     * @date: 2017年2月7日    上午9:19:00
     * @param template
     * @return Template
     */
    private Template reversal(com.ucpaas.smsp.model.Template template){
    	Template apiTemplate = new Template();
    	apiTemplate.setCheckStatus(template.getCheck_status());
    	apiTemplate.setCheckStatusName(template.getCheck_status_name());
    	apiTemplate.setClientId(template.getClient_id());
    	apiTemplate.setContent(template.getContent());
    	apiTemplate.setCreateTime(template.getCreate_time());
    	apiTemplate.setSign(template.getSign());
    	apiTemplate.setSubmitType(template.getSubmit_type());
    	apiTemplate.setTemplateId(template.getTemplate_id());
    	apiTemplate.setType(template.getType());
    	apiTemplate.setTypeName(template.getType_name());
    	apiTemplate.setUpdateTime(template.getUpdate_time());
    	if(apiTemplate.getType().equals(9)){
    		apiTemplate.setSmsType(template.getSms_type());
    		apiTemplate.setSmsTypeName(template.getSms_type_name());
    	}else{
    		apiTemplate.setSmsType(null);
    		apiTemplate.setSmsTypeName(null);
    	}
    	return apiTemplate;
    }
    
    /**
     * @Description 修改模板 http://sms.ucpaas.com/api/template/{clientid}/editTemplate
     * 短信：审核通过、不通过、待审核，已删除模板除外）
     * 彩印：待审核的可修改,审核通过、不通过的模板不允许修改
     * @param clientid
     * @param params
     * @return
     * @author wangwei
     * @date 2017年3月6日 上午10:26:29
     */
    @RequestMapping(value ="/template/{clientid}/editTemplate", method = RequestMethod.POST,
			consumes = "application/json",
			produces = {"application/json;charset=utf-8" })
    @ResponseBody
    public ResultVO modifyTemplate(@PathVariable String clientid,@RequestBody Map<String, String> params)
    {
    	ResultVO result = new ResultVO();
    	String password = params.get("password");
    	logger.debug("短信接口:修改模板请求参数:{}",params);

    	try {
    		//  1.参数校验
	 		SmsAccountModel clientInfo = loginService.getApiClientInfo(clientid);
	 		
	 		// 获取 原短信模板
	 		Map<Object, Object> queryMap = new HashMap<Object, Object>();
	 		queryMap.put("template_id", params.get("templateId"));
	 		com.ucpaas.smsp.model.Template originTemplate = templateService.query(queryMap);
	 		// 1.1 账号校验
	 		if(null == password || null == clientInfo || !password.equals(EncryptUtils.encodeMd5(clientInfo.getPassword()))){
	 			result.setSuccess(false);
	 			result.setMsg("账号不存在或者密码错误");
	 			result.setCode(40001);
	 			logger.debug("请求接口:修改短信模板失败  ---> {}",result);
	 			return result;
	 		}
	 		if(1 != clientInfo.getStatus()){
	 			result.setSuccess(false);
	 			result.setMsg("账号被冻结、注销或锁定");
	 			result.setCode(40008);
	 			logger.debug("请求接口:修改短信模板失败  ---> {}\r\n账号状态 ---> {}",result,clientInfo.getStatus());
	 			return result;
	 		}
	 		
	 		// 1.2 模板校验 已删除模板视为不存在
	 		if(null == originTemplate || !originTemplate.getClient_id().equals(clientid) 
	 				|| 4 == originTemplate.getCheck_status())
	 		{
	 			result.setSuccess(false);
	 			result.setMsg("templateId为空或无效");
	 			result.setCode(40011);
	 			logger.debug("请求接口:修改短信模板失败  ---> {}",result);
	 			return result;
	 		}
	 		if (null == params.get("content") || StringUtils.isEmpty(params.get("content").trim())) {
				result.setSuccess(false);
				result.setMsg("content不能为空");
				result.setCode(40005);
				logger.debug("请求接口:修改短信模板失败  ---> {}",result);
				return result;
	 		}
	 		if (null == params.get("sign") || StringUtils.isEmpty(params.get("sign").trim())) {
	 	       	result.setSuccess(false);
	 	       	result.setMsg("sign不能为空");
	 	       	result.setCode(40006);
	 	       	logger.debug("请求接口:修改短信模板失败  ---> {}",result);
	 	       	return result;
	 		}
			if(((String) params.get("sign")).length()< 2 || ((String) params.get("sign")).length() > 8){
				result.setSuccess(false);
				result.setMsg("模版签名长度范围应该2-8个字符");
				result.setCode(40007);
				logger.debug("请求接口:添加短信模板失败  ---> {}",result);
				return result;
			}
 			// 短信/USSD/闪信 统一只能修改待处于待审核状态
 			if(0 != originTemplate.getCheck_status()){
 	 			result.setSuccess(false);
 	 			result.setMsg("templateId已审核,不允许修改");
 	 			result.setCode(40012);
 	 			logger.debug("请求接口:修改短信模板失败  ---> {}",result);
 	 			return result;
 			}
 			
	 	    String type = params.get("type");
	 		if(StringUtils.isEmpty(type) || !StringUtil.isNumeric(type) || !checkType(Integer.valueOf(type))){
	 		    result.setSuccess(false);
	 		    result.setMsg("type为空或者无效");
	 		    result.setCode(40004);
	 		    logger.debug("请求接口:修改短信模板失败  ---> {}\r\ntype ---> {}",result,type);
	 		    return result;
	 		}
	 		if(params.get("sign").length() < 2 || params.get("sign").length() > 8){
	 			result.setSuccess(false);
	 			result.setMsg("模版签名长度范围应该2-8个字符");
	 			result.setCode(40007);
	 			logger.debug("请求接口:修改短信模板失败  ---> {}",result);
	 			return result;
	 		}
	 		if((params.get("sign") + params.get("content")).length() > 498){
	 		    result.setSuccess(false);
	 		    result.setMsg("短信签名和短信内容总长度不能超过498个字符");
	 		    result.setCode(40003);
	 		    logger.debug("请求接口修改短信模板失败  ---> {}",result);
	 		    return result;
	 		}
	 		
	 		// 2. 修改模板，原模板审核通过,只更新template_temporary表;
	 		// 			       原模板审核未通过,更新template表 和 template_temporary
			params.put("clientid", clientid);
			com.ucpaas.smsp.model.Template convert = this.convert(params);
			convert.setAgent_id(clientInfo.getAgentId());
			convert.setCreate_time(originTemplate.getCreate_time());
			convert.setUpdate_time(originTemplate.getUpdate_time());
	 		result = templateService.update(convert);
	 		if(!result.isSuccess()){
	 			result.setData(null);
	 			return result;
	 		}
	 		
	 		// 3. 返回修改后的结果
			Map<Object, Object> queryParams = new HashMap<>();
	        queryParams.put("template_id",convert.getTemplate_id()); //审核通过的原模版
			com.ucpaas.smsp.model.Template queryTemporary = templateService.queryTemporary(queryParams);
			if(queryTemporary != null){
				result.setData(this.reversal(queryTemporary));
			}
			logger.debug("请求接口:添加短信模板  ---> {}",result);
	 		
	    }catch (Exception e) {
        	result.setSuccess(false);
        	result.setMsg("系统错误  "+DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_LINE));
		    result.setCode(10001);
			logger.error("请求接口:添加短信模板失败  ---> {}",e);
			return result;
		}
    	
    	return result;
    }
    
    /**
     * @Description 删除模板 http://sms.ucpaas.com/api/template/{clientid}/deleteTemplate
     * </br>修改模板状态标识
     * @param clientid
     * @param params
     * @return
     * @author wangwei
     * @date 2017年3月6日 上午10:44:59
     */
    @RequestMapping(value="/template/{clientid}/deleteTemplate", 
    		method = RequestMethod.POST,
    		consumes = "application/json",
			produces = {"application/json;charset=utf-8" })
    @ResponseBody
    public Map<String, Object> deleteTemplate(@PathVariable String clientid,@RequestBody Map<String, String> params)
    {
    	Map<String, Object> result = new HashMap<String, Object>();
    	String password = params.get("password");
    	logger.debug("短信接口:删除模板请求参数:{}",params);
    	try {
    		// 1. 参数校验
     		SmsAccountModel clientInfo = loginService.getApiClientInfo(clientid);
     		
     		// 1.1 账号校验
     		if(null == password || null == clientInfo || !password.equals(EncryptUtils.encodeMd5(clientInfo.getPassword()))){
     			result.put("data", null);
     			result.put("msg", "账号不存在或者密码错误");
     			result.put("code", 40001);
     			logger.debug("请求接口:删除短信模板失败  ---> {}",result);
     			return result;
     		}
     		
     		if(1 != clientInfo.getStatus()){
     			result.put("data", null);
     			result.put("msg", "账号被冻结、注销或锁定");
     			result.put("code", 40008);
     			logger.debug("请求接口:删除短信模板失败  ---> {}\r\n账号状态 ---> {}",result,clientInfo.getStatus());
     			return result;
     		}
     		
     		// 1.2 模板校验
     		Map<Object, Object> queryMap = new HashMap<Object, Object>();
     		queryMap.put("template_id", params.get("templateId"));
     		com.ucpaas.smsp.model.Template originTemplate = templateService.query(queryMap);
     		
     		if(null == originTemplate || !clientid.equals(originTemplate.getClient_id()) 
     				|| 4 == originTemplate.getCheck_status()){
     			result.put("data", null);
     			result.put("msg", "templateId为空或无效");
     			result.put("code", 40011);
     			logger.debug("请求接口:删除短信模板失败,无效templateId或者模板不属于当前用户  ---> {}",result);
     			return result;
     		}
     		
        	// 2. 删除模板
     		int templateId = Integer.parseInt(params.get("templateId"));
     		ResultVO resultVO = templateService.deleteTemplate(templateId);
     		result.put("code", resultVO.getCode());
     		result.put("msg", resultVO.getMsg());
     		
     		// 3. 返回结果
			Map<Object, Object> queryParams = new HashMap<>();
	        queryParams.put("template_id",templateId); //审核通过的原模版
			com.ucpaas.smsp.model.Template temporary = templateService.queryTemporary(queryParams);
			if(temporary != null){
				result.put("data", this.reversal(temporary));
			}else {
				result.put("data", null);
			}
     		
		} catch (Exception e) {
			result.put("data", null);
        	result.put("msg","系统错误  "+DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_LINE));
		    result.put("code", 10001);
			logger.error("请求接口:添加短信模板失败  ---> {}",e);
			return result;
		}
    	
    	return result;
    }
    
    // 模板审核有结果后推送通知到申请方 主动发起
}
