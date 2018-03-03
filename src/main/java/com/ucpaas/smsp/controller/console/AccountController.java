/**    
 * @Title: AccountController.java  
 * @Package: com.ucpaas.smsp.controller.account  
 * @Description: TODO
 * @author: Niu.T    
 * @date: 2016年9月23日 上午11:20:31  
 * @version: V1.0    
 */
package com.ucpaas.smsp.controller.console;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.controller.BaseController;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.service.account.AccountService;
import com.ucpaas.smsp.service.common.CommonService;

/**  
 * @ClassName: AccountController  
 * @Description: 官网&客户平台 -> 账户管理Controller
 * @author: Niu.T 
 * @date: 2016年9月23日 上午11:20:31  
 */
@Controller
@RequestMapping("console/account")
@Api(value = "console/account" , description = "账户管理")
//url:/模块/{id}/细分/list
public class AccountController extends BaseController{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private CommonService commonService;
	
	/**
	 * @Description: 账户管理 -> (跳转)获取账户基本信息
	 * @author: Niu.T 
	 * @date: 2016年9月23日 上午11:41:54  
	 * @param model
	 * @return: String
	 */
	@ApiOperation(value = "账户基本信息页面")
	@RequestMapping(value="/info", method = RequestMethod.GET)
	public String accountInfo(HttpServletRequest request,Model model){
		// 1. 获取当前登录账户的ID
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession().getAttribute(Constant.LOGIN_USER_INFO);
		String clientId = accountModel != null ? accountModel.getClientId() : null;
		// 2. 调用业务获取对应账户的基本信息
		SmsAccountModel accountInfo = accountService.getAccountInfo(clientId);
		// 3. 将数据添加到视图模型中
		model.addAttribute("accountModel",accountModel);
		model.addAttribute("accountInfo",accountInfo);
		return "console/account/info";//smsp-web/WebContent/WEB-INF/page/account/accountInfo.jsp
	}	
	
