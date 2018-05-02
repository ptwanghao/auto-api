package com.yongche.framework.core;



public class TestExpectResult{
	private String name;
	private String value;
    private boolean compare = true;

    public TestExpectResult(){}

    public TestExpectResult(String name,String value){
        this.name  = name;
        this.value = value;
        compare = true;
    }

    public void setValue(String value){this.value = value;}

    public void setCheck(String compare){
        if(compare.toLowerCase().equals("false")){
            this.compare = false;
        }
    }

    public boolean isCompare(){return compare;}

	public String getName() {return name;}
    
    public void setName(String name) {this.name = name;}

    public String getExpectResult() {return value;}

    public String toString(){return name + ":" + value;}
}
