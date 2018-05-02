package com.yd.testckd;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.yd.test.Sign;
import com.yd.test.framework.common.AbstractTest;
import com.yd.test.framework.common.HttpResult;
import com.yd.test.framework.exception.TestHttpInvokeException;

/**
 * 
 * @author GAOPX
 * 接口如下：
 * order/list 
 * order get
 * order/track 
 * order/abnormal get
 * order/delete post
 * order/comment post 
 * order/arrangelist 欠款行程
 */

public class OrderAll extends AbstractTest {

	private static final Logger log = LoggerFactory.getLogger(OrderAll.class);
	HttpResult result = null;
	Map<String, String> mapParams = new HashMap<>();
	Map<String, String> headerParams = new HashMap<>();
	public String OrderIDAnd="";
	
	@BeforeClass
	public void setUp() {
		mapParams.put("nonce", "" + System.currentTimeMillis() / 1000);
		headerParams.put("Authorization", "Bearer "+new AccessToken().tokenLogin);
//		headerParams.put("Authorization", "Bearer " + "bc0aa8caf44ff1d0f2bf1f711e10db4b0d6ac311");
 
	}

	@Test(description = "order/List 安卓行程列表 " ,priority=1)
	public void orderListAndroid() {
		headerParams.put("User-Agent", this.getFromFixedConfig("UaAndroid"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("---"+result.toJSONResponseData());
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
	    Assert.assertTrue(result.getResponseCode()==200,"order/List 安卓行程列表  errorCode="+retCode);
	    JSONArray histArr=result.toJSONResponseData().getJSONObject("result").getJSONArray("history");
	    JSONObject  objArr=histArr.getJSONObject(0);
	    OrderIDAnd=objArr.getString("order_id");	   
	    Assert.assertTrue(!OrderIDAnd.equals(""),"order/List 安卓行程列表  没有行程");
	   }

	@Test(description = "order/List IOS行程列表 ",priority=2)
	public void orderListIOS() {
		headerParams.put("User-Agent", this.getFromFixedConfig("UaIOS"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int respoCode=result.getResponseCode();
		Assert.assertTrue(respoCode==200,"order/List IOS行程列表  安卓 respoCode="+respoCode);
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		Assert.assertTrue(result.getResponseCode()==200,"order/List IOS行程列表  errorCode="+retCode);
	}

 
	@Test(description = "order get  安卓订单信息 ",priority=3)
	public void orderGetAndroid() {
		mapParams.put("order_id", OrderIDAnd);
		headerParams.put("User-Agent", this.getFromFixedConfig("UaAndroid"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("-------" + result.toJSONResponseData());
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		Assert.assertTrue(result.getResponseCode()==200,"order 安卓订单信息  errorCode="+retCode);
	}
	
	@Test(description = "order get  IOS订单信息 ",priority=4)
	public void orderGetIOS() {
		mapParams.put("order_id", OrderIDAnd);
		headerParams.put("User-Agent", this.getFromFixedConfig("UaIOS"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		Assert.assertTrue(result.getResponseCode()==200,"order IOS订单信息  errorCode="+retCode);
	}
	
	 
	@Test(description = "order/track  安卓行程轨迹信息 ",priority=5)
	public void orderTrackAndroid() {
		mapParams.put("order_id", OrderIDAnd);
		headerParams.put("User-Agent", this.getFromFixedConfig("UaAndroid"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("-------" + result.toJSONResponseData());
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		Assert.assertTrue(result.getResponseCode()==200,"order 安卓行程轨迹信息  errorCode="+retCode);
	}
	
	@Test(description = "order/track  IOS行程轨迹信息 ",priority=6)
	public void orderTrackIOS() {
		mapParams.put("order_id", OrderIDAnd);
		headerParams.put("User-Agent", this.getFromFixedConfig("UaIOS"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		Assert.assertTrue(result.getResponseCode()==200,"IOS行程轨迹信息  errorCode="+retCode);
	}
	
	
	//----  /order/abnormal 订单异议
	@Test(description = "/order/abnormal 订单异议 安卓 ",priority=7)
	public void orderAbnoGetAnd() {
		mapParams.put("order_id", OrderIDAnd);
		headerParams.put("User-Agent", this.getFromFixedConfig("UaAndroid"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		Assert.assertTrue((retCode==200 || retCode==540),"/order/abnormal 订单异议 安卓 errorCode="+retCode);
	}
	
	@Test(description = "/order/abnormal 订单异议 IOS",priority=8)
	public void orderAbnoGetIOS() {
		mapParams.put("order_id", OrderIDAnd);
		headerParams.put("User-Agent", this.getFromFixedConfig("UaIOS"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		Assert.assertTrue((retCode==200 || retCode==540),"/order/abnormal 订单异议 ios  errorCode="+retCode);
	}
	
	//-----/order/delete 行程中删除订单 
	@Test(description = "/order/delete  安卓 ",priority=11)
	public void orderDeleAnd() {
		mapParams.put("order_ids", OrderIDAnd);
		headerParams.put("User-Agent", this.getFromFixedConfig("UaAndroid"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"/order/delete 安卓 errorCode="+retCode+" retErr"+retErr);
	}
	
	@Test(description = "/order/delete   IOS",priority=12)
	public void orderDeleIOS() {
		mapParams.put("order_ids", OrderIDAnd);
		headerParams.put("User-Agent", this.getFromFixedConfig("UaIOS"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue(retCode==200,"/order/delete  ios  errorCode="+retCode+" retErr"+retErr);
	}
	
	
	//--------POST  /order/comment 评价订单
	@Test(description = "/order/comment 安卓 ",priority=9)
	public void orderCommAnd() {
		mapParams.put("order_id", OrderIDAnd);
		headerParams.put("User-Agent", this.getFromFixedConfig("UaAndroid"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      //410 retErr=您还不能评价该订单
		int respoCode=result.getResponseCode();
		Assert.assertTrue(respoCode==200,"/order/comment  安卓 respoCode="+respoCode);
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==410),"/order/comment  安卓 errorCode="+retCode+" retErr="+retErr);
	}
	
	@Test(description = "order/comment IOS",priority=10)
	public void orderCommIOS() {
		mapParams.put("order_id", OrderIDAnd);
		headerParams.put("User-Agent", this.getFromFixedConfig("UaIOS"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int respoCode=result.getResponseCode();
		Assert.assertTrue(respoCode==200,"/order/comment  ios  respoCode="+respoCode);
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		String retErr=(String)JSONPath.read(result.toJSONResponseData().toString(), "$.ret_msg");
		Assert.assertTrue((retCode==200 || retCode==410 || retCode==300),"order/comment  ios  errorCode="+retCode+" retErr="+retErr);
	}
	
	
	//欠款行程---order/arrangelist
	@Test(description = "order/arrangelist 安卓 ",priority=13)
	public void orderArrAnListAnd() {		 
		headerParams.put("User-Agent", this.getFromFixedConfig("UaAndroid"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//		System.out.println("----"+result.toJSONResponseData());
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		Assert.assertTrue(retCode==200,"order/arrangelist  安卓 errorCode="+retCode);
	}
	
	@Test(description = "order/arrangelist IOS",priority=14)
	public void orderArrAnListIOS() {
		 
		headerParams.put("User-Agent", this.getFromFixedConfig("UaIOS"));
		try {
			result = this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int retCode = (int) JSONPath.read(result.toJSONResponseData().toString(), "$.ret_code");
		Assert.assertTrue(retCode==200,"order/arrangelist  ios  errorCode="+retCode);
	}
	
	
	
	@Override
	public String testDataFileName() {
		// TODO Auto-generated method stub
		return "OrderAll";
	}

}
