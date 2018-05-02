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
 * 测试接口: order/getHistoryOrderList
 * 接口说明: 根据请求条件返回某用户的历史订单记录
  */
public class GetHistoryOrderTest {
    public static Logger log = LoggerFactory.getLogger(GetHistoryOrderTest.class);
    private String new_order_id;
    /**
     * 测试目的：<br>
     * 确保获取历史形成接口（GetHistoryOrderList）能正确返回历史订单<br>
     *<br>
     * 测试步骤：
     * 1.调用createOrder接口创建订单，获得新生成订单号<br>
     * 2.模拟订单全部流程直到订单完结
     *   检查点：能成功接单
     * 3.请求order/getHistoryOrderList接口查询历史记录中是否有该订单存在<br>
     *   检查点：接口返回结果中有新创建的订单存在<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    @Test
    public void order_getHistoryOrderList_shouldSucceed_withValidParameters() throws Exception{
        //新创建订单 生成订单号
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id",rider.getUserId());//本订本乘
        MapUtil.put(inputParameterMap,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParameterMap,"product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        MapUtil.put(inputParameterMap,"car_type_id", ParamValue.CAR_TYPE_ID_COMFORTABLE);
        MapUtil.put(inputParameterMap,"car_type","舒适车型");
        TestParameter order_id = Order.createOrderByProductTypeId(inputParameterMap);
        new_order_id = order_id.getValue();

        //走完订单全流程
        APIUtil.acceptOrder(new_order_id);
        //请求历史订单列表接口
        Map<String,TestParameter> inputParamMap = MapUtil.getParameterMap("user_id",rider.getUserId());
        MapUtil.put(inputParamMap,"cellphone", rider.getCellphone());
        Map<String,TestParameter> result = CaseExecutor.runTestCase("order_getHistoryOrder.xml",inputParamMap);
        //处理返回结果格式
        JSONArray history_list =  Order.getOrderList(result);
        //遍历订单列表中是否包含新创建的订单id
        boolean inList = Order.checkOrderInList(new_order_id,history_list);
        Assert.assertTrue(inList,"order should be in list !");
    }
    
    @AfterMethod
    public void teardown(ITestResult test){
        CommonUtil.sleep(10000);
        log.info("================================TEARDOWN==================================");
        log.info("Case name is —— " + test.getName());
        TearDown.cancelAndDeleteOrder(new_order_id);
    }
}
