package com.yongche.framework.core;


import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.collections.Lists;

public class TestCase {
	private String name;
    private String method;
    private String service_type;
    private String service_uri;
    private String module;
    private String description;

    //Use Map instead of List so that parameter object can be easily updated by Map's key value
    private Map<String,TestParameter> inputParameterMap;
    private Map<String,TestExpectResult> expectResultMap;
    private List<TestParameter> outputParameterList;

    public TestCase(){
        inputParameterMap = new HashMap<>();
        expectResultMap = new HashMap<>();
        outputParameterList = Lists.newArrayList();
    }

    public List<NameValuePair> getParameterNameValuePairList() {
        List<NameValuePair> formParameters = new ArrayList<>();

        for(Map.Entry<String,TestParameter> entry: inputParameterMap.entrySet()){
            TestParameter param = entry.getValue();
            formParameters.add(new BasicNameValuePair(param.getName(), param.getValue()));
        }

        return formParameters;
    }

    public Map<String, TestParameter> getInputParameterMap() {
        return inputParameterMap;
    }

    public Map<String, TestExpectResult> getExpectedTestResultMap() {return expectResultMap;}

    public List<TestParameter> getOutputParameterList() {return outputParameterList;}

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
    
    public String getMethod() {
        if(null == method){
            method = "psf";
        }
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getService_uri() {
        return service_uri;
    }

    public void setService_uri(String service_uri) {
        this.service_uri = service_uri;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }
    
    public void addInputParameter(TestParameter parameter) {this.inputParameterMap.put(parameter.getName(),parameter);}
    
    public void addExpectResult(TestExpectResult result) {
        this.expectResultMap.put(result.getName(),result);
    }
    
    public void addOutputParameter(TestParameter parameter) {
        this.outputParameterList.add(parameter);
    }

}
