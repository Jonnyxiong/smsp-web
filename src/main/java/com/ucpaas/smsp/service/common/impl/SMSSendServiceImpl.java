package com.ucpaas.smsp.service.common.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.ucpaas.smsp.common.entity.ResultVO;
import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.constant.Constant.OrderProductType;
import com.ucpaas.smsp.constant.Constant.SmsType;
import com.ucpaas.smsp.constant.Constant.TemplateType;
import com.ucpaas.smsp.dao.MasterDao;
import com.ucpaas.smsp.dao.common.SMSSendDao;
import com.ucpaas.smsp.dao.common.SmsAccountDao;
import com.ucpaas.smsp.enums.HttpProtocolType;
import com.ucpaas.smsp.model.AccessSmsBO;
import com.ucpaas.smsp.model.JsmsSendForm;
import com.ucpaas.smsp.model.SMSResponse;
import com.ucpaas.smsp.model.Template;
import com.ucpaas.smsp.service.common.SMSSendService;
import com.ucpaas.smsp.util.CommonUtil;
import com.ucpaas.smsp.util.ConfigUtils;
import com.ucpaas.smsp.util.HttpUtils;
import com.ucpaas.smsp.util.JsonUtils;
import com.ucpaas.smsp.util.encrypt.EncryptUtils;
import com.ucpaas.smsp.util.file.UcpaasFileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class SMSSendServiceImpl implements SMSSendService {

    public static final int M5_SIZE = 5 * 1024 * 1024;

    @Autowired
    private SMSSendDao smsSendDao;

    @Autowired
    private SmsAccountDao smsAccountDao;

    @Autowired
    private MasterDao masterDao;


    private static final Logger logger = LoggerFactory.getLogger(SMSSendServiceImpl.class);

    @Override
    public Map<String, Object> smsExperience(String mobile, String ip) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        // 发送前校验手机号码和IP是否超频
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", mobile);
        params.put("ip", ip);
        params.put("date", DateTime.now().toString("yyyy-MM-dd"));
        Map<String, Object> checkResult = smsSendDao.smsExperienceCheck(params);

        int ipExpNum = 0;
        int mobileExpNum = 0;
        if (checkResult != null) {
            ipExpNum = Integer.parseInt(Objects.toString(checkResult.get("ipExpNum"), "0"));
            mobileExpNum = Integer.parseInt(Objects.toString(checkResult.get("mobileExpNum"), "0"));
        }

        if (ipExpNum >= ConfigUtils.sms_experience_ipcount) {
            result.put("success", false);
            result.put("msg", "您今天的体验次数已经用完");
            logger.debug("【用户体验短信】：IP体验次数用完");
            return result;
        }
        if (mobileExpNum >= ConfigUtils.sms_experience_mobilecount) {
            result.put("success", false);
            result.put("msg", "您今天的体验次数已经用完");
            logger.debug("【用户体验短信】：手机号码体验次数用完");
            return result;
        }

        // 通过超频校验开始发送短信
        AccessSmsBO smsModel = new AccessSmsBO();
        smsModel.setClientid(ConfigUtils.sms_experience_clientid);
        smsModel.setPassword(EncryptUtils.encodeMd5(ConfigUtils.sms_experience_paasword));
        smsModel.setMobile(mobile);
        String msgCode = RandomStringUtils.randomNumeric(4); // 体验验证码
        smsModel.setContent(String.format(Constant.sms_experience_template, msgCode));
        smsModel.setSmstype(SmsType.VERIFICATION_CODE.getValue());

        String resultJson;
        String smsp_access_url = ConfigUtils.smsp_access_url_json.replace("{clientid}", smsModel.getClientid());
        logger.info("-------------smsp_access_url---------->{}", smsp_access_url);
        boolean needSSL;
        if ("https".startsWith(smsp_access_url)) {
            logger.debug("使用https协议请求短信接口");
            // 线上
            needSSL = true;
        } else {
            logger.debug("使用http协议请求短信接口");
            needSSL = false;
        }

        ContentType contentType ;
        resultJson = HttpUtils.httpPost(smsp_access_url, JsonUtils.toJson(smsModel), needSSL);

        SMSResponse accessResp = JsonUtils.toObject(resultJson, SMSResponse.class);
        logger.debug("【用户体验短信】smsp-access返回状态报告：{}", accessResp);
        if (accessResp != null) {
            if (accessResp.getData() != null && accessResp.getData().size() > 0) {
                Map<String, String> smsStatusMap = accessResp.getData().get(0); // 体验短信状态报告
                String code = "0";
                if (smsStatusMap != null) {
                    code = Objects.toString(smsStatusMap.get("code"), "0");//TODO
                }
                Map<String, String> smsRecord = new HashMap<String, String>();
                smsRecord.put("mobile", mobile);
                smsRecord.put("ip", ip);
                smsRecord.put("smsid", Objects.toString(smsStatusMap.get("sid"), ""));
                smsRecord.put("smstype", SmsType.VERIFICATION_CODE.getValue());
                smsRecord.put("date", DateTime.now().toString("yyyy-MM-dd"));

                if (code.equals("0")) {
                    result.put("success", true);
                    result.put("msg", "体验短信发送成功");
                    result.put("msgCode", msgCode);
                    logger.info("【用户体验短信】手机号码={}，生成验证码 ={}", mobile, msgCode);
                    // 记录用户短信体验
                    smsRecord.put("status", "1");
                    smsSendDao.insertExpRecord(smsRecord);

                } else {
//					result.put("success", false);
                    result.put("msg", "体验短信发送失败");

                    // 记录用户短信体验
                    smsRecord.put("status", "0");
                    smsSendDao.insertExpRecord(smsRecord);
                    logger.info("【用户体验短信】请求SMSP短信失败，errorCode = {}，smsModel = {}", code, smsModel);
                }
            }
        } else {
            result.put("success", false);
            result.put("msg", "体验短信发送失败");
        }

        return result;
    }

    @Override
    public ResultVO sendSms(AccessSmsBO smsModel, HttpProtocolType httpProtocolType) throws Exception {
        ResultVO result = new ResultVO();
        Template template = null;
        if (!StringUtils.isEmpty(smsModel.getTemplateid())) {  //模版短信
            StringBuffer param = new StringBuffer();
            String delim = "";
            Map params = new HashMap();
            params.put("template_id",smsModel.getTemplateid());
            params.put("check_status",1); //审核通过的模版
            template = masterDao.selectOne("templateMapper.queryTemplateInfo", params);
            if (template == null) {
                result.setSuccess(false);
                result.setMsg("该短信模版不存在，请刷新页面后再操作");
                return result;
            }
            String content = template.getContent();
            String pattern = "(\\{.*?\\})";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(content);
            List<String> temp = new ArrayList<>();
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                String match = content.substring(start, end);
                match = match.replaceAll("\\{","\\\\{");
                temp.add(match);
            }
            if(temp.size()>0){
	            if (temp.size() != smsModel.getContents().length) {
	                result.setSuccess(false);
	                result.setMsg("内容和模版匹配不上，请刷新页面后再操作");
	                return result;
	            }
	            for (int i = 0; i < smsModel.getContents().length; i++) {
	                content = content.replaceFirst(temp.get(i), smsModel.getContents()[i]);
	                param.append(delim).append(smsModel.getContents()[i]);
	                delim = ";";
	            }
	            smsModel.setParam(param.toString());
            }
            smsModel.setContent("【"+template.getSign()+"】"+content);

            setSmsType(smsModel,template);
        }
        // 短信参数校验
        ResultVO checkResult = checkSmsPara(smsModel);
        if (checkResult.isFail()) { 
            return checkResult;
        }

        // 用户账户校验
        checkResult = checkSmsAccount(smsModel,template);
        if (checkResult.isFail()) {
            return checkResult;
        }

        SMSResponse smspResp = null;
        String allMobile = smsModel.getMobile();
        System.out.println(allMobile.split(",").length);
        // 按手机最大发送数量将手机号码分组
        List<String> mobileList = splitSmsMobile(allMobile.split(","));

        String resultJson = "";
        logger.info("【短信发送】短信分组批量发送开始，分组数量 ={}", mobileList.size());

        boolean isTemplateSms = isTemplateSms(smsModel);
        if(isTemplateSms &&
                ! HttpProtocolType.HTTPS_JSON.getValue().equals(httpProtocolType.getValue())){ // 只有json协议才能发送彩印

            result.setSuccess(false);
            result.setMsg("当前协议类型，不支持发送彩印");
            return result;
        }
        for (int pos = 0; pos < mobileList.size(); pos++) {
            int groupNum = pos + 1;
            logger.info("----------- 第{}组开始发送----------->", groupNum);
            smsModel.setMobile(mobileList.get(pos));
//			if(!ConfigUtils.spring_profiles_active.equals("production")){
            String smsp_access_url;
            if (isTemplateSms){

                smsp_access_url = ConfigUtils.smsp_access_template_url.replace("{clientid}", smsModel.getClientid());
                logger.info("-------------smsp_access_url---------->{}", smsp_access_url);
                boolean needSSL = needSSL(smsp_access_url);
                String toJson = JsonUtils.toJson(smsModel);
                logger.debug("请求短信接口,url={},data={}",smsp_access_url,toJson);
                resultJson = HttpUtils.httpPost(smsp_access_url, JsonUtils.toJson(smsModel), needSSL,ContentType.APPLICATION_JSON);
            }else{
                if(HttpProtocolType.HTTPS_JSON.getValue().equals(httpProtocolType.getValue())){
                    smsp_access_url = ConfigUtils.smsp_access_url_json.replace("{clientid}", smsModel.getClientid());
                    String toJson = JsonUtils.toJson(smsModel);
                    logger.info("-------------smsp_access_url---------->{},data ==> {}", smsp_access_url,toJson);
                    resultJson = HttpUtils.httpPost(smsp_access_url,toJson, needSSL(smsp_access_url),ContentType.APPLICATION_JSON);
                }else if(HttpProtocolType.HTTPS_GET_POST.getValue().equals(httpProtocolType.getValue())){
                    smsp_access_url = ConfigUtils.smsp_access_url_form.replace("{clientid}", smsModel.getClientid());
                    JsmsSendForm jsmsSendForm = new JsmsSendForm();
                    BeanUtils.copyProperties(smsModel, jsmsSendForm);
                    String formString = jsmsSendForm.toFormString();
                    logger.info("-------------smsp_access_url---------->{},data ==> {}", smsp_access_url,formString);
                    resultJson = HttpUtils.httpPost(smsp_access_url, jsmsSendForm.toFormString(), needSSL(smsp_access_url),ContentType.APPLICATION_FORM_URLENCODED);
                }else{
                    smsp_access_url = ConfigUtils.smsp_access_url_json.replace("{clientid}", smsModel.getClientid());
                    String toJson = JsonUtils.toJson(smsModel);
                    logger.info("-------------smsp_access_url---------->{},data ==> {}", smsp_access_url,toJson);
                    resultJson = HttpUtils.httpPost(smsp_access_url, toJson, needSSL(smsp_access_url),ContentType.APPLICATION_JSON);
                }
            }

            if (StringUtils.isBlank(resultJson)) {
                logger.info("【短信发送失败】第{}组发送失败，请求SMSP无响应", groupNum);
            } else {
                try {
                    smspResp = JsonUtils.toObject(resultJson, SMSResponse.class);
                    logger.debug("----------- 第{}组发送成功，accessResp = {}", groupNum, smspResp);
                } catch (Exception e) {
                    logger.error("【短信发送异常】SMSP短信返回数据异常，resultJson = {}", resultJson);
                    throw new RuntimeException(e);
                }
            }
        }

        if (smspResp != null) {
            result.setSuccess(true);
            result.setMsg("短信提交成功");
        } else {
            result.setSuccess(false);
            result.setMsg("短信提交失败，请联系客服");
        }

        return result;
    }

    private boolean isTemplateSms(AccessSmsBO smsModel){
        if (!StringUtils.isEmpty(smsModel.getTemplateid())) {  //模版短信
            return true;
        }
        return false;
    }

    private boolean needSSL(String url){
        boolean needSSL;
        if ("https".startsWith(url)) {
            logger.debug("使用https协议请求短信接口");
            // 线上
            needSSL = true;
        } else {
            logger.debug("使用http协议请求短信接口");
            needSSL = false;
        }
        return needSSL;
    }

    private void setSmsType(AccessSmsBO smsModel, Template template) {
    	  if (TemplateType.NOTIFY.getValue().equals(template.getType())) {
    		  smsModel.setSmstype(SmsType.NOTIFY.getValue());
    	  }
    	  if (TemplateType.VERIFICATION_CODE.getValue().equals(template.getType())) {
    		  smsModel.setSmstype(SmsType.VERIFICATION_CODE.getValue());
    	  }
    	  if (TemplateType.MARKETING.getValue().equals(template.getType())) {
    		  smsModel.setSmstype(SmsType.MARKETING.getValue());
    	  }
    	  if (TemplateType.USSD.getValue().equals(template.getType())) {
    		  smsModel.setSmstype(SmsType.USSD.getValue());
    	  }
    	  if (TemplateType.FLASH.getValue().equals(template.getType())) {
    		  smsModel.setSmstype(SmsType.FLASH.getValue());
    	  }
    	  if (TemplateType.GUAJI.getValue().equals(template.getType())) {
    		  smsModel.setSmstype(template.getSms_type().toString());
    	  }
		
	}

	/**
     * 短信账户校验
     *
     * @param smsModel
     * @return
     * @throws Exception
     */
    private ResultVO checkSmsAccount(AccessSmsBO smsModel, Template template) throws Exception {
       ResultVO result = new ResultVO();
        result.setSuccess(true);

        // 01.校验短信账户认证状态
        Map<String, Object> clientAccStatusResult = clientAccountStatusCheck(smsModel.getClientid());
        if (clientAccStatusResult != null) {
            boolean oauthStatus = (boolean) clientAccStatusResult.get("oauthStatus");
            String accountStatus = String.valueOf(clientAccStatusResult.get("accountStatus"));

            if (!oauthStatus) {
                result.setSuccess(false);
                result.setMsg("账户未完成资质认证");
                return result;
            }
            // 5：冻结，6：注销，7：锁定
            if (accountStatus.equals("5")) {
                result.setSuccess(false);
                result.setMsg("账户被冻结");
                return result;
            }
            if (accountStatus.equals("6")) {
                result.setSuccess(false);
                result.setMsg("账户被注销");
                return result;
            }
            if (accountStatus.equals("7")) {
                result.setSuccess(false);
                result.setMsg("账户被锁定");
                return result;
            }
        } else {
            result.setSuccess(false);
            result.setMsg("账户不存在");
            return result;
        }

        // 02.后付费用户 不需要余额校验
        Integer clientPaytype = smsSendDao.getClientPaytype(smsModel.getClientid());
        if (clientPaytype.equals(Constant.CLIENT_POST_PAY)) {
            logger.debug("【短信发送失败】后付费用户 = {},不需要判断是否购买产品包", smsModel.getClientid());
            result.setSuccess(true);
            return result;
        }

        // 发送短信前检查客户订单中是否还有可用短信条数
        // 1.页面中验证码和通知对应订单中行业短信  2.暂时不校验国际短信
		String[] mobileList = smsModel.getMobile().split(",");
		boolean hasNorderSms = false;
        for(String mobile:mobileList){
			if(StringUtils.isNoneBlank(mobile)){
				if(mobile.trim().startsWith("00")){ //国际短信
					  
				}else{
					hasNorderSms = true;
					break;
				}
			}
		}
        logger.debug("发送号码{},是否存在普通短信hasNorderSms={}",mobileList,hasNorderSms);
        if(hasNorderSms){
	        OrderProductType productType = OrderProductType.INDUSTRY;
            OrderProductType productType1 = OrderProductType.INDUSTRY;
	        String smstype = smsModel.getSmstype();
	        /**按短信类型确认产品包 begin**/
	        if(smstype.equals(SmsType.USSD.getValue()) ){ 
	        	productType = OrderProductType.USSD;
                productType1=OrderProductType.USSD;
	        } if(smstype.equals(SmsType.FLASH.getValue()) ){
	        	productType = OrderProductType.FLASH;
                productType1=OrderProductType.FLASH;
	        } if(smstype.equals(SmsType.MARKETING.getValue()) ) {
	        	productType = OrderProductType.MARKETING;
                productType1=OrderProductType.MARKETING;
	        } 
	        /**按短信类型确认产品包 end**/
	        
	        /**按模版类型确认产品包 begin**/
	        if(template!=null){
	       	  if (TemplateType.NOTIFY.getValue().equals(template.getType())) {
	       		  productType = OrderProductType.INDUSTRY;
                  productType1=OrderProductType.NOTIFY;
	    	  }
	    	  if (TemplateType.VERIFICATION_CODE.getValue().equals(template.getType())) {
	       		  productType = OrderProductType.INDUSTRY;
                  productType1=OrderProductType.NOTIFY;
	    	  }
	    	  if (TemplateType.MARKETING.getValue().equals(template.getType())) {
	    		  productType = OrderProductType.MARKETING;
                  productType1=OrderProductType.MARKETING;
	    	  }
	    	  if (TemplateType.USSD.getValue().equals(template.getType())) {
	    		  productType = OrderProductType.USSD;
                  productType1=OrderProductType.USSD;
	    	  }
	    	  if (TemplateType.FLASH.getValue().equals(template.getType())) {
	    		  productType = OrderProductType.FLASH;
                  productType1=OrderProductType.FLASH;
	    	  }
	    	  if (TemplateType.GUAJI.getValue().equals(template.getType())) {
	    		  productType = OrderProductType.GUAJI;
                  productType1=OrderProductType.GUAJI;
	    	  }
	        }
	        /**按模版类型确认产品包 end**/
	        
	        Map<String, String> checkPara = new HashMap<String, String>();
	        checkPara.put("client_id", smsModel.getClientid());
	        checkPara.put("product_type", productType.getValue());
            checkPara.put("product_type1", productType1.getValue());
	        int useableNum = smsSendDao.smsAccUsableCheck(checkPara);
	        logger.debug("client_id={},product_type(smstype)={},有效订单数为:{}",smsModel.getClientid(),smstype,useableNum);
	        if (useableNum <= 0) {
	
	            StringBuffer reminder = new StringBuffer(); //提示语
	            reminder.append("短信发送失败！<br/>您账户下的");
	            if (OrderProductType.INDUSTRY.getValue().equals(productType.getValue())) { 
	                reminder.append(OrderProductType.INDUSTRY.getDesc())//
	                        .append("类型短信不足")
	                        .append("（包括").append(SmsType.NOTIFY.getDesc()).append("、")
	                        .append(SmsType.VERIFICATION_CODE.getDesc()).append("）。");
	            } else { 
	                reminder = reminder.append(productType.getDesc())//
	                        .append("类型短信不足。");
	            }
	
	            result.setSuccess(false);
	            result.setMsg(reminder.toString());
	            return result;
	        }
        }
        return result;
    }

    /**
     * 短信参数校验
     *
     * @param smsModel
     * @return
     */
    private ResultVO checkSmsPara(AccessSmsBO smsModel) {
        ResultVO result = new ResultVO();
        String content = smsModel.getContent();
        String smstype = smsModel.getSmstype();
        String mobile = smsModel.getMobile(); // TODO

        if (SmsType.FLASH.getValue().equals(smstype)||SmsType.USSD.getValue().equals(smstype)) {  //彩印模版长度不一样	
            if (content.length() == 0 || content.length() > 62) {
                result.setSuccess(false);
                result.setMsg( "彩印短信内容加上签名长度不能为空且长度不能超过62个字符");
                return result;
            }
        }else { //普通短信
            if (content.length() == 0 || content.length() > 500) {
                result.setSuccess(false);
                result.setMsg("短信内容加上签名长度不能为空且长度不能超过500个字符");
                return result;
            }
            
            if (!isValidSmsType(smstype)) {  //只需判断非模版短信的短信类型即可
                result.setSuccess(false);
                result.setMsg("短信类型不合法");
                return result;
            }

        }
        
        
//		if(sign.length() < 2 || sign.length() > 12){
//			result.put("success", false);
//			result.put("msg", "短信类型不合法");
//			return result;
//		}
        result.setSuccess(true);
        return result;
    }

    private boolean isValidSmsType(String smstype) {
        if(smstype.equals(SmsType.MARKETING.getValue()))
            return true;
        if(smstype.equals(SmsType.FLASH.getValue()))
            return true;
        if(smstype.equals(SmsType.NOTIFY.getValue()))
            return true;
        if(smstype.equals(SmsType.USSD.getValue()))
            return true;
        if(smstype.equals(SmsType.VERIFICATION_CODE.getValue()))
            return true;
        return false;
    }

    /**
     * 将手机号码分组，每组字符串中的号码以英文逗号分隔
     *
     * @param array
     * @return
     */
    private List<String> splitSmsMobile(String[] array) {
        List<String> mobileList = new ArrayList<String>();

        List<String> arrayList = Arrays.asList(array);
        List<String> temp = new ArrayList<String>();

        if (array.length <= Constant.SMS_SEND_MAX_NUM) {
            mobileList.add(StringUtils.join(array, ","));
            return mobileList;
        }
        for (int pos = 0; pos < arrayList.size(); pos++) {
            temp.add(arrayList.get(pos));

            if (((pos + 1) % Constant.SMS_SEND_MAX_NUM == 0) || (pos == arrayList.size() - 1)) {
                mobileList.add(StringUtils.join(temp.toArray(), ","));
                temp = new ArrayList<String>();
            }
        }

        return mobileList;
    }

    /**
     * 从Excel中导入手机号码
     */
    @Override
    public Map<String, Object> importMobile(CommonsMultipartFile file)
            throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String fileName = file.getOriginalFilename();
        logger.debug("access importMobile parmas[fileName={}]", fileName);

        if (file.getSize() > M5_SIZE) {
            result.put("success", false);
            result.put("msg", "您选择的文件大于5M,请将excel拆分后重新导入");
            return result;
        }
        //客户端有可能传来application/octet-stream 这种类型
        logger.debug("导入号码的文件类型 ----> {}",file.getContentType());
        //application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
