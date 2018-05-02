package com.yd.testckd;

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

public class ConfigTest extends AbstractTest{

	HttpResult result = null;
	Map<String, String> mapParams = new HashMap<>();
	Map<String, String> headerParams = new HashMap<>();

	@BeforeClass
	public void setup() {
		 mapParams.put("nonce", ""+System.currentTimeMillis()/1000);
//	     headerParams.put("Authorization", "Bearer "+"aeba7c46268e2d30b78111569ca6f007636184a0");
	     headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);  //获取token
	}
	
	@Test(description="/config  安卓")
	public void configTestAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/config  安卓  ret_code="+retCode);
	 }
	
	@Test(description="/config  IOS")
	public void configTestIos(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"config  IOS  ret_code="+retCode);
	 }
	
	
	//config/price
	
	@Test(description="/config/price  安卓")
	public void configPriceAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int responCode=result.getResponseCode();
		Assert.assertTrue((responCode==200 || responCode==302),"/config/price  安卓 responCode="+responCode);
 
	 }
	
	@Test(description="/config/price  IOS")
	public void configPriceIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	    int responCode=result.getResponseCode();
	    Assert.assertTrue(responCode==302 || responCode==200,"config/price  IOS responCode="+responCode);
	 
	 }
	
	
//	config/cityOrderShort 
	@Test(description="/config/cityOrderShort  安卓")
	public void configCityOrderShortAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("----"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"config/cityOrderShort   安卓  ret_code="+retCode);
	 }
	
	@Test(description=" /config/cityOrderShort  IOS")
	public void configCityOrderShortIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"config/cityOrderShort   IOS  ret_code="+retCode);
	 }
	

	@Override
	public String testDataFileName() {
		// TODO Auto-generated method stub
		return "ConfigData";
	}
	
	
}
