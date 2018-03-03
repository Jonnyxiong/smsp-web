package com.ucpaas.smsp.controller.console;

import com.jsmsframework.common.dto.JsmsPage;
import com.jsmsframework.common.enums.ProductType;
import com.jsmsframework.common.util.BeanUtil;
import com.jsmsframework.product.dto.JsmsProductInfoDTO;
import com.jsmsframework.product.entity.JsmsProductInfo;
import com.jsmsframework.product.service.JsmsProductInfoService;
import com.ucpaas.smsp.common.entity.AjaxResult;
import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.dao.common.SmsAccountDao;
import com.ucpaas.smsp.entity.message.ClientBalanceAlarm;
import com.ucpaas.smsp.model.PageBean;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.model.param.PageParam;
import com.ucpaas.smsp.model.po.SmsAccountModelPo;
import com.ucpaas.smsp.service.common.LoginService;
import com.ucpaas.smsp.service.index.ClientBalanceAlarmService;
import com.ucpaas.smsp.service.order.OrderManagerService;
import com.ucpaas.smsp.util.ConfigUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/console")
public class ConsoleController {

	private static Logger logger = LoggerFactory.getLogger(ConsoleController.class);

	@Autowired
	private OrderManagerService orderManagerService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private ClientBalanceAlarmService clientBalanceAlarmService;
	
	@Autowired
	private SmsAccountDao smsAccountDao;

	@Autowired
	private JsmsProductInfoService jsmsProductInfoService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getProductListByClientId(HttpServletRequest request, ModelAndView mv, PageParam pageParam) {

		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		String clientId = accountModel.getClientId();
		SmsAccountModel accountModel2 = loginService.getLoginClientInfo(clientId);

		List<Integer> productTypes = new ArrayList<>();
		Map<String, Object> sqlParams = new HashMap<>();
		sqlParams.put("clientId", accountModel.getClientId());
		SmsAccountModel dbAccount = smsAccountDao.getAccountInfoByClientId(sqlParams );
		if(dbAccount.getSmsfrom()==6){
			productTypes = Arrays.asList(ProductType.行业.getValue(),ProductType.营销.getValue(),ProductType.国际.getValue(),ProductType.验证码.getValue(),ProductType.通知.getValue(),ProductType.USSD.getValue(),ProductType.闪信.getValue());
		}else{
			if(dbAccount.getSmstype()==0||dbAccount.getSmstype()==4){
				productTypes = Arrays.asList(ProductType.行业.getValue(),ProductType.国际.getValue(),ProductType.验证码.getValue(),ProductType.通知.getValue());
			}else{
				productTypes = Arrays.asList(ProductType.营销.getValue(),ProductType.国际.getValue());
			}
		}
		
//		PageBean pageBean = orderManagerService.getProductListByClientId(clientId, pageParam,productTypes);
		JsmsPage jsmsPage = new JsmsPage();
		sqlParams.put("productTypes", productTypes);
		jsmsPage.setRows(pageParam.getPageSize());
		jsmsPage.setPage(pageParam.getGoalPage());
		jsmsPage.getParams().put("productTypes",productTypes);
		jsmsPage = jsmsProductInfoService.queryProxiedList(jsmsPage,accountModel.getAgentId(),accountModel.getClientId());
		List<JsmsProductInfoDTO> jsmsProductInfoDTOS = new ArrayList<>();
		if(jsmsPage.getData()!=null&&jsmsPage.getData().size()>0){
			List<JsmsProductInfo> jsmsProductInfos = jsmsPage.getData();
			for(JsmsProductInfo jsmsProductInfo:jsmsProductInfos) {
				JsmsProductInfoDTO dto = new JsmsProductInfoDTO();
				BeanUtil.copyProperties(jsmsProductInfo,dto);
				if (dto.getProductType().intValue() == 2){
					dto.setProductPrice(dto.getProductPrice().setScale(4, 4));

					dto.setTotalPriceStr(dto.getQuantity().multiply(dto.getProductPrice()).setScale(4, 4).toString());
				}

				jsmsProductInfoDTOS.add(dto);
			}
		}
		jsmsPage.setData(jsmsProductInfoDTOS);

		accountModel.setOauthStatus(accountModel2.getOauthStatus());

		List<Map> remainQuantityList = orderManagerService.getOrderRemainQuantity(accountModel2);
		for (Map map : remainQuantityList) { // 产品类型，0：行业，1：营销，2：国际，7：USSD，8：闪信，9：挂机短信，其中0和1为普通短信，2为国际短信
			int productType = (int) map.get("productType");
			if (productType == ProductType.行业.getValue().intValue()) {
				mv.addObject("remainHangYe", map.get("remainQuantity"));
			} else if (productType == ProductType.验证码.getValue().intValue()) {
				mv.addObject("remainYanZhengMa", map.get("remainQuantity"));
			} else if (productType == ProductType.通知.getValue().intValue()) {
				mv.addObject("remainTongZhi", map.get("remainQuantity"));
			} else if (productType == ProductType.营销.getValue().intValue()) {
				mv.addObject("remainYingXiao", map.get("remainQuantity"));
			} else if (productType == ProductType.国际.getValue().intValue()) {
				mv.addObject("remainGuoJi", map.get("remainQuantity"));
			} else if (productType == ProductType.USSD.getValue().intValue()) {
				mv.addObject("remainUSSD", map.get("remainQuantity"));
			} else if (productType == ProductType.闪信.getValue().intValue()) {
				mv.addObject("remainShanXin", map.get("remainQuantity"));
			}
		}

		mv.addObject("jsmsPage", jsmsPage);
		mv.addObject("accountModel", accountModel);
		if(1==accountModel.getPaytype()){
			return new ModelAndView("redirect:/console/bussiness/statistics");

		}else{
			mv.setViewName("console/index");
		}


		return mv;

	}

