package com.ucpaas.smsp.service.order.impl;

import com.ucpaas.smsp.common.entity.AjaxResult;
import com.ucpaas.smsp.constant.Constant;
import com.ucpaas.smsp.dao.common.SmsAccountDao;
import com.ucpaas.smsp.dao.order.SmsClientOrderDao;
import com.ucpaas.smsp.dao.order.SmsClientProductDao;
import com.ucpaas.smsp.dao.order.SmsProductInfoDao;
import com.ucpaas.smsp.model.PageBean;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.model.SmsClientOrder;
import com.ucpaas.smsp.model.param.GetOrderParam;
import com.ucpaas.smsp.model.param.PageParam;
import com.ucpaas.smsp.model.param.PlaceOrderParam;
import com.ucpaas.smsp.model.po.SmsClientOrderPo;
import com.ucpaas.smsp.model.po.SmsClientProductPo;
import com.ucpaas.smsp.model.po.SmsProductInfoPo;
import com.ucpaas.smsp.service.order.OrderManagerService;
import com.ucpaas.smsp.util.ConfigUtils;
import com.ucpaas.smsp.util.DateUtil;
import com.ucpaas.smsp.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class OrderManagerServiceImpl implements OrderManagerService {
	
	
	private Logger logger = LoggerFactory.getLogger(OrderManagerServiceImpl.class);
	
	
	@Autowired
	private SmsProductInfoDao smsProductInfoDao;
	
	@Autowired
	private SmsClientProductDao smsClientProductDao;
	
	@Autowired
	private SmsClientOrderDao smsClientOrderDao;
	
	@Autowired
	private SmsAccountDao smsAccountDao;

	public List<Map> getOrderRemainQuantity(SmsAccountModel account){

        return smsClientOrderDao.getOrderRemainQuantity(account);

    }


	@Override
	public PageBean getProductListByClientId(String clientId,PageParam pageParam, List<Integer> productTypes) {
		
		
		// 获取产品信息
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("clientId", clientId);
		sqlParams.put("productTypes", productTypes);
		sqlParams.put("startIndex", pageParam.getStartIndex());
		sqlParams.put("pageSize", pageParam.getPageSize());
		
		PageBean pageBean = null;

		
		List<SmsProductInfoPo> productInfoPoList = smsProductInfoDao.getProductListByClientId(sqlParams);
		if(productInfoPoList == null || productInfoPoList.size() ==0){
			pageBean = new PageBean(pageParam.getPageSize(),pageParam.getGoalPage(),0);
			pageBean.setList(productInfoPoList);
			return pageBean;
		}
		
		int totalRows = smsProductInfoDao.getProductListByClientIdCount(sqlParams);
		
		//如果存在产品信息
		if(totalRows != 0){
			
			//计算折扣
			List<SmsProductInfoPo> parmasList = new ArrayList<>();
			for(SmsProductInfoPo po : productInfoPoList){
				SmsProductInfoPo parmasPo = new SmsProductInfoPo();
				parmasPo.setProductId(po.getProductId());
				parmasPo.setClientid(clientId);
				parmasList.add(parmasPo);
			}
			
			//根据折扣计算产品的精确的价格
			this.calculateProductDiscount(productInfoPoList, parmasList);
			
			//转化产品销售价的单位和计算金额字段
			for(SmsProductInfoPo po : productInfoPoList){
				if(po.getProductType() != 2){ //普通短信
					po.setProductPriceStr(po.getProductPrice()+"元");
					po.setTotalPrice(po.getProductPrice()+"");
					
				}else{//国际短信
					po.setProductPriceStr(po.getProductPrice()+"");
					po.setTotalPrice(po.getQuantity()+"");
				}
			}
			
		}
		
		pageBean = new PageBean(pageParam.getPageSize(),pageParam.getGoalPage(),totalRows);
		pageBean.setList(productInfoPoList);
		return pageBean;
	}

	@Override
	public List<Map<String,Object>> exportOrder(GetOrderParam getOrderParam) {

		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("params", getOrderParam);
		sqlParams.put("startIndex",0 );
		sqlParams.put("pageSize", 60000);
		List<Map<String,Object>> orderList = smsClientOrderDao.getOrderListInfo4export(sqlParams);




		return orderList;
	}

	//计算产品的折扣
	private void calculateProductDiscount(List<SmsProductInfoPo> productInfoPoList,
			List<SmsProductInfoPo> parmasList){

		//根据用户id和产品id求出smsClientProduct信息，里面包含折扣
		Map<String,Object> sqlParams2 = new HashMap<>();
		sqlParams2.put("list", parmasList);
		List<SmsClientProductPo> smsClientProductList = smsClientProductDao.getSmsClientProductList(sqlParams2);
		
		if(smsClientProductList != null && smsClientProductList.size() != 0){
			//通过折扣，计算产品精确的价格
			for(SmsProductInfoPo productInfoPo : productInfoPoList){
				for(SmsClientProductPo clientProductPo : smsClientProductList){
					if(productInfoPo.getClientid().equals(clientProductPo.getClientId()) 
							&& productInfoPo.getProductId().intValue() == clientProductPo.getProductId().intValue() 
							&& clientProductPo.getPriceDiscount() != null){
						
						BigDecimal endPrice = productInfoPo.getProductPrice().multiply(clientProductPo.getPriceDiscount());
						endPrice = endPrice.setScale(4, BigDecimal.ROUND_HALF_UP);
						productInfoPo.setProductPrice(endPrice);
					}
				}
			}
			
		}
		
	}

	@Override
	public AjaxResult createOrder(PlaceOrderParam placeOrderParam) {
		
		AjaxResult result = new AjaxResult();
		
		String clientId = placeOrderParam.getClientid();
		Integer agentId = placeOrderParam.getAgentId();
		List<SmsProductInfoPo> productInfoPoList = placeOrderParam.getProductInfoPoList();
		for(SmsProductInfoPo po : productInfoPoList){
			po.setClientid(clientId);
		}
		
		//下单的时候再次判断产品是否已经下架
		boolean flag = this.checkForPalceOrder(productInfoPoList,agentId, result);
		if(flag == false){
			return result;
		}
		
		//根据客户id获取账户信息
//		Map<String,Object> clientIdParmas = new HashMap<>();
//		clientIdParmas.put("clientId", clientId);
//		SmsAccountModel account = smsAccountDao.getAccountInfoByClientId(clientIdParmas);
//		Integer agentId = account.getAgentId();
		
		
		
		//查询商品信息并且回填
		this.getProductInfo(productInfoPoList);
		
		//计算折扣
		List<SmsProductInfoPo> parmasList = new ArrayList<>();
		for(SmsProductInfoPo po : productInfoPoList){
			SmsProductInfoPo parmasPo = new SmsProductInfoPo();
			parmasPo.setProductId(po.getProductId());
			parmasPo.setClientid(clientId);
			parmasList.add(parmasPo);
		}
		this.calculateProductDiscount(productInfoPoList, parmasList);
		
		//把订单插入数据库
		this.insertOrder(productInfoPoList, clientId, agentId, result);
		
		return result;
	}
	
	//订单插进数据库
	private void insertOrder(List<SmsProductInfoPo> productInfoPoList,String clientId,Integer agentId,
			AjaxResult result){
		
		List<SmsClientOrder> orderList = new ArrayList<>();
		Date date = new Date();
		
		//获取订单id
		Long orderId = this.getOrderId();
		
		for(int i=0; i<productInfoPoList.size(); i++){
			
			SmsProductInfoPo po = productInfoPoList.get(i);
			
			SmsClientOrder order = new SmsClientOrder();
			
			order.setOrderId(orderId);
			order.setClientId(clientId);
			order.setAgentId(agentId);
			order.setProductId(po.getProductId());
			order.setProductType(po.getProductType());
			order.setProductName(po.getProductName());
			order.setOrderType(0);//默认是0 客户购买
			order.setStatus(0);//默认是0 待审核
			order.setActivePeriod(po.getActivePeriod());
			
			BigDecimal productNum = po.getProductNum();
			order.setProductNumber(productNum);
			order.setQuantity(productNum.multiply(po.getQuantity()));
			order.setRemainQuantity(null);
			
			//如果产品是国际产品，订单的销售价和产品定价不用乘以数量
			if(po.getProductType() == 2){
				order.setSalePrice(po.getProductPrice());
				order.setProductCost(po.getProductCost());
			}else{
				order.setSalePrice(productNum.multiply(po.getProductPrice()));
				order.setProductCost(productNum.multiply(po.getProductCost()));
			}
			order.setCreateTime(date);
			
			orderList.add(order);
		}
		
		Map<String,Object> orderListParams = new HashMap<>();
		orderListParams.put("list", orderList);
		smsClientOrderDao.insertOrderBatch(orderListParams);
		
		result.setIsSuccess(true);
		result.setMessage("下单成功！");
	}
	
	//查询商品信息，并回填
	private void getProductInfo(List<SmsProductInfoPo> productInfoPoList){
		List<String> productCodeList = new ArrayList<>();
		for(SmsProductInfoPo po : productInfoPoList){
			productCodeList.add(po.getProductCode());
		}
		
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("list", productCodeList);
		List<SmsProductInfoPo> poList = smsProductInfoDao.getProductListByProductCode(sqlParams);
		
		for(SmsProductInfoPo smsProductInfoPo : productInfoPoList){
			for(SmsProductInfoPo po : poList){
				if(smsProductInfoPo.getProductCode().equals(po.getProductCode())){
					smsProductInfoPo.setProductType(po.getProductType());
					smsProductInfoPo.setQuantity(po.getQuantity());
					smsProductInfoPo.setProductId(po.getProductId());
					smsProductInfoPo.setProductName(po.getProductName());
					smsProductInfoPo.setActivePeriod(po.getActivePeriod());
					smsProductInfoPo.setProductCost(po.getProductCost());
					smsProductInfoPo.setProductPrice(po.getProductPrice());
				}
			}
		}
	}
	
	//检查订单的下单情况
	private boolean checkForPalceOrder(List<SmsProductInfoPo> productInfoPoList,Integer agentId,AjaxResult result){
		
		boolean flag = true;
		List<String> weishangjiaList = new ArrayList<>();
		List<String> yixiajiaList = new ArrayList<>();
		
		List<String> productCodeList = new ArrayList<>();
		for(SmsProductInfoPo po : productInfoPoList){
			productCodeList.add(po.getProductCode());
		}
		
		Map<String,Object> productCodeParams = new HashMap<>();
		productCodeParams.put("list", productCodeList);
		List<SmsProductInfoPo> productInfoList = smsProductInfoDao.getProductListByProductCode(productCodeParams);
		
		//判断商品是否已经下架
		for(SmsProductInfoPo po : productInfoList){
			int productStatus = po.getStatus();
			if(productStatus == 0){
				weishangjiaList.add(po.getProductName());
				flag = false;
				
			}
			if(productStatus == 2){
				yixiajiaList.add(po.getProductName());
				flag = false;
			}
		}
		
		if(flag == false){
			result.setIsSuccess(false);
			String message1 = weishangjiaList.size() == 0?"":(weishangjiaList.toString()+"未上架，请重新下单！");
			String message2 = yixiajiaList.size() == 0?"":(yixiajiaList.toString()+"已下架，请重新下单！");
			
			if(!"".equals(message1) && !"".equals(message2)){
				result.setMessage(message1+","+message2);
			}
			if(!"".equals(message1) && "".equals(message2)){
				result.setMessage(message1);
			}
			if("".equals(message1) && !"".equals(message2)){
				result.setMessage(message2);
			}
			
			return flag;
		}
		
		//判断商品是否已经取消代理
		
		List<String> canceldailiList = new ArrayList<>();
		
		
		Map<String,Object> queryNumParams = new HashMap<>();
		queryNumParams.put("agent_id", agentId);
		
		for(SmsProductInfoPo po : productInfoList){
			Integer product_id = po.getProductId();
			queryNumParams.put("product_id", product_id);
			
			int num = smsProductInfoDao.queryCountAgentProduct(queryNumParams);
			if(num < 1){
				canceldailiList.add(po.getProductName());
				flag = false;
			}
		}
		
		if(flag == false){
			result.setIsSuccess(false);
			String msg = (canceldailiList.toString()+"已取消代理，请重新下单！");
			result.setMessage(msg);
			
			return flag;
		}
		
		return flag;
	}
	
	
	//生成订单号
	public synchronized Long getOrderId(){
		
		Date date = new Date();
		int num = 0;
		String orderIdPre = DateUtil.dateToStr(date,"yyMMdd") + DateUtil.dateToStr(date, "HHmm") + ConfigUtils.platform_order_identify; // 0 , platform_order_identify区分各个平台(客户与OEM)
		if(orderIdPre.equals(Constant.ORDERID_PRE)){
			num = Constant.ORDER_NUM;
			Constant.ORDER_NUM = num + 1;
		}else{
			
			Constant.ORDERID_PRE = orderIdPre;
			num = 1;
			Constant.ORDER_NUM = num + 1;
		}
		
		//拼成订单号
		String orderIdStr = orderIdPre + StringUtil.addZeroForNum(num, 4, "0");
		Long orderId = Long.valueOf(orderIdStr);
		
		return orderId;
	}

	@Override
	public String getTheMostNumForMinute(String orderIdPre) {
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("orderIdPre", orderIdPre);
		String numStr =  smsClientOrderDao.getMostNum(sqlParams);
		return numStr;
	}

	@Override
	public PageBean getOrderListInfo(GetOrderParam getOrderParam,PageParam pageParam) {
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("params", getOrderParam);
		sqlParams.put("startIndex", pageParam.getStartIndex());
		sqlParams.put("pageSize", pageParam.getPageSize());
		List<SmsClientOrderPo> orderList = smsClientOrderDao.getOrderListInfo(sqlParams);
		int totalRows = smsClientOrderDao.getOrderListInfoCount(sqlParams);
		
		PageBean pageBean = new PageBean(pageParam.getPageSize(),pageParam.getGoalPage(),totalRows);
		pageBean.setList(orderList);
		return pageBean;
		
//		return orderList;
	}

	@Override
	public SmsClientOrderPo getOrderDetailBySubId(String subId) {
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("subId", subId);
		return smsClientOrderDao.getOrderDetailBySubId(sqlParams);
	}

	@Override
	public void updateOrderStatus(String status,String subId) {
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("status", status);
		sqlParams.put("subId", subId);
		smsClientOrderDao.updateOrderStatus(sqlParams);
	}

	@Override
	public AjaxResult cancelOrder(String subId) {
		
		AjaxResult result = new AjaxResult();
		
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("status", "4"); //
		sqlParams.put("subId", subId);
		
		//判断订单的状态是否符合取消的条件
		
		SmsClientOrderPo po = smsClientOrderDao.getOrderDetailBySubId(sqlParams);
		int old_status = po.getStatus().intValue();
		if(old_status != 0){
			result.setMessage("取消失败，订单的状态不是待审核");
			result.setIsSuccess(false);
			return result;
		}
		
		this.smsClientOrderDao.cancelOrder(sqlParams);
		
		result.setMessage("取消成功");
		result.setIsSuccess(true);
		return result;
	}

	@Override
	public PageBean getOrderListByProductType(Map<String, Object> orderParam, PageParam pageParam) {
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("params", orderParam);
		sqlParams.put("startIndex", pageParam.getStartIndex());
		sqlParams.put("pageSize", pageParam.getPageSize());
		List<SmsClientOrderPo> orderList = smsClientOrderDao.getOrderListByProductType(sqlParams);
		int totalRows = smsClientOrderDao.getOrderListByProductTypeCount(sqlParams);
		
		PageBean pageBean = new PageBean(pageParam.getPageSize(),pageParam.getGoalPage(),totalRows);
		pageBean.setList(orderList);
		return pageBean;
	}

	@Override
	public Map<String,Integer> groupOrderListByProductType(Map<String, Object> orderParam) {
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("params", orderParam);
		sqlParams.put("startIndex",0);
		sqlParams.put("pageSize", 99999);
		List<SmsClientOrderPo> orderList = smsClientOrderDao.getOrderListByProductType(sqlParams);
		if(orderList==null)
			return null;
		Map<String,Integer> remainMap = new HashMap<>();
		for(SmsClientOrderPo po:orderList){
			String key = po.getProductType()+"-"+po.getProductTypeName();
			Integer remain = remainMap.get(key);
			if(remain==null)
				remain = 0;
			if(po.getRemainQuantity()==null)
				po.setRemainQuantity(BigDecimal.ZERO);
			remain = po.getRemainQuantity().intValue()+remain;
			remainMap.put(key, remain);
		}
		return remainMap;
	}

	@Override
	public PageBean listRemain(Map<String, Object> orderParam, PageParam pageParam) {
		Map<String,Object> sqlParams = new HashMap<>();
		sqlParams.put("params", orderParam);
		sqlParams.put("startIndex", pageParam.getStartIndex());
		sqlParams.put("pageSize", pageParam.getPageSize());
		List<Map> orderList =new ArrayList<>();
		if("2".equals(orderParam.get("productType").toString())){
			orderList = smsClientOrderDao.listRemain1(sqlParams);
		}else {
			orderList = smsClientOrderDao.listRemain(sqlParams);
		}

		if(orderList!=null){
			int rownum = pageParam.getStartIndex();
			for(Map map:orderList){
				map.put("rownum", ++rownum);
				if(map.get("product_type_name")==null){
					map.put("product_type_name", "");
				}
				if(map.get("remain_quantity")==null){
					map.put("remain_quantity", 0);
				}
				if(map.get("product_type")==null){
					map.put("product_type", "");
				}
				if(map.get("end_time_str")==null){
					map.put("end_time_str", "");
				}
				if(map.get("price")==null){
					map.put("price", "0");
				}
				
				if(map.get("product_type").toString().equals("2")){ //国际短信，价格都显示
					map.put("price", "请参考国际短信报价");
				}
			}
		}
		int totalRows = smsClientOrderDao.listRemainCount(sqlParams);
		
		PageBean pageBean = new PageBean(pageParam.getPageSize(),pageParam.getGoalPage(),totalRows);
		pageBean.setList(orderList);
		return pageBean;
	}

	@Override
	public List<Integer> getProductTypes(Map<String, Object> orderParam) {
		return smsClientOrderDao.getProductTypes(orderParam);
	}
	
	

}
