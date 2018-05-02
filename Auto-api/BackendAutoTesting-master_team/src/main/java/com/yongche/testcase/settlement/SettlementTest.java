package com.yongche.testcase.settlement;

import com.yongche.framework.api.APIUtil;
import com.yongche.framework.api.order.Order;
import com.yongche.framework.api.settlement.Settlement;
import com.yongche.framework.core.*;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.Map;

public class SettlementTest {

    private String str_order_id=null;
    public static Logger log = LoggerFactory.getLogger(SettlementTest.class);

    @Test
    public void settlement_createOrder_withDeluxeCar() {
        settlement_unAccountedOrder(ParamValue.CAR_TYPE_ID_DELUXE,ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,false);
    }

    @Test
    public void settlement_createOrder_withYoungCar() {
        settlement_unAccountedOrder(ParamValue.CAR_TYPE_ID_YOUNG,ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,false);
    }

    @Test
    public void settlement_createOrder_withComfortableCar() {
        settlement_unAccountedOrder(ParamValue.CAR_TYPE_ID_COMFORTABLE,ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,false);
    }

    @Test
    public void settlement_createOrder_withLuxuryCar() {
        settlement_unAccountedOrder(ParamValue.CAR_TYPE_ID_LUXURY,ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,false);
    }

    @Test
    public void settlement_createOrder_withLuxuryCar_withAdjustAmount() {
        settlement_unAccountedOrder(ParamValue.CAR_TYPE_ID_LUXURY,ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,true);
    }

    @Test
    public void settlement_withAdjustAmount_Accounted() {
        Settlement.check_distribute(null,null,true,true);
    }

    public void settlement_unAccountedOrder(String car_type_id,String product_type_id,boolean isAdjustAmount) {
        //构造请求参数
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        Map<String, TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id", rider.getUserId());
        MapUtil.put(inputParameterMap, "passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap, "passenger_name", rider.getPassengerName());
        MapUtil.put(inputParameterMap, "car_type_id", car_type_id);
        MapUtil.put(inputParameterMap, "product_type_id", product_type_id);

        //创建订单
        TestParameter order_id = Order.createOrderByProductTypeId(inputParameterMap);
        str_order_id = order_id.getValue();

        //判断是否有司机接单
        boolean ret = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(ret);
        String driverId = Order.getFieldsByOrderId(str_order_id).get("driver_id").getValue();
        APIUtil.afterAcceptOrder(str_order_id);
        CommonUtil.sleep(3000);//等待第1次分佣结束，否则调用调整订单接口时会报错{"ret_code":403,"ret_msg":"等待订单分佣"}
        Order.adjustAmount(str_order_id);
        CommonUtil.sleep(3000);//等待第2次分佣结束，否则查询数据库时只返回1条结果
        Settlement.check_distribute(str_order_id, driverId, isAdjustAmount, false);
    }

    @AfterMethod
    public void teardown(ITestResult test){
        CommonUtil.sleep(10000);
        log.info("================================TEARDOWN==================================");
        log.info("Case name is —— " + test.getName());
        if(str_order_id!=null){
            TearDown.cancelAndDeleteOrder(str_order_id);
        }
    }

}
