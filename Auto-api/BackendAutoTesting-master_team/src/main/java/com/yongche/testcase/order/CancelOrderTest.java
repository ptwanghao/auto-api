package com.yongche.testcase.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.*;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.MapUtil;
import com.yongche.framework.utils.db.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.yongche.framework.api.order.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class CancelOrderTest {
    public static Logger log = LoggerFactory.getLogger(CancelOrderTest.class);
    private String new_order_id;//新创建的订单id
    private boolean needAdjustAmount = false; // 是否需要调整订单金额以完成订单服务

    //@Test
    public void cancelOrder() throws Exception{
        Map<String,TestParameter> expectedParams = null;
        Map<String, TestParameter> inputParams = null;

        //=====Create Order
        expectedParams = CaseExecutor.runTestCase("state_createOrder_immediate.xml");

        //=====Cancel order success
        //Get Order id
        TestParameter order_id = expectedParams.get("order_id");
        inputParams = new HashMap<>();
        inputParams.put("order_id",order_id);

        //Cancel order
        CaseExecutor.runTestCase("state_cancelOrder_success.xml",inputParams);

        //Cancel order again
        CaseExecutor.runTestCase("state_cancelOrder_order_has_been_cancelled.xml",inputParams);

        /*
        //====================Update expected result parameter//
        Map<String,TestParameter> expectResultParams = null;
        //CANCEL_ORDER_ORDER_HAS_BEEN_CANCELLED = "order has beend canceled, service_order_id=";
        TestParameter ret_msg = new TestParameter ("ret_msg",Message.CANCEL_ORDER_ORDER_HAS_BEEN_CANCELLED + order_id.getValue());
        expectResultParams = new HashMap<>();
        expectResultParams.put("ret_msg",ret_msg);

        CaseExecutor.runTestCase("state_cancelOrder_order_has_been_cancelled.xml",inputParams,expectResultParams);
        */
    }

    /**
     * 测试目的：<br>
     * 确保可以成功在服务前用APP取消订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.读取xml文件中的请求参数<br>
     * 2.调用createOrder()创建(APP下单-本订本乘-预约用车-Young车型-系统选车下单)订单<br>
     * 3.检查service_order_id,user_id,passenger_phone,product_type_id,car_type_id字段在数据库service_order,service_order_ext中的存在<br>
     * 检查点：以上字段按照xml输入更新在数据库表中<br>
     * 4.读取state_cancelOrder_success.xml文件中的请求参数<br>
     * 5.调用cancelOrder()取消订单<br>
     * 6.校验订单是否取消成功。用service_order_id作为查询条件查询service_order表，查出的status=8表示取消成功<br>
     * <br>
     * 测试用例开发人员：苏静<br>
     */
    @Test
    public void order_cancelOrder_shouldSucceed_beforeServiceAndCancel(){
        //本订本乘
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.SECOND_USER);
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id",rider.getUserId());
        MapUtil.put(inputParameterMap,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", rider.getPassengerName());
        //预约用车
        MapUtil.put(inputParameterMap,"product_type_id", ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR);
        //Young车型
        MapUtil.put(inputParameterMap,"car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        //系统选车下单
        MapUtil.put(inputParameterMap,"is_auto_dispatch", "1");
        //APP下单
        MapUtil.put(inputParameterMap,"source", ParamValue.SOURCE_ANDROIDAPP);


        //创建(本订本乘-预约用车-Young车型-系统选车下单)订单，校验数据库字段，并删除测试数据
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParameterMap);
        new_order_id = order_id.getValue();

        //校验数据库表字段
        check_Database_For_Create_Order(new_order_id,inputParameterMap.get("user_id").getValue(),inputParameterMap.get("passenger_phone").getValue(),
                inputParameterMap.get("product_type_id").getValue(),inputParameterMap.get("car_type_id").getValue(),inputParameterMap.get("source").getValue());

        //订单id为刚下单成功返回的订单id
        TestParameter order_idParam = TestParameter.getTestParameter("order_id", new_order_id);
        //是否用户二次确认0
        TestParameter user_confirmed = TestParameter.getTestParameter("user_confirmed", "0");
        Map<String,TestParameter> inputParameterMap1 = new HashMap<>();
        inputParameterMap1.put("order_id",order_idParam);
        inputParameterMap1.put("user_confirmed",user_confirmed);

        //取消订单
        CaseExecutor.runTestCase("state_cancelOrder_success.xml",inputParameterMap1);
        //校验订单是否取消成功。
        check_Database_For_Cancel_Order(new_order_id);
    }

    /**
     * 测试目的：<br>
     * 确保可以成功在服务前用ERP取消订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.读取xml文件中的请求参数<br>
     * 2.调用createOrder()创建(ERP下单-本订本乘-预约用车-Young车型-系统选车下单)订单<br>
     * 3.检查service_order_id,user_id,passenger_phone,product_type_id,car_type_id字段在数据库service_order,service_order_ext中的存在<br>
     * 检查点：以上字段按照xml输入更新在数据库表中<br>
     * 4.读取state_cancelOrder_success.xml文件中的请求参数<br>
     * 5.调用cancelOrder()取消订单<br>
     * 6.校验订单是否取消成功。用service_order_id作为查询条件查询service_order表，查出的status=8表示取消成功<br>
     * <br>
     * 测试用例开发人员：苏静<br>
     */
    //@Test
    public void order_cancelOrder_shouldSucceed_beforeServiceAndCancelFromERP(){
        //本订本乘
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.SECOND_USER);
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id", rider.getUserId());
        MapUtil.put(inputParameterMap,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", rider.getPassengerName());
        //预约用车
        MapUtil.put(inputParameterMap,"product_type_id", ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR);
        //Young车型
        MapUtil.put(inputParameterMap,"car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        //系统选车下单
        MapUtil.put(inputParameterMap,"is_auto_dispatch", "1");
        //ERP下单
        MapUtil.put(inputParameterMap,"source", ParamValue.SOURCE_ERP);


        //创建(本订本乘-预约用车-Young车型-系统选车下单)订单，校验数据库字段，并删除测试数据
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParameterMap);
        new_order_id = order_id.getValue();

        //校验数据库表字段
        check_Database_For_Create_Order(new_order_id,inputParameterMap.get("user_id").getValue(),inputParameterMap.get("passenger_phone").getValue(),
                inputParameterMap.get("product_type_id").getValue(),inputParameterMap.get("car_type_id").getValue(),inputParameterMap.get("source").getValue());

        //订单id为刚下单成功返回的订单id
        TestParameter order_idParam = TestParameter.getTestParameter("order_id", new_order_id);
        //是否用户二次确认0
        TestParameter user_confirmed = TestParameter.getTestParameter("user_confirmed", "0");
        Map<String,TestParameter> inputParameterMap1 = new HashMap<>();
        inputParameterMap1.put("order_id",order_idParam);
        inputParameterMap1.put("user_confirmed",user_confirmed);

        //取消订单
        CaseExecutor.runTestCase("state_cancelOrder_success.xml",inputParameterMap1);
        //校验订单是否取消成功。
        check_Database_For_Cancel_Order(new_order_id);
    }

    /**
     * 测试目的：<br>
     * 确保可以在服务开始后成功取消订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.构造创建订单的参数<br>
     * 2.调用createOrder()创建订单<br>
     * 3.循环查询订单是否被接单成功<br>
     *   检查点：订单状态变为4，已接单状态<br>
     * 4.调用setDriverDepart和driveArrived接口，模拟司机到达开始服务<br>
     *   检查点：订单状态为服务中，status=6 <br>
     * 5.调用cancelOrder()取消订单<br>
     * 6.校验订单是否取消成功。<br>
     *   检查点：用service_order_id作为查询条件查询service_order表，查出的status=8表示取消成功<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    @Test
    public void order_cancelOrder_shouldSucceed_InService(){
        //创建订单并开始服务
        createOrderAndStartService();

        //构造取消订单的参数
        TestParameter order_idParam = TestParameter.getTestParameter("order_id", new_order_id);
        TestParameter user_confirmed = TestParameter.getTestParameter("user_confirmed", "0"); //是否用户二次确认0
        TestParameter return_min = TestParameter.getTestParameter("return_min","0"); //扣除低消
        Map<String,TestParameter> inputParameterMap1 = new HashMap<>();
        inputParameterMap1.put("order_id",order_idParam);
        inputParameterMap1.put("user_confirmed",user_confirmed);
        inputParameterMap1.put("return_min",return_min);

        //取消前先查询订单状态，应为服务中状态
        String status=Order.getStatus(new_order_id);
        Assert.assertEquals(Integer.parseInt(status) , OrderStatus.IN_SERVICE,"order status should be in service");

        //调用cancelOrder接口取消订单
        CaseExecutor.runTestCase("state_cancelOrder_success.xml",inputParameterMap1);

        //校验订单是否取消成功。
        check_Database_For_Cancel_Order(new_order_id);
    }

    /**
     * 测试目的：<br>
     * 确保可以在服务开始后强制取消订单成功<br>
     * <br>
     * 测试步骤：<br>
     * 1.构造创建订单的参数<br>
     * 2.调用createOrder()创建订单<br>
     * 3.循环查询订单是否被接单成功<br>
     *   检查点：订单状态变为4，已接单状态<br>
     * 4.调用setDriverDepart和driveArrived接口，模拟司机到达开始服务<br>
     *   检查点：订单状态为服务中，status=6（已接单）<br>
     * 5.调用cancelOrder()强制取消订单<br>
     * 6.校验订单是否取消成功。<br>
     *   检查点：用service_order_id作为查询条件查询service_order表，查出的status=8且reason_id=69(69代表强制取消)<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    @Test
    public void order_cancelOrder_shouldSucceed_forceCancelInService(){
        //创建订单并开始服务
        createOrderAndStartService();

        //构造取消订单的参数
        TestParameter order_idParam = TestParameter.getTestParameter("order_id", new_order_id);
        TestParameter user_confirmed = TestParameter.getTestParameter("user_confirmed", "0"); //是否用户二次确认0
        TestParameter return_min = TestParameter.getTestParameter("return_min","0"); //扣除低消
        TestParameter reason_id = TestParameter.getTestParameter("reason_id",ParamValue.FORCE_CANCEL_ORDER); //取消原因id ,强制取消
        Map<String,TestParameter> inputParameterMap1 = new HashMap<>();
        inputParameterMap1.put("order_id",order_idParam);
        inputParameterMap1.put("user_confirmed",user_confirmed);
        inputParameterMap1.put("return_min",return_min);
        inputParameterMap1.put("reason_id",reason_id);

        //取消前先查询订单状态，应为服务中状态
        String status=Order.getStatus(new_order_id);
        Assert.assertEquals(Integer.parseInt(status) , OrderStatus.IN_SERVICE,"order status should be in service");

        //调用cancelOrder接口取消订单
        CaseExecutor.runTestCase("state_cancelOrder_success.xml",inputParameterMap1);

        //校验订单是否取消成功，校验取消理由是否为强制取消
        check_Database_For_Cancel_Order(new_order_id);
    }

    /**
     * 测试目的：<br>
     * 确保订单在服务开始后,可从ERP上强制结束订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.构造创建订单的参数<br>
     * 2.调用createOrder()创建订单<br>
     * 3.循环查询订单是否被接单成功<br>
     *   检查点：订单状态变为4，已接单状态<br>
     * 4.调用setDriverDepart和driveArrived接口，模拟司机到达<br>
     *   检查点：订单状态为服务中，status=6（已接单）<br>
     * 5.调用endByHand接口强制取消订单<br>
     * 6.校验订单是否强制结束成功。<br>
     *   检查点：用service_order_id作为查询条件查询service_order表，查出的status为7<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    @Test
    public void state_endByHand_shouldSucceed_whenOrderInService(){
        // 手动结束订单，会触发订单疑议
        // 处理结果：测试结束后需要调整订单金额以完成订单服务，解除疑议
        needAdjustAmount = true;
        //创建订单并开始服务
        createOrderAndStartService();

        //构造结束订单的参数
        TestParameter order_idParam = TestParameter.getTestParameter("order_id", new_order_id);
        long now_time = (new Date()).getTime();
        TestParameter end_timeParam = TestParameter.getTestParameter("end_time", String.valueOf(now_time));
        TestParameter end_statusParam = TestParameter.getTestParameter("end_status", "1");
        TestParameter dependable_distanceParam = TestParameter.getTestParameter("dependable_distance", "1"); //强制结束时填写的实际公里数
        Map<String,TestParameter> inputParameterMap1 = new HashMap<>();
        inputParameterMap1.put("order_id",order_idParam);
        inputParameterMap1.put("end_time",end_timeParam);
        inputParameterMap1.put("end_status",end_statusParam);
        inputParameterMap1.put("dependable_distance",dependable_distanceParam);

        //取消前先查询订单状态，应为服务中状态
        String status=Order.getStatus(new_order_id);
        Assert.assertEquals(Integer.parseInt(status) , OrderStatus.IN_SERVICE,"order status should be in service");

        //调用endByHand接口强制结束订单
        CaseExecutor.runTestCase("state_endByHand.xml",inputParameterMap1);

        //校验订单是否强制结束成功
        check_Database_For_Finish_Order(new_order_id);
    }


    public void createOrderAndStartService(){
        //准备创建订单参数
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id", rider.getUserId());//本订本乘
        MapUtil.put(inputParameterMap,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParameterMap,"product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        MapUtil.put(inputParameterMap,"car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        MapUtil.put(inputParameterMap,"car_type","Young车型");
        MapUtil.put(inputParameterMap,"is_auto_dispatch", "1");
        MapUtil.put(inputParameterMap,"source", ParamValue.SOURCE_ANDROIDAPP);

        //调用createOrder接口创建订单
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParameterMap);
        new_order_id = order_id.getValue();

        //判断订单是否被接单
        boolean isAccepted = Order.hasDriverResponse(new_order_id);
        Assert.assertTrue(isAccepted,"accept order failed !!!");
        //接单成功则继续设置司机到达，服务开始
        Order.setDriverDepart(new_order_id);
        State.driverArrived(new_order_id);
        State.startTiming(new_order_id);
    }

    public void check_Database_For_Create_Order(String new_order_id,String user_id,String passenger_phone,String product_type_id,
                                                String car_type_id,String source){
        //check the table service_order
        String str_sql = "SELECT COUNT(*) FROM service_order WHERE service_order_id=" + new_order_id + " AND user_id= " + user_id
                + " AND passenger_phone= "+passenger_phone + " AND product_type_id= " + product_type_id + " AND car_type_id = " + car_type_id+ " AND source = " + source;
        String order_count = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql, DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count) > 0);

        //check the table service_order_ext
        String str_sql_ext = "SELECT COUNT(*) FROM service_order_ext WHERE service_order_id=" + new_order_id ;
        String order_count_ext = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql_ext,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count_ext) > 0);
    }

    public void check_Database_For_Cancel_Order(String new_order_id){
        //check status in table service_order
        String str_sql = "SELECT status FROM service_order WHERE service_order_id=" + new_order_id;
        String order_status = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql, DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_status) == OrderStatus.CANCELED);
        //检查reason_id
        str_sql = "SELECT reason_id FROM service_order WHERE service_order_id=" + new_order_id;
        String reason_id = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql, DbTableColumn.COUNT);
        if (!reason_id.equals("0")){
            //默认为0，不为0应为69（强制取消）
            Assert.assertEquals(reason_id , ParamValue.FORCE_CANCEL_ORDER);
        }
    }

    public void check_Database_For_Finish_Order(String new_order_id){
        //check status in table service_order
        String str_sql = "SELECT status FROM service_order WHERE service_order_id=" + new_order_id;
        String order_status = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql, DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_status) == OrderStatus.FINISHED);
    }

    @BeforeMethod
    public void init(){
        needAdjustAmount = false;
    }

    //处理未取消成功的订单
    @AfterMethod
    public void teardown(ITestResult test){
        CommonUtil.sleep(10000);
        log.info("================================TEARDOWN==================================");
        log.info("Case name is —— " + test.getName());
        TearDown.cancelAndDeleteOrder(new_order_id);
        if(needAdjustAmount){
            log.info("Need to adjust amount for order : " + new_order_id);
            TearDown.adjustAmountToCompleteOrder(new_order_id);
        }
    }
}
