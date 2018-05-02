package com.yongche.testcase.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.api.APIUtil;
import com.yongche.framework.api.order.Order;
import com.yongche.framework.core.*;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.MapUtil;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试接口: order/getOrderInService
 * 接口说明: 根据请求条件返回某用户的历史订单记录
  */
public class GetOrderInServiceTest {
    public static Logger log = LoggerFactory.getLogger(GetOrderInServiceTest.class);
    private String new_order_id;
    /**
     * 测试目的：
     * 确保GetOrderInService能正确返回当前正在服务中的订单
     *
     * 测试步骤：
     * 1.新建订单，获得新创建的订单号<br>
     * 2.等待司机接单<br>
     *   检查点：接单成功，即订单状态为4<br>
     * 3.获取接单司机的id<br>
     * 4.请求接口order/getOrderInService，获取接单司机当前在服务中的订单列表<br>
     *   检查点：接口返回成功<br>
     * 5.遍历订单列表中是否包含新创建的订单id<br>
     *   检查点：新创建订单在该司机的服务列表中<br>
     * 6.为避免司机无法再次接单，走完订单剩余流程<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    @Test
    public void getOrderInService_Should_Success_With_Valid_Parameters() throws Exception{
        //构造请求参数
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id",rider.getUserId());
        MapUtil.put(inputParameterMap,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParameterMap,"product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);

        //新创建订单 生成订单号
        TestParameter order_id = Order.createOrderByProductTypeId(inputParameterMap);
        new_order_id = order_id.getValue();

        //判断是否接单成功
        boolean isAccepted = Order.hasDriverResponse(new_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");

        //获取接单司机id
        String driver_id = Order.getDriverIdByOrderId(new_order_id);

        //查询司机当前服务的订单
        Map<String,TestParameter> params = MapUtil.getParameterMap("driver_id",driver_id);
        Map<String,TestParameter> result = CaseExecutor.runTestCase("order_getOrderInService.xml",params);

        //处理返回的特殊格式
        JSONArray order_list = Order.getOrderList(result,"order_list");

        //遍历订单列表中是否包含新创建的订单id
        boolean inList = Order.checkOrderInList(new_order_id,order_list);

        //判断订单是否应该出现在当前行程列表中
        Assert.assertTrue(inList,"order not in list !");
        //走完剩下的流程
        APIUtil.afterAcceptOrder(new_order_id);
    }

    @AfterMethod
    public void teardown(ITestResult test){
        CommonUtil.sleep(10000);
        log.info("================================TEARDOWN==================================");
        log.info("Case name is —— " + test.getName());
        TearDown.cancelAndDeleteOrder(new_order_id);
    }
}
