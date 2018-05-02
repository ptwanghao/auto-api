package com.yongche.framework.utils;

import com.yongche.framework.core.ParamValue;
import com.yongche.framework.core.TestParameter;
import com.yongche.testcase.order.CreateOrderTest;
import net.sf.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class JsonUtil {
    private static final String REGULAR_JSON_ITEM_NAME = "\"([^\\\" ]+?)\":";
    public static Logger log = LoggerFactory.getLogger(JsonUtil.class);

    public static Object regexJson(String jsonStr) {
        if (jsonStr == null)
            throw new NullPointerException("JsonString shouldn't be null");

        try {
            if (isJsonObject(jsonStr)) {
                final Pattern pattern = Pattern.compile(REGULAR_JSON_ITEM_NAME);
                final Matcher matcher = pattern.matcher(jsonStr);
                final Map<String, Object> map = new HashMap<String, Object>();
                final JSONObject jsonObject = JSONObject.fromObject(jsonStr);

                for ( ; matcher.find(); ) {
                    String groupName = matcher.group(1);
                    Object obj = jsonObject.opt(groupName);
                    if (obj != null && isJsonArray(obj.toString()))
                        matcher.region(matcher.end() + obj.toString().replace("\\", "").length(), matcher.regionEnd());
                    if (obj != null && !map.containsKey(groupName))
                        map.put(groupName, regexJson(obj.toString()));

                }
                return map;
            }else if (isJsonArray(jsonStr)) {
                List<Object> list = new ArrayList<Object>();
                JSONArray jsonArray = JSONArray.fromObject(jsonStr);
                for (int i = 0; i < jsonArray.size(); i++) {
                    Object object = jsonArray.opt(i);
                    list.add(regexJson(object.toString()));
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    private static boolean isJsonObject(String jsonStr) {
        if (jsonStr == null) return false;

        try {
            //new JSONObject(jsonStr);
            JSONObject.fromObject(jsonStr);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    private static boolean isJsonArray(String jsonStr) {
        if (jsonStr == null) return false;

        try {
            JSONArray.fromObject(jsonStr);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    public static String hashMapToJson(HashMap<String, Object> map) {
        String string = "{";
        for (Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Object> entry = it.next();
            string += "'" + entry.getKey() + "':";
            string += "'" + entry.getValue() + "',";
        }
        string = string.substring(0, string.lastIndexOf(","));
        string += "}";
        return string;
    }

    public static HashMap<String,Object> JsonToHashMap(String jsonStr){
        JSONObject jsonObj = JSONObject.fromObject(jsonStr);
        HashMap<String, Object> hashMap = new HashMap<>();

        for(Object key : jsonObj.keySet()){
            hashMap.put((String)key,jsonObj.getString((String)key));
        }

        return hashMap;
    }


    //这个方法主要用于将response.get("result")所得的字符串 转换成JSONOBject
    public static JSONObject getJSONObject(String str) throws Exception {
        JSONObject jsonObject = null;

        if(null == str){
            throw new Exception("String is null");
        }

        //JSON String
        if(!str.contains("{")){
            throw new Exception(str + " : is not legal JSON String");
        }

        //检查是不是JSON数组   [ {"id":"1","name":"Ma"} , {"id":"2","name":"Yi"} ]
        //if(str.contains("[") && str.contains("]")){
        if(str.startsWith("[") && str.contains("]")){
            JSONArray jsonArray = JSONArray.fromObject(str);
            //如果是数组 只返回数组的第一个对象
            jsonObject =(JSONObject) jsonArray.get(0);
        }else {
            //如果不是数组 根据string 构造JSONObject 对象
            jsonObject = JSONObject.fromObject(str);
        }

        return jsonObject;
    }


    //{result={"time_length":1046, "37":{"最小消费金额":5,"起步价":5,"实际计费分钟":17} }
    public static String getValueFromResult(Map<String,TestParameter> paramMap, String key, String subKey){
        String jsonStr = paramMap.get(ParamValue.RESPONSE_RESULT).getValue();
        String value = null;

        try {
            JSONObject jsonObj = JsonUtil.getJSONObject(jsonStr);
            jsonObj = (JSONObject) jsonObj.get("result");
            jsonObj = (JSONObject) jsonObj.get(key);
            value = String.valueOf((jsonObj.get(subKey)));
        }catch (Exception e){
            log.error("Fail to get value for Key: " + key + " Subkey: " + subKey + " " + e);
        }

        return value;
    }
}
