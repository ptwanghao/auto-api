package com.yongche.framework.api.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.ParamValue;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.HttpUtil;
import com.yongche.framework.utils.MapUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Coupon {
    public static Logger log = LoggerFactory.getLogger(Coupon.class);

    /**
     * 调用发放优惠券接口,返回优惠券id<br>
     * @param user_id       用户id
     * @param coupon_list   优惠券模板id
     * @param comment       发放原因
     * @return      优惠券id
     */
    public static String sendCoupon(String user_id,String coupon_list,String operator_id,String comment){
        String url = "http://testing.preference.yongche.org/Coupon/sendCoupon";
        List<NameValuePair> formParameters = new ArrayList<>();
        formParameters.add(new BasicNameValuePair("user_id",user_id));
        formParameters.add(new BasicNameValuePair("coupon_list",coupon_list));
        formParameters.add(new BasicNameValuePair("comment",comment));
        formParameters.add(new BasicNameValuePair("operator_id",operator_id));
        String response = HttpUtil.get(url,formParameters);
        //log.info(response);

        JSONObject obj = JSONObject.fromObject(response);
        String res = obj.getString("result");
        obj =JSONObject.fromObject(res);
        res = obj.getString("ids");
        //log.info(res);

        JSONArray jsonArray = JSONArray.fromObject(res);

        String coupon_id = jsonArray.getString(0);

        return coupon_id;
    }

    public static String getFirstAvailableCoupon(String userId,
                                          String product_type_id,
                                          String car_type_id){
        Map<String, TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id_list",userId);
        MapUtil.put(inputParameterMap,"product_type_id",product_type_id);
        MapUtil.put(inputParameterMap,"car_type_id",car_type_id);

        Map<String,TestParameter> outputMap = CaseExecutor.runTestCase("charge/couponMember_getAvailableCouponMemberList.xml",inputParameterMap);
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