//        if(!("application/vnd.ms-excel".equalsIgnoreCase(file.getContentType())) &&
//                !("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").equalsIgnoreCase(file.getContentType())){
//            result.put("success", false);
//            result.put("msg", "导入文件格式错误，目前只支持Excel导入，请使用模板");
//            return result;
//        }

        String path = new StringBuilder(ConfigUtils.temp_file_dir).append(UUID.randomUUID()).append(fileName).toString();
        UcpaasFileUtils.upload2(path, fileName, file);
//        List<List<String>> tempList = null;
//        try {
//            tempList = ExcelUtils.importExcel(path);
//        } catch (Exception e) {
//            logger.error("解析excel失败：filePath=" + path, e);
//            result.put("success", false);
//            result.put("msg", "导入文件格式错误，目前支持.xls格式(97-2003)，请使用模板");
//            return result;
//        }
        List<Map> tempList = null;
        try {
//	            tempList = ExcelUtils.importExcel(path);
            ImportParams importParams = new ImportParams();
            importParams.setHeadRows(0);

            tempList = ExcelImportUtil.importExcel(new File(path), Map.class, importParams);
            logger.debug("号码Excel 读取完成 , 删除文件 ----------> {}",path);
            UcpaasFileUtils.delete(path);
        } catch (Exception e) {
            logger.debug("号码Excel 读取失败 , 删除文件 ----------> {}",path);
            UcpaasFileUtils.delete(path);
            logger.error("解析excel失败：filePath=" + path, e);
            result.put("success", false);
//            result.put("msg", "导入文件格式错误，目前支持.xls格式(97-2003)，请使用模板");
            result.put("msg", "导入文件格式错误，目前只支持Excel导入，请使用模板");
            return result;
        }
        logger.debug("号码Excel 读取完成  ----------> 开始解析");
        if(tempList==null){
            logger.error("解析excel失败：filePath=" + path);
            result.put("success", false);
            result.put("msg", "导入文件格式错误，目前只支持Excel导入，请使用模板");
            return result;
        }

        List<String> mobileList = new ArrayList<String>();
        int errorMobileCount = 0;
