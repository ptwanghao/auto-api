package com.yd.testckd;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

 
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.yd.test.Sign;
import com.yd.test.framework.common.AbstractTest;
import com.yd.test.framework.common.HttpResult;
import com.yd.test.framework.exception.TestHttpInvokeException;

public class OrderEstimateTest extends AbstractTest{

	HttpResult result=null;
	Map<String,String> mapParams = new HashMap<>();	
	Map<String,String> headerParams = new HashMap<>();
	public Random rd=new Random(); 
	
	@BeforeClass
	public void setUp(){
        mapParams.put("nonce", ""+System.currentTimeMillis()/1000);
        headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);
//        headerParams.put("Authorization", "Bearer "+"5631873aaa3cb144ae336338e4dd3c3935ad1054");
	}
	
	//----/order/estimate 预估
	@Test(description="/order/estimate  安卓")
	public void orderEstimAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");		
		 JSONObject jsonObj=result.toJSONResponseData().getJSONObject("result");
		 System.out.println("---"+result.toJSONResponseData());
		 Assert.assertTrue(jsonObj.containsKey("price"),"/order/estimate  ios error");
		Assert.assertTrue(retCode==200,"/order/estimate  安卓  ret_code="+retCode);
	 }
	
	
	@Test(description="/order/estimate  安卓")
	public void orderEstimIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
//		JSONArray priceArr= result.toJSONResponseData().getJSONObject("reuslt").getJSONArray("price");
	    JSONObject jsonObj=result.toJSONResponseData().getJSONObject("result");
	    System.out.println("---"+result.toJSONResponseData());
	    Assert.assertTrue(jsonObj.containsKey("price"),"/order/estimate  ios error");
		Assert.assertTrue(retCode==200,"/order/estimate  IOS  ret_code="+retCode);
	 }
	
	
	@Override
	public String testDataFileName() {
		// TODO Auto-generated method stub
		return "OrderEstimaTest";
	}

}
