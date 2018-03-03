package com.ucpaas.smsp.service.template;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucpaas.smsp.common.entity.ResultVO;
import com.ucpaas.smsp.dao.MasterDao;
import com.ucpaas.smsp.model.PageContainer;
import com.ucpaas.smsp.model.SmsException;
import com.ucpaas.smsp.model.Template;
import com.ucpaas.smsp.util.JsonUtils;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {
	private static Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);
	
	@Autowired
	private MasterDao masterDao;

	@Override
	public PageContainer queryList(Map<Object, Object> params) throws Exception {
		params.put("check_status",1); //审核通过的模版
		return masterDao.getSearchPage("templateMapper.queryTemplateList", "templateMapper.queryTemplateListCount", params);
	}

	@Override
	public PageContainer queryTemporaryList(Map<Object, Object> queryParams) throws Exception {
		return masterDao.getSearchPage("templateMapper.queryTemporaryTemplateList", "templateMapper.queryTemporaryTemplateListCount", queryParams);
	}

	@Override
	public Template query(Map<Object, Object> queryParams) throws Exception {
		return masterDao.selectOne("templateMapper.queryTemplateInfo",queryParams);
	}

	@Override
	public Template queryTemporary(Map<Object, Object> queryParams) throws Exception {
		return masterDao.selectOne("templateMapper.queryTemplateTemporaryInfo",queryParams);
	}

	@Override
	public ResultVO save(Template template) {
		logger.debug("template.save params={}",JsonUtils.toJson(template));
		ResultVO result = new ResultVO();
		String pattern = "^[^\\{\\}]*([^\\{\\}]*\\{[^\\{\\}]*\\}[^\\{\\}]*){0,4}[^\\{\\}]*$";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(template.getContent());
		if(!m.find()){
			result.setSuccess(false);
			result.setMsg("仅支持0-4个参数");
			result.setCode(40002);
			return result;
		}

		Date now = new Date();
		template.setCreate_time(now);
		template.setUpdate_time(now);
        template.setCheck_status(0); //待审核
        if(template.getSubmit_type() == null){
        	template.setSubmit_type(0);
        }//客户提交
        template.setUpdate_type(5); //客户平台
//		if(template.getType().intValue()!=9) {  //fix: 新增短信模板，模板表和模板审核表的smstype字段有值，预期应该为空。
//			template.setSms_type(template.getType());
//		}
        
        /* TODO 新增逻辑处理:
         * (1) 新模板判断 clientId + sign + content 是否与已有模板内容相同,如果审核状态 通过则直接返回原模板id
         * (2) 判断是否删除过,已删除 且 已转审 且 有结果,则生成一条新模板记录,check_status保持原结果
         * (3) 其他 直接添加新的模板记录,走常规流程
         * */
        result = this.handleExistTemplate(template);
        if(null != result && result.getCode() == 0){
    		logger.debug("template.save result={}",JsonUtils.toJson(result));
        	return result;
        }
        
        // 不存在的模板原逻辑处理
        result = new ResultVO();
        int insert = masterDao.insert("templateMapper.saveTemplate",template);
        int insertTemp = masterDao.insert("templateMapper.saveTemplateTemporary",template);
        if(insert > 0 && insertTemp > 0){
        	result.setData(template);
        	result.setSuccess(true);
        	result.setMsg("添加模版成功");
        	result.setCode(0);
        }else{
        	DateTime dt = DateTime.now();
        	result.setSuccess(false);
        	result.setMsg("系统错误  "+dt.toString("yyyy-MM-dd HH:mm:ss"));
        	result.setCode(10001);	//	系统错误
        }
		logger.debug("template.save result={}",JsonUtils.toJson(result));
		return result;
	}

	@Override
	public ResultVO update(Template template) throws Exception {
		ResultVO result = new ResultVO();
		String pattern = "^[^\\{\\}]*([^\\{\\}]*\\{[^\\{\\}]*\\}[^\\{\\}]*){0,4}[^\\{\\}]*$";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(template.getContent());
		if(!m.find()){
			result.setSuccess(false);
			result.setMsg("仅支持0-4个参数");
			result.setCode(40002);
			return result;
		}

		Date now = new Date();
		template.setOld_update_time(template.getUpdate_time());
		template.setUpdate_time(now);
		template.setCheck_status(0); //待审核
		template.setUpdate_type(5); //客户平台
//		if(template.getType().intValue()!=9) { //fix: 新增短信模板，模板表和模板审核表的smstype字段有值，预期应该为空。
//			template.setSms_type(template.getType());
//		}

//		logger.debug("更新模版表，更新为{}", JsonUtils.toJson(template));
//		int i = masterDao.update("templateMapper.updateTemplate",template);
//		if(i!=1){
//
//			Map<Object, Object> queryParams = new HashMap<>();
//			queryParams.put("template_id",template.getTemplate_id()); //审核通过的原模版
//			Template origin = this.query(queryParams);
//			logger.debug("更新模版表失败，最新模版为{}",JsonUtils.toJson(origin));
//			throw  new SmsException("更新失败，请刷新页面后重试");
//		}
		int i = masterDao.update("templateMapper.updateTemplateTemporary",template);
		
		// TODO 如果原模板一直处于待审核状态则同时更新template_temporary表
		if(i > 0){
			Map<Object, Object> paraMap = new HashMap<>();
			paraMap.put("template_id",template.getTemplate_id());
			Template tmpl = masterDao.selectOne("templateMapper.queryTemplateInfo", paraMap);
			if(0 == tmpl.getCheck_status()){
				masterDao.update("templateMapper.updateTemplate", template);
			}
		}
		
		if(i != 1){
			Map<Object, Object> queryParams = new HashMap<>();
			queryParams.put("template_id",template.getTemplate_id()); //审核通过的原模版
			Template origin = this.queryTemporary(queryParams);
			logger.debug("更新模版临时表失败，最新临时模版为{}",JsonUtils.toJson(origin));
			throw  new SmsException("更新失败，请刷新页面后重试");
		}
		result.setSuccess(true);
		result.setCode(0);
		result.setMsg("修改模板成功！");
		return result;
	}
	
	@Override
	public ResultVO deleteTemplate(int templateId)
	{
		ResultVO result = new ResultVO();
		Date date = new Date();
		Map<Object, Object> paraMap = new HashMap<Object, Object>();
		paraMap.put("template_id", templateId);
		paraMap.put("update_time", date);
		
		boolean status = masterDao.update("templateMapper.deleteTemplateTemporaryLogically", paraMap) > 0;
		if(status){
			masterDao.update("templateMapper.deleteTemplateLogically", paraMap);
		}
		result.setSuccess(true);
		result.setMsg("删除模板成功");
		result.setCode(0);
		return result;
	}
	
	/**
	 * @Description 检查是否存在sign/client_id/content相同的模板</br>
	 * 由于审核结果即时返回，此处不推送通知
	 * @param template
	 * @return ResultVO
	 * @author wangwei
	 * @date 2017年3月7日 上午11:49:20
	 */
	private ResultVO handleExistTemplate(Template template)
	{
		ResultVO result = new ResultVO();
        Map<String, String> sqlParams = new HashMap<>();
        sqlParams.put("client_id", template.getClient_id());
        sqlParams.put("sign", template.getSign());
        sqlParams.put("content", template.getContent());
        Template existTemplate = masterDao.selectOne("templateMapper.querySameTemplate", sqlParams);
		logger.debug("template.handleExistTemplate existTemplate={}",JsonUtils.toJson(existTemplate));
        // 无相同模板直接返回null
        if(null == existTemplate){
        	return null;
        }
    	// 状态，0：待审核，1：审核通过，2：转审，3：审核不通过，4：删除
    	int checkStatus = existTemplate.getCheck_status();
    	if(1 == checkStatus){
    		result.setData(existTemplate);
    		result.setSuccess(true);
    		result.setMsg("添加模板成功");
    		result.setCode(0);
    		return result;
    	}else if (4 == checkStatus) {
			// 原模板转审结果 1审核通过,3:审核不通过
    		Map<String, Integer> param = new HashMap<String, Integer>();
    		param.put("template_id", existTemplate.getTemplate_id());
    		int auditStatus = masterDao.selectOne("templateMapper.queryAuditStatus", param);
    		// 转审有结果
    		if(auditStatus > 0){
    			template.setCheck_status(auditStatus);
    			boolean tflag = masterDao.insert("templateMapper.saveTemplate",template) > 0;
    			boolean ttflag = masterDao.insert("templateMapper.saveTemplateTemporary",template) > 0;
    			if(tflag && ttflag){
    				String checkStatusName = "待审核";
    				if(1 == auditStatus){
    					checkStatusName = "审核通过";
    				}else if(3 == auditStatus){
						checkStatusName = "审核不通过";
					}
    				template.setCheck_status_name(checkStatusName);
    	        	result.setData(template);
    	        	result.setSuccess(true);
    	        	result.setMsg("添加模版成功");
    	        	result.setCode(0);
    			}
    		} else {
    	        int insert = masterDao.insert("templateMapper.saveTemplate",template);
    	        int insertTemp = masterDao.insert("templateMapper.saveTemplateTemporary",template);
    	        if(insert > 0 && insertTemp > 0){
    	        	result.setData(template);
    	        	result.setSuccess(true);
    	        	result.setMsg("添加模版成功");
    	        	result.setCode(0);
    	        }else{
    	        	DateTime dt = DateTime.now();
    	        	result.setSuccess(false);
    	        	result.setMsg("系统错误  "+dt.toString("yyyy-MM-dd HH:mm:ss"));
    	        	result.setCode(10001);	//	系统错误
    	        }
			}
		}else {
	        int insert = masterDao.insert("templateMapper.saveTemplate",template);
	        int insertTemp = masterDao.insert("templateMapper.saveTemplateTemporary",template);
	        if(insert > 0 && insertTemp > 0){
	        	result.setData(template);
	        	result.setSuccess(true);
	        	result.setMsg("添加模版成功");
	        	result.setCode(0);
	        }else{
	        	DateTime dt = DateTime.now();
	        	result.setSuccess(false);
	        	result.setMsg("系统错误  "+dt.toString("yyyy-MM-dd HH:mm:ss"));
	        	result.setCode(10001);	//	系统错误
	        }
		}
    	return result;
	}
	
	public static void main(String[] args) {
		String pattern = "^[^\\{\\}]*([^\\{\\}]*\\{[^\\{\\}]*\\}[^\\{\\}]*){0,4}[^\\{\\}]*$";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher("{tool.chinaz.测8试com|88");
		if(!m.find()){
			System.out.println("仅支持0-4");
		}

	}
}
