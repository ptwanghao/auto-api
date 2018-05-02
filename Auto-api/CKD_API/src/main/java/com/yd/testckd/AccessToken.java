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
 * 未登录的TOKEN 
 * 密码登录
 * user/captcha/voice 获取语音验证码   
 * /user/captcha/mobile 获取验证码
 * /user/captcha  图片验证码
 */

public class AccessToken  extends AbstractTest  {
	static HttpResult result = null;
	Map<String, String> mapParams = new HashMap<>();
	Map<String, String> headerParams = new HashMap<>();
	public  long testTime=System.currentTimeMillis()/1000;
	String tokenNoLog="";
	public static  String tokenLogin="";
	Random rd=new Random();
	
	@BeforeClass
	public void setup(){
		 mapParams.put("nonce", ""+testTime);	   
	     headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
	}
	
	
   @Test(description="未登录的token",priority=1)
	public void  accTokenNologin(){
		headerParams.put("Authorization", "Basic dGVzdDp0ZXN0");
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		tokenNoLog=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.result.access_token");
		Assert.assertTrue(result.getResponseCode()==200,"获取未登录的token是发生异常！ error");
	 }
   
	
	@Test(description="密码登录",priority=2)
	public void accTokenLogin(){
		  headerParams.put("Authorization", "Bearer "+tokenNoLog);
		  try {
				result=this.execute(new Sign(), headerParams, mapParams);			
			} catch (TestHttpInvokeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		  int responCode=result.getResponseCode();
		  Assert.assertTrue(responCode==200,"获取登录的access_token是发生异常！ error");
		  tokenLogin=result.toJSONResponseData().getJSONObject("result").getString("access_token");
		  Assert.assertTrue(result.getResponseCode()==200,"获取登录的access_token是发生异常！ error"); 
		  Assert.assertTrue(!tokenLogin.equals(""),"获取登录的access_token是发生异常！ error"); 	 
	}
	 
	
	
	//----user/captcha/voice ---获得语音验证码
	@Test(description="user/captcha/voice 安卓",priority=3)
	public void userCaptVoidPostAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
//		headerParams.put("Authorization", "Bearer "+"fd24978f9de149a3324eb5dc53e307403697f62a");
		headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);
		mapParams.put("cellphone", "1688313288"+rd.nextInt(9));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int responCode=result.getResponseCode();       
		Assert.assertTrue(responCode==200,"user/captcha/voice  安卓 responCode="+responCode);  
		//449 60s后再试
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue((retCode==200 || retCode==460 || retCode==421 || retCode==449),"user/captcha/voice  安卓  ret_code="+retCode);
	 }
	
	@Test(description="user/captcha/voice IOS",priority=4)
	public void userCaptVoidPostIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
//		headerParams.put("Authorization", "Bearer "+"fd24978f9de149a3324eb5dc53e307403697f62a");
		mapParams.put("cellphone", "1688313288"+rd.nextInt(9));
		headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int responCode=result.getResponseCode(); 
		Assert.assertTrue(responCode==200,"user/captcha/voice  IOS responCode="+responCode);
		// ret_code=421 ret_msg=请求太频繁，24小时后再试
		System.out.println("----"+result.toJSONResponseData());    //460 图片验证码不能为空
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
	    Assert.assertTrue((retCode==200 || retCode==460 || retCode==421 || retCode==449),"user/captcha/voice  IOS  ret_code="+retCode+" ret_msg="+retErr);
	 }
	
	//------/user/captcha/mobile 获取验证码
	@Test(description="user/captcha/mobile 安卓",priority=5)
	public void userCaptMobilAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
//		headerParams.put("Authorization", "Bearer "+"8a44cbd2fd2026d4b6e6248b58933ad178104f5f");
		headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);
		mapParams.put("cellphone", "1688313288"+rd.nextInt(9));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int responCode=result.getResponseCode(); 
		Assert.assertTrue(responCode==200,"user/captcha/mobile  安卓 responCode="+responCode);
		System.out.println("----"+result.toJSONResponseData()); 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==460),"user/captcha/mobile  安卓  ret_code="+retCode+" retErr="+retErr);
	 }
	
	@Test(description="user/captcha/mobile IOS",priority=6)
	public void userCaptMobilIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
//		headerParams.put("Authorization", "Bearer "+"8a44cbd2fd2026d4b6e6248b58933ad178104f5f");
		mapParams.put("cellphone", "1688313288"+rd.nextInt(9));
		headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("----"+result.toJSONResponseData()); 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue((retCode==200 || retCode==460),"user/captcha/mobile  IOS  ret_code="+retCode);
	 }
	
	//---- /user/captcha (get)  图片验证码
	@Test(description="user/captcha (get) 安卓",priority=5)
	public void userCaptGetAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
//		headerParams.put("Authorization", "Bearer "+"8a44cbd2fd2026d4b6e6248b58933ad178104f5f");
		headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);
		mapParams.put("cellphone", "1688313288"+rd.nextInt(9));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=result.getResponseCode();  //返回图片
	    Assert.assertTrue(retCode==200,"user/captcha (get) 安卓  ret_code="+retCode);
	 }
	
	@Test(description="user/captcha (get) IOS",priority=6)
	public void userCaptGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
//		headerParams.put("Authorization", "Bearer "+"8a44cbd2fd2026d4b6e6248b58933ad178104f5f");
		mapParams.put("cellphone", "1688313288"+rd.nextInt(9));
		headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			 
		int retCode=result.getResponseCode();	 
	    Assert.assertTrue(retCode==200,"user/captcha (get)  IOS  ret_code="+retCode);
	 }
	
	
	@Override
	public String testDataFileName() {
		// TODO Auto-generated method stub
		return "AccessToken";
	}
	
	 

}
