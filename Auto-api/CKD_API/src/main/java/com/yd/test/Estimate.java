package com.yd.test;

import com.alibaba.fastjson.JSONObject;
import com.yd.test.framework.common.AbstractTest;
import com.yd.test.framework.common.HttpResult;
import com.yd.test.framework.exception.TestHttpInvokeException;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;


/**t
 * Created by yaojing on 2017/1/19.
 */
public class Estimate extends AbstractTest {
	
	public String time;
	public String accessToken;

	
	@BeforeClass(alwaysRun = true) 
    public void setUp() throws Exception {
//        log.info("setup....");
    	time=(System.currentTimeMillis()/1000)+"";
    	HttpResult result=this.executeWithDataFile(null, GetToken.headerParams, GetToken.mapParams, "getToken", "getToken");
    	//HttpResult result=this.executeWithDepenency(null, GetToken.headerParams, GetToken.mapParams, "getToken", "getToken");
//    	log.info("==="+result.getResponseData());
    	accessToken = result.toJSONResponseData().getString("access_token");
    	System.out.println("accessToken:==========+++++++++"+accessToken);
    }

    @Test
    public void testEstimate(){

        HttpResult result=null;
		Map<String,String> mapParams = new HashMap<>();	
		Map<String,String> headerParams = new HashMap<>();
   	
		mapParams.put("start_time", time);
		mapParams.put("nonce", time);

		headerParams.put("Authorization", "Bearer "+accessToken);   		    		
		try {
			result=this.execute(new Sign(), headerParams, mapParams);
		} catch (TestHttpInvokeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("=====++++++++"+result.toString());

		JSONObject jsonObj=result.toJSONResponseData();
		       
		Assert.assertEquals(jsonObj.getString("ret_code").toString(), "200","/order/estimate 获取价格预估接口："+result.toJSONResponseData());         
       //  Assert.assertTrue(jsonObj.getInteger("ret_code")==201,"/order/estimate 获取价格预估接口："+result.toJSONResponseData());//   Assert,.assertEquals(jsonObj.getInteger("ret_code"), 200,"/order/estimate 获取价格预估接口："+rs.toString());
    }
    
    
  
    @Override
    public String testDataFileName() {
        return "estimate";
    }
}
