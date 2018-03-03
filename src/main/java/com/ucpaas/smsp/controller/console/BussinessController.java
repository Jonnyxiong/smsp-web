package com.ucpaas.smsp.controller.console;

import com.ucpaas.smsp.common.entity.AjaxResult;
import com.ucpaas.smsp.common.entity.ResultVO;
import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.controller.BaseController;
import com.ucpaas.smsp.enums.HttpProtocolType;
import com.ucpaas.smsp.model.*;
import com.ucpaas.smsp.model.param.PageParam;
import com.ucpaas.smsp.model.po.SmsAccountModelPo;
import com.ucpaas.smsp.service.bussiness.BussinessService;
import com.ucpaas.smsp.service.common.SMSSendService;
import com.ucpaas.smsp.util.*;
import com.ucpaas.smsp.util.encrypt.EncryptUtils;
import com.ucpaas.smsp.util.file.ExcelUtils;
import com.ucpaas.smsp.util.file.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/console/bussiness")
public class BussinessController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(BussinessController.class);

	@Autowired
	private BussinessService bussinessService;

	@Autowired
	private SMSSendService smsSendService;

	/**
	 * 短信发送页面
	 */
	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public ModelAndView smsView(HttpServletRequest request, ModelAndView mv) throws Exception {
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);
		mv.setViewName("console/bussiness/send");

		return mv;
	}

	/**
	 * 短信发送
	 */
	@RequestMapping(value = "/sendSms", method = RequestMethod.POST)
	@ResponseBody
	public ResultVO sendSms(@ModelAttribute("smsModel") AccessSmsBO accessSmsBO,@RequestParam(value = "arr[]",required=false)String[] arr) throws Exception {
	     ResultVO result = new ResultVO();
		accessSmsBO.setContents(arr);
		if(arr!=null)
			for(String ar:arr){
			    if(ar.contains("{")||ar.contains("}")||ar.contains("#*#")){
	                result.setMsg("内容不能含有关键字符'{'、'}'或'#*#'");
	                return result;
	            }
	        }
		SmsAccountModel userInfo = AuthorityUtil.getLoginUserInfo(request);
		accessSmsBO.setClientid(userInfo.getClientId());
		accessSmsBO.setPassword(EncryptUtils.encodeMd5(userInfo.getPassword()));
		HttpProtocolType httpProtocolType = HttpProtocolType.getInstanceByValue(userInfo.getHttpProtocolType());
		result = smsSendService.sendSms(accessSmsBO, httpProtocolType);

		return result;
	}

	@RequestMapping(value = "/importMobile", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> importMobile(@RequestParam("excel") CommonsMultipartFile excel) throws Exception {
		SmsAccountModel userInfo = AuthorityUtil.getLoginUserInfo(request);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			logger.debug("clientid={}正在批量导入手机号", userInfo.getClientId());
			result = smsSendService.importMobile(excel);
		} catch (Exception e) {
			logger.error("短信号码批量导入失败 ", e);
			result.put("success", false);
			result.put("msg", "短信号码批量导入失败，请联系客服");
		}

		logger.debug("clientid={}批量导入手机号导入完成, result={}", userInfo.getClientId(), JsonUtils.toJson(result));
		return result;

	}

	@RequestMapping(value = "/mobile-example")
	public ResponseEntity<byte[]> download() throws IOException {

		String path = request.getSession().getServletContext().getRealPath("/template/sms-template.xls");
		String fileName = "sms-template.xls";
		File downloadFile = new File(path);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", fileName);

		return new ResponseEntity<byte[]>(org.apache.commons.io.FileUtils.readFileToByteArray(downloadFile), headers,
				HttpStatus.CREATED);
	}

	/**
	 * 短信发送记录
	 */
	@RequestMapping(value = "/record")
	public ModelAndView record() throws Exception {

		Map<String, String> params = SpringUtil.getFormData(request);
		params.put("clientid", AuthorityUtil.getLoginUserInfo(request).getClientId());
		String start_time = params.get("start_time");
		String end_time = params.get("end_time");
		String mobile = params.get("mobile");
		String content = params.get("content");
		String state = params.get("state");
		// 设置默认时间
		if (start_time == null || end_time == null) {
			DateTime dt = DateTime.now();
			start_time = dt.withMillisOfDay(0).toString("yyyy-MM-dd HH:mm:ss");
			end_time = dt.toString("yyyy-MM-dd HH:mm:ss");
			params.put("start_time", start_time);
			params.put("end_time", end_time);
		}
		request.setAttribute("start_time", start_time);
		request.setAttribute("end_time", end_time);
		request.setAttribute("mobile", mobile);
		request.setAttribute("content", content);
		request.setAttribute("state", state);
		DateTime dt = DateTime.now();
		request.setAttribute("min_time", dt.minusMonths(3).toString("yyyy-MM-dd"));
		request.setAttribute("max_time", dt.toString("yyyy-MM-dd"));

		// 查询短信记录
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);

		// String clientid = accountModel.getClientId();
		// params.put("clientid", clientid);
		Map<String, Object> queryParams = new HashMap<>();
		for (Map.Entry e : params.entrySet()) {
			queryParams.put(e.getKey().toString(), e.getValue());
		}
		List<Integer> smsTypes = new ArrayList<>();
		smsTypes.add(Integer.valueOf(Constant.SmsType.NOTIFY.getValue()));
		smsTypes.add(Integer.valueOf(Constant.SmsType.VERIFICATION_CODE.getValue()));
		smsTypes.add(Integer.valueOf(Constant.SmsType.MARKETING.getValue()));
		queryParams.put("smsTypes",smsTypes);
		PageContainer page = bussinessService.querySmsSendRecord(queryParams);

		// 指定返回视图和模型
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("console/bussiness/record");
		modelAndView.addObject("page", page);

		modelAndView.addObject("accountModel", accountModel);

		return modelAndView;
	}

	// 短信导出实时检查数据
	@RequestMapping(value = "/checkSmsRecordNum")
	@ResponseBody
	public AjaxResult checkSmsRecordNum() throws Exception {
		AjaxResult ajaxResult = new AjaxResult();

		Map<String, String> params = SpringUtil.getFormData(request);
		params.put("clientid", AuthorityUtil.getLoginUserInfo(request).getClientId());
		String start_time = params.get("start_time");
		String end_time = params.get("end_time");

		// 设置默认时间
		if (start_time == null || end_time == null) {
			DateTime dt = DateTime.now();
			start_time = dt.withMillisOfDay(0).toString("yyyy-MM-dd HH:mm");
			end_time = dt.toString("yyyy-MM-dd HH:mm");
			params.put("start_time", start_time);
			params.put("end_time", end_time);
		}

		Map<String, Object> queryParams = new HashMap<>();
		for (Map.Entry e : params.entrySet()) {
			queryParams.put(e.getKey().toString(), e.getValue());
		}
		List<Integer> smsTypes = new ArrayList<>();
		smsTypes.add(Integer.valueOf(Constant.SmsType.NOTIFY.getValue()));
		smsTypes.add(Integer.valueOf(Constant.SmsType.VERIFICATION_CODE.getValue()));
		smsTypes.add(Integer.valueOf(Constant.SmsType.MARKETING.getValue()));
		queryParams.put("smsTypes",smsTypes);
		// 查询短信记录数量
		List<Map<String, Object>> resultList = bussinessService.querySmsSendRecordCount(queryParams);

		
		int totalCount = 0;
		if (resultList!=null&&resultList.size() > 0) {
			totalCount = Integer.parseInt(resultList.get(0).get("totalCount").toString());
		}

		if (totalCount == 0) {
			ajaxResult.setIsSuccess(false);
		} else {
			ajaxResult.setIsSuccess(true);
		}

		return ajaxResult;
	}

	

	@RequestMapping(value = "/exportRecord")
	@ResponseBody
	public Map exportRecord(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", false);
		result.put("msg", "生成报表失败");
		try {
			SmsAccountModelPo accountModel = (SmsAccountModelPo) session.getAttribute(Constant.LOGIN_USER_INFO);
			if (accountModel == null) {
				result.put("success", false);
				result.put("msg", "非法请求");
				return result;
			}
			String clientId = accountModel.getClientId();
			Map<String, String> params = SpringUtil.getFormData(request);
			params.put("clientid", clientId);
			String start_time = params.get("start_time");
			String end_time = params.get("end_time");
			StringBuffer fileName = new StringBuffer();
			fileName.append("短信记录-");
			// 设置默认时间
			if (start_time == null || end_time == null) {
				DateTime dt = DateTime.now();
				start_time = dt.minusMinutes(3).toString("yyyy-MM-dd HH:mm");
				end_time = dt.toString("yyyy-MM-dd HH:mm");
				params.put("start_time", start_time);
				params.put("end_time", end_time);
			}
			Date endTime = DateUtil.stringToDate(end_time, "yyyy-MM-dd HH:mm");
			fileName.append(DateUtil.dateToStr(endTime, "yyyyMMddHHmm"));
			fileName.append(".xls");

			String text = params.get("text");
			String filePath = ConfigUtils.temp_file_dir + "/" + clientId + "/" + fileName.toString();


			Excel excel = new Excel();
			excel.setFilePath(filePath);
			excel.setTitle("短信记录");
			if (text != null) {
				excel.addRemark("查询条件：" + text);
			}
			excel.addHeader(20, "手机号", "phone");
			excel.addHeader(20, "短信类型", "smstype_name");
			excel.addHeader(20, "发送内容", "content");
			excel.addHeader(20, "发送状态", "status");
			excel.addHeader(20, "状态码", "errorcode_name");
			excel.addHeader(20, "提交时间", "subTime");
			excel.addHeader(20, "发送时间", "sendTime");
			Map<String, Object> queryParams = new HashMap<>();
			for (Map.Entry e : params.entrySet()) {
				queryParams.put(e.getKey().toString(), e.getValue());
			}
			List<Integer> smsTypes = new ArrayList<>();
			smsTypes.add(Integer.valueOf(Constant.SmsType.NOTIFY.getValue()));
			smsTypes.add(Integer.valueOf(Constant.SmsType.VERIFICATION_CODE.getValue()));
			smsTypes.add(Integer.valueOf(Constant.SmsType.MARKETING.getValue()));
			queryParams.put("smsTypes",smsTypes);
			excel.setDataList(bussinessService.querySmsRecord4Excel(queryParams));
			if (ExcelUtils.exportExcel(excel)) {
				result.put("success", true);
				result.put("msg", "报表生成成功");
				result.put("fileName", fileName.toString());
				return result;
			}
		} catch (Exception e) {
			logger.error("生成报表失败", e);
		}
		return result;

	}
	
	@RequestMapping(value = "/downloadExcel")
	@ResponseBody
	public void downloadExcel(HttpSession session, HttpServletResponse response, String fileName) throws Exception {
		SmsAccountModelPo accountModel = (SmsAccountModelPo) session.getAttribute(Constant.LOGIN_USER_INFO);
		if (accountModel == null) {
			logger.info("非法用户请求导出报表，请求参数fileName{}", fileName);
			return;
		}
		String filePath = ConfigUtils.temp_file_dir + "/" + accountModel.getClientId() + "/" + fileName;
		logger.debug("下载报表fileName={}", filePath);
		FileUtils.download(response, filePath);
		FileUtils.delete(filePath);
	}
	
	

	@RequestMapping(value = "/price")
	public ModelAndView interShortMessage(String countryInfo, PageParam pageParam, ModelAndView mv) {
		PageBean pageBean = bussinessService.queryInterShortMessage(pageParam, countryInfo);
		mv.addObject("countryInfo", countryInfo);
		mv.addObject("pageBean", pageBean);
		mv.setViewName("console/bussiness/price");

		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);

		return mv;
	}

