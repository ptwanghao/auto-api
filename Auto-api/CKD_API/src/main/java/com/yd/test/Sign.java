package com.yd.test;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.yd.test.framework.common.ISign;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sign implements ISign{
	private static final Logger log = LoggerFactory.getLogger(Sign.class);
	String key="1bW4KASQPawc2G8DNBtIJmr35SzRcBCb";	
	
	@Override
	public String generatorSignString(Map<String, String> allParams) {

		 String paraString=doGetParamsStr(allParams);
		log.info("参数：{}",JSON.toJSONString(allParams));
		   paraString=encyptString(paraString)+"&key=1bW4KASQPawc2G8DNBtIJmr35SzRcBCb";
		  String signkey= md5Encypt(paraString);
		return signkey;
		
	}

	@Override
	public String signName() {
		return "sign";
	}
	
	public static String md5Encypt(String encyptString){  //md5加密
		String md5String="";
		try {
			 MessageDigest  md = MessageDigest.getInstance("MD5");
			 md.update(encyptString.getBytes("utf-8"));
			  byte b[] = md.digest();
			  StringBuffer buf = new StringBuffer("");
			 int i=0; 
			  for (int offset = 0; offset < b.length; offset++) {
				  i = b[offset];
				  if (i < 0) 
					  i += 256;
				  if (i < 16)
					  buf.append("0");
				  buf.append(Integer.toHexString(i)); 
				  }
			  md5String=buf.toString().toUpperCase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  md5String;
	}
	 
	   
	 public static String encyptString(String paraString){    //param排序 加Nonce
		 String encyptStr="";
		 if(paraString.toLowerCase().contains("nonce")){
			 encyptStr=paraString;
		 }else{
			 encyptStr=paraString+"&nonce="+((System.currentTimeMillis()/1000)+"");
		 }			   
		 encyptStr=getSortParams(encyptStr);		
		 return encyptStr;
	 }
	 
 
	 public static String getSortParams(String params) {  //param排序 
	        String result = "";
	        ArrayList<String> paramList = new ArrayList<>();	       
	            String[] paramArray = params.split("&");
	            if (paramArray.length > 0) {
	                for (int i = 0; i < paramArray.length; i++) {
	                    String param = paramArray[i];
	                    if (param.contains("=")) {
	                        String value = getElementFromArray(param.split("="), 1);	                       
	                            paramList.add(param);	                     
	                }
	                Collections.sort(paramList);
	            }

	            StringBuilder builder = new StringBuilder();
	            for (int i = 0; i < paramList.size(); i++) {
	                builder.append(paramList.get(i) + "&");
	            }
	            result = builder.toString();
	            if (paramList.size() > 0){
	                result = result.substring(0, result.length() - 1);   
	                }         
	            }
	        return result;
	    }
	 
	   public static <T> T getElementFromArray(T[] array, int index) {
	        if (isArrayEmpty(array)) {
	            return null;
	        }

	        if (index < 0 || index >= array.length) {
	            return null;
	        }

	        return array[index];
	    }
	   public static <T> boolean isArrayEmpty(T[] array) {
	        return array == null || array.length == 0;
	    }
	
	   
	   public  String doGetParamsStr(Map<String, String> params) {
			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (!"".equals(entry.getValue()) && !"null".equals(entry.getValue())) {
					sb.append(entry.getKey() + "=" + entry.getValue() + "&");
				}
			}
			return sb.substring(0, sb.length()-1);
		}
	   

}
