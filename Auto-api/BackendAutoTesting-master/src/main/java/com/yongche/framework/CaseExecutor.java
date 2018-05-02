package com.yongche.framework;


import com.yongche.framework.core.*;
import com.yongche.framework.utils.*;
import com.yongche.framework.utils.psf.PSFUtil;
import net.sf.json.JSONObject;
import org.slf4j.*;
import org.testng.Assert;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CaseExecutor {

    public static Logger log = LoggerFactory.getLogger(CaseExecutor.class);

    public static  Map<String,TestParameter>  runTestCase(String xmlFileName){
        return runTestCase(xmlFileName,null,null);
    }

    public static  Map<String,TestParameter>   runTestCase(String xmlFileName, Map<String,TestParameter> inputParams){
        return runTestCase(xmlFileName,inputParams,null);
    }

    public static Map<String,TestParameter>  runTestCase(String xmlFileName,
                                                         Map<String,TestParameter> inputParamUpdateMap,
                                                         Map<String,TestParameter> expectResultUpdateMap){
        log.info("==================================" + xmlFileName + "==================================");

        xmlFileName = "XMLCase/"+ xmlFileName;
        TestSuite suite = XMLUtil.getTestSuiteByPath(xmlFileName);
        //Map<String,Object> response = null;
        String response = null;

        TestCase testcase = suite.getTestCase().get(0);
        log.info("Executing test case :" + testcase.getName());

        //Update request parameters according to the input parameter list.
        Map<String,TestParameter> inputParaMap = testcase.getInputParameterMap();

        //根据config.ini 处理一些参数的值
        processWithIniValue(inputParaMap);

        //根据inputParamUpdateMap 更新API的输入参数
        if(inputParamUpdateMap != null){
            for (Map.Entry<String, TestParameter> entry : inputParamUpdateMap.entrySet()){
                MapUtil.put(inputParaMap,entry.getKey(),entry.getValue().getValue());
            }
        }

        //Process with different request method: POST/GET/PSF
        switch(testcase.getMethod().toLowerCase()) {
            case "psf":
                response = PSFUtil.sendRequest(testcase.getService_type(), testcase.getService_uri(), testcase.getParameterNameValuePairList());
                break;
            case "get":
                response = HttpUtil.get(CommonUtil.getHttpUrl(suite,testcase), testcase.getParameterNameValuePairList());
                break;
            case "post":
                response = HttpUtil.post(CommonUtil.getHttpUrl(suite,testcase), testcase.getParameterNameValuePairList());
                break;
            default:
                log.error("Wrong method !!!!! : " + testcase.getMethod() );
                response = null;
                break;
        }

        Assert.assertNotNull(response,"Wrong method : " + testcase.getMethod() + "  response is null");


        //Update expected result according to input expected parameters
        Map<String,TestExpectResult> expectResultParamMap = testcase.getExpectedTestResultMap();
        if (expectResultUpdateMap != null) {
            for (Map.Entry<String, TestParameter> entry : expectResultUpdateMap.entrySet()){
                TestExpectResult testPara = new TestExpectResult(entry.getKey(), entry.getValue().getValue());
                expectResultParamMap.put(entry.getKey(), testPara);
            }
            log.info("Expected result are updated by user specified values :" + expectResultUpdateMap);
        }


        //Process variables in expect result.
        processVariable(expectResultParamMap,inputParaMap);
        log.info("Expected result : " + expectResultParamMap.values().toString());

        //Verify response
        log.info("Response : " + response.toString());
        log.info("Request Method : " + testcase.getMethod());

        AssertUtil.AssertExpectResult(expectResultParamMap, JsonUtil.JsonToHashMap(response));


        //Get value for output parameters , they will be returned and passed to next test case
        Map<String,TestParameter> outputParaMap = getOutputParameter(testcase,response);

        //Add response String to outputParaMap
        TestParameter result = new TestParameter(ParamValue.RESPONSE_RESULT,response.toString());
        outputParaMap.put(ParamValue.RESPONSE_RESULT,result);

        return outputParaMap;
    }


    public static void processWithIniValue( Map<String,TestParameter> inputParamMap){
        //行程开始时间 加5分钟
        if(null != inputParamMap.get("expect_start_latitude")) {

            long time = (new Date()).getTime()/1000 + 300;
            inputParamMap.put("expect_start_time",new TestParameter("expect_start_time",String.valueOf(time)));

            String use_config_location = ConfigUtil.getValue(ConfigConst.PARAMETER_DEFAULT_VALUE,"use_config_location").toLowerCase();

            if(use_config_location.equals("true")) {
                String expect_start_latitude = ConfigUtil.getValue(ConfigConst.PARAMETER_DEFAULT_VALUE, "expect_start_latitude");
                String expect_start_longitude = ConfigUtil.getValue(ConfigConst.PARAMETER_DEFAULT_VALUE, "expect_start_longitude");
                MapUtil.put(inputParamMap, "expect_start_latitude", expect_start_latitude);
                MapUtil.put(inputParamMap, "expect_start_longitude", expect_start_longitude);
                }
            }
    }

    public static  Map<String,TestParameter> getOutputParameter(TestCase testcase,String response){
        Map<String,TestParameter> outputParameterMap = new HashMap<>();

        if(0 == testcase.getOutputParameterList().size()){
            return outputParameterMap;
        }

        //String jsonStr = (String)response.get("result");
        JSONObject jsonObj;

        try {
            // Exception will be thrown when json string is something like this {"ret_code":200,"result":true}
            jsonObj = JsonUtil.getJSONObject(response);
            String res = jsonObj.getString("result");
            jsonObj = JsonUtil.getJSONObject(res);
        }catch (Exception e){
            log.error("Fail to convert to JSONObject for result string : " + response + " " );
            log.error(e.getMessage());
            return outputParameterMap;
        }

        for(TestParameter param : testcase.getOutputParameterList()){
            String paramName;

            if(null != param.getAlias()){
                paramName = param.getAlias();
            }else{
                paramName = param.getValue();
            }

            String value = jsonObj.get(param.getValue()).toString();
            log.info("Output parameter : " + paramName + " = " + value);

            outputParameterMap.put(paramName,new TestParameter(paramName,value));
        }

        log.info("Parameters passed to next case: " + outputParameterMap.values() );
        return outputParameterMap;
    }

    //Replace variable like $order_id$ to its value mapped in input parameter map
    public static void processVariable(Map<String,TestExpectResult> resultMap,
                                Map<String,TestParameter> inputParaMap){
        for(Map.Entry<String,TestExpectResult> entry: resultMap.entrySet()){
            TestExpectResult expectResult = entry.getValue();
            String value = expectResult.getExpectResult();

            //User Regular expression lazy match pattern
            Pattern p=Pattern.compile("\\$.*?\\$");
            Matcher m=p.matcher(value);

            while(m.find()){
                String matchedStr = m.group();
                String key = matchedStr.replace("$","");
                log.info("Found variable : " + key );

                String replaceStr = inputParaMap.get(key).getValue();
                value = value.replace(matchedStr,replaceStr);
                log.info("Replace variable -" + key + "- with value -" +replaceStr +"-");
                expectResult.setValue(value);
            }
        }
    }
}
