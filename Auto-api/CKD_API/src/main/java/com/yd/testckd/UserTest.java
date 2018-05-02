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
 * #接口如下： /user(get)  
 *             /user/coupon,/user/coupon/exchange,
 *             /user/collectdriver,/driver ,
 *             /driver/comment,/user/collectdriver/delete,
 *             user/collectdriver,/user/favor
 *             user/blackdriver(get),user/blackdriver(post)
 *             user/blackdriver/delete  /user(post)
 */


public class UserTest extends AbstractTest{

	
	HttpResult result = null;
	Map<String, String> mapParams = new HashMap<>();
	Map<String, String> headerParams = new HashMap<>();
	public String driverID="";
	public String blackDriID="";
	
	@BeforeClass
	public void setup() {
		 mapParams.put("nonce", ""+System.currentTimeMillis()/1000);
//	     headerParams.put("Authorization", "Bearer "+"f2ee763c3182a86c68b29f99a7d1b562f3632aaa");
		 headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);  //获取token
	}
	
	
	//user  get 用户信息
	@Test(description="/user  安卓")
	public void userGetAndroid(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user  安卓  ret_code="+retCode);
	 }

	@Test(description="/user  IOS")
	public void userGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user  IOS  ret_code="+retCode);
	 }
	
	
	//-----user/coupon get 获取优惠券
	@Test(description="/user/coupon get  安卓获取优惠券")
	public void userCouponGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user/coupon get  安卓获取优惠券  ret_code="+retCode);
	 }

	@Test(description="user/coupon get 获取优惠券 IOS")
	public void userCouponGetAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"user/coupon get  IOS  ret_code="+retCode);
	 }
	
	//----user/coupon/exchange 兑换兑换码
	@Test(description="user/coupon/exchange post  安卓兑换兑换码")
	public void userCouponExchAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		              //retCode=404 ret_msg	String	您的兑换码有误，请重新输入
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue((retCode==200 || retCode==404),"user/coupon/exchange post  安卓兑换兑换码 ret_code="+retCode);
	 }

	@Test(description="user/coupon/exchange post IOS")
	public void userCouponExchIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue((retCode==200 || retCode==404),"user/coupon/exchange post  IOS  ret_code="+retCode);
	 }
	
	//---user/collectdriver	
	@Test(description="user/collectdriver  get 安卓",priority=1)
	public void userCollGetAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("======"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		driverID=result.toJSONResponseData().getJSONObject("result").getJSONArray("list").getJSONObject(0).getString("driver_id");
		System.out.println("-----"+driverID);
		Assert.assertTrue(retCode==200,"user/collectdriver  get 安卓  ret_code="+retCode);
	 }

	
	@Test(description="user/collectdriver  get ios",priority=2)
	public void userCollGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"user/collectdriver get IOS  ret_code="+retCode);
	 }
	
	//---driver 
	@Test(description="driver  get 安卓",priority=3)
	public void driverGetAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("driver_id", driverID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"driver  get 安卓   ret_code="+retCode);
	 }	
	@Test(description="driver  get ios",priority=4)
	public void driverGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("driver_id",driverID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"driver  get ios  ret_code="+retCode);
	 }
	
	//-------driver/comment 司机评价列表
	@Test(description="driver/comment get安卓",priority=5)
	public void driCommGetAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("driver_id", driverID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"driver/comment get安卓   ret_code="+retCode);
	 }
	
	@Test(description="driver/comment get ios",priority=6)
	public void driCommGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("driver_id",driverID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"driver/comment get ios ret_code="+retCode);
	 }
	
	// ----取消收藏司机 、收藏司机
	@Test(description="/user/collectdriver/delete post安卓",priority=7)
	public void userCollDrivDeleAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("driver_id", driverID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("-----"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user/collectdriver/delete post安卓  ret_code="+retCode);
	 }
	
	@Test(description="user/collectdriver post安卓",priority=8)
	public void userCollDrivAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("driver_id", driverID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int responCode=result.getResponseCode();
		Assert.assertTrue(responCode==200,"user/collectdriver post 安卓 responCode="+responCode);
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"user/collectdriver post 安卓  ret_code="+retCode);
	 }
	
	@Test(description="/user/collectdriver/delete  ios",priority=9)
	public void userCollDrivDeleIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("driver_id",driverID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user/collectdriver/delete  iosret_code="+retCode);
	 }
	
	@Test(description="user/collectdriver post  ios",priority=10)
	public void userCollDrivIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("driver_id",driverID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"user/collectdriver post iosret_code="+retCode);
	 }
	
	//用车喜好 /user/favor 
	@Test(description="/user/favor  get安卓",priority=11)
	public void userFavorGetAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));		 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user/favor  get  安卓  ret_code="+retCode);
	 }
	
	@Test(description="/user/favor  post安卓",priority=12)
	public void userFavorPostAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));		 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user/favor pot  安卓  ret_code="+retCode);
	 } 
	 
	@Test(description="/user/favor  get IOS",priority=11)
	public void userFavorGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));		 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user/favor get IOS  ret_code="+retCode);
	 }
	
	@Test(description="/user/favor  post Ios",priority=12)
	public void userFavorPostIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));		 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user/favor pot IOS  ret_code="+retCode);
	 }
	
	//--- /user/blackdriver 黑名单列表 -拉黑司机 
	@Test(description="/user/blackdriver GET 安卓",priority=13)
	public void userBackDriGetAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));		 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		if(result.toJSONResponseData().getJSONObject("result").getJSONArray("list").size()>0)
		 blackDriID=result.toJSONResponseData().getJSONObject("result").getJSONArray("list").getJSONObject(0).getString("driver_id");
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"/user/blackdriver GET 安卓  ret_code="+retCode+" retErr"+retErr);
	 } 
	 
	@Test(description="/user/blackdriver GET IOS",priority=14)
	public void userBackDriGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));		 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user/blackdriver get IOS  ret_code="+retCode);
	 }
	
	//---取消拉黑司机
	@Test(description="user/blackdriver/delete安卓",priority=15)
	public void userBackDriDelAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		mapParams.put("driver_id", blackDriID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
	    Assert.assertTrue(retCode==200,"user/blackdriver/delete 安卓  ret_code="+retCode+" retErr"+retErr);
	 } 
	
	@Test(description="user/blackdriver/delete IOS",priority=17)
	public void userBackDriDelIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("driver_id", blackDriID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"user/blackdriver/delete IOS  ret_code="+retCode+" retErr"+retErr);
	 } 
	
	
	//--拉黑司机- /user/blackdriver
	@Test(description="user/blackdriver(post) 安卓",priority=16)
	public void userBackDriPostAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
