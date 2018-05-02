package com.yongche.testcase;


import com.yongche.framework.CaseExecutor;
import com.yongche.framework.api.APIUtil;
import com.yongche.framework.api.order.Coupon;
import com.yongche.framework.api.order.Order;
import com.yongche.framework.api.order.State;
import com.yongche.framework.core.OrderStatus;
import com.yongche.framework.core.ParamValue;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.JsonUtil;
import com.yongche.framework.utils.MapUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;


/**
 *
 */
public class DispatchDemo {
    public static Logger log = LoggerFactory.getLogger(DispatchDemo.class);
    private String orderId;
    private  static final int seconds = 80*1000;

    @DataProvider(name="cartypelist")
    public Object[][] getCarTypeList(){
        return new Object[][]{
                {"2"},
                {"3"},
                {"4"},
                {"37"},
        };
    }

    public String createOrder_withCarTypeId(String car_type_id){
        log.info("+++++++++++++++++++car_type_id: " +car_type_id);

        Map<String,TestParameter> parameterMap = MapUtil.getParameterMap("car_type_id",car_type_id);
        MapUtil.put(parameterMap,"car_type_ids",car_type_id);
        //TestParameter order = Order.getOrderId("state_createOrder_airport_dropOff.xml",parameterMap,null);
        TestParameter order = Order.getOrderId("dispatch_demo.xml",parameterMap,null);
        String orderId = order.getValue();

        log.info("order_id: " + orderId);
        return  orderId;
    }

    //@Test(dataProvider = "cartypelist")
    public void testAllDrivers(String car_type_id){
        orderId = createOrder_withCarTypeId(car_type_id);

        boolean ret = Order.hasDriverResponse(orderId,seconds);
        Assert.assertTrue(ret);
    }

   //@Test
    public void dispatch() {
        String car_type_id="3";
        testAllDrivers(car_type_id);
    }

    //@Test
    public void useCoupon(){
        String user_id = "26007737";
        String car_type_id = "3";
        String product_type_id = "1";
        String couponId = Coupon.getFirstAvailableCoupon(user_id,product_type_id,car_type_id);

        Map<String,TestParameter> inputParaMap = MapUtil.getParameterMap("coupon_member_id",couponId);
        CaseExecutor.runTestCase("charge/couponMember_useCoupon.xml",inputParaMap);
    }

    @AfterMethod
    public void afterMe(){
        log.info(" afterMe(), order id" + orderId);

        TestParameter param_order_id = TestParameter.getTestParameter("order_id",orderId);
        int current_status = Integer.parseInt(Order.getStatus(orderId));

        if(OrderStatus.CANCELED != current_status && OrderStatus.FINISHED != current_status){
            Order.cancelOrderById(param_order_id);
        }

    }
}
