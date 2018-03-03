package com.ucpaas.smsp.controller.offcial;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ucpaas.smsp.common.entity.AjaxResult;
import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.controller.BaseController;
import com.ucpaas.smsp.model.AgentApplyVO;
import com.ucpaas.smsp.model.po.SmsAccountModelPo;
import com.ucpaas.smsp.service.common.CommonService;
import com.ucpaas.smsp.service.home.HomeService;
import com.ucpaas.smsp.util.CommonUtil;

@Controller
public class IndexController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	private HomeService homeService;
	@Autowired
	private CommonService commonService;

	/**
	 * 主页
	 */
	@RequestMapping(value = "/")
	public void welcome(ModelAndView model,HttpServletResponse response) throws Exception {
		// return "redirect:/index.html";
		try {
			response.sendRedirect("/index.html");
		} catch (IOException e) {
			logger.error("springmvc转发/login失败", e);
			throw e;
		}
	}

	/**
	 * 主页
	 */
	@RequestMapping(value = "/index.html")
	public ModelAndView homePage(ModelAndView model) throws Exception {
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		model.addObject("account", accountModel);
		model.setViewName("official/index");
		return model;
	}

	/**
	 * 在线体验页面
	 */
	@RequestMapping(value = "/experience.html")
	public String experience(Model model) throws Exception {
		return "official/experience";
	}

	/**
	 * 校验验证码
	 */
	@RequestMapping(value = "/randCodeCheck")
	@ResponseBody
	public Map<String, Object> randCodeCheck() throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", commonService.randCodeCheck(request));

		return result;
	}

	/**
	 * 体验短信
	 */
	@RequestMapping(value = "/sms_experience", method = RequestMethod.POST)
	@ResponseBody
	public AjaxResult sms(@RequestParam("mobile") String mobile) throws Exception {

		AjaxResult ajaxResult;
		Map<String, Object> result = homeService.smsExperience(mobile, CommonUtil.getClientIP(request));

		if (result != null) {
			ajaxResult = new AjaxResult(AjaxResult.SUCCESS, (Object) result);
		} else {
			ajaxResult = new AjaxResult(AjaxResult.ERROR, "短信体验失败");
		}

		// AjaxResult ajaxResult = new AjaxResult(AjaxResult.SUCCESS, true);

		return ajaxResult;
	}

	/**
	 * 代理商申请
	 */
	@RequestMapping(value = "/agentApply", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> agentApply(@ModelAttribute("agentApplyVO") AgentApplyVO agentApplyVO) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result = homeService.agentApply(agentApplyVO);
		return result;
	}

	/**
	 * 生成验证码图片
	 */
	@RequestMapping(value = "/randCheckCodePic")
	@ResponseBody
	public void randCheckCodePic() throws Exception {
		commonService.randCheckCodePic(request, response);
	}

}
