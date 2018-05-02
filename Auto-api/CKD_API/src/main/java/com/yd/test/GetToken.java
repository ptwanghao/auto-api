package com.yd.test;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;


/**
 * Created by yaojing on 2017-1-19
 */
public class  GetToken {
	private static final Logger log = LoggerFactory.getLogger(GetToken.class);
	public static Map<String,String> mapParams = new HashMap<>();	
	public static Map<String,String> headerParams = new HashMap<>();
	
	static {
    		mapParams.put("grant_type", "password");
    		mapParams.put("username", "13391951392");
    		mapParams.put("password", "qqqqqq");
    		mapParams.put("device_token", "DuKAyFATsjcrIG6+GtnSFwYzmnOyXgpSd+h+jPiIit0S+zaK28Ie+oR7e72qGRnOc2BYGjR+UNanXoDWv4YCfkVg");
    		mapParams.put("uuid", "861795031479572");
    		mapParams.put("macaddress", "02:00:00:00:00:00");
    		mapParams.put("imei", "861795031479572");
    		mapParams.put("vdtype", "pwd");
    		mapParams.put("brand_id", "SM-G9300");
    		mapParams.put("serialno", "898600c00116f0016156");
    		
    		headerParams.put("Authorization", "Basic YzU0NmU2YTlmYTI3N2UxMzk1NWYxMGUwYjBkZDljYjM6YmU1NDVmM2NlMzZlYThmYmI1ODYxMmQ3MmM0MjIyZGU=");   //登录接口认证用的BASIC空格 +" " 
    		headerParams.put("User-Agent", "aWeidao/7.2.0(C106;Android 6.0.1)");
    		headerParams.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            
    }
    
    

}
