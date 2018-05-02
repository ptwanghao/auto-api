package com.yongche.testcase.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.OrderStatus;
import com.yongche.framework.core.ParamValue;
import com.yongche.framework.core.TearDown;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.api.order.*;
import com.yongche.framework.utils.CommonUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试接口: order/getCurrentTrip
 * 接口说明: 根据请求条件返回某用户当前在进行中的订单记录
  */
public class GetCurrentTripTest {
    public static Logger log = LoggerFactory.getLogger(GetCurrentTripTest.class);
    private String new_order_id;

    /**
     * 测试目的：<br>
     * 获取当前订单能正确返回当前服务中订单<br>
     *<br>
     * 测试步骤：<br>
     * 1.调用createOrder接口创建订单，获得新生成订单号<br>
     * 2.请求当前订单列表接口getCurrentTrip，获得当前进行中订单列表<br>
     *   检查点：新生成的订单信息出现在当前订单列表中,且状态为3（等待司机接单状态）<br>
     * 3.循环查询是否接单成功<br>
     *   检查点：订单在50秒内变为已接单状态<br>
     * 4.请求当前订单列表接口getCurrentTrip，获得当前进行中订单列表<br>
     *   检查点：新生成的订单信息出现在当前订单列表中,且状态为4（司机已接单状态）<br>
     * 5.调用order/setDriverDepart接口，模拟司机出发<br>
     * 6.调用state/driveArrived接口，模拟司机到达<br>
     * 7.请求当前订单列表接口getCurrentTrip，获得当前进行中订单列表<br>
     *   检查点：新生成的订单信息出现在当前订单列表中,且状态为5（等待服务开始状态）<br>
     * 8.调用state/startTiming接口，开始服务<br>
     * 9.请求当前订单列表接口getCurrentTrip，获得当前进行中订单列表<br>
     *   检查点：新生成的订单信息出现在当前订单列表中,且状态为6（服务中状态）<br>
     * 10.请求state/endTiming接口，结束服务<br>
     * 11.请求state/confirmCharge接口，结算费用<br>
     * 12.请求当前订单列表接口getCurrentTrip，获得当前进行中订单列表<br>
     *    检查点：新建的订单此时应该为结束状态，不会出现在当前订单列表中<br>
     * 13.取消未结束的订单，销毁数据库中新生成的订单数据<br>
     * <br>
     * 注意：创建订单和查询订单的user_id 和cellphone参数要一致<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    @Test
    public void order_getCurrentTrip_shouldSucceed_withValidParameters() throws Exception{

        //新创建订单 生成订单号
        TestParameter user_id = TestParameter.getTestParameter("user_id", "26016450");
        TestParameter passenger_phone = TestParameter.getTestParameter("passenger_phone", "16888800000");
        TestParameter product_type_id = TestParameter.getTestParameter("product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        TestParameter car_type_id = TestParameter.getTestParameter("car_type_id", ParamValue.CAR_TYPE_ID_DELUXE);
        TestParameter car_type = TestParameter.getTestParameter("car_type","豪华车型");
        Map<String,TestParameter> inputParameterMap = new HashMap<>();
        inputParameterMap.put("user_id",user_id);
        inputParameterMap.put("passenger_phone",passenger_phone);
        inputParameterMap.put("product_type_id",product_type_id);
        inputParameterMap.put("car_type_id",car_type_id);
        inputParameterMap.put("car_type",car_type);

        TestParameter order_id = Order.createOrderByProductTypeId(inputParameterMap);
        new_order_id = order_id.getValue();
        CommonUtil.sleep(10000);
        //刚创建的订单有可能为正在派单状态3，也有可能接单较快 为已接单状态4
        int status = checkOrder(new_order_id,true);
        if (status!= OrderStatus.WAIT_FOR_ACCEPT && status!= OrderStatus.WAIT_FOR_DRIVER){
            Assert.assertTrue(false,"status should be 3 or 4!");
        }

        //如果当前为正在派单状态3 则继续判断是否接单成功
        if (status == OrderStatus.WAIT_FOR_ACCEPT ) {
            boolean isAccepted = Order.hasDriverResponse(new_order_id);
            Assert.assertTrue(isAccepted, "accept order failed!!!");
        }

        //接单后,订单在返回列表中，且订单状态为4
        status = checkOrder(new_order_id,true);
        Assert.assertEquals(status,OrderStatus.WAIT_FOR_DRIVER,"status should be 4 !");

        //服务开始后,订单在返回列表中，且订单状态为5
        Order.setDriverDepart(new_order_id);
        State.driverArrived(new_order_id);
        Assert.assertEquals(checkOrder(new_order_id,true),OrderStatus.SERVICE_START,"status should be 5 !");

        //服务中,订单在返回列表中，且订单状态为6
        State.startTiming(new_order_id);
        Assert.assertEquals(checkOrder(new_order_id,true),OrderStatus.IN_SERVICE,"status should be 6 !");

        //司机结束行程，订单不应该出现在返回列表中
        State.endTiming(new_order_id);
        State.confirmCharge(new_order_id);
        CommonUtil.sleep(2000);
        Assert.assertEquals(checkOrder(new_order_id,false),OrderStatus.UNVALID);
    }

    //include 判断订单是否应该出现在当前行程列表中
    public int checkOrder(String new_order_id,boolean include){
        //查询订单，获得返回结果
        Map<String,TestParameter> result = CaseExecutor.runTestCase("order_getCurrentTrip.xml");
        boolean flag = false;  //flag为true表示查询结果中包含新建订单，flag为false表示查询结果中不包含新建订单
        int status = -1;//待返回的订单状态字段
        //处理返回的特殊格式
        JSONArray unfinished_list = Order.getOrderList(result,"unfinished");
        //遍历订单列表中是否包含新创建的订单id
        for(int i=0; i<unfinished_list.size();i++){
            JSONObject json_order = (JSONObject)unfinished_list.get(i);
            String service_order_id = json_order.get("service_order_id").toString();
            status = Integer.parseInt(json_order.get("status").toString());
            if(new_order_id.equals(service_order_id)){
                flag = true;
                break;
            }
        }
        if(include){
            //校验新创建的订单在接口返回列表中
            Assert.assertTrue(flag,"order is not in response list!");
        }else{
            //校验新创建的订单不在接口返回列表中
            Assert.assertFalse(flag,"order is in response list!");
            status = -1;
        }

        //接口返回的订单状态
        return status;
    }

    @AfterMethod
    public void teardown(ITestResult test){
        log.info("================================TEARDOWN==================================");
        log.info("Case name is —— " + test.getName());
        TearDown.cancelAndDeleteOrder(new_order_id);
    }

}
