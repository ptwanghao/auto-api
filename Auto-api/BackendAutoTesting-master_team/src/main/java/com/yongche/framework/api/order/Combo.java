package com.yongche.framework.api.order;

import com.yongche.App;
import com.yongche.framework.CaseExecutor;
import com.yongche.framework.config.ConfigUtil;
import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.core.RiderCategoryConst;
import com.yongche.framework.core.RiderInfo;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.HttpUtil;
import com.yongche.framework.utils.MapUtil;
import net.sf.json.JSONArray;
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
 * 套包相關
 */
public class Combo {

    public static Logger log = LoggerFactory.getLogger(Combo.class);

    /**
     * 给服务中的订单使用套包
     * @param orderId 处于服务中的订单，即 处于从司机出发开始，到司机结束行程之前的阶段的订单
     * @return
     */
    public static Map<String, TestParameter> updateCombo(String orderId) {
        log.info("使用套包");
        String availableUserComboId = Combo.getFirstAvailableCombo(orderId);
        if ( null == availableUserComboId ) {
            Combo.sendCombo(RiderCategoryConst.FIRST_USER);
            CommonUtil.sleep( 10 * 1000);
            availableUserComboId = Combo.getFirstAvailableCombo(orderId);
        }
        log.info("套包ID : " + availableUserComboId);
        return Combo.updateCombo(orderId,availableUserComboId);
    }

    /**
     * 订单期间更新套包信息
     *
     * @param orderId               訂單ID
     * @param user_mileage_combo_id 套包ID
     * @return
     */
    public static Map<String, TestParameter> updateCombo(String orderId, String user_mileage_combo_id) {
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("service_order_id",orderId);
        MapUtil.put(inputParameterMap,"user_mileage_combo_id", user_mileage_combo_id);

        return CaseExecutor.runTestCase("coupon_updateCombo.xml",inputParameterMap);
    }


    public static String sendCombo(int indexOfPassenger) {
        RiderInfo rider = CommonUtil.getRider(indexOfPassenger);
        String userId = rider.getUserId();
        String operatorId = userId;
        String mileage_combo_list =ConfigUtil.getValue(ConfigConst.COMBO_INFO,"mileage_combo_list");
        log.info("给用户发放套包");
        String userComboId = Combo.sendCombo(userId,mileage_combo_list,operatorId,"send user combo to " + rider.toString());
        log.info("用户套包ID : " + userComboId);
        return  userComboId;
    }

    /**
     * 给用户发放套包
     * @param user_id 用户套包
     * @param mileage_combo_list 后台套包ID 列表
     * @param operator_id 操作者ID
     * @param comment 说明
     * @return 用户套包ID
     */
    public static String sendCombo(String user_id,String mileage_combo_list,String operator_id,String comment){
        String url = String.format("http://%s/MileageCombo/sendMileageCombo",
                ConfigUtil.getValue(ConfigConst.HOST,"COMBO"));
        List<NameValuePair> formParameters = new ArrayList<>();
        formParameters.add(new BasicNameValuePair("user_id",user_id));
        formParameters.add(new BasicNameValuePair("mileage_combo_list",mileage_combo_list.trim()));
        formParameters.add(new BasicNameValuePair("comment",comment));
        formParameters.add(new BasicNameValuePair("operator_id",operator_id));

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("User-Agent","Mozilla\\/5.0 (Windows NT 6.1; WOW64) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/57.0.2987.110 Safari\\/537.36)");
        String response = HttpUtil.get(url,formParameters,headerMap);
        log.info(response);

        JSONObject obj = JSONObject.fromObject(response);
        String res = obj.getString("result");
        obj =JSONObject.fromObject(res);
        res = obj.getString("ids");
        log.info(res);

        JSONArray jsonArray = JSONArray.fromObject(res);

        String user_mileage_combo_id = jsonArray.getString(0);
        log.info("user_mileage_combo_id : " + user_mileage_combo_id);

        return user_mileage_combo_id;
    }