//        Date date1 = new Date();
        String phone = "";

        List<String> list = new ArrayList();
        for (Map map : tempList) {
            for(Object object: map.values()){
                if(object == null){
                    continue;
                }
                if(object instanceof Double){
                    BigDecimal bigDecimal = new BigDecimal((double) object);
                    phone = bigDecimal.toString();
                }else if(object instanceof String){
                    phone = (String) object;
                }else {
                    try {
                        phone = String.valueOf(object) ;
                    }catch (Exception e){
                        logger.error("{} 无法装换",object);
                    }
                }
                list.add(phone);
            }
        }
        if(list.size() > Integer.parseInt(ConfigUtils.excel_max_import_num)){
            logger.error("excel 数量限制超过限制：excel_max_import_num = {}, 实际导入数 = {}" ,ConfigUtils.excel_max_import_num,list.size());
            result.put("success", false);
            result.put("msg", "Excel文件导入数据超过限制<br/>不能超过"+ ConfigUtils.excel_max_import_num);
            return result;
        }
        Set<String> set = new TreeSet(list);
        for (String string: set){
            if(CommonUtil.isMobile(string) || CommonUtil.isOverSeaMobile(string)){
                mobileList.add(string);
            }else{
                if(StringUtils.isNoneBlank(string)){
                    ++ errorMobileCount;
                }
            }
        }
