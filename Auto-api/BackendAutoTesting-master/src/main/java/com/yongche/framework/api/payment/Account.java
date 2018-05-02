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
        CaseExecutor.runTestCase("payment/account_addAmount.xml",inputParaMap);
    }

    public static void reduceAmount(Map<String,TestParameter> inputParaMap){
        CaseExecutor.runTestCase("payment/account_reduceAmount.xml",inputParaMap);
    }

    public static void findAccount(String accountId){
        Map<String,TestParameter> inputParaMap = new HashMap<>();
        MapUtil.put(inputParaMap,"account_id",accountId);
        CaseExecutor.runTestCase("payment/account_reduceAmount.xml",inputParaMap);
    }
}
