package com.ucpaas.smsp.controller.console;

import com.jsmsframework.common.dto.Result;
import com.jsmsframework.common.enums.ClientOrderType;
import com.jsmsframework.common.enums.ProductType;
import com.jsmsframework.order.product.service.JsmsClientOrderProductService;
import com.ucpaas.smsp.common.entity.AjaxResult;
import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.controller.BaseController;
import com.ucpaas.smsp.dao.common.SmsAccountDao;
import com.ucpaas.smsp.model.Excel;
import com.ucpaas.smsp.model.PageBean;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.model.param.GetOrderParam;
import com.ucpaas.smsp.model.param.PageParam;
import com.ucpaas.smsp.model.param.PlaceOrderParam;
import com.ucpaas.smsp.model.po.SmsAccountModelPo;
import com.ucpaas.smsp.model.po.SmsClientOrderPo;
import com.ucpaas.smsp.model.po.SmsProductInfoPo;
import com.ucpaas.smsp.service.common.LoginService;
import com.ucpaas.smsp.service.order.OrderManagerService;
import com.ucpaas.smsp.util.ConfigUtils;
import com.ucpaas.smsp.util.DateUtil;
import com.ucpaas.smsp.util.JsonUtils;
import com.ucpaas.smsp.util.file.ExcelUtils;
import com.ucpaas.smsp.util.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/console/order")
public class OrderController extends BaseController{

	private static  final Logger logger= LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderManagerService orderManagerService;
	
	@Autowired
	private LoginService loginService;
		
	@Autowired
	private SmsAccountDao smsAccountDao;
	@Autowired
	private JsmsClientOrderProductService jsmsClientOrderProductService;
	
	

	@RequestMapping(value = "/createOrder", method = RequestMethod.POST)
	@ResponseBody
	public AjaxResult createOrder(HttpServletRequest request,String placeOrderParamStr) {
		PlaceOrderParam placeOrderParam = JsonUtils.toObject(placeOrderParamStr, PlaceOrderParam.class);
		SmsAccountModelPo accountModel = (SmsAccountModelPo) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		placeOrderParam.setAgentId(accountModel.getAgentId());
		List<String> productCodes = new ArrayList<>();
		List<Integer> purchaseNums = new ArrayList<>();
		List<Long> orderIds = new ArrayList<>();
		for(SmsProductInfoPo smsProductInfoPo:placeOrderParam.getProductInfoPoList()){
			productCodes.add(smsProductInfoPo.getProductCode());
			purchaseNums.add(smsProductInfoPo.getProductNum().intValue());
			orderIds.add(orderManagerService.getOrderId());
		}
		AjaxResult ajaxResult = new AjaxResult();
		try {
			Result result = jsmsClientOrderProductService.createOrder(productCodes, purchaseNums, orderIds, ClientOrderType.客户购买, accountModel.getAgentId(), accountModel.getClientId());
			ajaxResult.setIsSuccess(result.isSuccess());
			ajaxResult.setMessage(result.getMsg());
		}catch (Exception e){
			logger.error("创建订单失败",e);
			String clazz = e.getClass().toString();
			ajaxResult.setIsSuccess(false);
			if(clazz.contains("jsms")||clazz.contains("Jsms")){
				ajaxResult.setMessage(e.getMessage());
			}else{
				ajaxResult.setMessage("创建订单失败");
			}
			return ajaxResult;
		}
//		AjaxResult result = orderManagerService.createOrder(placeOrderParam);
		return ajaxResult;
	}
	
	
	
