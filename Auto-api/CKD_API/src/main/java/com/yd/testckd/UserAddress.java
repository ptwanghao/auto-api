package com.yd.testckd;

import java.util.HashMap;

import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 *  接口如下：
 *  /user/address/recommend 
 *  user/address (get)
 *  /user/address/history
 *  /user/address(post)
 *  /user/address/update
 *  /user/address/delete
 *  
 */
public class UserAddress extends AbstractTest {
	 private static final Logger log = LoggerFactory.getLogger(UserAddress.class);
	HttpResult result=null;
	Map<String,String> mapParams = new HashMap<>();	
	Map<String,String> headerParams = new HashMap<>();
	public Random rd=new Random();
	public String userAddId="";    //user_address_id
	public String userLocaHisID="";    //user_location_history_id
	
	@BeforeClass
	public void setUp(){
		mapParams.put("nonce", ""+System.currentTimeMillis()/1000);
		headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);
//        headerParams.put("Authorization", "Bearer "+"f2ee763c3182a86c68b29f99a7d1b562f3632aaa");
	}
	
	@Test(description="/user/address/recommend 安卓上车地点推荐")
	public void recommendTestAndroid(){				
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"/user/address/recommend_anroid Error code="+retCode);
	}
	
	@Test(description="/user/address/recommend   IOS上车地点推荐")
	public void recommendTestIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);
		  } catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"/user/address/recommend_ios Error code="+retCode);
	}
	
	@Test(description="/user/address/recommend   IOS下车地点推荐")
	public void recommend2TestIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);
		  } catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"/user/address/recommend_ios Error code="+retCode);
	}
	
	//--user/address (get) 获取常用地址
	@Test(description="user/address (get) 安卓" ,priority=1)
	public void userAddreGetAnd(){				
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("---"+result.toJSONResponseData());
		userAddId= result.toJSONResponseData().getJSONArray("result").getJSONObject(0).getString("user_address_id");
		System.out.println("---"+userAddId);
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"user/address (get) 安卓  code="+retCode);
	}
	
	@Test(description="user/address (get) IOS",priority=2)
	public void userAddreGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);
		  } catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"user/address (get) IOS retCode="+retCode);
	}
	
	//---/user/address/history 获取历史 地址列表
	@Test(description="/user/address/history (get) 安卓",priority=7)
	public void userAddreHisGetAnd(){				
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userLocaHisID=result.toJSONResponseData().getJSONArray("result").getJSONObject(0).getString("user_location_history_id");
//		System.out.println("---"+userLocaHisID);
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"/user/address/history (get) 安卓  code="+retCode);
	}
	
	@Test(description="/user/address/history (get) IOS")
	public void userAddreHisGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);
		  } catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"/user/address/history IOS retCode="+retCode);
	}
	 
	
	//----/user/address(post)
	@Test(description="/user/address(post) 安卓")
	public void userAddPostAnd(){				
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("address_name", "天安门_"+rd.nextInt(100));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"user/address(post)安卓  code="+retCode);
	}
	
	@Test(description="user/address(post)IOS")
	public void userAddPostIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("address_name", "天安门_"+rd.nextInt(100));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);
		  } catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"/user/address(post) IOS retCode="+retCode);
	}
	
	//--/user/address/update 常用地址更新
	@Test(description="user/address/update  安卓" ,priority=3)
	public void userAddUpdateAnd(){				
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("address_name", "南苑机场_"+rd.nextInt(100));
		mapParams.put("user_address_id", userAddId);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==409),"user/address/update 安卓  code="+retCode+" retErr ="+retErr);
	}
	
	@Test(description="user/address/update IOS",priority=4)
	public void userAddUpdateIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("address_name", "南苑机场_"+rd.nextInt(100)); 
		mapParams.put("user_address_id", userAddId); 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);
		  } catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retMsg=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==409),"user/address/update IOS retCode="+retCode+" retMsg="+retMsg);
	}
	
	//-----/user/address/delete 删除常用地址
	@Test(description="user/address/delete  安卓" ,priority=5)
	public void userAddDeleAnd(){				
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));		 
		mapParams.put("user_address_id", userAddId);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"user/address/delete 安卓  code="+retCode);
	}
	
	@Test(description="user/address/delete IOS",priority=6)
	public void userAddDeleIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("user_address_id", userAddId);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);
		  } catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int respoCode=result.getResponseCode();
		Assert.assertTrue(respoCode==200,"user/address/delete IOS respoCode="+respoCode);
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"user/address/delete IOS retCode="+retCode);
	}
	
	
	//---/user/address/DeleteHistor 删除历史 地址
	@Test(description="user/address/DeleteHistor  安卓" ,priority=8)
	public void userAddDeleHisAnd(){				
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));		 
		mapParams.put("user_location_history_id", userLocaHisID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("---userAddDeleHisAnd----"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"user/address/DeleteHistor 安卓  code="+retCode+" retErr="+retErr);
	}
	
	@Test(description="user/address/DeleteHistor IOS",priority=9)
	public void userAddDeleHisIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("user_location_history_id", userLocaHisID);//userLocaHisID
		try {
			result=this.execute(new Sign(), headerParams, mapParams);
		  } catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("---"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue((retCode==200 || retCode==404),"user/address/DeleteHistor IOS retCode="+retCode);
	}
	
	
	
	@Override
    public String testDataFileName() {
        return "UserAddress";
    }
}
