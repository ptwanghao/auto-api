package com.yd.testckd;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.yd.test.Sign;
import com.yd.test.framework.common.AbstractTest;
import com.yd.test.framework.common.HttpResult;
import com.yd.test.framework.exception.TestHttpInvokeException;

/**
 * 
 * @author gaopx 
 * order post  product 1,7,8 11,12,13
 * order/estimat 
 * /driver/dispatch
 * /order/acceptcar
 * /order/status
 * /order/cancel
 *  /user/promotions 
 *  estimate/driver
 *  user/commonword(POST)
 *   /user/commonword(get)
 *    user/commonword/delete
 *   
 */


public class Orders extends AbstractTest {

	 private static final Logger log = LoggerFactory.getLogger(UserAddress.class);
		HttpResult result=null;
		Map<String,String> mapParams = new HashMap<>();	
		Map<String,String> headerParams = new HashMap<>();
		public Random rd=new Random(); 
		
		@BeforeClass
		public void setUp(){
	        mapParams.put("nonce", ""+System.currentTimeMillis()/1000);
	        headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);
//	        headerParams.put("Authorization", "Bearer "+"f2ee763c3182a86c68b29f99a7d1b562f3632aaa");
		}
	
	public String orderPro1IdAnd="";
	public String orderPro1IdIOS="";
	public String orderPro11ID="";  //半日租orderID confirm接口使用
	
	@Test(description="order post 安卓 下单 product_type_id=1",priority=1)
	public void orderProd1Android(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		int retCode=0;
		for (int i = 0; i < 2; i++) {
			try {
				result = this.execute(new Sign(), headerParams, mapParams);
			} catch (TestHttpInvokeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");		  
			if(retCode==200){
			 orderPro1IdAnd=result.toJSONResponseData().getJSONObject("result").getJSONObject("orderinfo").getString("order_id");
			 break;
			}else if(retCode==540){
				String bidId="";
				bidId=result.toJSONResponseData().getJSONObject("result").getJSONObject("bidding").getString("bidding_id");
				mapParams.put("bidding_id", bidId);
			}
		 }
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"安卓 下单 product_type_id=1 ret_code="+retCode+" retErr="+retErr);
		Assert.assertTrue(!orderPro1IdAnd.equals(""),"order post 马上用车 error 订单号为空！"); 
	}
	
 
	@Test(description="order post  IOS 下单 product_type_id=1",priority=5)
	public void orderProd1IOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("-------"+result.toJSONResponseData());
		int responCode=result.getResponseCode();
		Assert.assertTrue(responCode==200,"order post 马上用车 responCode="+responCode);
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		
		orderPro1IdIOS=result.toJSONResponseData().getJSONObject("result").getJSONObject("orderinfo").getString("order_id");
		System.out.println("---"+orderPro1IdIOS);
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"IOS 下单 product_type_id=1 ret_code="+retCode+" retErr="+retErr);
		Assert.assertTrue(!orderPro1IdIOS.equals(""),"order post 马上用车 error 订单号为空！");
	}
	
 
	@Test(description="order post 安卓 预约下单 product_type_id=1 AND ASSAP=0 ")
	public void orderProd1Asap0And(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==540),"安卓 预约下单 product_type_id=1 ret_code="+retCode+" retErr="+retErr);
	}	
	
	@Test(description="order post  IOS 预约下单 product_type_id=1")
	public void orderProd1Asap0IOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("-------"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"IOS 预约下单 product_type_id=1 ret_code="+retCode+" retErr="+retErr);
	}
	
	@Test(description="order post 安卓 接机  product_type_id=7 ")
	public void orderProd7FlyAndroid(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"安卓接机  product_type_id=7 ret_code="+retCode+" retErr="+retErr);
	}	
	
	@Test(description="order post  IOS 接机下单 product_type_id=7")
	public void orderProd7FlyIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("-------"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg"); 
		Assert.assertTrue(retCode==200,"IOS 接机 product_type_id=7 ret_code="+retCode+" retErr="+retErr);
	}
	
	
	@Test(description="order post 安卓 送机  product_type_id=8 ")
	public void orderProd8FlyAndroid(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"安卓送机  product_type_id=8 ret_code="+retCode+" retErr="+retErr);
	}	
	
	@Test(description="order post  IOS 送机下单 product_type_id=8")
	public void orderProd8FlyIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("-------"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"IOS 送机 product_type_id=8 ret_code="+retCode+" retErr="+retErr);
	}
	
	
	@Test(description="order post 安卓 接站  product_type_id=7 ")
	public void orderProd7StaAndroid(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg"); 
		Assert.assertTrue(retCode==200,"安卓接站  product_type_id=7 ret_code="+retCode+" retErr="+retErr);
	}	
	
	@Test(description="order post  IOS 接站下单 product_type_id=7")
	public void orderProd7StaIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("-------"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"IOS 接站 product_type_id=7 ret_code="+retCode+" retErr="+retErr);
	}
	
	
	@Test(description="order post 安卓 送站  product_type_id=8 ")
	public void orderProd8StaAndroid(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"安卓送站  product_type_id=8 ret_code="+retCode+" retErr="+retErr);
	}	
	
	@Test(description="order post  IOS 送站下单 product_type_id=8")
	public void orderProd8StaIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("-------"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"IOS 送站 product_type_id=8 ret_code="+retCode+" retErr="+retErr);
	}
  
	
	@Test(description="order post 安卓半日租  product_type_id=11 " ,priority=9)
	public void orderProd11Android(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		JSONArray windArr=result.toJSONResponseData().getJSONObject("result").getJSONObject("wind").getJSONArray("order_ids");
		orderPro11ID=""+ windArr.get(0);
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==12004),"安卓半日租  product_type_id=11 ret_code="+retCode+" retErr="+retErr);
	}	
	
	//------------confirm orderConfirmAnd
	@Test(description="order/confirm  确认担保  ",priority=10)
	public void orderConfirmAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("order_id", orderPro11ID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"order/confirm  确认担保 ret_code="+retCode+" retErr="+retErr);
	}
	
	
	@Test(description="order post  IOS 半日租  product_type_id=11")
	public void orderProd11IOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		int responCode=result.getResponseCode();
		Assert.assertTrue(responCode==200,"IOS 半日租  product_type_id=11 responCode="+responCode);
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==12004),"IOS 半日租  product_type_id=11  ret_code="+retCode+" retErr="+retErr);
	}
	
	
	@Test(description="order post 安卓日租  product_type_id=12")  
	public void orderProd12And(){   //日租返回12003 半日租 12004
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==12003),"安卓日租  product_type_id=12 ret_code="+retCode+" retErr="+retErr);
	}	
	
	@Test(description="order post  IOS 日租  product_type_id=12")
	public void orderProd12IOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==12003),"IOS 日租  product_type_id=12  ret_code="+retCode+" retErr="+retErr);
	}
	
	
	@Test(description="order post 安卓热门线路  product_type_id=13")
	public void orderProd13And(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	    String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==12007),"安卓热门线路  product_type_id=13 ret_code="+retCode+" retErr="+retErr);
	}	
	
	@Test(description="order post  IOS 热门线路  product_type_id=13")
	public void orderProd13IOS(){  //热门线路 12007
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int responCode=result.getResponseCode();
		Assert.assertTrue(responCode==200,"IOS 热门线路  product_type_id=13 responCode="+responCode);
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==12007),"IOS 热门线路  product_type_id=13  ret_code="+retCode+" retErr="+retErr);
	}
	
	
	
	@Test(description="order/estimate get 安卓预约")
	public void orderEstimateAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"安卓order/estimate 预估  ret_code="+retCode+" ret_err="+retErr);
	}	
	
	@Test(description="order/estimate post  IOS 预估 ")
	public void orderEstimateIOS(){   
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("========"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200," IOS order/estimate 预估  ret_code="+retCode+" retErr="+retErr);
	}
	
	
	//---派单
	@Test(description="driver/dispatch get 安卓 派单",priority=2) //系统指派
	public void driverDisAndroid(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("order_id", orderPro1IdAnd);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"driver/dispatch get 安卓 派单  ret_code="+retCode+" retErr="+retErr);
	}	
	
	
	@Test(description="driver/dispatch get ios派单 ",priority=6)  //人工选车
	public void driverDisIOS(){   
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("order_id", orderPro1IdIOS);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("========"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"IOS driver/dispatch  ret_code="+retCode+" retErr ="+retErr);
	}
	int driverID=0;
	// 人工选车的调 /order/acceptcar IOS
	@Test(description="/order/acceptcar IOS ",priority=7)  //人工选车  接口要轮询
	public void orderAcceptCarIOS() throws InterruptedException{   
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("order_id", orderPro1IdIOS);  
		mapParams.put("driver_ids", "");   
		mapParams.put("count", "5"); 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("========"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		int i=0;
		 JSONArray carList;		
		 int waitUserTime = 0;
		 boolean acceptCAR=true;
		 while(i<5 && retCode!=498){
			 try {
					result=this.execute(new Sign(), headerParams, mapParams);			
				} catch (TestHttpInvokeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 			  
			  retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
			  if( acceptCAR){
//				int codeResult=testDriverOrder(orderID);   调用司机接单接口
//				   if(codeResult==200){
//					   acceptCAR=false;
//				   }				  
			  }
			  if(retCode==200){
				  carList= (JSONArray) JSONPath.read(result.toJSONResponseData().toString(),"$.result.car_list") ;
				  waitUserTime=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.result.wait_user_time_length");
				  if(carList.size()!=0 ){ 			    
					 JSONObject jsonCar=(JSONObject) carList.get(0);
					 driverID=(int) jsonCar.get("driver_id");
				  }					  
			  }
			 if(retCode==498 || (waitUserTime<10 && waitUserTime!=0) || driverID !=0){
				 break;
			 }
			 Thread.sleep(3000);
			 i++;			 
			 if(driverID!=0){
				 mapParams.put("driver_ids", Integer.toString(driverID)); 
			 }	
			 String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
			 Assert.assertTrue((retCode==498 || retCode==200 || retCode==540), "order/acceptcar ret_code="+retCode+" retErr="+retErr); 
		 }		
//		Assert.assertTrue(retCode==200,"IOS driver/dispatch  ret_code="+retCode);
	}
	

	//------选择司机/order/decisiondrive--
	@Test(description="/order/decisiondrive IOS",priority=8) //系统指派 订单状态 接口轮询
	public void userDecisDriIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		if(driverID==0){
			mapParams.put("driver_id", "50062234");  //线下测试司机
		}else{
			mapParams.put("driver_id", ""+driverID);
		}		
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int respCode=result.getResponseCode();		 
		Assert.assertTrue(respCode==200,"/order/decisiondrive IOS respCode="+respCode);
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toJSONString(), ".ret_msg");
	    Assert.assertTrue(retCode==200,"/order/decisiondrive IOS  ret_code="+retCode+" respCode ="+respCode+" retErr="+retErr);
	}
		
 
	@Test(description="/order/status get 安卓 订单状态",priority=3) //系统指派 订单状态 接口轮询
	public void orderStatusAnd() throws InterruptedException{
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("order_id", orderPro1IdAnd);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	
		String rate="";
		String biddingId="";
		if(retCode==540){
			 biddingId=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.result.bidding.bidding_id"); 
		     rate=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.result.bidding.rate"); 
		 }
		 int i=0;		 
		 while(i<20){	
			 try {
					result=this.execute(new Sign(), headerParams, mapParams);			
				} catch (TestHttpInvokeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				 
			 retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
			 if(retCode==540){
			  String  rate1=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.result.bidding.rate"); 
			  biddingId=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.result.bidding.bidding_id");			
			if(!rate.equals(rate1)){
				mapParams.put("bidding_id", biddingId);	
			}
			rate=rate1;
			}
			Thread.sleep(3000);
			i++;
		 }	 //520 retErr=抱歉，网络不稳定，刚才的加价倍率已经失效
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
	    Assert.assertTrue((retCode==200 || retCode==520),"order/status get 安卓 订单状态  ret_code="+retCode+" retErr="+retErr);
	}
	
 
	@Test(description="/order/cancel post 安卓 取消订单",priority=4)  
	public void orderCancelAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("order_id", orderPro1IdAnd);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int responCode=result.getResponseCode();
		Assert.assertTrue(responCode==200,"order/cancel post 取消订单  responCode="+responCode);
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"order/cancel post 取消订单  ret_code="+retCode+" retErr="+retErr);
	}
	public int promotionsID=0; 
	//------ /user/promotions  im页面获取可用优惠券
	@Test(description="/user/promotions 安卓",priority=10)  
	public void userProGetAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("order_id", orderPro1IdAnd);  //orderPro1IdAnd
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		if(retCode==200){
			if(result.toJSONResponseData().getJSONObject("result").getJSONArray("list").size()>0){
			promotionsID=result.toJSONResponseData().getJSONObject("result").getJSONArray("list").getJSONObject(0).getInteger("promotion_id");
		    }
		}
		System.out.println("-----"+promotionsID);
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
	    Assert.assertTrue(retCode==200,"/user/promotions 安卓   ret_code="+retCode+" retErr="+retErr);
	}
	
	@Test(description="/user/promotions IOS",priority=11)  
	public void userProGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("order_id", orderPro1IdAnd);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"/user/promotions IOS   ret_code="+retCode+" retErr="+retErr);
	}
	
	//---- /estimate/driver  im获取司机预估
	@Test(description="estimate/driver 安卓",priority=12)  
	public void estimateDriAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("order_id", orderPro1IdAnd);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"estimate/driver 安卓   ret_code="+retCode+" retErr="+retErr);
	}
	
	@Test(description="estimate/driver IOS",priority=13)  
	public void estimateDriIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("order_id", orderPro1IdAnd);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toJSONString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"/estimate/driver IOS   ret_code="+retCode +"retErr="+retErr);
	}
	
	
	//----添加 常用语---/user/commonword(POST)
	@Test(description="user/commonword(POST) 安卓",priority=14 )  
	public void userCommWordPostAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("word","hahahahha"+rd.nextInt(100));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	    Assert.assertTrue(retCode==200,"user/commonword(POST) 安卓   ret_code="+retCode);
	}
	
	@Test(description="user/commonword(POST) IOS" ,priority=15)  
	public void userCommWordPostIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("word","hahahahha"+rd.nextInt(100));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	//403 etErr=最多可添加10条常用语，滑动可以删除
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
	    Assert.assertTrue((retCode==200 || retCode==403),"user/commonword(POST) IOS   ret_code="+retCode+" retErr="+retErr);
	}
	
	public String wordComm="";
	//------用户常用列表  /user/commonword(get)
	@Test(description="user/commonword(get) 安卓" ,priority=16)  
	public void userCommWordGetAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("word","hahahahha"+rd.nextInt(100));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		wordComm=(String) result.toJSONResponseData().getJSONArray("result").get(0); //获取word
		System.out.println("----"+wordComm);
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	    Assert.assertTrue(retCode==200,"user/commonword(get) 安卓   ret_code="+retCode);
	}
	
	@Test(description="user/commonword(get) IOS" ,priority=17)  
	public void userCommWordGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("word","hahahahha"+rd.nextInt(100));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	    Assert.assertTrue(retCode==200,"user/commonword(get) IOS   ret_code="+retCode);
	}
	
	
	//-----删除常用语
	@Test(description="user/commonword/delete 安卓" ,priority=18)  
	public void userCommWordDeleAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("word",wordComm);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"user/commonword/delete 安卓   ret_code="+retCode+" retErr="+retErr);
	}
	
	@Test(description="user/commonword/delete IOS" ,priority=19)  
	public void userCommWordDeleIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("word",wordComm);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	    Assert.assertTrue(retCode==200,"user/commonword/delete IOS   ret_code="+retCode);
	}
	
	//----driver/location  IM获取司机位置   
	@Test(description="driver/location 安卓" ,priority=20)  
	public void drvierLocaGetAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		if(driverID==0){
		 mapParams.put("driver_id", "50062234");  //线下测试司机
	    }else{
		 mapParams.put("driver_id", ""+driverID);
		}
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	    Assert.assertTrue(retCode==200,"driver/location 安卓   ret_code="+retCode);
	}
	
	@Test(description="driver/location IOS" ,priority=21)  
	public void drvierLocaGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		if(driverID==0){
			 mapParams.put("driver_id", "50062234");  //线下测试司机
		    }else{
			 mapParams.put("driver_id", ""+driverID);
			} 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	    Assert.assertTrue(retCode==200,"driver/location IOS   ret_code="+retCode);
	}
	
	//----取消订单提示 order/canceltip
	@Test(description="order/canceltip 安卓" ,priority=23)   //22 
	public void orderCancelTipAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("order_id", orderPro1IdAnd) ;
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());		//409已取消 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==409),"order/canceltip 安卓   ret_code="+retCode+" retErr="+retErr);
	}
	
	@Test(description="order/canceltip IOS" ,priority=24)  
	public void orderCancelTipIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("order_id", orderPro1IdAnd) ;
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==409),"order/canceltipn IOS   ret_code="+retCode+" retErr="+retErr);
	}
	
	//---取消订单原因 /order/cancelreason----
	@Test(description="/order/cancelreason 安卓" ,priority=25)  
	public void orderCanReaAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("order_id", orderPro1IdAnd) ;
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	    Assert.assertTrue(retCode==200,"/order/cancelreason 安卓   ret_code="+retCode);
	}
	
	@Test(description="/order/cancelreason IOS" ,priority=26)  
	public void orderCanReaIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("order_id", orderPro1IdAnd) ;
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	    Assert.assertTrue(retCode==200,"/order/cancelreason IOS   ret_code="+retCode);
	}
	
	//-----选择优惠券----- /order/promotion------
	@Test(description="/order/promotion 选择优惠券",priority=22)
	public void orderProPostAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("order_id", orderPro1IdAnd);
		mapParams.put("promotion_id", ""+promotionsID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());	 // ret_code=476 retErr=行程状态更新中，优惠券暂不可换	 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
	    Assert.assertTrue((retCode==200 || retCode==476),"/order/promotion 选择优惠券 安卓   ret_code="+retCode+" retErr="+retErr);
	}
	
	@Test(description="/order/promotion 选择优惠券  IOS" ,priority=23)  
	public void orderProPostIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("order_id", orderPro1IdAnd);
		mapParams.put("promotion_id", ""+promotionsID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("===="+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
	    Assert.assertTrue((retCode==200 || retCode==476),"/order/promotion 选择优惠券  IOS   ret_code="+retCode+" retErr="+retErr);
	}
	
	
	@Override
	public String testDataFileName() {
		// TODO Auto-generated method stub
		return "Orders";
	}

}
