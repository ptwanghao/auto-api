package com.yongche.framework.core;


public class TestParameter {
	private String name;
	private String value;
	private String alias;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

    public TestParameter(){}

	public static TestParameter getTestParameter(String name, String value){
		return new TestParameter(name,value);
	}

	public TestParameter(String name, String value){
		this(name,value,"");
	}

	public TestParameter(String name, String value,String alias){
		this.name = name;
		this.value = value;
		this.alias = alias;
	}

	public String getName() {
        return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }

	public String getValue() {
        if(null == value){
            value = "";
        }
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString(){
        return name + " : " + value;
    }
}
