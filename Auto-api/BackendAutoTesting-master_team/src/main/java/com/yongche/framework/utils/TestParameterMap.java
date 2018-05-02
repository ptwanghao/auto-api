package com.yongche.framework.utils;


import com.yongche.framework.core.TestParameter;

import java.util.*;

public class TestParameterMap {
    private Map<String, TestParameter> map;

    public TestParameterMap() {
        map = new HashMap<String, TestParameter>();
    }

    public TestParameterMap(String key, String value) {
        TestParameter parameter = new TestParameter(key, value);
        map = new HashMap<String, TestParameter>();
        map.put(key, parameter);
    }

    public void put(String key, String value) {
        map.put(key, new TestParameter(key, value));
    }

    public void put(String key, Object obj) {
        map.put(key, new TestParameter(key, String.valueOf(obj)));
    }

    public int getInt(String key) {
        return Integer.parseInt(map.get(key).getValue());
    }

    public double getDouble(String key) {
        return Double.parseDouble(map.get(key).getValue());
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(map.get(key).getValue());
    }

    public String getString(String key) {
        return (null != map.get(key)) ? map.get(key).getValue() : null;
    }

    public boolean containsKey(String key){
        return map.containsKey(key);
    }
}
