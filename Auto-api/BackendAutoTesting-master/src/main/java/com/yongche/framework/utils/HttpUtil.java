package com.yongche.framework.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    public static Logger log = LoggerFactory.getLogger(HttpUtil.class);


    public static String get(String url, List<NameValuePair> params) {
        log.info("GET URL is : " + url);
        CloseableHttpClient httpClient =null;
        CloseableHttpResponse response = null;
        Map<String, Object> value = null;
        String jsonStr = null;

        try {
            //httpClient = HttpClients.createDefault();
            /*
            * HttpClient4.5.2请求时默认的Cookie策略是CookieSpecs.BEST_MATCH，当程序中无需传递cookie值时会出现“Cookie rejected”的警告信息；
            * 所以注释掉35行，添加39、40行，修改cookie策略为忽略cookie传递检查；
            */
            RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
            httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
            HttpGet httpGet = new HttpGet(url+"?"+ URLEncodedUtils.format(params, "UTF-8"));
            log.info("HTTP Request : " + httpGet);
            //httpGet.setHeader("Authorization:"," Bearer 8cc626741fd5c8aa2a617d80daeaba73fc462436");
            response = httpClient.execute(httpGet);

            HttpEntity httpEntity = response.getEntity();
            jsonStr = EntityUtils.toString(httpEntity);
            log.info("GET response entity:" + jsonStr);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonStr;
    }

    public static String get(String url, List<NameValuePair> params, Map<String,String> headers) {
        CloseableHttpClient httpClient =null;
        CloseableHttpResponse response = null;
        String jsonStr = null;
        try {
            RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
            httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
            HttpGet httpGet = new HttpGet(url+"?"+ URLEncodedUtils.format(params, "UTF-8"));
            log.info("HTTP Request : " + httpGet);
            //添加header
            for(Map.Entry<String,String> entry:headers.entrySet()){
                httpGet.setHeader(entry.getKey(),entry.getValue());
            }
            response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            jsonStr = EntityUtils.toString(httpEntity);
            log.info("GET response entity:" + jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonStr;
    }

    public static String post(String url,List<NameValuePair> params) {
        log.info("POST URL is : " + url);

        CloseableHttpClient httpClient =null;
        CloseableHttpResponse response = null;
        Map<String, Object> value = null;

        String jsonStr=null;

        try {
            httpClient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(url);

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);
            log.info("Post entity: " + EntityUtils.toString(entity));

            response = httpClient.execute(httpPost);

            HttpEntity httpEntity = response.getEntity();

            jsonStr = EntityUtils.toString(httpEntity,"UTF-8");
            log.info("Post response entity : " + jsonStr);


            //Delete debug message
            /*
            //value = (Map<String,Object>) JsonUtil.regexJson(jsonStr);
            value = (Map<String,Object>) JsonUtil.JsonToHashMap(jsonStr);
            Iterator<Map.Entry<String, Object>> it = value.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                String mapKey = entry.getKey().toString();
                if (mapKey.toLowerCase().contains("debug")) {
                    it.remove();
                }
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //return value;
        return jsonStr;
    }

    public static String post(String url,List<NameValuePair> params, Map<String,String> headers) {
        CloseableHttpClient httpClient =null;
        CloseableHttpResponse response = null;
        String jsonStr=null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            //添加header
            for(Map.Entry<String,String> entry:headers.entrySet()){
                httpPost.setHeader(entry.getKey(),entry.getValue());
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);
            log.info("Post entity: " + EntityUtils.toString(entity));
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            jsonStr = EntityUtils.toString(httpEntity,"UTF-8");
            log.info("Post response entity : " + jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonStr;
    }
}