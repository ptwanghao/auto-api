package com.yongche.framework.api.order;

import com.yongche.framework.core.ParamValue;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.*;

/**
 * app-api
 */
public class ClientAgent {
    public static Logger log = LoggerFactory.getLogger(ClientAgent.class);

    /**
     * 调用app-api的order/decisiondriver接口，决策订单给一个司机<br>
     * @param order_id      订单id
     * @param driver_id     接单司机id
     * @return    是否决策成功
     */
    public static boolean decisionDriver(String order_id,String driver_id){
        //请求decisionDriver接口的url
        String url = "http://testing.client-agent.yongche.org/order/decisiondriver";
        //构造参数
        List<NameValuePair> formParameters = new ArrayList<>();
        formParameters.add(new BasicNameValuePair("order_id",order_id));
        formParameters.add(new BasicNameValuePair("driver_id",driver_id));
        formParameters.add(new BasicNameValuePair("nonce","af5ac125aeb80133c1838733cb8b3d96"));
        //构造header
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("User-Agent","aWeidao\\/7.3.0 (HUAWEI NXT-AL10; Android 6.0)");
        headerMap.put("ACCEPT_ENCODING","gzip");
        headerMap.put("USER_LOCATION","39.989663070869,116.316513");
        headerMap.put("AUTHORIZATION",ParamValue.ACCESS_TOKEN);  //AUTHORIZATION与userId是一一对应的，目前尚无法实时获取，暂时用user_id=26016450的，即被决策的订单下单者必须为此用户。
        headerMap.put("SIGN",generateSign(formParameters));
        headerMap.put("CONTENT_TYPE","application\\/x-www-form-urlencoded");
        headerMap.put("CONTENT_LENGTH","255");
        headerMap.put("HOST","testing.client-agent.yongche.org");
        headerMap.put("CONNECTION","Keep-Alive");

        String response = HttpUtil.post(url,formParameters,headerMap);
        //log.info(response);
        //返回结果有两层，先确定外层的错误码为200
        JSONObject obj = JSONObject.fromObject(response);
        String ret_code = obj.getString("ret_code");
        String ret_msg = obj.getString("ret_msg");
        Assert.assertEquals(ret_code,"200",ret_msg);
        //然后校验内层错误码和错误信息
        String result = obj.getString("result");
        obj =JSONObject.fromObject(result);
        ret_code = obj.getString("ret_code");
        ret_msg = obj.getString("ret_msg");
        if( (200 == Integer.parseInt(ret_code)) && ret_msg.compareTo("success")==0){
            return true;
        }
        return false;
    }

    /**
     * 调用app-api的acceptcar接口，返回手动选车订单的抢单司机列表中第一个司机的id<br>
     * @param order_id      订单id
     * @param user_id       下单用户id
     * @return    抢单司机列表中第一个司机的id
     */
    public static String getAcceptCar(String order_id,String user_id){
        //请求acceptcar接口的url
        String url = "http://testing.client-agent.yongche.org/order/acceptcar";
        //构造参数
        List<NameValuePair> formParameters = new ArrayList<>();
        formParameters.add(new BasicNameValuePair("order_id",order_id));
        formParameters.add(new BasicNameValuePair("user_id",user_id));
        formParameters.add(new BasicNameValuePair("count", Integer.toString(5)));//抢单司机列表大小，这里固定为5个
        formParameters.add(new BasicNameValuePair("nonce","af5ac125aeb80133c1838733cb8b3d96"));

        //构造header
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("AUTHORIZATION",ParamValue.ACCESS_TOKEN);
        headerMap.put("SIGN",generateSign(formParameters));
        headerMap.put("User-Agent","aWeidao\\/7.3.0 (HUAWEI NXT-AL10; Android 6.0)");
        headerMap.put("ACCEPT_ENCODING","gzip");
        headerMap.put("USER_LOCATION","39.989663070869,116.316513");
        headerMap.put("HOST","testing.client-agent.yongche.org");
        headerMap.put("CONNECTION","Keep-Alive");

        int accept_car_num=0;
        int interval = 3*1000;
        int timeout = ParamValue.GET_ACCEPT_CAR_TIMEOUT;
        int loopCount = timeout / interval;
        String driver_id = "-1";
        while(accept_car_num<1 && loopCount>0){
            loopCount --;
            String response = HttpUtil.get(url,formParameters,headerMap);
            //log.info(response);
            JSONObject obj = JSONObject.fromObject(response);
            Assert.assertEquals("200",obj.getString("ret_code"),obj.getString("ret_msg"));
            String result = obj.getString("result");
            obj = JSONObject.fromObject(result);
            JSONArray car_list = (JSONArray)obj.get("car_list");
            accept_car_num = car_list.size();
            if (accept_car_num>=1){
                driver_id = car_list.getJSONObject(0).getString("driver_id");
                break;
            }
            CommonUtil.sleep(interval);
        }
        return driver_id;
    }

    /**
     * 调用app-api的接口需要一个根据所传参数动态计算出的签名sign,原签名算法为PHP，下面是JAVA翻译后的方法<br>
     * @param    formParameters      请求参数列表<br>
     * @return   根据参数计算出的签名<br>
     */
    private static String generateSign(List<NameValuePair> formParameters){
        String sign;
        //将参数按照key的升序排序
        TreeMap<String,String> paramMap = new TreeMap<>();
        for(NameValuePair parameter:formParameters){
            paramMap.put(parameter.getName(),parameter.getValue());
        }
        Map<String, String> sortMap = new HashMap<>();
        sortMap.putAll(paramMap);

        //排序后的参数格式化为"key1=value1&key2=value2"的形式，相当于php的http_build_query方法
        String s="";
        for (Map.Entry<String,String> entry:sortMap.entrySet()){
            s = s + entry.getKey() + "=" + entry.getValue() + "&";
        }
        s = s.substring(0,s.length()-1);

        //urldecode 然后 把html实体转化为字符串
        try {
            s = URLDecoder.decode(s,"UTF-8");
            s = StringEscapeUtils.unescapeHtml4(s);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //参数中添加约定的key
        s = s + "&key=" + ParamValue.APP_API_SECRET_KEY;

        //计算以上字符串的md5值
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes("UTF-8"));    //问题主要出在这里，Java的字符串是unicode编码，不受源码文件的编码影响；而PHP的编码是和源码文件的编码一致，受源码编码影响。
            StringBuffer buf=new StringBuffer();
            for(byte b:md.digest()){
                buf.append(String.format("%02x", b&0xff));
            }
            sign = buf.toString();
        }catch( Exception e ){
            e.printStackTrace();
            return null;
        }

        //返回大写的MD5值
        return sign.toUpperCase();
    }
}
