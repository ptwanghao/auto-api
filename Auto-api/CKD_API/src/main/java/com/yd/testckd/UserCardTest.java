package com.yd.testckd;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
 * /user/card (get)
 * /user/card/delete (POST)
 *
 */


public class UserCardTest extends   AbstractTest{

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
	
	//------/user/card(get)获取用户绑卡信息
	@Test(description="/user/card(get) 安卓")
	public void userCardGetAnd(){				
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  //200或404 (404未绑卡)
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue((retCode==200 || retCode==404),"/user/card(get) 安卓 Error code="+retCode);
	}
	
	@Test(description="/user/card(get) IOS")
	public void userCardGetIOS(){				
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue((retCode==200 || retCode==404),"/user/card(get) IOS  Error code="+retCode);
	}
		
	//-----解绑卡---- /user/card/delete
	@Test(description="/user/card/delete (POST) 安卓")
	public void userCardDeleAnd(){				
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  //200或450 (450有未完成的行程)
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue((retCode==200 || retCode==450),"/user/card/delete (POST)安卓 Error code="+retCode);
	}
	
	@Test(description="/user/card/delete (POST) IOS")
	public void userCardDeleIOS(){				
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue((retCode==200 || retCode==450),"/user/card/delete (POST) IOS  Error code="+retCode);
	}
	
	
	
	
	
	@Override
	public String testDataFileName() {
		// TODO Auto-generated method stub
		return "UserCard";
	}

}
