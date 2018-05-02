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

    /**
     * Parses the string value of TestParameter as a boolean
     * @param map
     * @param key
     * @return String值为“true”时，返回true；其他情况返回false。不区分大小写
     */
    public static Boolean getBoolean(Map<String,TestParameter> map,String key) {
        return Boolean.parseBoolean(getString(map,key));
    }
    /**
     * Parses the string value of TestParameter as a boolean
     * @param key
     * @return String值为“true”时，返回true；其他情况返回false。不区分大小写
     */
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(map,key));
    }

    public static String getString(Map<String,TestParameter> map,String key){
        TestParameter parameter = map.get(key);
        return (null != parameter) ? parameter.getValue() : null;
    }
    public String getString(String key){
        TestParameter parameter = map.get(key);
        return (null != parameter) ? parameter.getValue() : null;
    }

    public static Map<String,TestParameter> cloneMap(Map<String,TestParameter> map){
        Map<String,TestParameter> newMap = new HashMap<>();
        newMap.putAll(map);

        return newMap;
    }
}
