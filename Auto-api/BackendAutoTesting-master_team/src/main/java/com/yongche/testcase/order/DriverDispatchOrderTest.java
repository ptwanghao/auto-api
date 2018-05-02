package com.yongche.testcase.order;


import com.yongche.framework.api.APIUtil;
import com.yongche.framework.api.order.DriverAgent;
import com.yongche.framework.api.order.Order;
import com.yongche.framework.config.ConfigUtil;
import com.yongche.framework.core.*;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

public class DriverDispatchOrderTest {

    private String str_order_id;
    public static Logger log = LoggerFactory.getLogger(DriverDispatchOrderTest.class);

    /**
     * 测试目的：<br>
     * 司机改派.<br>
     * <br>
     * 测试步骤：<br>
     * 1.构造创建订单参数
     * 2.调用createOrder()创建一个订单<br>
     * 3.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在90秒内变为已接单状态<br>
     * 4.调用Dispatch/driverRedispatch接口，司机发起改派<br>
     * 5.循环查询订单的状态，判断是否接单成功<br>
     *   检查点：订单在90秒内变为已接单状态<br>
     * 6.调用Dispatch/getChangeDriverStatus，查询司机改派状态<br>
     *   检查点：返回202，则改派完成<br>
     * 7.走完订单整个流程（司机到达--开始服务--结束服务--确认账单）<br>
     * <br>
     * 测试用例开发人员：罗文平<br>
     */
    @Test
    public void order_redispatchOrder_driverDispatch_should_success(){
        //构造请求参数
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id",rider.getUserId());//本订本乘
        MapUtil.put(inputParameterMap,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParameterMap,"car_type_id", ParamValue.CAR_TYPE_ID_DELUXE);
        MapUtil.put(inputParameterMap,"product_type_id",ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        MapUtil.put(inputParameterMap,"car_type","Young车型");

        //创建订单
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParameterMap);
        str_order_id = order_id.getValue();

        //判断是否有司机接单
        boolean ret = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(ret);

        //司机已接单  获取司机ID
        String driverId = Order.getFieldsByOrderId(str_order_id).get("driver_id").getValue();
        String carId = Order.getFieldsByOrderId(str_order_id).get("car_id").getValue();
        log.info("改派前：driver id : " + driverId + " car id :" + carId);

        //司机申请改派
        String bidding_id= ConfigUtil.getValue(ConfigConst.BIDDING_INFO,"bidding_id");
        String deduct_fine="5";
        String deduct_marks="2";
        String driver_base_rights_id="2";
        DriverAgent.driverRedispatch(str_order_id,driverId,bidding_id,deduct_fine,deduct_marks,driver_base_rights_id);

        ret = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(ret);
        int status = DriverAgent.getChangeDriverStatus(str_order_id,driverId,bidding_id);
        Assert.assertEquals(status,202,"driver redispatch failed!");
        APIUtil.afterAcceptOrder(str_order_id);
    }

    @BeforeMethod
    public void init(){
        str_order_id=null;
    }

    @AfterMethod
    public void teardown(ITestResult test){
        if(null!=str_order_id) {
            CommonUtil.sleep(10000);
            log.info("================================TEARDOWN==================================");
            log.info("Case name is —— " + test.getName());
            TearDown.cancelAndDeleteOrder(str_order_id);
        }
    }
}