//	/**
//	 * 日发现统计
//	 * @param request
//	 * @param mv
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping(value = "/daystat")
//	public ModelAndView dayStat(HttpServletRequest request, ModelAndView mv) throws Exception {
//
//		// 代理商信息
//		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
//				.getAttribute(Constant.LOGIN_USER_INFO);
//		mv.addObject("accountModel", accountModel);
//
//		// 日发送数据分页查询
//		Map<String, String> params = SpringUtil.getFormData(request);
//
//		String start_time = params.get("start_time");
//		String end_time = params.get("end_time");
//		// 设置默认时间
//		if (start_time == null || end_time == null) {
//			DateTime dt = DateTime.now();
//			dt = dt.minusDays(1);
//			start_time = dt.minusMonths(3).toString("yyyy-MM-dd");
//			end_time = dt.toString("yyyy-MM-dd");
//			params.put("start_time", start_time);
//			params.put("end_time", end_time);
//		}
//		request.setAttribute("start_time", start_time);
//		request.setAttribute("end_time", end_time);
//		DateTime dt = DateTime.now();
//		dt = dt.minusDays(1);
//		request.setAttribute("min_time", dt.minusMonths(3).toString("yyyy-MM-dd"));
//		request.setAttribute("max_time", dt.toString("yyyy-MM-dd"));
//
//
//		params.put("clientid", AuthorityUtil.getLoginUserInfo(request).getClientId());
//
//		Map<String, Object> queryParams = new HashMap<>();
//		for (Map.Entry e : params.entrySet()) {
//			queryParams.put(e.getKey().toString(), e.getValue());
//		}
//		List<Integer> productTypes = new ArrayList<>();
//		productTypes.add(Integer.valueOf(Constant.OrderProductType.INDUSTRY.getValue()));
//		productTypes.add(Integer.valueOf(Constant.OrderProductType.MARKETING.getValue()));
//		productTypes.add(Integer.valueOf(Constant.OrderProductType.INTERNATIONAL.getValue()));
//		queryParams.put("productTypes",productTypes);
//		queryParams.put("startDate", Integer.valueOf(start_time.replace("-", "")));
//		queryParams.put("endDate",Integer.valueOf(end_time.replace("-", "")));
//		PageContainer dayStatPage = bussinessService.queryDayStat(queryParams);
//		mv.addObject("dayStatPage", dayStatPage);
//
//		// 返回视图
//		mv.setViewName("console/bussiness/daystat");
//		return mv;
//	}
//



	/**
	 * 短信模版页面
	 */
	@RequestMapping(value = "/template", method = RequestMethod.GET)
	public ModelAndView template(HttpServletRequest request, ModelAndView mv) throws Exception {
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);
		mv.setViewName("console/bussiness/template");

		return mv;
	}
