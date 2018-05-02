package com.yongche.framework.api.payment;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.ParamValue;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.JsonUtil;
import com.yongche.framework.utils.MapUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Account {
    public static Logger log = LoggerFactory.getLogger(Account.class);

    public static Map<String,TestParameter> getAccountAmountExecute(String accountId){
        Map<String,TestParameter> inputParaMap = new HashMap<>();
        MapUtil.put(inputParaMap,"account_id",accountId);

        return  CaseExecutor.runTestCase("payment/account_getAccountAmount.xml",inputParaMap);
    }

    //获取用户的账户余额
    public static int getAccountAmount(String accountId,String balanceType){
        Map<String,TestParameter> outputMap = getAccountAmountExecute(accountId);
        String jsonStr = outputMap.get(ParamValue.RESPONSE_RESULT).getValue();
        String value = null;

        //{"ret_code":"200","ret_msg":"success","result":{
        // "1":[{"balance_id":"2350","account_id":"75000000778","type":"1","credit_amount":"0","amount":"1545000","total_recharge_amount":"1802703","total_consume_amount":"257605","status":"1","attribute":"1","currency":"CNY","create_time":"1474515611","update_time":"1487058764","activity_id":"","expire_time":"0","total_withdraw_amount":"0"}],
        // "2":[{"balance_id":"2354","account_id":"75000000778","type":"2","credit_amount":"0","amount":"0","total_recharge_amount":"200000","total_consume_amount":"200000","status":"1","attribute":"1","currency":"CNY","create_time":"1474515611","update_time":"1480053552","activity_id":"","expire_time":"0","total_withdraw_amount":"0"}],
        // "4":[],
        // "3":[{"balance_id":"2352","account_id":"75000000778","type":"3","credit_amount":"0","amount":"1800000","total_recharge_amount":"0","total_consume_amount":"-100","status":"1","attribute":"1","currency":"CNY","create_time":"1474515611","update_time":"1482904049","activity_id":"","expire_time":"0","total_withdraw_amount":"0"}]}}
        try {
            JSONObject jsonObj = JsonUtil.getJSONObject(jsonStr);
            jsonObj = (JSONObject) jsonObj.get("result");
            JSONArray jsonary = (JSONArray) jsonObj.get(balanceType);
            jsonObj = jsonary.getJSONObject(0);
            value = jsonObj.get("amount").toString();
            log.info(value);

        }catch (Exception e){
            log.error("fail to convert json object");
        }

        return Integer.parseInt(value);
    }

    public static void addAmount(Map<String,TestParameter> inputParaMap){
        CaseExecutor.runTestCase("payment/consumer_adding.xml",inputParaMap);
    }

    public static void reduceAmount(Map<String,TestParameter> inputParaMap){
        CaseExecutor.runTestCase("payment/consumer_charging.xml",inputParaMap);
    }

    public static void findAccount(String accountId){
        Map<String,TestParameter> inputParaMap = new HashMap<>();
        MapUtil.put(inputParaMap,"account_id",accountId);
        CaseExecutor.runTestCase("payment/account_findAmount.xml",inputParaMap);
    }

    public static  void updateAccount(Map<String,TestParameter> inputParaMap){
        CaseExecutor.runTestCase("payment/account_updateAccount.xml",inputParaMap);
    }

    public static TestParameter createAccount(){
        Map<String,TestParameter> outputParaMap = CaseExecutor.runTestCase("payment/account_createAccount.xml");
        return outputParaMap.get("account_id");
    }

    public static TestParameter addBound(Map<String,TestParameter> inputParaMap){
        Map<String,TestParameter> outputParaMap = CaseExecutor.runTestCase("payment/bound_addBound.xml",inputParaMap);
        return outputParaMap.get("bound_id");
    }

    public static TestParameter unBound(Map<String,TestParameter> inputParaMap){
        Map<String,TestParameter> outputParaMap = CaseExecutor.runTestCase("payment/bound_unBound.xml",inputParaMap);
        return outputParaMap.get("bound_id");
    }

    public static Map<String,TestParameter> getBoundInfo(Map<String,TestParameter> inputParaMap){
        return CaseExecutor.runTestCase("payment/bound_getboundInfo.xml",inputParaMap);
    }

    public static Map<String,TestParameter> recharge(Map<String,TestParameter> inputParaMap){
        return CaseExecutor.runTestCase("payment/payment_recharge_withWebChat.xml",inputParaMap);
    }

    public static TestParameter getAccountAmount(String str_account_id){
        Map<String,TestParameter> inputParaMap = new HashMap<>();
        TestParameter account_id = TestParameter.getTestParameter("account_id",str_account_id);
        inputParaMap.put("account_id",account_id);
        Map<String,TestParameter> outputParaMap = CaseExecutor.runTestCase("payment/account_findAccount.xml",inputParaMap);
        return outputParaMap.get("amount");

    }

}