//        List<String> mobileList = new ArrayList<String>();
//        List<String> errorMobileList = new ArrayList<String>();
//        List<String> duplicateMobileList = new ArrayList<String>();
//
//        String phone = "";
//        for (List<String> list : tempList) {
//            if (list.isEmpty()) {
//                continue;
//            } else {
//                phone = list.get(0);
//                if (CommonUtil.isMobile(phone) || CommonUtil.isOverSeaMobile(phone)) {
//                    if (!mobileList.contains(phone)) {
//                        mobileList.add(phone);
//                    } else {
//                        duplicateMobileList.add(phone);
//                    }
//                } else {
//                    if (StringUtils.isNoneBlank(phone)) {
//                        errorMobileList.add(list.get(0));
//                    }
//                }
//            }
//        }

//		if(mobileList.size() > 1000){
//			result.put("success", false);
//			result.put("msg", "号码池中号码不能超过1000个");
//			return result;
//		}

        result.put("success", true);
        result.put("mobileList", mobileList);
        result.put("errorMobileCount", errorMobileCount);
        result.put("duplicateMobileCount", list.size() - set.size());
//        result.put("success", true);
//        result.put("mobileList", mobileList);
//        result.put("errorMobileList", errorMobileList);
//        result.put("duplicateMobileList", duplicateMobileList);
        return result;
    }

    @Override
    public Map<String, Object> clientAccountStatusCheck(String clientId) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> clientAccountStatus = smsSendDao.getClientAccountStatus(clientId);
        if (String.valueOf(clientAccountStatus.get("oauthStatus")).equals("3")) {
            result.put("oauthStatus", true);
        } else {
            result.put("oauthStatus", false);
        }

        // 1：注册完成，5：冻结，6：注销，7：锁定
        result.put("accountStatus", clientAccountStatus.get("accountStatus"));

        return result;
    }

    public static void main(String[] args) {
        System.out.println("test哈哈哈");
        String pattern = "(\\{.*?\\})";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher("hello，你的验证码是{xxx}，请在{xxx}秒之内完成验证。");
        System.out.println(String.valueOf(m.groupCount()));
        if (m.find()) {
            System.out.println("Found value: " + m.group(0));
            System.out.println("Found value: " + m.group(1));
            System.out.println("Found value: " + m.group(2));
        } else {
            System.out.println("NO MATCH");
        }
    }

}
