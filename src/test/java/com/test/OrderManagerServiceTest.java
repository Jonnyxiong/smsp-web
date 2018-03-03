package com.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ucpaas.smsp.model.SmsClientOrder;
import com.ucpaas.smsp.model.param.GetOrderParam;
import com.ucpaas.smsp.model.param.PlaceOrderParam;
import com.ucpaas.smsp.model.po.SmsProductInfoPo;
import com.ucpaas.smsp.service.order.OrderManagerService;

public class OrderManagerServiceTest extends SupperTest{
	

	@Autowired
	private OrderManagerService orderManagerService;
	
	
	@Test
	public void getProductListByClientIdTest(){
//		String clientId = "hzz";
//		List<SmsProductInfoPo> productInfoList = 
//				orderManagerService.getProductListByClientId(clientId);
//		
//		
//		System.out.println("----------"+productInfoList);
	}
	
	@Test
	public void createOrderTest(){
		
		PlaceOrderParam placeOrderParam = new PlaceOrderParam();
		placeOrderParam.setClientid("hzz");
		
		List<SmsProductInfoPo> productInfoPoList = new ArrayList<>();
		SmsProductInfoPo p1 = new SmsProductInfoPo();
		p1.setProductId(1);
		p1.setProductCode("c1");
		p1.setProductName("产品1");
		p1.setProductPrice(new BigDecimal("155"));
		p1.setProductType(0);
		p1.setProductCost(new BigDecimal("100"));
		p1.setQuantity(new BigDecimal("100"));
		p1.setActivePeriod(0);
		p1.setProductNum(new BigDecimal("20"));
		
		SmsProductInfoPo p2 = new SmsProductInfoPo();
		p2.setProductId(1);
		p2.setProductCode("c2");
		p2.setProductName("产品1");
		p2.setProductPrice(new BigDecimal("255"));
		p2.setProductType(0);
		p2.setProductCost(new BigDecimal("200"));
		p2.setQuantity(new BigDecimal("200"));
		p2.setActivePeriod(0);
		p2.setProductNum(new BigDecimal("20"));
		
		productInfoPoList.add(p1);
		productInfoPoList.add(p2);
		
		placeOrderParam.setProductInfoPoList(productInfoPoList);
		
		orderManagerService.createOrder(placeOrderParam);
		
		System.out.println("-------------->");
		
	}
	
	@Test
	public void getOrderListInfoTest(){
		
		GetOrderParam getOrderParam = new GetOrderParam();
		getOrderParam.setClientId("hzz");
		getOrderParam.setOrderCode_productName("产品1");
		getOrderParam.setStatus("0");
//		getOrderParam.setStartTime("201608");
//		getOrderParam.setEndTime("201609");
		
//		List<SmsClientOrder> orderList = orderManagerService.getOrderListInfo(getOrderParam);
//		for(SmsClientOrder order : orderList){
//			System.out.println("----->"+order.toString());
//		}
		
	}
	
	
	
	
	
	
	
	
	

}