//	// 短信导出实时检查数据
//	@RequestMapping(value = "/checkDayStatNum")
//	@ResponseBody
//	public AjaxResult checkDayStatNum() throws Exception {
//		AjaxResult ajaxResult = new AjaxResult();
//
//		Map<String, String> params = SpringUtil.getFormData(request);
//		params.put("clientid", AuthorityUtil.getLoginUserInfo(request).getClientId());
//		String start_time = params.get("start_time");
//		String end_time = params.get("end_time");
//
//		// 设置默认时间
//		if (start_time == null || end_time == null) {
//			DateTime dt = DateTime.now();
//			dt = dt.minusDays(1);
//			start_time = dt.withMillisOfDay(0).toString("yyyy-MM-dd HH:mm");
//			end_time = dt.toString("yyyy-MM-dd HH:mm");
//			params.put("start_time", start_time);
//			params.put("end_time", end_time);
//		}
//
//		Map<String, Object> queryParams = new HashMap<>();
//		for (Map.Entry e : params.entrySet()) {
//			queryParams.put(e.getKey().toString(), e.getValue());
//		}
//		List<Integer> productTypes = new ArrayList<>();
//		productTypes.add(Integer.valueOf(Constant.OrderProductType.INTERNATIONAL.getValue()));
//		productTypes.add(Integer.valueOf(Constant.OrderProductType.MARKETING.getValue()));
//		productTypes.add(Integer.valueOf(Constant.OrderProductType.INDUSTRY.getValue()));
//		queryParams.put("productTypes",productTypes);
//		queryParams.put("startDate", Integer.valueOf(start_time.replace("-", "")));
//		queryParams.put("endDate",Integer.valueOf(end_time.replace("-", "")));
//		// 查询短信记录数量
//		int totalCount =  bussinessService.exportBussinessDataCount(queryParams);
//
//
////		int totalCount = 0;
////		if (resultList!=null&&resultList.size() > 0) {
////			totalCount = Integer.parseInt(resultList.get(0).get("totalCount").toString());
////		}
//
//		if (totalCount == 0) {
//			ajaxResult.setIsSuccess(false);
//		} else {
//			ajaxResult.setIsSuccess(true);
//		}
//
//		return ajaxResult;
//	}

