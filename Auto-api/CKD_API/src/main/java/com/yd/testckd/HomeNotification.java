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

/**
 * 
 * @author gaopx
 * home/notification
 * 
 *
 */

public class HomeNotification  extends AbstractTest {

	HttpResult result = null;
	Map<String, String> mapParams = new HashMap<>();
	Map<String, String> headerParams = new HashMap<>();
	
	@BeforeClass
	public void setup() {
		 mapParams.put("nonce", ""+System.currentTimeMillis()/1000);
//	     headerParams.put("Authorization", "Bearer "+"fd24978f9de149a3324eb5dc53e307403697f62a");
	     headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);  //获取token
	}
	
	//----home/notification  腰线行程 -------
	@Test(description="home/notification  安卓 ")    
	public void homeNotifAnd() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"home/notification  安卓  retCode="+retCode);		 
	}
	
	@Test(description="home/notification IOS")
	public void homeNotifIOS() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"home/notification retCode="+retCode);
	}
	
	//----小礼盒  home/discovery----
	@Test(description="home/discovery  安卓 ")    
	public void homeDiscoveryAnd() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"home/discovery 安卓  retCode="+retCode);		 
	}
	
	@Test(description="home/discovery IOS")
	public void homeDiscoveryIOS() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"home/discovery retCode="+retCode);
	}
	
	@Override
	public String testDataFileName() {
		// TODO Auto-generated method stub
		return "HomeNotification";
	}

}
