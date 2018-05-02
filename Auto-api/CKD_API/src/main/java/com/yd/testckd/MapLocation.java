package com.yd.testckd;

import java.util.HashMap;


import java.util.Map;

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
 *  
 * 接口如下：/map/location/search  
 *          /map/location/suggest  
 *          /map/location  
 *          /map/location/poi
 *           
 */

public class MapLocation extends AbstractTest {
	private static final Logger log = LoggerFactory.getLogger(UserAddress.class);
	HttpResult result = null;
	Map<String, String> mapParams = new HashMap<>();
	Map<String, String> headerParams = new HashMap<>();

	@BeforeClass
	public void setup() {
		 mapParams.put("nonce", ""+System.currentTimeMillis()/1000);
//	     headerParams.put("Authorization", "Bearer "+"aeba7c46268e2d30b78111569ca6f007636184a0");
	     headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);  //获取token
	}

	@Test(description="map/location/search 国内附近 安卓 ")
	public void mapLocSearNearbyAndroid() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"map/location/search 国内附近 retCode="+retCode);
		int dataSize=0;
		if(retCode==200){
			dataSize=result.toJSONResponseData().getJSONArray("result").size();
		} 
		Assert.assertTrue(dataSize!=0,"map/location/search 国内附近 error");
	}
	
	@Test(description="map/location/search 国内附近 IOS")
	public void mapLocSearNearbyIOS() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"map/location/search 国内附近 retCode="+retCode);
	}
	
	
 
	@Test(description="map/location/search 安卓国内上车地点精确搜索")
	public void mapLocSearAndroid(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"map/location/search 安卓国内上车地点精确搜索 retCode="+retCode);
	}
	
 
	@Test(description="map/location/search IOS国内上车地点精确搜索")
	public void mapLocSearIOS(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"map/location/search IOS国内上车地点精确搜索 retCode="+retCode);
	}
	
    @Test(description="map/location/search ios国外附近")
	public void mapLocSearNeaForIOS(){
    	headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			 		
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"map/location/search ios国外附近 retCode="+retCode);
	}
  
    @Test(description="map/location/search 安卓国外附近")
	public void mapLocSearNeaForAnd(){    	 
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"map/location/search 安卓国外附近  retCode="+retCode);
	}
    
    
    @Test(description="map/location/search ios国外搜索")
  	public void mapLocSearForIOS(){
      	headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
  		try {
  			result=this.execute(new Sign(), headerParams, mapParams);			 		
  		} catch (TestHttpInvokeException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  	    int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
  		Assert.assertTrue(retCode==200,"map/location/search ios国外搜索 retCode="+retCode);
  	}
    
    @Test(description="map/location/search 安卓国外搜索")
	public void mapLocSearForAdroid(){
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"map/location/search 安卓国外搜索  retCode="+retCode+" retErr"+retErr);
	}
      
    
	
	@Test(description = "map/location/suggest address_type=1 安卓上车地点模糊 搜索")
	public void mapLocSug1Android() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"map/location/sugges 安卓上车地点模糊 搜索  retCode="+retCode);
	}

	@Test(description = "map/location/suggest address_type=2 安卓下车地点模糊 搜索")
	public void mapLocSug2Android() {
		headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
		try {
			result=this.execute(new Sign(), headerParams, mapParams);			
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("----"+result.toJSONResponseData());
		int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
		Assert.assertTrue(retCode==200,"map/location/suggest 安卓下车地点模糊 搜索  retCode="+retCode);
	}

	 
	 @Test(description="mapLocSug1IOS ios国内上车地点模糊搜索")
	  	public void mapLocSug1IOS(){
	      	headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
	  		try {
	  			result=this.execute(new Sign(), headerParams, mapParams);			 		
	  		} catch (TestHttpInvokeException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  	    int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	  		Assert.assertTrue(retCode==200,"map/location/suggest ios国内上车地点模糊搜索 retCode="+retCode);
	  	}
	 
	 @Test(description="map/location/search ios国内下车地点模糊搜索")
	  	public void mapLocSug2IOS(){
	      	headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
	  		try {
	  			result=this.execute(new Sign(), headerParams, mapParams);			 		
	  		} catch (TestHttpInvokeException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  	    int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	  		Assert.assertTrue(retCode==200,"map/location/suggest  ios国内下车地点模糊搜索 retCode="+retCode);
	  	}
 
	 @Test(description = "/map/location  安卓位置")
		public void mapLocationAndroid() {
			headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
			try {
				result=this.execute(new Sign(), headerParams, mapParams);			
			} catch (TestHttpInvokeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
			Assert.assertTrue(retCode==200,"/map/location  安卓定位  retCode="+retCode);
		}
	 
	 
	 @Test(description="/map/location  IOS 位置 ")
	  	public void mapLocationIOS(){
	      	headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
	  		try {
	  			result=this.execute(new Sign(), headerParams, mapParams);			 		
	  		} catch (TestHttpInvokeException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  	    int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	  		Assert.assertTrue(retCode==200,"/map/location  IOS定位  retCode="+retCode);
	  	}
	 
	  
	 @Test(description = "/map/location/poi  安卓poi点回传")
		public void mapLocationPoiAnd() {
			headerParams.put("User-Agent",  this.getFromFixedConfig("UaAndroid"));
			try {
				result=this.execute(new Sign(), headerParams, mapParams);			
			} catch (TestHttpInvokeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			System.out.println("----"+result.toJSONResponseData());
			int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
			Assert.assertTrue(retCode==200,"/map/location/poi  安卓poi点回传  retCode="+retCode);
		}
	 
 
	 
	 @Test(description="/map/location/poi  IOS poi点回传 ")
	  	public void mapLocationPoiIOS(){
	      	headerParams.put("User-Agent",  this.getFromFixedConfig("UaIOS"));
	  		try {
	  			result=this.execute(new Sign(), headerParams, mapParams);			 		
	  		} catch (TestHttpInvokeException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  	    int retCode=(int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");	 
	  		Assert.assertTrue(retCode==200,"map/location/poi  IOS poi点回传 retCode="+retCode);
	  	}
	 
	
	@Override
	public String testDataFileName() {
		// TODO Auto-generated method stub
		return "MapLocation";
	}

}
