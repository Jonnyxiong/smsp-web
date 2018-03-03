package com.ucpaas.smsp.controller.console;

import com.ucpaas.smsp.common.entity.AjaxResult;
import com.ucpaas.smsp.common.entity.ResultVO;
import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.controller.BaseController;
import com.ucpaas.smsp.enums.HttpProtocolType;
import com.ucpaas.smsp.model.AccessSmsBO;
import com.ucpaas.smsp.model.Excel;
import com.ucpaas.smsp.model.PageContainer;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.model.po.SmsAccountModelPo;
import com.ucpaas.smsp.service.bussiness.BussinessService;
import com.ucpaas.smsp.service.common.SMSSendService;
import com.ucpaas.smsp.util.AuthorityUtil;
import com.ucpaas.smsp.util.ConfigUtils;
import com.ucpaas.smsp.util.DateUtil;
import com.ucpaas.smsp.util.SpringUtil;
import com.ucpaas.smsp.util.encrypt.EncryptUtils;
import com.ucpaas.smsp.util.file.ExcelUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 彩信模块
 */	
@Controller
@RequestMapping("/console/mms")
public class MmsController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(MmsController.class);

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
		mv.setViewName("console/mms/send");

		return mv;
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
		smsTypes.add(Integer.valueOf(Constant.SmsType.USSD.getValue()));
		smsTypes.add(Integer.valueOf(Constant.SmsType.FLASH.getValue()));
		queryParams.put("smsTypes",smsTypes);
		PageContainer page = bussinessService.querySmsSendRecord(queryParams);

		// 指定返回视图和模型
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("console/mms/record");
		modelAndView.addObject("page", page);

		modelAndView.addObject("accountModel", accountModel);

		return modelAndView;
	}




	/**
	 * 短信模版页面
	 */
	@RequestMapping(value = "/template", method = RequestMethod.GET)
	public ModelAndView template(HttpServletRequest request, ModelAndView mv) throws Exception {
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);
		mv.setViewName("console/mms/template");

		return mv;
	}
	/**
	 * 短信发送
	 */
	@RequestMapping(value = "/sendSms", method = RequestMethod.POST)
	@ResponseBody
	public ResultVO sendSms(@ModelAttribute("smsModel") AccessSmsBO accessSmsBO, @RequestParam(value = "arr[]",required=false)String[] arr) throws Exception {
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
		result = smsSendService.sendSms(accessSmsBO,httpProtocolType);
		return result;
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
		smsTypes.add(Integer.valueOf(Constant.SmsType.USSD.getValue()));
		smsTypes.add(Integer.valueOf(Constant.SmsType.FLASH.getValue()));
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
			fileName.append("彩印短信记录-");
			// 设置默认时间
			if (start_time == null || end_time == null) {
				DateTime dt = DateTime.now();
				start_time = dt.withMillisOfDay(0).toString("yyyy-MM-dd HH:mm");
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
			smsTypes.add(Integer.valueOf(Constant.SmsType.USSD.getValue()));
			smsTypes.add(Integer.valueOf(Constant.SmsType.FLASH.getValue()));
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

	/**
	 * USSD日统计
	 * @param request
	 * @param mv
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ussdday")
	public ModelAndView ussdday(HttpServletRequest request, ModelAndView mv) throws Exception {

		// 代理商信息
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);

		// 日发送数据分页查询
		Map<String, String> params = SpringUtil.getFormData(request);

		String start_time = params.get("start_time");
		String end_time = params.get("end_time");
		// 设置默认时间
		if (start_time == null || end_time == null) {
			DateTime dt = DateTime.now();
			dt = dt.minusDays(1);
			start_time = dt.minusMonths(3).toString("yyyy-MM-dd");
			end_time = dt.toString("yyyy-MM-dd");
			params.put("start_time", start_time);
			params.put("end_time", end_time);
		}
		request.setAttribute("start_time", start_time);
		request.setAttribute("end_time", end_time);
		DateTime dt = DateTime.now();
		dt = dt.minusDays(1);
		request.setAttribute("min_time", dt.minusMonths(3).toString("yyyy-MM-dd"));
		request.setAttribute("max_time", dt.toString("yyyy-MM-dd"));


		params.put("clientid", AuthorityUtil.getLoginUserInfo(request).getClientId());

		Map<String, Object> queryParams = new HashMap<>();
		for (Map.Entry e : params.entrySet()) {
			queryParams.put(e.getKey().toString(), e.getValue());
		}
		List<Integer> productTypes = new ArrayList<>();
		productTypes.add(Integer.valueOf(Constant.OrderProductType.USSD.getValue()));
		queryParams.put("productTypes",productTypes);
		queryParams.put("startDate", Integer.valueOf(start_time.replace("-", "")));
		queryParams.put("endDate",Integer.valueOf(end_time.replace("-", "")));
		PageContainer dayStatPage = bussinessService.queryDayStat(queryParams);
		mv.addObject("dayStatPage", dayStatPage);

		// 返回视图
		mv.setViewName("console/mms/ussdday");
		return mv;
	}

	// 短信导出实时检查数据
	@RequestMapping(value = "/checkUSSDDayNum")
	@ResponseBody
	public AjaxResult checkUSSDDayNum() throws Exception {
		AjaxResult ajaxResult = new AjaxResult();

		Map<String, String> params = SpringUtil.getFormData(request);
		params.put("clientid", AuthorityUtil.getLoginUserInfo(request).getClientId());
		String start_time = params.get("start_time");
		String end_time = params.get("end_time");

		// 设置默认时间
		if (start_time == null || end_time == null) {
			DateTime dt = DateTime.now();
			dt = dt.minusDays(1);
			start_time = dt.withMillisOfDay(0).toString("yyyy-MM-dd HH:mm");
			end_time = dt.toString("yyyy-MM-dd HH:mm");
			params.put("start_time", start_time);
			params.put("end_time", end_time);
		}

		Map<String, Object> queryParams = new HashMap<>();
		for (Map.Entry e : params.entrySet()) {
			queryParams.put(e.getKey().toString(), e.getValue());
		}
		List<Integer> productTypes = new ArrayList<>();
		productTypes.add(Integer.valueOf(Constant.OrderProductType.USSD.getValue()));
		queryParams.put("productTypes",productTypes);
		queryParams.put("startDate", Integer.valueOf(start_time.replace("-", "")));
		queryParams.put("endDate",Integer.valueOf(end_time.replace("-", "")));
		// 查询短信记录数量
		int totalCount =  bussinessService.exportBussinessDataCount(queryParams);


//		int totalCount = 0;
//		if (resultList!=null&&resultList.size() > 0) {
//			totalCount = Integer.parseInt(resultList.get(0).get("totalCount").toString());
//		}

		if (totalCount == 0) {
			ajaxResult.setIsSuccess(false);
		} else {
			ajaxResult.setIsSuccess(true);
		}

		return ajaxResult;
	}


	/**
	 * 导出USSD日统计
	 * @param request
	 * @param mv
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/exportUssdDayStat")
	@ResponseBody
	public ResultVO exportUssdDayStat(HttpServletRequest request, ModelAndView mv, HttpSession session) throws Exception {
		ResultVO result = new ResultVO();
		// 代理商信息
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);

		Map<String, String> params = SpringUtil.getFormData(request);

		String start_time = params.get("start_time");
		String end_time = params.get("end_time");
		// 设置默认时间
		if (start_time == null || end_time == null) {
			DateTime dt = DateTime.now();
			dt = dt.minusDays(1);
			start_time = dt.minusDays(45).toString("yyyy-MM-dd");
			end_time = dt.toString("yyyy-MM-dd");
			params.put("start_time", start_time);
			params.put("end_time", end_time);
		}
		request.setAttribute("start_time", start_time);
		request.setAttribute("end_time", end_time);

		Map<String, Object> queryParams = new HashMap<>();
		for (Map.Entry e : params.entrySet()) {
			queryParams.put(e.getKey().toString(), e.getValue());
		}

		queryParams.put("clientid",accountModel.getClientId());

		List<Integer> productTypes = new ArrayList<>();
		productTypes.add(Integer.valueOf(Constant.OrderProductType.USSD.getValue()));
		queryParams.put("productTypes",productTypes);
		queryParams.put("startDate", Integer.valueOf(start_time.replace("-", "")));
		queryParams.put("endDate",Integer.valueOf(end_time.replace("-", "")));

		try{
			queryParams.put("limit", "LIMIT 0 , 60000");
//			List<Map<String, Object>> data = statDao.getSearchList("bussiness.queryStatistic", formData);
			List<Map<String, Object>> data = bussinessService.exportBussinessData(queryParams);
			if(data==null||data.size()<1){
				result.setMsg( "根据条件查询到的记录数为0，导出Excel文件失败");
				return result;
			}
			StringBuffer fileName = new StringBuffer();
			fileName.append("USSD日统计");
			Excel excel = new Excel();
			fileName.append( queryParams.get("startDate")).append("-").append(queryParams.get("endDate"));
			excel.setTitle(fileName.toString());
			fileName.append(".xls");
			String filePath = ConfigUtils.temp_file_dir + "/" + accountModel.getClientId() + "/"+fileName.toString();
			excel.setFilePath(filePath);
			excel.addHeader(10, "序号", "rownum");
			excel.addHeader(20, "日期", "date");
			excel.addHeader(20, "提交条数", "usersmstotal");
			excel.addHeader(20, "发送条数", "sendTotal");
			excel.addHeader(20, "成功条数", "successTotal");
			excel.addHeader(20, "平台拦截条数", "interceptTotal");
			excel.addHeader(20, "失败条数", "failTotal");
			excel.addHeader(20, "计费条数", "chargeTotal");


			excel.setShowRownum(false);
			int i = 1;
			for(Map da:data){
				da.put("rownum", i++);
			}
			excel.setDataList(data);
			if (ExcelUtils.exportExcel(excel)) {
				result.setSuccess(true);
				result.setMsg("报表生成成功");
				result.setData(fileName.toString());
				return result;
			}
		}catch(Exception e){
			logger.error("传统短信日统计报表生成失败", e);
		}
		return result;
	}


	/**
	 * USSD月发送统计
	 * @param request
	 * @param mv
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ussdmonthly")
	public ModelAndView monthlyStat(HttpServletRequest request, ModelAndView mv) throws Exception {

		// 代理商信息
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);

		// 月发送数据分页查询
		Map<String, String> params = SpringUtil.getFormData(request);
		params.put("clientid", accountModel.getClientId());


		Map<String, Object> queryParams = new HashMap<>();
		for (Map.Entry e : params.entrySet()) {
			queryParams.put(e.getKey().toString(), e.getValue());
		}
		List<Integer> productTypes = new ArrayList<>();
		productTypes.add(Integer.valueOf(Constant.OrderProductType.USSD.getValue()));
		queryParams.put("productTypes",productTypes);

		PageContainer monthStatPage = bussinessService.queryMonthStat(queryParams);
		mv.addObject("monthStatPage", monthStatPage);

		// 返回视图
		mv.setViewName("console/mms/ussdmonthly");
		return mv;
	}

	/**
	 * 导出USSD月统计
	 * @param request
	 * @param mv
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/exportussdmonthly")
	@ResponseBody
	public ResultVO exportUssdMonthly(HttpServletRequest request, ModelAndView mv) throws Exception {

		ResultVO result = new ResultVO();
		// 代理商信息
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);

		try{
			Map params = new HashMap();
			params.put("clientid",accountModel.getClientId());
			List<Integer> productTypes = new ArrayList<>();
			productTypes.add(Integer.valueOf(Constant.OrderProductType.USSD.getValue()));
			params.put("productTypes",productTypes);
			params.put("limit", "LIMIT 0 , 60000");
			List<Map<String, Object>> data = bussinessService.exportMonthStat(params);
			if(data==null||data.size()<1){
				result.setMsg( "根据条件查询到的记录数为0，导出Excel文件失败");
				return result;
			}
			StringBuffer fileName = new StringBuffer();
			fileName.append("USSD月统计");
			Excel excel = new Excel();
			excel.setTitle(fileName.toString());
			fileName.append(".xls");
			String filePath = ConfigUtils.temp_file_dir + "/" + accountModel.getClientId() + "/"+fileName.toString();
			excel.setFilePath(filePath);
			excel.addHeader(10, "序号", "rownum");
			excel.addHeader(20, "日期", "date");
			excel.addHeader(20, "提交条数", "usersmstotal");
			excel.addHeader(20, "发送条数", "sendTotal");
			excel.addHeader(20, "成功条数", "successTotal");
			excel.addHeader(20, "平台拦截条数", "interceptTotal");
			excel.addHeader(20, "失败条数", "failTotal");
			excel.addHeader(20, "计费条数", "chargeTotal");


			excel.setShowRownum(false);
			int i = 1;
			for(Map da:data){
				da.put("rownum", i++);
			}
			excel.setDataList(data);
			if (ExcelUtils.exportExcel(excel)) {
				result.setSuccess(true);
				result.setMsg("报表生成成功");
				result.setData(fileName.toString());
				return result;
			}
		}catch(Exception e){
			logger.error("传统短信月统计报表生成失败", e);
		}
		return result;
	}





	/**
	 * 闪信日统计
	 * @param request
	 * @param mv
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/flashday")
	public ModelAndView flashDay(HttpServletRequest request, ModelAndView mv) throws Exception {

		// 代理商信息
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);

		// 日发送数据分页查询
		Map<String, String> params = SpringUtil.getFormData(request);

		String start_time = params.get("start_time");
		String end_time = params.get("end_time");
		// 设置默认时间
		if (start_time == null || end_time == null) {
			DateTime dt = DateTime.now();
			dt = dt.minusDays(1);
			start_time = dt.minusMonths(3).toString("yyyy-MM-dd");
			end_time = dt.toString("yyyy-MM-dd");
			params.put("start_time", start_time);
			params.put("end_time", end_time);
		}
		request.setAttribute("start_time", start_time);
		request.setAttribute("end_time", end_time);
		DateTime dt = DateTime.now();
		dt = dt.minusDays(1);
		request.setAttribute("min_time", dt.minusMonths(3).toString("yyyy-MM-dd"));
		request.setAttribute("max_time", dt.toString("yyyy-MM-dd"));


		params.put("clientid", AuthorityUtil.getLoginUserInfo(request).getClientId());

		Map<String, Object> queryParams = new HashMap<>();
		for (Map.Entry e : params.entrySet()) {
			queryParams.put(e.getKey().toString(), e.getValue());
		}
		List<Integer> productTypes = new ArrayList<>();
		productTypes.add(Integer.valueOf(Constant.OrderProductType.FLASH.getValue()));
		queryParams.put("productTypes",productTypes);
		queryParams.put("startDate", Integer.valueOf(start_time.replace("-", "")));
		queryParams.put("endDate",Integer.valueOf(end_time.replace("-", "")));
		PageContainer dayStatPage = bussinessService.queryDayStat(queryParams);
		mv.addObject("dayStatPage", dayStatPage);

		// 返回视图
		mv.setViewName("console/mms/flashday");
		return mv;
	}

	// 短信导出实时检查数据
	@RequestMapping(value = "/checkFlashDayNum")
	@ResponseBody
	public AjaxResult checkFlashDayNum() throws Exception {
		AjaxResult ajaxResult = new AjaxResult();

		Map<String, String> params = SpringUtil.getFormData(request);
		params.put("clientid", AuthorityUtil.getLoginUserInfo(request).getClientId());
		String start_time = params.get("start_time");
		String end_time = params.get("end_time");

		// 设置默认时间
		if (start_time == null || end_time == null) {
			DateTime dt = DateTime.now();
			dt = dt.minusDays(1);
			start_time = dt.withMillisOfDay(0).toString("yyyy-MM-dd HH:mm");
			end_time = dt.toString("yyyy-MM-dd HH:mm");
			params.put("start_time", start_time);
			params.put("end_time", end_time);
		}

		Map<String, Object> queryParams = new HashMap<>();
		for (Map.Entry e : params.entrySet()) {
			queryParams.put(e.getKey().toString(), e.getValue());
		}
		List<Integer> productTypes = new ArrayList<>();
		productTypes.add(Integer.valueOf(Constant.OrderProductType.FLASH.getValue()));
		queryParams.put("productTypes",productTypes);
		queryParams.put("startDate", Integer.valueOf(start_time.replace("-", "")));
		queryParams.put("endDate",Integer.valueOf(end_time.replace("-", "")));
		// 查询短信记录数量
		int totalCount =  bussinessService.exportBussinessDataCount(queryParams);


//		int totalCount = 0;
//		if (resultList!=null&&resultList.size() > 0) {
//			totalCount = Integer.parseInt(resultList.get(0).get("totalCount").toString());
//		}

		if (totalCount == 0) {
			ajaxResult.setIsSuccess(false);
		} else {
			ajaxResult.setIsSuccess(true);
		}

		return ajaxResult;
	}


	/**
	 * 导出闪信日统计
	 * @param request
	 * @param mv
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/exportFlashDayStat")
	@ResponseBody
	public ResultVO exportFlashDayStat(HttpServletRequest request, ModelAndView mv, HttpSession session) throws Exception {
		ResultVO result = new ResultVO();
		// 代理商信息
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);

		Map<String, String> params = SpringUtil.getFormData(request);

		String start_time = params.get("start_time");
		String end_time = params.get("end_time");
		// 设置默认时间
		if (start_time == null || end_time == null) {
			DateTime dt = DateTime.now();
			dt = dt.minusDays(1);
			start_time = dt.minusDays(45).toString("yyyy-MM-dd");
			end_time = dt.toString("yyyy-MM-dd");
			params.put("start_time", start_time);
			params.put("end_time", end_time);
		}
		request.setAttribute("start_time", start_time);
		request.setAttribute("end_time", end_time);

		Map<String, Object> queryParams = new HashMap<>();
		for (Map.Entry e : params.entrySet()) {
			queryParams.put(e.getKey().toString(), e.getValue());
		}

		queryParams.put("clientid",accountModel.getClientId());

		List<Integer> productTypes = new ArrayList<>();
		productTypes.add(Integer.valueOf(Constant.OrderProductType.FLASH.getValue()));
		queryParams.put("productTypes",productTypes);
		queryParams.put("startDate", Integer.valueOf(start_time.replace("-", "")));
		queryParams.put("endDate",Integer.valueOf(end_time.replace("-", "")));

		try{
			queryParams.put("limit", "LIMIT 0 , 60000");
//			List<Map<String, Object>> data = statDao.getSearchList("bussiness.queryStatistic", formData);
			List<Map<String, Object>> data = bussinessService.exportBussinessData(queryParams);
			if(data==null||data.size()<1){
				result.setMsg( "根据条件查询到的记录数为0，导出Excel文件失败");
				return result;
			}
			StringBuffer fileName = new StringBuffer();
			fileName.append("闪信日统计");
			Excel excel = new Excel();
			fileName.append( queryParams.get("startDate")).append("-").append(queryParams.get("endDate"));
			excel.setTitle(fileName.toString());
			fileName.append(".xls");
			String filePath = ConfigUtils.temp_file_dir + "/" + accountModel.getClientId() + "/"+fileName.toString();
			excel.setFilePath(filePath);
			excel.addHeader(10, "序号", "rownum");
			excel.addHeader(20, "日期", "date");
			excel.addHeader(20, "提交条数", "usersmstotal");
			excel.addHeader(20, "发送条数", "sendTotal");
			excel.addHeader(20, "成功条数", "successTotal");
			excel.addHeader(20, "平台拦截条数", "interceptTotal");
			excel.addHeader(20, "失败条数", "failTotal");
			excel.addHeader(20, "计费条数", "chargeTotal");


			excel.setShowRownum(false);
			int i = 1;
			for(Map da:data){
				da.put("rownum", i++);
			}
			excel.setDataList(data);
			if (ExcelUtils.exportExcel(excel)) {
				result.setSuccess(true);
				result.setMsg("报表生成成功");
				result.setData(fileName.toString());
				return result;
			}
		}catch(Exception e){
			logger.error("传统短信日统计报表生成失败", e);
		}
		return result;
	}


	/**
	 * 闪信月发送统计
	 * @param request
	 * @param mv
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/flashmonthly")
	public ModelAndView flashMonthly(HttpServletRequest request, ModelAndView mv) throws Exception {

		// 代理商信息
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);

		// 月发送数据分页查询
		Map<String, String> params = SpringUtil.getFormData(request);
		params.put("clientid", accountModel.getClientId());


		Map<String, Object> queryParams = new HashMap<>();
		for (Map.Entry e : params.entrySet()) {
			queryParams.put(e.getKey().toString(), e.getValue());
		}
		List<Integer> productTypes = new ArrayList<>();
		productTypes.add(Integer.valueOf(Constant.OrderProductType.FLASH.getValue()));
		queryParams.put("productTypes",productTypes);

		PageContainer monthStatPage = bussinessService.queryMonthStat(queryParams);
		mv.addObject("monthStatPage", monthStatPage);

		// 返回视图
		mv.setViewName("console/mms/flashmonthly");
		return mv;
	}

	/**
	 * 导出闪信月统计
	 * @param request
	 * @param mv
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/exportFlashMonthly")
	@ResponseBody
	public ResultVO exportFlashMonthly(HttpServletRequest request, ModelAndView mv) throws Exception {

		ResultVO result = new ResultVO();
		// 代理商信息
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);

		try{
			Map params = new HashMap();
			params.put("clientid",accountModel.getClientId());
			List<Integer> productTypes = new ArrayList<>();
			productTypes.add(Integer.valueOf(Constant.OrderProductType.FLASH.getValue()));
			params.put("productTypes",productTypes);
			params.put("limit", "LIMIT 0 , 60000");
			List<Map<String, Object>> data = bussinessService.exportMonthStat(params);
			if(data==null||data.size()<1){
				result.setMsg( "根据条件查询到的记录数为0，导出Excel文件失败");
				return result;
			}
			StringBuffer fileName = new StringBuffer();
			fileName.append("闪信月统计");
			Excel excel = new Excel();
			excel.setTitle(fileName.toString());
			fileName.append(".xls");
			String filePath = ConfigUtils.temp_file_dir + "/" + accountModel.getClientId() + "/"+fileName.toString();
			excel.setFilePath(filePath);
			excel.addHeader(10, "序号", "rownum");
			excel.addHeader(20, "日期", "date");
			excel.addHeader(20, "提交条数", "usersmstotal");
			excel.addHeader(20, "发送条数", "sendTotal");
			excel.addHeader(20, "成功条数", "successTotal");
			excel.addHeader(20, "平台拦截条数", "interceptTotal");
			excel.addHeader(20, "失败条数", "failTotal");
			excel.addHeader(20, "计费条数", "chargeTotal");


			excel.setShowRownum(false);
			int i = 1;
			for(Map da:data){
				da.put("rownum", i++);
			}
			excel.setDataList(data);
			if (ExcelUtils.exportExcel(excel)) {
				result.setSuccess(true);
				result.setMsg("报表生成成功");
				result.setData(fileName.toString());
				return result;
			}
		}catch(Exception e){
			logger.error("传统短信月统计报表生成失败", e);
		}
		return result;
	}
}
