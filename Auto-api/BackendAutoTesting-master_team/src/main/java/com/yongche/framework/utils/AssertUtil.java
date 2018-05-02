package com.yongche.framework.utils;


import java.util.Map;
import java.util.Map.Entry;
import com.yongche.framework.core.TestExpectResult;
import com.yongche.testcase.charge.ChargePassengerTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class AssertUtil {
    public static Logger log = LoggerFactory.getLogger(AssertUtil.class);

    public static void AssertExpectResult(Map<String, TestExpectResult> expectResultMap,
                                          Map<String, Object> actualResultMap) {
        final String RESULT_KEY = "result";

        for (Entry<String, TestExpectResult> entry : expectResultMap.entrySet()) {
            String key = entry.getKey();

            //Do verification if check=true
            if (entry.getValue().isCompare()) {

                if (!key.equals(RESULT_KEY)) {
                    String actualValue = (String) actualResultMap.get(key);
                    String expectedValue = entry.getValue().getExpectResult();
                    Assert.assertEquals(actualValue, expectedValue, actualValue + " doesn't equal with " + expectedValue);
                } else {
                    //"Result" key needs to be processed by JSON
                    String result = (String) actualResultMap.get(key);
                    Map<String, Object> actualJsonMap = JsonUtil.JsonToHashMap(result);
                    Map<String, Object> expectedJsonMap = JsonUtil.JsonToHashMap(entry.getValue().getExpectResult());
                    AssertMapEquals(expectedJsonMap, actualJsonMap);
                }
            }
        }
    }

    public static void AssertMapEquals(Map<String, Object> expected, Map<String, Object> actual) {
        for (Entry<String, Object> entry : expected.entrySet()) {
            String value = (String) actual.get(entry.getKey());
            Assert.assertEquals(value,entry.getValue());
        }
    }
    public static void assertDiff(double actual,double expected){
        int diff =2;
        assertDiff(actual,expected,diff);
    }

    public static void assertDiff(double actual,double expected, int diff){
        double priceDiff = Math.abs(actual - expected);

        //校验系统预估价格与计算所得的价格是否相等
        if( priceDiff > diff ){
            priceDiff = CommonUtil.round(priceDiff);
            log.info("预期差值: " + diff + " 实际差值: " + priceDiff);
            Assert.assertEquals(actual, expected);
        }
    }

}
    /*
    public static boolean AssertMap(Map<? extends String, ? extends Object> expectResultMap,
                                    Map<String, Object> actualResultMap) {
        boolean bEquals = false;
        Object expectValue = null;
        Object actualValue = null;

        for( Entry<? extends String, ? extends Object>  entry : expectResultMap.entrySet()) {
            String key = entry.getKey();
            actualValue = actualResultMap.get(key);

            if(actualValue != null) {
                expectValue = entry.getValue();
                //actualValue = actualResultMap.get(key);

                if(null!=expectValue && null!=actualValue){
                    if(expectValue instanceof String)
                    {
                        bEquals = expectValue.equals(actualValue);
                    }
                    else if(expectValue instanceof Map && actualValue instanceof Map)
                    {
                        @SuppressWarnings("unchecked")
                        HashMap<String, Object> expectMap = (HashMap<String, Object>)expectValue;
                        @SuppressWarnings("unchecked")
                        HashMap<String, Object> actualMap = (HashMap<String, Object>)actualValue;

                        bEquals = AssertMap(expectMap,actualMap);
                    }
                    else if(expectValue instanceof List && actualValue instanceof List)
                    {
                        @SuppressWarnings("unchecked")
                        List<Object> expectList = (List<Object>)expectValue;
                        @SuppressWarnings("unchecked")
                        List<Object> actualList = (List<Object>)actualValue;

                       // bEquals = AssertList(expectList,actualList);
                    }
                    else{
                        bEquals=false;
                    }

                    if(bEquals){
                        continue;
                    }
                    else{
                        break;
                    }
                }else if(null==expectValue && null==actualValue){
                    bEquals=true;
                    continue;
                }else{
                    bEquals=false;
                    break;
                }
            }
            else
            {
                bEquals = false;
                break;
            }
        }


        Assert.assertTrue(bEquals);

        return bEquals;
    }

    public static boolean AssertMap(Map<? extends  String, ? extends  Object> expectResultMap,
                                    Map<String, Object> actualResultMap,
                                    List<String> expectParameterList)
    {
        boolean bEquals = false;

        if(expectParameterList == null || expectParameterList.size()==0)
        {
            bEquals = AssertMap(expectResultMap,actualResultMap);
            return bEquals;
        }

        Object expectValue = null;
        Object actualValue = null;
        Iterator<Entry<String, Object>>  iterator = expectResultMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();

            if(actualResultMap.containsKey(key))
            {
                if(expectParameterList.contains(key))
                {
                    bEquals = true;
                }
                else
                {
                    expectValue = entry.getValue();
                    actualValue = actualResultMap.get(key);

                    if(null!=expectValue && null!=actualValue){
                        if(expectValue instanceof String)
                        {
                            bEquals = expectValue.equals(actualValue);
                        }
                        else if(expectValue instanceof Map && actualValue instanceof Map)
                        {
                            @SuppressWarnings("unchecked")
                            HashMap<String, Object> expectMap = (HashMap<String, Object>)expectValue;
                            @SuppressWarnings("unchecked")
                            HashMap<String, Object> actualMap = (HashMap<String, Object>)actualValue;

                            bEquals = AssertMap(expectMap,actualMap,expectParameterList);
                        }
                        else if(expectValue instanceof List && actualValue instanceof List)
                        {
                            @SuppressWarnings("unchecked")
                            List<Object> expectList = (List<Object>)expectValue;
                            @SuppressWarnings("unchecked")
                            List<Object> actualList = (List<Object>)actualValue;

                            bEquals = AssertList(expectList,actualList,expectParameterList);
                        }
                        else{
                            bEquals=false;
                        }

                        if(bEquals){
                            continue;
                        }
                        else{
                            break;
                        }
                    }else if(null==expectValue && null==actualValue){
                        bEquals=true;
                        continue;
                    }else{
                        bEquals=false;
                        break;
                    }
                }
            }
            else
            {
                bEquals = false;
                break;
            }
        }

        Assert.assertTrue(bEquals);
        return bEquals;
    }

    private static boolean AssertList(List<Object> expectList,List<Object> actualList)
    {
        boolean bEquals = false;
        for(int i = 0;i<expectList.size();i++)
        {
            boolean bContains = false;
            for(int j = 0;j<actualList.size();j++)
            {
                if(Compare(expectList.get(i),actualList.get(j)))
                {
                    bContains = true;
                    break;
                }
            }

            if(!bContains)
            {
                bEquals = false;
                break;
            }

            bEquals = true;
        }
        return bEquals;
    }

    private static boolean AssertList(List<Object> expectList,List<Object> actualList,List<String> expectParameterList)
    {
        if(expectParameterList == null || expectParameterList.size()==0)
        {
            return AssertList(expectList,actualList);
        }

        boolean bEquals = false;
        for(int i = 0;i<expectList.size();i++)
        {
            boolean bContains = false;
            for(int j = 0;j<actualList.size();j++)
            {
                if(Compare(expectList.get(i),actualList.get(j),expectParameterList))
                {
                    bContains = true;
                    break;
                }
            }

            if(!bContains)
            {
                bEquals = false;
                break;
            }

            bEquals = true;
        }
        return bEquals;
    }

    private static boolean Compare(Object expectObj,Object actualObj)
    {
        if(expectObj instanceof Map && actualObj instanceof Map)
        {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> expectMap = (HashMap<String, Object>)expectObj;
            @SuppressWarnings("unchecked")
            HashMap<String, Object> actualMap = (HashMap<String, Object>)actualObj;

            boolean bEquals = false;
            Iterator<Entry<String, Object>>  iterator =  expectMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();
                Object expect = entry.getValue();
                Object actual = actualMap.get(entry.getKey());

                if(!expect.equals(actual))
                {
                    bEquals = false;
                    break;
                }
                else
                {
                    bEquals = true;
                    continue;
                }
            }

            return bEquals;
        }
        else
        {
            return expectObj.equals(actualObj);
        }
    }

    private static boolean Compare(Object expectObj,Object actualObj,List<String> expectParameterList)
    {
        if(expectObj instanceof Map && actualObj instanceof Map)
        {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> expectMap = (HashMap<String, Object>)expectObj;
            @SuppressWarnings("unchecked")
            HashMap<String, Object> actualMap = (HashMap<String, Object>)actualObj;

            boolean bEquals = false;
            Iterator<Entry<String, Object>>  iterator =  expectMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();

                if(ContainsExpectParameter(entry.getKey(),expectParameterList))
                {
                    bEquals = true;
                    continue;
                }
                else
                {
                    Object expect = entry.getValue();
                    Object actual = actualMap.get(entry.getKey());

                    if(!expect.equals(actual))
                    {
                        bEquals = false;
                        break;
                    }
                    else
                    {
                        bEquals = true;
                        continue;
                    }
                }
            }

            return bEquals;
        }
        else
        {
            return expectObj.equals(actualObj);
        }
    }

    private static boolean ContainsExpectParameter(String content,List<String> expectParameterList)
    {
        for(int i = 0;i<expectParameterList.size();i++)
        {
            if(content.contains(expectParameterList.get(i)))
            {
                return true;
            }
        }
        return false;
    }
    */