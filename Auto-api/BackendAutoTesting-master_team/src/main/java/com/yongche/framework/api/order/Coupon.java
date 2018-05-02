package com.yongche.framework.api.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.config.ConfigUtil;
import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.core.ParamValue;
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
 *
 */
public class Coupon {
    public static Logger log = LoggerFactory.getLogger(Coupon.class);

    public static String coupon_list = ConfigUtil.getValue(ConfigConst.COUPON_INFO,"coupon_list");

    /**
     * 调用发放优惠券接口,返回优惠券id<br>
     * @param user_id       用户id
     * @param coupon_list   优惠券模板id
     * @param comment       发放原因
     * @return      优惠券id
     */
    public static String sendCoupon(String user_id,String coupon_list,String operator_id,String comment){
        //String url = "http://testing.preference.yongche.org/Coupon/sendCoupon";
        String url = String.format("http://%s/Coupon/sendCoupon", ConfigUtil.getValue(ConfigConst.HOST,"COUPON"));
        List<NameValuePair> formParameters = new ArrayList<>();
        formParameters.add(new BasicNameValuePair("user_id",user_id));
        formParameters.add(new BasicNameValuePair("coupon_list",coupon_list));
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

        String coupon_id = jsonArray.getString(0);
        log.info("coupon Id : " + coupon_id);

        return coupon_id;
    }

    /**
     * 给服务中的订单使用套包
     * @param service_order_id 处于服务中的订单，即 处于从司机出发开始，到司机结束行程之前的阶段的订单
     * @return
     */
    public static  void updateCoupon(String service_order_id){
        log.info("使用优惠券");
        String coupon_member_id = Coupon.getFirstAvailableCoupon(service_order_id);
        Map<String,TestParameter> outputParameters = Order.getFieldsByOrderId(service_order_id);
        Map<String,TestParameter> fields = CommonUtil.OutputResult(outputParameters);
        String user_id = MapUtil.getString(fields,"user_id");
        if(null==coupon_member_id){
            Coupon.sendCoupon(user_id,coupon_list,user_id,"send coupon to user["+user_id+"]");
            CommonUtil.sleep(3*1000);
            coupon_member_id = Coupon.getFirstAvailableCoupon(service_order_id);
        }
       Coupon.updateCoupon(service_order_id,coupon_member_id);
    }

    public static void updateCoupon(String service_order_id,String coupon_member_id){
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("service_order_id",service_order_id);
        MapUtil.put(inputParameterMap,"coupon_member_id",coupon_member_id);
        CaseExecutor.runTestCase("",inputParameterMap);
    }


    public static String getFirstAvailableCoupon(String service_order_id){
        Map<String,TestParameter> outputParameters = Order.getFieldsByOrderId(service_order_id);
        Map<String,TestParameter> fields = CommonUtil.OutputResult(outputParameters);
        String userId = MapUtil.getString(fields,"user_id");
        String product_type_id = MapUtil.getString(fields,"product_type_id");
        String car_type_id = MapUtil.getString(fields,"car_type_id");
        return getFirstAvailableCoupon(userId,product_type_id,car_type_id);
    }

    public static String getFirstAvailableCoupon(String userId,
                                          String product_type_id,
                                          String car_type_id){
        Map<String, TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id_list",userId);
        MapUtil.put(inputParameterMap,"product_type_id",product_type_id);
        MapUtil.put(inputParameterMap,"car_type_id",car_type_id);

        Map<String,TestParameter> outputMap = CaseExecutor.runTestCase("charge/couponMember_getAvailableCouponMemberList.xml",inputParameterMap);
        CommonUtil.sleep(3*1000);
        String response = outputMap.get(ParamValue.RESPONSE_RESULT).getValue();
        JSONObject obj = JSONObject.fromObject(response);
        String temp = obj.getString("result");
        obj = JSONObject.fromObject(temp);
        temp = obj.getString("available");
        //"available"是个数组列出了用户所有可用的优惠券 即使只有一个也以数组的形式列出
        JSONArray jsonArray = JSONArray.fromObject(temp);
        obj = (JSONObject)jsonArray.get(0);
        String couponId = obj.getString("id");
        log.info("First available coupon: " +couponId);

        return couponId;
    }
}
