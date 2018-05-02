package com.yongche.framework.core;

import java.util.List;

import org.apache.http.NameValuePair;
import org.testng.collections.Lists;


public class TestSuite {
	private String m_name;
	private String m_host;

    private List<TestCase> testcaseList;
    
    public TestSuite()
    {
		testcaseList = Lists.newArrayList();
    }
    
    public String getName() {
        return m_name;
    }
    
    public void setName(String name) {
    	m_name = name;
    }
    
    public String getHost() {
        return m_host;
    }
    
    public void setHost(String host) {
    	m_host = host;
    }
    
    public List<TestCase> getTestCase() {
        return testcaseList;
    }

    public TestCase getTestCaseByName(String caseName){
        TestCase testcase = null;

        for(TestCase temp : testcaseList){
            if(temp.getName().equals(caseName)){
                testcase = temp;
                break;
            }
        }
        return  testcase;
    }

    public void addTestCase(TestCase testCase) {  
        this.testcaseList.add(testCase);
    }
    
	public List<NameValuePair> getRequestParamsByCaseName(String caseName)
	{
		List<NameValuePair> formparams = null;
		
		for (int i=0;i<testcaseList.size();i++) {
			if (testcaseList.get(i).getName().equals(caseName)) {
				formparams = testcaseList.get(i).getParameterNameValuePairList();
				
				break;
			}
		}
		
		return formparams;
	}

	/*
	public HashMap<String, Object> getExpectResultMapByCaseName(String caseName)
	{
		HashMap<String, Object> resultMap = null;

		for(TestCase testcase : testcaseList) {
			if (testcase.getName().equals(caseName)) {
				resultMap = testcase.getTestResultMap();
				break;
			}
		}

		return resultMap;
	}
	*/

	

}