    /**
     * 获取订单第一个可用的套包ID
     * @param order_id 订单Id
     * @return
     */
    public static String getFirstAvailableCombo(String order_id){

        Map<String,TestParameter> outputParameters = Order.getFieldsByOrderId(order_id);
        Map<String,TestParameter> fields = CommonUtil.OutputResult(outputParameters);
        String user_id_list = MapUtil.getString(fields,"user_id");
        String product_type_id = MapUtil.getString(fields,"product_type_id");
        String car_type_id = MapUtil.getString(fields,"car_type_id");
        String start_time = MapUtil.getString(fields,"start_time");
        String create_time = MapUtil.getString(fields,"create_time");
        //boolean is_asap = MapUtil.getBoolean(fields,"is_asap");
        final boolean Is_ASAP = true;
        String city = MapUtil.getString(fields,"city");
        final String Platform = "10";
        final String App_id = "1";
        final boolean Can_Use_Mileage_Combo = true;
        final String Field_List = "product_type_id_list,car_type,city_list,source_list";

        return getFirstAvailableCombo(user_id_list,
                product_type_id,
                car_type_id,
                start_time,
                create_time,
                Is_ASAP,
                city,
                Platform,
                order_id,
                App_id,
                Can_Use_Mileage_Combo,
                Field_List);

    }

    /**
     * 获得订单可用的套包
     * @param user_id_list
     * @param product_type_id
     * @param car_type_id
     * @param start_time
     * @param create_time
     * @param is_asap
     * @param city
     * @param platform
     * @param order_id
     * @param app_id
     * @param can_use_mileage_combo
     * @param field_list
     * @return
     */
    public static String getFirstAvailableCombo(String user_id_list,
                                                String product_type_id,
                                                String car_type_id,
                                                String start_time,
                                                String create_time,
                                                boolean is_asap,
                                                String city,
                                                String platform,
                                                String order_id,
                                                String app_id,
                                                boolean can_use_mileage_combo,
                                                String field_list){
        String url = String.format("http://%s/MileageCombo/getAvailableUserMileageComboList",
                ConfigUtil.getValue(ConfigConst.HOST,"COMBO"));

        List<NameValuePair> formParameters = new ArrayList<>();
        formParameters.add(new BasicNameValuePair("user_id_list",user_id_list));
        formParameters.add(new BasicNameValuePair("product_type_id",product_type_id));
        formParameters.add(new BasicNameValuePair("car_type_id",car_type_id));
        formParameters.add(new BasicNameValuePair("start_time",start_time));
        formParameters.add(new BasicNameValuePair("create_time",create_time));
        formParameters.add(new BasicNameValuePair("is_asap",is_asap?"1":"0"));
        formParameters.add(new BasicNameValuePair("city",city));
        formParameters.add(new BasicNameValuePair("platform",platform));
        formParameters.add(new BasicNameValuePair("order_id",order_id));
        formParameters.add(new BasicNameValuePair("app_id",app_id));
        formParameters.add(new BasicNameValuePair("can_use_mileage_combo",can_use_mileage_combo ? "1" : "0"));
        formParameters.add(new BasicNameValuePair("field_list",field_list));

        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("User-Agent","Mozilla\\/5.0 (Windows NT 6.1; WOW64) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/57.0.2987.110 Safari\\/537.36)");
        String response = HttpUtil.get(url,formParameters,headerMap);
        log.info(response);

        return getFirstAvailableComboFromResponse(response);
    }

    /**
     * 获取第一个剩余基本里程和剩余基本时间均大于0的套包id
     * @param response
     * @return
     */
    public static String getFirstAvailableComboFromResponse(String response){

        JSONObject obj = JSONObject.fromObject(response);
        String res = obj.getString("result");

        if (res.equals("[]")){
            return null;
        }
        obj =JSONObject.fromObject(res);

        res = obj.getString("available");
        log.info(res);
        if (null == res) {
            log.info("No available combo for the product , car type, time period, etc");
        }

        JSONArray jsonArray = JSONArray.fromObject(res);

        JSONObject available_mileage_combo = null;
        // 遍历所有可用套包，查找第一个剩余基本里程和剩余基本时间均大于0的套包
        for(Object comboObject : jsonArray) {
            JSONObject current_mileage_combo = ((JSONObject)comboObject).getJSONObject("mileage_combo");
            long distance = current_mileage_combo.getLong("distance");
            long time_length = current_mileage_combo.getLong("time_length");
            if ( distance >0 && time_length > 0) {
                available_mileage_combo = (JSONObject)comboObject;
                break;
            }
        }

        // 获取套包Id
        String user_mileage_combo_id =
                (null != available_mileage_combo)?available_mileage_combo.getString("user_mileage_combo_id"):null;

        log.info("user_mileage_combo_id : " + user_mileage_combo_id);

        return user_mileage_combo_id;
    }

}