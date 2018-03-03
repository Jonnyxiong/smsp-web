package com.ucpaas.smsp.controller.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ucpaas.smsp.common.entity.ResultVO;
import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.controller.BaseController;
import com.ucpaas.smsp.model.PageContainer;
import com.ucpaas.smsp.model.SmsException;
import com.ucpaas.smsp.model.Template;
import com.ucpaas.smsp.model.po.SmsAccountModelPo;
import com.ucpaas.smsp.service.template.TemplateService;
import com.ucpaas.smsp.util.SpringUtil;

/**
 * 模版管理
 *
 * @author huangwenjie
 * @Date 2016-12-29 17:33
 */
@Controller
@RequestMapping("/console/template")
public class TemplateController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(TemplateController.class);


    @Autowired
    private TemplateService templateService;

    @RequestMapping(value = "/list")
    @ResponseBody
    public PageContainer list(HttpServletRequest request) {
        SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
                .getAttribute(Constant.LOGIN_USER_INFO);
        PageContainer pageContainer = new PageContainer();
        Map<Object, Object> queryParams = new HashMap<>();
        Map<String, String> params = SpringUtil.getFormData(request);
        for (Map.Entry e : params.entrySet()) {
            queryParams.put(e.getKey(), e.getValue());
        }
        List<Integer> types = new ArrayList<>();
        if (!StringUtils.isEmpty(params.get("types[]"))) {
            String[] smsType = params.get("types[]").split(",");
            for (String st : smsType) {
                types.add(Integer.valueOf(st));
            }
            queryParams.put("types", types);
        }
        queryParams.put("client_id", accountModel.getClientId());
        try {
            if (queryParams.get("pageRowCount") == null) {
                queryParams.put("pageRowCount", "10");
            }
            pageContainer = templateService.queryList(queryParams);
            return pageContainer;
        } catch (Exception e) {
            logger.error("获取模版列表失败", e);
        }
        return pageContainer;
    }

    @RequestMapping(value = "/temporary")
    @ResponseBody
    public PageContainer temporaryList(HttpServletRequest request) {
        SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
                .getAttribute(Constant.LOGIN_USER_INFO);
        PageContainer pageContainer = new PageContainer();
        Map<Object, Object> queryParams = new HashMap<>();
        Map<String, String> params = SpringUtil.getFormData(request);
        for (Map.Entry e : params.entrySet()) {
            queryParams.put(e.getKey(), e.getValue());
        }
        List<Integer> types = new ArrayList<>();
        if (!StringUtils.isEmpty(params.get("types[]"))) {
            String[] smsType = params.get("types[]").split(",");
            for (String st : smsType) {
                types.add(Integer.valueOf(st));
            }
            queryParams.put("types", types);
        }
        queryParams.put("client_id", accountModel.getClientId());
        try {
            pageContainer = templateService.queryTemporaryList(queryParams);
            for (Map<String, Object> template : pageContainer.getList()) {
                int type = (int) template.get("type");
                int status = (int) template.get("check_status"); 
                if (status == 0) { //待审核
                    Map<Object, Object> tempParams = new HashMap<>();
                    tempParams.put("template_id", template.get("template_id"));
                    tempParams.put("check_status", 1); //审核通过的原模版
                    tempParams.put("client_id", accountModel.getClientId()); //审核通过的原模版
                    Template templateHis = templateService.query(tempParams);
                    String[] opera = new String[]{"编辑"};
                    if (templateHis != null) {
                        opera = new String[]{"编辑", "查看历史"};
                    }
                    template.put("opera", opera);
                }
                if (status == 1 || status == 3) { //审核通过 或 审核不通过 
                	String[] opera = new String[]{"修改"};
                	if((status==1||status==2||status==3) &&(type==7 || type ==8)){  //审核通过、转审、审核不通过的USSD模版和闪信模版不能修改
                		opera = new String[]{""};
                	}
                    template.put("opera", opera);
                }
            }
            return pageContainer;
        } catch (Exception e) {
            logger.error("获取模版列表失败", e);
        }
        return pageContainer;
    }


    @RequestMapping(value = "/get")
    @ResponseBody
    public Template get(HttpServletRequest request) {
        SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
                .getAttribute(Constant.LOGIN_USER_INFO);
        Map<Object, Object> queryParams = new HashMap<>();
        Map<String, String> params = SpringUtil.getFormData(request);
        for (Map.Entry e : params.entrySet()) {
            queryParams.put(e.getKey(), e.getValue());
        }
        queryParams.put("check_status", 1); //审核通过的原模版
        queryParams.put("client_id", accountModel.getClientId()); //审核通过的原模版
        Template templateHis = new Template();
        try {
            templateHis = templateService.query(queryParams);
        } catch (Exception e) {
            logger.error("获取模版失败", e);
        }
        return templateHis;
    }


    @RequestMapping(value = "/add")
    @ResponseBody
    public ResultVO add(@Valid Template template, BindingResult errorResult, HttpServletRequest request) {
        ResultVO result = new ResultVO();
        if (errorResult.hasErrors()) {
            for (ObjectError error : errorResult.getAllErrors()) {
                result.setFail(true);
                result.setMsg(error.getDefaultMessage());
                return result;
            }
        }
        if((template.getSign()+template.getContent()).length()>498){
            result.setFail(true);
            result.setMsg("短信签名和短信内容总长度不能超过500个字符！");
            return result;
        }
        SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
                .getAttribute(Constant.LOGIN_USER_INFO);
        template.setClient_id(accountModel.getClientId());
        template.setAgent_id(accountModel.getAgentId());
        result = templateService.save(template);
        return result;
    }


    @RequestMapping(value = "/update")
    @ResponseBody
    public ResultVO update(@Valid Template template, BindingResult errorResult, HttpServletRequest request) {
        ResultVO result = new ResultVO();
        if (errorResult.hasErrors()) {
            for (ObjectError error : errorResult.getAllErrors()) {
                result.setFail(true);
                result.setMsg(error.getDefaultMessage());
                return result;
            }
        }
        if((template.getSign()+template.getContent()).length()>498){
            result.setFail(true);
            result.setMsg("短信签名和短信内容总长度不能超过500个字符！");
            return result;
        }
        SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
                .getAttribute(Constant.LOGIN_USER_INFO);
        template.setClient_id(accountModel.getClientId());
        template.setAgent_id(accountModel.getAgentId());
        try {
            result = templateService.update(template);
        }catch (Exception e){
            if(e instanceof SmsException){
                logger.debug("更新失败",e);
                result.setMsg(e.getMessage());
                return result;
            }else{
                logger.error("更新异常",e);
                result.setMsg("更新失败，请刷新页面后重试");
                return result;
            }
        }
        if(result.isSuccess()){
            Map<Object, Object> queryParams = new HashMap<>();
            queryParams.put("template_id",template.getTemplate_id()); //审核通过的原模版
            try {
                Template lastest = templateService.queryTemporary(queryParams);
                int status = lastest.getCheck_status();
                if (status == 0) { //待审核
                    Map<Object, Object> tempParams = new HashMap<>();
                    tempParams.put("template_id", lastest.getTemplate_id());
                    tempParams.put("check_status", 1); //审核通过的原模版
                    tempParams.put("client_id", accountModel.getClientId()); //审核通过的原模版
                    Template templateHis = templateService.query(tempParams);
                    String[] opera = new String[]{"编辑"};
                    if (templateHis != null) {
                        opera = new String[]{"编辑", "查看历史"};
                    }
                    lastest.setOpera(opera);
                }
                if (status == 1 || status == 3) { //审核通过 或 审核不通过
                    String[] opera = new String[]{"修改"};
                    lastest.setOpera(opera);
                }
                result.setData(lastest);
            } catch (Exception e) {
                result.setMsg("更新异常，请刷新页面后重试");
                return result;
            }
        }
        return result;
    }
    
}
