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
 * /user/passenger
 *
 */

public class UserPassengerTest extends AbstractTest {

	HttpResult result=null;
	Map<String,String> mapParams = new HashMap<>();	
	Map<String,String> headerParams = new HashMap<>();
	public String userPassID="";

	@BeforeClass
	public void setUp(){
		mapParams.put("nonce", ""+System.currentTimeMillis()/1000);
		headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);
//        headerParams.put("Authorization", "Bearer "+"ee6463cf8e5fdd695ea97d4dd7317296b7624110");
	}
	
	//-----/user/passenger 乘车人列表
	@Test(description="user/passenger  安卓 ",priority=1)   
	public void userPassGetAnd() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		int userPassSize=result.toJSONResponseData().getJSONArray("result").size();
		if(userPassSize>1){
		userPassID=result.toJSONResponseData().getJSONArray("result").
				                         getJSONObject(0).getString("user_passenger_history_id");
		}else{
			userPassID="1111111";
		}			 
		Assert.assertTrue(retCode==200,"user/passenger  安卓  retCode="+retCode);		 
	}
	
	@Test(description="user/passenger IOS",priority=2)
	public void userPassGetIOS() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200," user/passenger  retCode="+retCode);
	}
	 
	//-------/user/passenger/delete 删除历史 乘车人
	@Test(description="/user/passenger/delete  安卓 ",priority=3)   
	public void userPassDeleAnd() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("user_passenger_id", userPassID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		Assert.assertTrue(retCode==200,"user/passenger/delete  安卓  retCode="+retCode);		 
	}
	
	@Test(description="user/passenger/delete IOS",priority=4)
	public void userPassDeleIOS() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("user_passenger_id", userPassID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("----"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200," user/passenger/delete retCode="+retCode);
	}
	
	
	
	
	@Override
	public String testDataFileName() {
		// TODO Auto-generated method stub
		return "UserPassenger";
	}

}