//		String driverArr[]={"50062632","50062631","50062630","50062629","50062628","50062627","50062626","50062625","50062624"};
		mapParams.put("driver_id", blackDriID);
//		for(int i=0;i<driverArr.length;i++){
//			mapParams.put("driver_id", driverArr[i]);	 		 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//		} 
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
	    Assert.assertTrue(retCode==200,"user/blackdriver(post)安卓  ret_code="+retCode+" retErr"+retErr);
	 } 
	
	
	@Test(description="user/blackdriver(post) IOS",priority=18)
	public void userBackDriPostIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		mapParams.put("driver_id", blackDriID);
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"user/blackdriver(post) IOS  ret_code="+retCode+" retErr"+retErr);
	 }
	
	//------/user(post) 修改用户信息
	@Test(description="/user(post)安卓")
	public void userPostAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));		 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user(post)安卓  ret_code="+retCode);
	 } 
	
	@Test(description="/user(post)IOS")
	public void userPostIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));		 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user(post)IOS   ret_code="+retCode);
	 }
	
	//----用户标签 /user/tags(get) 
	@Test(description="/user/tags(get)安卓")
	public void userTagsGetAnd(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));		 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user/tags(get)安卓  ret_code="+retCode);
	 } 
	
	@Test(description="/user/tags(get)")
	public void userTagsGetIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));		 
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(retCode==200,"/user/tags(get) IOS   ret_code="+retCode);
	 }
	
	  
	
	
	
	
	
	
	@Override
	public String testDataFileName() {
		// TODO Auto-generated method stub
		return "UserTest";
	}
}