	@RequestMapping("/login")
	public String login() {
		return "console/login";
	}

	@RequestMapping("/lockscreen")
	public String lockscreen() {
		return "console/lockscreen";
	}

	@RequestMapping("/nav")
	public String nav() {
		return "console/nav";
	}

	@RequestMapping(value = "/loginValidate", method = RequestMethod.POST)
	@ResponseBody
	public AjaxResult loginValidate(HttpServletRequest request, String loginAccount, String password)
			throws IOException {
		AjaxResult result = loginService.loginValidate(loginAccount, password);
		request.getSession().setAttribute(Constant.LOGIN_USER_INFO, result.getData());
		request.getSession().setAttribute(Constant.WEB_ID, ConfigUtils.web_id);
		return result;
	}

	@RequestMapping(value = "/quit", method = RequestMethod.GET)
	public void quit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getSession().setAttribute(Constant.LOGIN_USER_INFO, null);
		// return "redirect:/login";
		try {
			response.sendRedirect("/login");
		} catch (IOException e) {
			logger.error("springmvc转发/login失败", e);
			throw e;
		}
	}

	@RequestMapping("/isSessionValid")
	@ResponseBody
	public AjaxResult isSessionValid() {
		AjaxResult result = new AjaxResult();
		result.setIsSuccess(true);
		return result;
	}

	@RequestMapping("/remainList")
	public ModelAndView remainList(HttpServletRequest request, ModelAndView mv,
			@RequestParam(required = false) Integer productType, @RequestParam(required = false) Integer goalPage,
			@RequestParam(required = false) Integer pageSize) {
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession().getAttribute(Constant.LOGIN_USER_INFO);
		if (productType == null)
			productType = -1;
		if (goalPage == null)
			goalPage = 1;
		if (pageSize == null)
			pageSize = 10;
		switch (productType) {
		case 0:
			mv.addObject("title", "行业余额");
			break;
		case 1:
			mv.addObject("title", "营销短信余额");
			break;
		case 2:
			mv.addObject("title", "国际短信余额");
			break;
		case 3:
				mv.addObject("title", "验证码短信余额");
				break;
		case 4:
				mv.addObject("title", "通知短信余额");
				break;
		case 7:
			mv.addObject("title", "USSD短信余额");
			break;
		case 8:
			mv.addObject("title", "闪信短信余额");
			break;
		case 9:
			mv.addObject("title", "挂机短信余额");
			break;
		default:
			mv.addObject("title", "短信余额");
			break;
		}

		PageParam pageParam = new PageParam();
		pageParam.setGoalPage(goalPage);
		pageParam.setPageSize(pageSize);
		Map<String, Object> orderParam = new HashMap<>();
		orderParam.put("clientId", accountModel.getClientId());
		orderParam.put("productType", productType);
		PageBean pageBean = orderManagerService.listRemain(orderParam, pageParam);
		mv.addObject("accountModel", accountModel);
		mv.addObject("pageBean", pageBean);
		mv.addObject("productType", productType);
		mv.setViewName("console/remainList");
		return mv;
	}

	@RequestMapping(value = "/balancealarm/save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveBalanceAlarm(@RequestBody ClientBalanceAlarm clientBalanceAlarm) {
		try {
			return clientBalanceAlarmService.saveClientBalanceAlarm(clientBalanceAlarm);
		} catch (Exception e) {
			logger.error("预付费客户余额预警信息设置失败{}", e);
			Map<String, Object> result = new HashMap<>();
			result.put("code", 500);
			result.put("msg", "设置失败！");
			return result;
		}
	}

	@RequestMapping(value = "/balancealarm/info", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getBalanceAlarm(String clientid) {
		try {
			if (StringUtils.isBlank(clientid)) {
				Map<String, Object> result = new HashMap<>();
				result.put("code", 500);
				result.put("msg", "客户ID不能为空！");
				return result;
			}

			ClientBalanceAlarm info = clientBalanceAlarmService.getByClientId(clientid);
			Map<String, Object> result = new HashMap<>();
			result.put("code", 0);
			result.put("msg", "查询成功！");
			result.put("data", info);
			return result;

		} catch (Exception e) {
			logger.error("预付费客户余额预警信息查询失败{}", e);
			Map<String, Object> result = new HashMap<>();
			result.put("code", 500);
			result.put("msg", "查询失败！");
			return result;
		}
	}

}