//	/**
//	 * 导出日统计
//	 * @param request
//	 * @param mv
//	 * @param session
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping(value = "/exportdaystat")
//	@ResponseBody
//	public ResultVO exportDayStat(HttpServletRequest request, ModelAndView mv, HttpSession session) throws Exception {
//		ResultVO result = new ResultVO();
//		// 代理商信息
//		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
//				.getAttribute(Constant.LOGIN_USER_INFO);
//		mv.addObject("accountModel", accountModel);
//
//		Map<String, String> params = SpringUtil.getFormData(request);
//
//		String start_time = params.get("start_time");
//		String end_time = params.get("end_time");
//		// 设置默认时间
//		if (start_time == null || end_time == null) {
//			DateTime dt = DateTime.now();
//			dt = dt.minusDays(1);
//			start_time = dt.minusDays(45).toString("yyyy-MM-dd");
//			end_time = dt.toString("yyyy-MM-dd");
//			params.put("start_time", start_time);
//			params.put("end_time", end_time);
//		}
//		request.setAttribute("start_time", start_time);
//		request.setAttribute("end_time", end_time);
//
//		Map<String, Object> queryParams = new HashMap<>();
//		for (Map.Entry e : params.entrySet()) {
//			queryParams.put(e.getKey().toString(), e.getValue());
//		}
//
//		queryParams.put("clientid",accountModel.getClientId());
//
//		List<Integer> productTypes = new ArrayList<>();
//		productTypes.add(Integer.valueOf(Constant.OrderProductType.INDUSTRY.getValue()));
//		productTypes.add(Integer.valueOf(Constant.OrderProductType.MARKETING.getValue()));
//		productTypes.add(Integer.valueOf(Constant.OrderProductType.INTERNATIONAL.getValue()));
//		queryParams.put("productTypes",productTypes);
//		queryParams.put("startDate", Integer.valueOf(start_time.replace("-", "")));
//		queryParams.put("endDate",Integer.valueOf(end_time.replace("-", "")));
//
//		try{
//			queryParams.put("limit", "LIMIT 0 , 60000");
////			List<Map<String, Object>> data = statDao.getSearchList("bussiness.queryStatistic", formData);
//			List<Map<String, Object>> data = bussinessService.exportBussinessData(queryParams);
//			if(data==null||data.size()<1){
//				result.setMsg( "根据条件查询到的记录数为0，导出Excel文件失败");
//				return result;
//			}
//			StringBuffer fileName = new StringBuffer();
//			fileName.append("短信日统计");
//			Excel excel = new Excel();
//			fileName.append( queryParams.get("startDate")).append("-").append(queryParams.get("endDate"));
//			excel.setTitle(fileName.toString());
//			fileName.append(".xls");
//			String filePath = ConfigUtils.temp_file_dir + "/" + accountModel.getClientId() + "/"+fileName.toString();
//			excel.setFilePath(filePath);
//			excel.addHeader(10, "序号", "rownum");
//			excel.addHeader(20, "日期", "date");
//			excel.addHeader(20, "提交条数", "usersmstotal");
//			excel.addHeader(20, "发送条数", "sendTotal");
//			excel.addHeader(20, "成功条数", "successTotal");
//			excel.addHeader(20, "平台拦截条数", "interceptTotal");
//			excel.addHeader(20, "失败条数", "failTotal");
//			excel.addHeader(20, "计费条数", "chargeTotal");
//
//
//			excel.setShowRownum(false);
//			int i = 1;
//			for(Map da:data){
//				da.put("rownum", i++);
//			}
//			excel.setDataList(data);
//			if (ExcelUtils.exportExcel(excel)) {
//				result.setSuccess(true);
//				result.setMsg("报表生成成功");
//				result.setData(fileName.toString());
//				return result;
//			}
//		}catch(Exception e){
//			logger.error("传统短信日统计报表生成失败", e);
//		}
//		return result;
//	}


