package com.yongche.framework.utils;

import com.yongche.framework.core.TestParameter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MapUtil {
    private Map<String,TestParameter> map;

    private MapUtil(Map<String,TestParameter> map){
        this.map = map;
    }

    public static MapUtil getInstance(Map<String,TestParameter> map){
        return new MapUtil(map);
    }

    public void put(String paramName, Object value){
        TestParameter parameter = new TestParameter(paramName,String.valueOf(value));
        map.put(paramName,parameter);
    }

    public static void put(Map<String,TestParameter> map,String key,String value){
        TestParameter param = TestParameter.getTestParameter(key,value);
        map.put(key,param);
    }

    public static Map<String,TestParameter> getParameterMap(String key, String value){
        Map<String,TestParameter> parameterMap = new HashMap<>();
        MapUtil paramUtil = MapUtil.getInstance(parameterMap);
        paramUtil.put(key,value);

        return parameterMap;
    }

    public static int getInt(Map<String,TestParameter> map,String key){
        return Integer.parseInt(map.get(key).getValue());
    }

    public static double getDouble(Map<String,TestParameter> map,String key){
        return Double.parseDouble(map.get(key).getValue());
    }

    public static String getString(Map<String,TestParameter> map,String key){
        return map.get(key).getValue();
    }
    public String getString(String key){
        return map.get(key).getValue();
    }

    public static Map<String,TestParameter> cloneMap(Map<String,TestParameter> map){
        Map<String,TestParameter> newMap = new HashMap<>();
        newMap.putAll(map);

        return newMap;
    }
}
