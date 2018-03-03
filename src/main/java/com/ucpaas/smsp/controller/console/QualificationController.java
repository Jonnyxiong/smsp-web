package com.ucpaas.smsp.controller.console;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.controller.BaseController;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.model.SmsOauthPic;
import com.ucpaas.smsp.service.account.AccountService;
import com.ucpaas.smsp.util.ConfigUtils;

@Controller
@RequestMapping("console/qualification")
public class QualificationController extends BaseController{

	private Logger logger = LoggerFactory.getLogger(QualificationController.class);
	
	@Autowired
	private AccountService accountService;
	
	/**
	 * @Description: 账户资料 -> (跳转至)账户资质信息(若为添加资质信息则跳转到添加页面)
	 * @author: huangwenjie
	 * @date: 2016年9月23日 下午12:02:10  
	 * @return: String
	 * @throws Exception 
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getCerInfo(HttpServletRequest request,ModelAndView mv,HttpSession session) throws Exception{
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession().getAttribute(Constant.LOGIN_USER_INFO);
		String clientId = accountModel != null ? accountModel.getClientId() : null;
		SmsOauthPic smsOauthPic = accountService.getCerInfo(clientId);
		String smspImgUrl =  ConfigUtils.smsp_img_url.endsWith("/")? ConfigUtils.smsp_img_url.substring(0, ConfigUtils.smsp_img_url.lastIndexOf("/")):ConfigUtils.smsp_img_url;
		if(smsOauthPic.getImgUrl()!=null){
			String img = smspImgUrl+"/file/scanPic.html?path="+smsOauthPic.getImgUrl();
			smsOauthPic.setImgUrl(img);
		}
		mv.addObject("cerInfo", smsOauthPic);
		mv.addObject("accountModel", accountModel);
		mv.addObject("smsp_img_url",smspImgUrl); // 图片服务器地址
		mv.setViewName(smsOauthPic.getId() != null ? "console/qualification/view" : "console/qualification/save");
		return mv;
	}
	
	
	/**
	 * @Description: 账户资料 -> 保存账户资质
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午2:06:54  
	 * @return: Map<String,Object>
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> saveCerInfo( SmsOauthPic smsOauthPic,@RequestParam(value="flag",required=false) String flag,
			HttpServletRequest request){
		
		Map<String,Object> data = new HashMap<String,Object>();
		// 1. 判断页面提交的数据是否有效

//			String uploadUrl = ConfigUtils.client_oauth_pic;// 客户资质图片存放文件夹
			// 上传图片文件名称
			// 更新数据库图片地址
		// 3. 获取flag,判断 :(1)add 新添加资质 (2)update更新资质
		if(flag == null || !"update".equals(flag)){//不是更新则是添加
			try {
				data = accountService.addCerInfo(smsOauthPic);
			} catch (Exception e) {
				data.put("msg","数据存储异常，请稍后再试");
				data.put("result","fail");
				logger.debug(e.getMessage(),e);
			}
			return data;
		}else if(flag != null && "update".equals(flag)){//如果是更新
			try {
				data = accountService.updateCerInfo(smsOauthPic);
			} catch (Exception e) {
				data.put("msg","数据存储异常，请稍后再试");
				data.put("result","fail");
				logger.debug(e.getMessage(),e);
			}
		}else{
			data.put("msg","请选择图片");
			data.put("result","fail");
			
		}
		return data;
	}
	
	/**
	 * @Description: 资质信息-重新认证
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午12:26:18  
	 * @return: String
	 * @throws Exception 
	 */
	@RequestMapping("/update")
	public ModelAndView updateCerInfo(HttpServletRequest request,ModelAndView mv,HttpSession session) throws Exception{
		// 1. 获取当前用户的资质信息
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession().getAttribute(Constant.LOGIN_USER_INFO);
		String clientId = accountModel != null ? accountModel.getClientId() : null;
		SmsOauthPic smsOauthPic = accountService.getCerInfo(clientId);
		String smspImgUrl =  ConfigUtils.smsp_img_url.endsWith("/")? ConfigUtils.smsp_img_url.substring(0, ConfigUtils.smsp_img_url.lastIndexOf("/")):ConfigUtils.smsp_img_url;
		String img = smspImgUrl+"/file/scanPic.html?path="+smsOauthPic.getImgUrl();
		smsOauthPic.setImgUrl(img);
		mv.addObject("cerInfo", smsOauthPic);
		mv.addObject("accountModel", accountModel);
		// 添加flag (1)update:更新客户资质 (2)add:新添加客户资质
		mv.addObject("flag","update");
		mv.addObject("smsp_img_url",smspImgUrl); // 图片服务器地址
		mv.setViewName("console/qualification/save");
		return mv;
	}
}