	/**
	 * @Description: 账户资料 -> 修改基本资料
	 * @author: Niu.T 
	 * @date: 2016年9月23日 上午11:59:44  
	 * @return: Map<String,Object>
	 */
	@ApiOperation(value = "修改基本资料")
	@RequestMapping(path="/update", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> updateAccount( @ApiParam(value = "账户信息", required = true) SmsAccountModel smsAccountModel){
		
		Map<String,Object> data = new HashMap<String,Object>();
		// 1. 调用service,进行业务处理
		data = accountService.updateAccInfo(smsAccountModel);
		return data;
	}
	
	/**
	 * @Description: 账户资料 -> (跳转至)账户资质信息(若为添加资质信息则跳转到添加页面)
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午12:02:10  
	 * @return: String
	 */
//	@RequestMapping("/cerInfo")
//	public String getCerInfo(HttpServletRequest request,Model model){
//		SmsAccountModel accountModel = (SmsAccountModel) request.getSession().getAttribute(Constant.LOGIN_USER_INFO);
//		String clientId = accountModel != null ? accountModel.getClientId() : null;
//		SmsOauthPic smsOauthPic = accountService.getCerInfo(clientId);
//		String smspImgUrl =  ConfigUtils.smsp_img_url.endsWith("/")? ConfigUtils.smsp_img_url.substring(0, ConfigUtils.smsp_img_url.lastIndexOf("/")):ConfigUtils.smsp_img_url;
//		if(smsOauthPic.getImgUrl()!=null){
//			String img = smspImgUrl+"/file/scanPic.html?path="+smsOauthPic.getImgUrl();
//			smsOauthPic.setImgUrl(img);
//		}
//		model.addAttribute("cerInfo", smsOauthPic);
//		model.addAttribute("accountModel", accountModel);
//		return smsOauthPic.getId() != null ? "console/account/cerInfo_view" : "console/account/cerInfo_save";
//	}
	
	/**
	 * @Description: 账户资料 -> (跳转)修改账户资质信息
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午12:26:18  
	 * @return: String
	 */
//	@RequestMapping("/updateCerInfo")
//	public String updateCerInfo(HttpServletRequest request,Model model){
//		// 1. 获取当前用户的资质信息
//		SmsAccountModel accountModel = (SmsAccountModel) request.getSession().getAttribute(Constant.LOGIN_USER_INFO);
//		String clientId = accountModel != null ? accountModel.getClientId() : null;
//		SmsOauthPic smsOauthPic = accountService.getCerInfo(clientId);
//		model.addAttribute("cerInfo", smsOauthPic);
//		model.addAttribute("accountModel", accountModel);
//		// 添加flag (1)update:更新客户资质 (2)add:新添加客户资质
//		model.addAttribute("flag","update");
//		return "console/account/saveCerInfo";
//	}
	
	/**
	 * @Description: 账户资料 -> 保存账户资质
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午2:06:54  
	 * @return: Map<String,Object>
	 */
//	@RequestMapping(value = "/saveCer", method = RequestMethod.POST)
//	@ResponseBody
//	public Map<String,Object> saveCerInfo( SmsOauthPic smsOauthPic,@RequestParam(value="flag",required=false) String flag,
//			HttpServletRequest request){
//		
//		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//		CommonsMultipartFile photoFile = (CommonsMultipartFile) multipartRequest.getFile("photoFile");
//		
//		Map<String,Object> data = new HashMap<String,Object>();
//		// 1. 判断页面提交的数据是否有效
//		if (photoFile != null && !photoFile.isEmpty()) {
//			String suffix = photoFile.getOriginalFilename().substring(photoFile.getOriginalFilename().lastIndexOf("."));
//			if (!suffix.toLowerCase().equals(".gif") && !suffix.toLowerCase().equals(".jpg")
//					&& !suffix.toLowerCase().equals(".jpeg")) {
//				data.put("msg", "对不起，上传的图片格式必须是jpg，gif，jpeg格式！");
//				data.put("result", "fail");
//				return data;
//			}
//			if (photoFile.getSize() > 1024 * 2048) {
//				data.put("msg", "对不起，上传的图片大小必须小于2MB！");
//				data.put("result", "fail");
//				return data;
//			}
//
//			String uploadUrl = ConfigUtils.client_oauth_pic;// 客户资质图片存放文件夹
//			// 上传图片文件名称
//			StringBuffer photoName = new StringBuffer(smsOauthPic.getClientId()).append(smsOauthPic.getIdType()).append(suffix);
//			String path = uploadUrl + photoName;
//			File saveFile = new File(new File(uploadUrl), photoName.toString());
//			if (!saveFile.getParentFile().exists()) {
//				saveFile.getParentFile().mkdir();
//			}
//			// 2. 复制上传的资质图片到服务指定路径
//			try {
//				FileUtils.copyInputStreamToFile(photoFile.getInputStream(), saveFile);
//			} catch (IOException e) {
//				data.put("msg","图片存储失败，请稍后再试");
//				data.put("result","fail");
//				logger.debug("图片复制失败 ----------" + e.getMessage(),e);
//				return data;
//			}
//			// 更新数据库图片地址
//			smsOauthPic.setImgUrl(path);
//			// 3. 获取flag,判断 :(1)add 新添加资质 (2)update更新资质
//			if(flag == null || !"update".equals(flag)){//不是更新则是添加
//				try {
//					data = accountService.addCerInfo(smsOauthPic);
//				} catch (Exception e) {
//					data.put("msg","数据存储异常，请稍后再试");
//					data.put("result","fail");
//					logger.debug(e.getMessage(),e);
//				}
//				return data;
//			}
//		}
//		if(flag != null && "update".equals(flag)){//如果是更新
//			try {
//				data = accountService.updateCerInfo(smsOauthPic);
//			} catch (Exception e) {
//				data.put("msg","数据存储异常，请稍后再试");
//				data.put("result","fail");
//				logger.debug(e.getMessage(),e);
//			}
//		}else{
//			data.put("msg","请选择图片");
//			data.put("result","fail");
//			
//		}
//		return data;
//	}
	
	/**
	 * @Description: 账户安全 -> (跳转至)账户安全信息
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午2:16:05  
	 * @param model
	 * @return: String
	 */
	@RequestMapping("/security")
	public String securityInfo(HttpServletRequest request,Model model){
		// 1. 获取当前登录账户的ID
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession().getAttribute(Constant.LOGIN_USER_INFO);
		String clientId = accountModel != null ? accountModel.getClientId() : null;
		// 2. 调用业务获取对应账户的基本信息
		SmsAccountModel accountInfo = accountService.getAccountInfo(clientId);
		// 3. 将数据添加到视图模型中
		model.addAttribute("accountModel",accountModel);
		model.addAttribute("accountInfo",accountInfo);

		return "console/account/security";
	}
	
	/**
	 * @Description: 判断session是否有效
	 * @author: Niu.T 
	 * @date: 2016年9月27日 下午8:45:41  
	 * @return: boolean
	 */
	@RequestMapping("/online")
	@ResponseBody
	public boolean isSessionValid(){
		return true;
	}
	
	/**
	 * @Description: 账户安全 -> 密码编辑页面
	 * @author: Niu.T 
	 * @date: 2016年9月23日 下午2:19:33  
	 * @return: String
	 */
	@RequestMapping("/modifyPassword")
	public String editPassword(HttpServletRequest request,Model model){
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession().getAttribute(Constant.LOGIN_USER_INFO);
		String clientId = accountModel.getClientId();
		model.addAttribute("clientId", clientId);
		model.addAttribute("accountModel", accountModel);
		return "console/account/modifyPassword";
	}
	
	/**
	 * @Description: 账户安全 -> 修改密码
	 * @author: Niu.T 
	 * @date: 2016年9月24日 上午11:26:49  
	 * @return: Map<String,Object>
	 */
	@RequestMapping("/updatePassword")
	@ResponseBody
	public Map<String,Object> updatePassword(HttpServletRequest request){
		Map<String,Object> result = new HashMap<String,Object>();
//		String oldPassword = EncryptUtils.decodeDes3(request.getParameter("oldPwd"));
//		String newPassword = EncryptUtils.decodeDes3(request.getParameter("password1"));
		String oldPassword = request.getParameter("oldPwd");
		String newPassword = request.getParameter("password1");
		
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession().getAttribute(Constant.LOGIN_USER_INFO);
		accountModel.setPassword(oldPassword);
		
		boolean isExist = accountService.checkPasswordById(accountModel);//判断旧密码是否正确
		if(isExist){
			accountModel.setPassword(newPassword);
			result = accountService.updatePassword(accountModel);
			
		}else{
			result.put("result", "fail");
			result.put("msg", "原密码错误");
		}
		return result;
	}
	
	@RequestMapping(value = "/isExistOauthPicInfo")
	@ResponseBody
	public Map<String, Object> isExistOauthPicInfo(HttpSession session) {
		SmsAccountModel accountModel = (SmsAccountModel) session.getAttribute(Constant.LOGIN_USER_INFO);
		Map<String, Object> result = new HashMap<String, Object>();
		String client_id = accountModel.getClientId();
		result = commonService.isExistOauthPicInfo(client_id);

		return result;
	}

	@RequestMapping(value="/isOauth", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> clientOauthStatusCheck(HttpSession session) throws Exception{
    	Map<String, Object> result = new HashMap<String, Object>();
    	SmsAccountModel accountModel = (SmsAccountModel) session.getAttribute(Constant.LOGIN_USER_INFO);
    	result = commonService.clientAccountStatusCheck(accountModel.getClientId());
    	
		return result;
    }
	
}