//	/**
//	 * 月发送统计
//	 * @param request
//	 * @param mv
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping(value = "/monthlystat")
//	public ModelAndView monthlyStat(HttpServletRequest request, ModelAndView mv) throws Exception {
//
//		// 代理商信息
//		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
//				.getAttribute(Constant.LOGIN_USER_INFO);
//		mv.addObject("accountModel", accountModel);
//
//		// 月发送数据分页查询
//		Map<String, String> params = SpringUtil.getFormData(request);
//		params.put("clientid", accountModel.getClientId());
//
//
//		Map<String, Object> queryParams = new HashMap<>();
//		for (Map.Entry e : params.entrySet()) {
//			queryParams.put(e.getKey().toString(), e.getValue());
//		}
//		List<Integer> productTypes = new ArrayList<>();
//		productTypes.add(Integer.valueOf(Constant.OrderProductType.INDUSTRY.getValue()));
//		productTypes.add(Integer.valueOf(Constant.OrderProductType.MARKETING.getValue()));
//		productTypes.add(Integer.valueOf(Constant.OrderProductType.INTERNATIONAL.getValue()));
//		queryParams.put("productTypes",productTypes);
//
//
//		PageContainer monthStatPage = bussinessService.queryMonthStat(queryParams);
//		mv.addObject("monthStatPage", monthStatPage);
//
//		// 返回视图
//		mv.setViewName("console/bussiness/monthlystat");
//		return mv;
//	}