	/**
	 * 我的订单
	 * @param request
	 * @param getOrderParam
	 * @param pageParam
	 * @param mv
	 * @return
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(HttpServletRequest request,
			GetOrderParam getOrderParam,PageParam pageParam,ModelAndView mv) {
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		
		getOrderParam.setClientId(accountModel.getClientId());
		PageBean pageBean = orderManagerService.getOrderListInfo(getOrderParam,pageParam);
		
		mv.addObject("pageBean",pageBean);
		mv.addObject("accountModel",accountModel);
		mv.setViewName("console/order/list");
		mv.addObject("getOrderParam", getOrderParam);
		return mv;
	}
	
	
	@RequestMapping(value = "/getOrderDetail")
	public ModelAndView getOrderDetailBySubId(HttpServletRequest request,ModelAndView mv,String subId){
		
		SmsClientOrderPo smsClientOrderPo = orderManagerService.getOrderDetailBySubId(subId);
		mv.addObject("orderDetial",smsClientOrderPo);
		mv.setViewName("console/order/orderDetail");
		
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		
		mv.addObject("accountModel",accountModel);
		return mv;
	}
	
	
	
	@RequestMapping(value = "/cancelOrder")
	@ResponseBody
	public AjaxResult cancelOrder(String subId){
		return orderManagerService.cancelOrder(subId);
	}
	
	
//	@RequestMapping(value = "/getOrderList")
//	public ModelAndView  test(ModelAndView mv){
//		mv.setViewName("/order/orderlist");
//		return mv;
//	}

	
	//没发现有使用的
	@RequestMapping(value = "/updateOrderStatus")
	@Deprecated
	public ModelAndView updateOrderStatus(ModelAndView mv,String status,String subId){
		orderManagerService.updateOrderStatus(status,subId);
		mv.setViewName("redirect:/order/getOrderListInfo");
		return mv;
	}
	
	
	@RequestMapping(value = "/listByProductType")
	@ResponseBody
	public PageBean listByProductType(HttpServletRequest request,String productType,Integer goalPage,Integer pageSize,ModelAndView mv) {
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		
		PageParam pageParam = new PageParam();
		pageParam.setGoalPage(goalPage);
		pageParam.setPageSize(pageSize);
		Map<String,Object> orderParam = new HashMap<>();
		orderParam.put("clientId", accountModel.getClientId());
		orderParam.put("productType", productType);
		PageBean pageBean = orderManagerService.getOrderListByProductType(orderParam,pageParam);
		return pageBean;
	}
	@RequestMapping(value = "/listRemain")
	@ResponseBody
	public PageBean listRemain(HttpServletRequest request,String productType,Integer goalPage,Integer pageSize,ModelAndView mv) {
		SmsAccountModel accountModel = (SmsAccountModel) request.getSession()
				.getAttribute(Constant.LOGIN_USER_INFO);
		
		PageParam pageParam = new PageParam();
		pageParam.setGoalPage(goalPage);
		pageParam.setPageSize(pageSize);
		Map<String,Object> orderParam = new HashMap<>();
		orderParam.put("clientId", accountModel.getClientId());
		orderParam.put("productType", productType);
		PageBean pageBean = orderManagerService.listRemain(orderParam,pageParam);
		return pageBean;
	}

    @RequestMapping(value = "/groupOrderListByProductTypeAll")
    @ResponseBody
    public Map groupOrderListByProductType(HttpServletRequest request, ModelAndView mv) {
        SmsAccountModel accountModel = (SmsAccountModel) request.getSession().getAttribute(Constant.LOGIN_USER_INFO);
        Map result = new HashMap();
        Map<String, Object> orderParam = new HashMap<>();
        orderParam.put("clientId", accountModel.getClientId());
        List<Integer> productTypes = new ArrayList<>();

        SmsAccountModel dbAccount = loginService.getLoginClientInfo(accountModel.getClientId());
        if (dbAccount.getSmsfrom() == 6) {
            productTypes = Arrays.asList(ProductType.行业.getValue(), ProductType.营销.getValue(),
                    ProductType.国际.getValue(), ProductType.验证码.getValue(), ProductType.通知.getValue(),
                    ProductType.USSD.getValue(), ProductType.闪信.getValue());
        } else {
            // 通知短信(0, "通知短信"), 验证码短信(4, "验证码短信"), 营销短信(5, "营销短信"), 告警短信(6,
            // "告警短信"), USSD(7, "USSD"), 闪信(8, "闪信");
            if (dbAccount.getSmstype() == 0 || dbAccount.getSmstype() == 4) {
                productTypes = Arrays.asList(ProductType.行业.getValue(), ProductType.国际.getValue(),
                        ProductType.验证码.getValue(), ProductType.通知.getValue());
            } else {
                productTypes = Arrays.asList(ProductType.营销.getValue(), ProductType.国际.getValue());
            }
        }

        if (productTypes == null || productTypes.size() == 0)
            return result;

        List<Map> remainQuantityList = orderManagerService.getOrderRemainQuantity(dbAccount);
        for (Map map : remainQuantityList) {
            // 产品类型，0：行业，1：营销，2：国际，7：USSD，8：闪信，9：挂机短信，其中0和1为普通短信，2为国际短信
            int productType = (int) map.get("productType");
            if (productType == ProductType.行业.getValue().intValue()) {
                result.put("remainHangYe", map.get("remainQuantity"));
            } else if (productType == ProductType.验证码.getValue().intValue()) {
                result.put("remainYanZhengMa", map.get("remainQuantity"));
            } else if (productType == ProductType.通知.getValue().intValue()) {
                result.put("remainTongZhi", map.get("remainQuantity"));
            } else if (productType == ProductType.营销.getValue().intValue()) {
                result.put("remainYingXiao", map.get("remainQuantity"));
            } else if (productType == ProductType.国际.getValue().intValue()) {
                result.put("remainGuoJi", map.get("remainQuantity").toString());
            } else if (productType == ProductType.USSD.getValue().intValue()) {
                result.put("remainUSSD", map.get("remainQuantity"));
            } else if (productType == ProductType.闪信.getValue().intValue()) {
                result.put("remainShanXin", map.get("remainQuantity"));
            }
        }

        boolean isHttp = dbAccount.getSmsfrom() == 6;
        if (isHttp) {
            if (result.get("remainHangYe") == null) {
                result.put("remainHangYe", 0);
            }

            if (result.get("remainYanZhengMa") == null) {
                result.put("remainYanZhengMa", 0);
            }

            if (result.get("remainTongZhi") == null) {
                result.put("remainTongZhi", 0);
            }

            if (result.get("remainYingXiao") == null) {
                result.put("remainYingXiao", 0);
            }

            if (result.get("remainUSSD") == null) {
                result.put("remainUSSD", 0);
            }

            if (result.get("remainShanXin") == null) {
                result.put("remainShanXin", 0);
            }
        } else {
            if (dbAccount.getSmstype() == 0) {
                if (result.get("remainHangYe") == null) {
                    result.put("remainHangYe", 0);
                }

                if (result.get("remainTongZhi") == null) {
                    result.put("remainTongZhi", 0);
                }

                if (result.get("remainYanZhengMa") != null)
                    result.remove("remainYanZhengMa");

                if (result.get("remainYingXiao") != null)
                    result.remove("remainYingXiao");

                if (result.get("remainUSSD") != null)
                    result.remove("remainUSSD");

                if (result.get("remainShanXin") != null)
                    result.remove("remainShanXin");

            } else if (dbAccount.getSmstype() == 4) {
                if (result.get("remainHangYe") == null) {
                    result.put("remainHangYe", 0);
                }
                if (result.get("remainYanZhengMa") == null) {
                    result.put("remainYanZhengMa", 0);
                }

                if (result.get("remainTongZhi") != null)
                    result.remove("remainTongZhi");

                if (result.get("remainYingXiao") != null)
                    result.remove("remainYingXiao");

                if (result.get("remainUSSD") != null)
                    result.remove("remainUSSD");

                if (result.get("remainShanXin") != null)
                    result.remove("remainShanXin");

            } else if (dbAccount.getSmstype() == 5) {
                if (result.get("remainYingXiao") == null) {
                    result.put("remainYingXiao", 0);
                }

                if (result.get("remainHangYe") != null)
                    result.remove("remainHangYe");

                if (result.get("remainYanZhengMa") != null)
                    result.remove("remainYanZhengMa");

                if (result.get("remainTongZhi") != null)
                    result.remove("remainTongZhi");

                if (result.get("remainUSSD") != null)
                    result.remove("remainUSSD");

                if (result.get("remainShanXin") != null)
                    result.remove("remainShanXin");
            } else {
                if (result.get("remainHangYe") != null)
                    result.remove("remainHangYe");

                if (result.get("remainYanZhengMa") != null)
                    result.remove("remainYanZhengMa");

                if (result.get("remainTongZhi") != null)
                    result.remove("remainTongZhi");

                if (result.get("remainYingXiao") != null)
                    result.remove("remainYingXiao");

                if (result.get("remainUSSD") != null)
                    result.remove("remainUSSD");

                if (result.get("remainShanXin") != null)
                    result.remove("remainShanXin");
            }
        }

        if (result.get("remainGuoJi") == null) {
            result.put("remainGuoJi", "0.0000");
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


	@RequestMapping(value = "/exportOrder")
	@ResponseBody
	public Map exportOrder(HttpServletRequest request,GetOrderParam getOrderParam, ModelAndView mv, HttpSession session) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", false);
		result.put("msg", "生成报表失败");
		SmsAccountModelPo accountModel = (SmsAccountModelPo) session.getAttribute(Constant.LOGIN_USER_INFO);
		mv.addObject("accountModel", accountModel);

		//Map<String, Object> orderParam = new HashMap<String, Object>();

//		orderParam.put("clientId",accountModel.getClientId());
		getOrderParam.setClientId(accountModel.getClientId());
		try{
			//orderParam.put("limit", "LIMIT 0 , 60000");
			List<Map<String,Object>> data = orderManagerService.exportOrder(getOrderParam);
			if(data==null||data.size()<1){
				result.put("msg", "根据条件查询到的记录数为0，导出Excel文件失败");
				return result;
			}





			StringBuffer fileName = new StringBuffer();

				fileName.append("我的订单");

			Excel excel = new Excel();
			excel.setTitle(fileName.toString());
			fileName.append(DateUtil.dateToStr(new Date(),"yyyyMMddHHmmss"));

			fileName.append(".xls");
			String filePath = ConfigUtils.temp_file_dir + "/" + accountModel.getClientId() + "/"+fileName.toString();
			excel.setFilePath(filePath);
			excel.addHeader(10, "序号", "rownum");
			excel.addHeader(20, "订单号", "order_id");
			excel.addHeader(20, "产品代码", "product_code");
			excel.addHeader(20, "产品名称", "product_name");
			excel.addHeader(20, "产品类型", "product_type_name");
			excel.addHeader(20, "运营商", "operator_codename");
			excel.addHeader(20, "区域", "area_codename");
			excel.addHeader(20, "总数量", "quantity_str");
			excel.addHeader(20, "剩余数量", "remain_quantity_str");
			excel.addHeader(20, "到期时间", "end_time_str");
			excel.addHeader(20, "订单状态", "status_name");




			excel.setShowRownum(false);

			excel.setDataList(data);
			if (ExcelUtils.exportExcel(excel)) {
				result.put("success", true);
				result.put("msg", "报表生成成功");
				result.put("fileName", fileName.toString());
				return result;
			}
		}catch(Exception e){
			logger.error("我的订单报表生成失败", e);
		}
		return result;
	}




	private String getName(Integer productType) {
		switch (productType) {
		case 0:
			return "行业";
		case 1:
			return "营销";
		case 2:
			return "国际";
		case 3:
            return "验证码";
        case 4:
            return "通知";
		case 7:
			return "USSD";
		case 8:
			return "闪信";
		default:
			return "未知";
		}
	}
}
