package com.yongche.framework.utils;

import com.yongche.driver.client.DriverInfo;
import com.yongche.framework.CaseExecutor;
import com.yongche.framework.config.ConfigUtil;
import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.core.TestParameter;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generate driver accounts, associated rental companies and labor companies
 */
public class DriverAccountUtil {

    public static Logger log = LoggerFactory.getLogger(DriverAccountUtil.class);
    private static final String MODULE_UPDATE_DRIVER_BY_ID ="V1/Driver/updateDriverById";
    private static final String MODULE_FIND_DRIVER_BY_ID = "V1/Driver/getDriver";
    private static final String MERCHANT_CENTER = ConfigUtil.getValue(ConfigConst.HOST,"MERCHANT");
    private static final String DRIVER_DATA_PATH = ConfigUtil.getValue(ConfigConst.DRIVER_INFO,"driver_info_data");
    private static final String COMPANY_ID = ConfigUtil.getValue(ConfigConst.DRIVER_INFO,"company_id");
    private static final String LABOUR_COMPANY_ID = ConfigUtil.getValue(ConfigConst.DRIVER_INFO,"labour_company_id");

    public static void main(String[] argus){
        DriverAccountUtil.createDriverAccount(DRIVER_DATA_PATH);
        DriverAccountUtil.updateDriverListInfo(DRIVER_DATA_PATH,COMPANY_ID,LABOUR_COMPANY_ID);
    }

    /**
     * Generate driver accounts
     * @param driverDataPath
     */
    public static void createDriverAccount(String driverDataPath){
        ArrayList<DriverInfo> driverList = CommonUtil.getDriverList(driverDataPath);
        String updateDriverURL = String.format("http://%s/%s",MERCHANT_CENTER,MODULE_UPDATE_DRIVER_BY_ID);
        for (DriverInfo driver:driverList){
            Map<String,TestParameter> inputParam = new HashMap();
            String driver_id = String.valueOf(driver.getUserId());
            if("0".equals(getAccountIdByUserId(driver_id))){
                MapUtil.put(inputParam,"user_id",String.valueOf(driver_id));
                MapUtil.put(inputParam,"merchant_id","2000");
                MapUtil.put(inputParam,"account_type","2");
                Map<String,TestParameter> outputParam = CaseExecutor.runTestCase("payment/account_createAccount.xml",inputParam);
                String account_id = outputParam.get("account_id").getValue();
                List<NameValuePair> formParameters = new ArrayList<>();
                formParameters.add(new BasicNameValuePair("driver_id",String.valueOf(driver_id)));
                formParameters.add(new BasicNameValuePair("account_id",account_id));
                Map<String,String> headerMap = new HashMap<>();
                headerMap.put("User-Agent","Mozilla\\/5.0 (Windows NT 6.1; WOW64) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/57.0.2987.110 Safari\\/537.36)");
                String response = HttpUtil.post(updateDriverURL,formParameters,headerMap);
                String result = JSONObject.fromObject(response).getString("result");
                if("true".equals(result)){
                    log.info("update driver["+driver_id+"] account info success");
                }else {
                    log.info("update driver["+driver_id+"] account info failed");
                }
            }else{
                log.info("driver["+driver_id+"] account info already existed,no need to update");
            }
        }
    }

    public static void updateDriverListInfo(String driverDataPath,String company_id,String labor_company_id){
        ArrayList<DriverInfo> driverList = CommonUtil.getDriverList(driverDataPath);
        for (DriverInfo driver:driverList){
            updateDriverInfo(String.valueOf(driver.getUserId()),company_id,labor_company_id);
        }
    }

    /**
     * Update company and labour_company information
     * @param driver_id
     * @param company_id
     * @param labour_company_id
     */
    public static void updateDriverInfo(String driver_id,String company_id,String labour_company_id){
        String updateDriverURL = String.format("http://%s/%s",MERCHANT_CENTER,MODULE_UPDATE_DRIVER_BY_ID);
        List<NameValuePair> formParameters = new ArrayList<>();
        formParameters.add(new BasicNameValuePair("driver_id",String.valueOf(driver_id)));
        formParameters.add(new BasicNameValuePair("company_id",company_id));
        formParameters.add(new BasicNameValuePair("labour_company_id",labour_company_id));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("User-Agent","Mozilla\\/5.0 (Windows NT 6.1; WOW64) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/57.0.2987.110 Safari\\/537.36)");
        String response = HttpUtil.post(updateDriverURL,formParameters,headerMap);
        String result = JSONObject.fromObject(response).getString("result");
        if("true".equals(result)){
            log.info("update driver["+driver_id+"] company and labour_company_id info success");
        }else {
            log.info("update driver["+driver_id+"] company and labour_company_id info failed");
        }

    }

    /**
     * Query account information by userId
     * @param userId
     * @return
     */
    public static String getAccountIdByUserId(String userId){
        String url = String.format("http://%s/%s",MERCHANT_CENTER,MODULE_FIND_DRIVER_BY_ID);
        List<NameValuePair> formParameters = new ArrayList<>();
        formParameters.add(new BasicNameValuePair("driver_id",String.valueOf(userId)));
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("User-Agent","Mozilla\\/5.0 (Windows NT 6.1; WOW64) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/57.0.2987.110 Safari\\/537.36)");
        String response = HttpUtil.get(url,formParameters,headerMap);
        JSONObject result  = (JSONObject)JSONObject.fromObject(response).get("result");
        String account_id = result.getString("account_id");
        return account_id;
    }
}
