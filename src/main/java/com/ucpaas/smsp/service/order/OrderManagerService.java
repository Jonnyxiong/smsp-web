package com.ucpaas.smsp.service.order;

import com.ucpaas.smsp.common.entity.AjaxResult;
import com.ucpaas.smsp.model.PageBean;
import com.ucpaas.smsp.model.SmsAccountModel;
import com.ucpaas.smsp.model.param.GetOrderParam;
import com.ucpaas.smsp.model.param.PageParam;
import com.ucpaas.smsp.model.param.PlaceOrderParam;
import com.ucpaas.smsp.model.po.SmsClientOrderPo;

import java.util.List;
import java.util.Map;

public interface OrderManagerService {
	
//	PageBean getProductListByClientId(String clientId,PageParam pageParam);
	
	AjaxResult createOrder(PlaceOrderParam placeOrderParam);
	
	PageBean getOrderListInfo(GetOrderParam getOrderParam,PageParam pageParam);
	
	SmsClientOrderPo getOrderDetailBySubId(String subId);
	
	void updateOrderStatus(String status,String subId);
	
	AjaxResult cancelOrder(String subId);
	
	String getTheMostNumForMinute(String orderIdPre);

	List<Map> getOrderRemainQuantity(SmsAccountModel account);

	Map<String,Integer> groupOrderListByProductType(Map<String, Object> orderParam);

	PageBean getOrderListByProductType(Map<String, Object> orderParam, PageParam pageParam);

	PageBean listRemain(Map<String, Object> orderParam, PageParam pageParam);

	List<Integer> getProductTypes(Map<String, Object> orderParam);

	PageBean getProductListByClientId(String clientId, PageParam pageParam, List<Integer> productTypes);

	List<Map<String,Object>> exportOrder(GetOrderParam getOrderParam);

	Long getOrderId();

}