//	/**
//	 * 导出月统计
//	 * @param request
//	 * @param mv
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping(value = "/exportmonthlystat")
//    @ResponseBody
//	public ResultVO exportMonthStat(HttpServletRequest request, ModelAndView mv) throws Exception {
//
//		ResultVO result = new ResultVO();
//		// 代理商信息
//		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
//				.getAttribute(Constant.LOGIN_USER_INFO);
//		mv.addObject("accountModel", accountModel);
//
//		try{
//            Map params = new HashMap();
//            params.put("clientid",accountModel.getClientId());
//
//			List<Integer> productTypes = new ArrayList<>();
//			productTypes.add(Integer.valueOf(Constant.OrderProductType.INDUSTRY.getValue()));
//			productTypes.add(Integer.valueOf(Constant.OrderProductType.MARKETING.getValue()));
//			productTypes.add(Integer.valueOf(Constant.OrderProductType.INTERNATIONAL.getValue()));
//			params.put("productTypes",productTypes);
//
//            params.put("limit", "LIMIT 0 , 60000");
//			List<Map<String, Object>> data = bussinessService.exportMonthStat(params);
//            if(data==null||data.size()<1){
//				result.setMsg( "根据条件查询到的记录数为0，导出Excel文件失败");
//				return result;
//			}
//			StringBuffer fileName = new StringBuffer();
//			fileName.append("短信月统计");
//			Excel excel = new Excel();
//			excel.setTitle(fileName.toString());
//			fileName.append(".xls");
//			String filePath = ConfigUtils.temp_file_dir + "/" + accountModel.getClientId() + "/"+fileName.toString();
//			excel.setFilePath(filePath);
//			excel.addHeader(10, "序号", "rownum");
//			excel.addHeader(20, "日期", "date");
//			excel.addHeader(20, "提交条数", "usersmstotal");
//			excel.addHeader(20, "发送条数", "sendTotal");
//			excel.addHeader(20, "成功条数", "successTotal");
//			excel.addHeader(20, "平台拦截条数", "interceptTotal");
//			excel.addHeader(20, "失败条数", "failTotal");
//			excel.addHeader(20, "计费条数", "chargeTotal");
//
//
//			excel.setShowRownum(false);
//			int i = 1;
//			for(Map da:data){
//				da.put("rownum", i++);
//			}
//			excel.setDataList(data);
//			if (ExcelUtils.exportExcel(excel)) {
//				result.setSuccess(true);
//				result.setMsg("报表生成成功");
//				result.setData(fileName.toString());
//				return result;
//			}
//		}catch(Exception e){
//			logger.error("传统短信月统计报表生成失败", e);
//		}
//		return result;
//	}
//
	// =============================================================重构,,以下未重构且未发现被使用

	

	



