package com.yd.testckd;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONPath;
import com.yd.test.Sign;
import com.yd.test.framework.common.AbstractTest;
import com.yd.test.framework.common.HttpResult;
import com.yd.test.framework.exception.TestHttpInvokeException;

/**
 * 
 * @author gaopx
 * /user/flighthistory(get)
 * flight/complete
 * /flight
 *
 */

public class FlightTest extends AbstractTest{
   
	HttpResult result = null;
	Map<String, String> mapParams = new HashMap<>();
	Map<String, String> headerParams = new HashMap<>();
	SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
	Calendar cal = Calendar.getInstance();
	
	@BeforeClass
	public void setup() {
		 mapParams.put("nonce", ""+System.currentTimeMillis()/1000);
//	     headerParams.put("Authorization", "Bearer "+"ee6463cf8e5fdd695ea97d4dd7317296b7624110");
	     headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);  //获取token
	     cal.setTime(new Date());   //时间在选择航班号时使用
	     cal.add(cal.DATE, 1);
	}
		
	//-----user/flighthistory(get) 航班历史 ---
	@Test(description="user/flighthistory  安卓 ")  // retCode=200或404(no data) 
	public void userFlightHisGetAnd() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue((retCode==200 || retCode==404),"user/flighthistory  安卓  retCode="+retCode);		 
	}
	
	@Test(description="user/flighthistory IOS")
	public void userFlightHisGetIOS() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue((retCode==200 || retCode==404),"user/flighthistory  retCode="+retCode);
	}
	
	
	//---flight/complete 航班搜索
	@Test(description="flight/complete 安卓 ")  //  
	public void flightComGetAnd() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"flight/complete  安卓  retCode="+retCode);		 
	}
	
	@Test(description="flight/complete IOS")
	public void flightComGetIOS() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"flight/complete  retCode="+retCode);
	}
	
	
	//---- /flight 获取航班信息
	@Test(description="/flight(get) 安卓 ")  //  
	public void flightGetAnd() {		
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("dep_date", sf.format(cal.getTime()));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("-------"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"/flight(get) 安卓  retCode="+retCode);		 
	}
	
	@Test(description="/flight(get) IOS")
	public void flightGetIOS() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("dep_date", sf.format(cal.getTime()));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"/flight(get) IOS retCode="+retCode);
	}
	
	
	
	
	
	@Override
	public String testDataFileName() {
		// TODO Auto-generated method stub
		return "FlightTest";
	}

}