//	@RequestMapping(value = "/month-stat")
//	public AjaxResult monthStat() throws Exception {
//
//		System.out.println(CommonUtil.getClientIP(request));
//		AjaxResult ajaxResult = null;
//		return ajaxResult;
//	}

	@RequestMapping(value = "/statistics")
	public ModelAndView statistics(HttpServletRequest request, ModelAndView mv, HttpSession session) throws Exception {

		SmsAccountModelPo accountModel = (SmsAccountModelPo) session.getAttribute(Constant.LOGIN_USER_INFO);
		/*if(accountModel.getSmsfrom() != 6 && accountModel.getSmstype() == 5){
			return new ModelAndView("redirect:/bussiness/statistics/market");
		}*/

		mv.addObject("accountModel", accountModel);
	//	everyPageShouldHave(mv, request.getServerName(), "/bussiness/statistics",request);
		Map<String, Object> formData = new HashMap<String, Object>();

		getStatisticFormData(request, formData);
		formData.put("paytype",accountModel.getPaytype());
		formData.put("clientId",accountModel.getClientId());
		PageContainer statPage = new PageContainer();
		Map subtotal = new HashMap<>();
		try{
			statPage = bussinessService.queryStatistic(formData);
			subtotal = bussinessService.queryStatisticTotal(formData);
		}catch(Exception e){
			logger.error("查询数据统计错误", e);
		}

		mv.addObject("subtotal", subtotal);
		mv.addObject("statPage", statPage);
		mv.addObject("start_time", formData.get("start_time"));
		mv.addObject("end_time", formData.get("end_time"));
		mv.addObject("minDate", DateUtil.dateToStr(DateUtil.getDateFromToday(-90), "yyyy-MM-dd"));
		mv.addObject("maxDate",DateUtil.dateToStr(DateUtil.getDateFromToday(-1), "yyyy-MM-dd"));
		mv.addObject("period",formData.get("period"));
		mv.addObject("smstype",formData.get("smstype"));
		mv.addObject("producttype",formData.get("producttype"));

		// 返回视图
		mv.setViewName("console/bussiness/statistics");
		return mv;
	}

	@RequestMapping(value = "/statistics/isms")
	public ModelAndView isms(HttpServletRequest request, ModelAndView mv,HttpSession session) throws Exception {
		SmsAccountModelPo accountModel = (SmsAccountModelPo) session.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);
	//	everyPageShouldHave(mv, request.getServerName(), "/bussiness/statistics",request);
		Map<String, Object> formData = new HashMap<String, Object>();


		if(formData.get("producttype")==null){
			formData.put("producttype", 2);
		}
		getStatisticFormData(request, formData);
		formData.put("paytype",accountModel.getPaytype());
		formData.put("clientId",accountModel.getClientId());
		PageContainer statPage = new PageContainer();
		Map subtotal = new HashMap<>();
		try{
			statPage = bussinessService.queryStatistic(formData);
			subtotal = bussinessService.queryStatisticTotal(formData);
		}catch(Exception e){
			logger.error("查询数据统计错误", e);
		}
		mv.addObject("subtotal", subtotal);


		mv.addObject("statPage", statPage);
		mv.addObject("start_time", formData.get("start_time"));
		mv.addObject("end_time", formData.get("end_time"));
		mv.addObject("minDate", DateUtil.dateToStr(DateUtil.getDateFromToday(-90), "yyyy-MM-dd"));
		mv.addObject("maxDate",DateUtil.dateToStr(DateUtil.getDateFromToday(-1), "yyyy-MM-dd"));
		mv.addObject("period",formData.get("period"));
		mv.addObject("smstype",formData.get("smstype"));
		mv.addObject("producttype",formData.get("producttype"));
		// 返回视图
		mv.setViewName("console/bussiness/isms");
		return mv;
	}


	@RequestMapping(value = "/exportStatistics")
	@ResponseBody
	public Map exportStatistics(HttpServletRequest request, ModelAndView mv, HttpSession session) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", false);
		result.put("msg", "生成报表失败");
		SmsAccountModelPo accountModel = (SmsAccountModelPo) session.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);
	//	everyPageShouldHave(mv, request.getServerName(), "/bussiness/statistics",request);
		Map<String, Object> formData = new HashMap<String, Object>();
		getStatisticFormData(request, formData);



		formData.put("paytype",accountModel.getPaytype());
		formData.put("clientId",accountModel.getClientId());
		try{
			formData.put("limit", "LIMIT 0 , 60000");
			List<Map<String, Object>> data = bussinessService.exportStatistics(formData);
			if(data==null||data.size()<1){
				result.put("msg", "根据条件查询到的记录数为0，导出Excel文件失败");
				return result;
			}


			this.changeChinese(data);


			StringBuffer fileName = new StringBuffer();
			String product_type = (String) (formData.get("producttype")==null?"":formData.get("producttype"));
			logger.info("页面中传的产品类型值为product_type={}",product_type);
			if("2".equals(product_type)){
				fileName.append("国际短信统计");
			}else{
				fileName.append("普通短信统计");
			}
			Excel excel = new Excel();
			fileName.append("(").append( formData.get("startDate")).append("-").append(DateUtil.getYeasterDay((Integer) formData.get("endDate"))).append(")");
			excel.setTitle(fileName.toString());
			fileName.append(".xls");
			String filePath = ConfigUtils.temp_file_dir + "/" + accountModel.getClientId() + "/"+fileName.toString();
			excel.setFilePath(filePath);
			excel.addHeader(10, "序号", "rownum");
			excel.addHeader(20, "日期", "date");
			excel.addHeader(20, "短信类型", "smstype");
//			if(accountModel.getPaytype()==0){
//				excel.addHeader(20, "产品类型", "product_type");
//			}
			excel.addHeader(20, "提交条数(条)", "num_all");
			excel.addHeader(20, "计费条数（条）", "chargetotal");
			excel.addHeader(20, "成功条数（条）", "num_sucs");
			excel.addHeader(20, "未知条数(条)", "num_known");
			excel.addHeader(20, "失败条数(条)", "num_fail");
			excel.addHeader(20, "待发送条数(条)", "notsend");
			excel.addHeader(20, "拦截条数(条)", "interceptTotal");



			excel.setShowRownum(false);
			int i = 1;
			for(Map da:data){
				da.put("rownum", i++);
			}
			int totalCount = data.size();
			Map subtotal = bussinessService.queryStatisticTotal(formData);
			subtotal.put("date", "共计");
			subtotal.put("num_all",subtotal.get("num_all_total"));
			subtotal.put("chargetotal",subtotal.get("chargeTotal_total"));
			subtotal.put("num_sucs",subtotal.get("num_sucs_total"));
			subtotal.put("num_known",subtotal.get("num_known_total"));
			subtotal.put("num_fail",subtotal.get("num_fail_total"));
			subtotal.put("notsend",subtotal.get("num_pending_total"));
			subtotal.put("interceptTotal",subtotal.get("num_intercept_total"));

			data.add(subtotal);
			excel.setDataList(data);
			if (ExcelUtils.exportExcel(excel,totalCount)) {
				result.put("success", true);
				result.put("msg", "报表生成成功");
				result.put("fileName", fileName.toString());
				return result;
			}
		}catch(Exception e){
			logger.error("行业短信统计报表生成失败", e);
		}
		return result;
	}

	public void getStatisticFormData(HttpServletRequest request, Map<String, Object> formData) {
		Calendar yeasterCal = Calendar.getInstance();
		yeasterCal.setTime(new Date());
		yeasterCal.add(Calendar.DATE, -1);
		String yesterday_time = DateUtil.dateToStr(yeasterCal.getTime(), "yyyy-MM-dd");
		String start_time = yesterday_time;
		String end_time = yesterday_time;
		// 日发送数据分页查询
		String value;
		for (Map.Entry<String, String[]> map : request.getParameterMap().entrySet()) {
			value = StringUtils.join(map.getValue(), ",");
			if (StringUtils.isNotBlank(value)) {
				formData.put(map.getKey(), value.trim());
			}
		}


		start_time = (String) (formData.get("start_time")==null?yesterday_time:formData.get("start_time"));
		end_time = (String) (formData.get("end_time")==null?yesterday_time:formData.get("end_time"));
		int period= formData.get("period")==null?0:Integer.valueOf((String) formData.get("period"));
		if(period==0){ //昨天
			int yesterday = DateUtil.getDateFromTodayInInt(-1);
			formData.put("startDate",yesterday);
			formData.put("endDate",Integer.valueOf(yesterday_time.replace("-", "")));
		}
		if(period==1){ //30天
			formData.put("startDate", DateUtil.getDateFromTodayInInt(-30));
			formData.put("endDate",Integer.valueOf(yesterday_time.replace("-", "")));
		}
		if(period==2){
			formData.put("startDate", Integer.valueOf(start_time.replace("-", "")));
			formData.put("endDate",Integer.valueOf(end_time.replace("-", "")));
		}

		formData.put("start_time", start_time);
		formData.put("end_time", end_time);
	}


	/**
	 * 转换短信类型和产品类型
	 * @param data
	 * @return
	 */
	private  List<Map<String ,Object>>  changeChinese(List<Map<String ,Object>> data){
		for (Map<String, Object> datum : data) {
			//短信类型转中文
			if("0".equals(datum.get("smstype"))){
				datum.put("smstype","通知");
			}else if ("4".equals(datum.get("smstype"))){
				datum.put("smstype","验证码");
			}else if ("5".equals(datum.get("smstype"))){
				datum.put("smstype","营销");
			}else if ("6".equals(datum.get("smstype"))){
				datum.put("smstype","告警");
			}else if ("7".equals(datum.get("smstype"))){
				datum.put("smstype","USSD");
			}else if ("8".equals(datum.get("smstype"))){
				datum.put("smstype","闪信");
			}
			//产品类型转中文
			if("0".equals(datum.get("product_type"))){
				datum.put("product_type","行业");
			}else if ("1".equals(datum.get("product_type"))){
				datum.put("product_type","营销");
			}else if ("2".equals(datum.get("product_type"))){
				datum.put("product_type","国际");
			}else if ("7".equals(datum.get("product_type"))){
				datum.put("product_type","USSD");
			}else if ("8".equals(datum.get("product_type"))){
				datum.put("product_type","闪信");
			}else if ("9".equals(datum.get("product_type"))){
				datum.put("product_type","挂机短信");
			}
		}


		return data;
	}






}
