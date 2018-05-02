package com.yongche.testcase.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.api.APIUtil;
import com.yongche.framework.core.*;
import com.yongche.framework.utils.*;
import com.yongche.framework.config.ConfigUtil;
import com.yongche.framework.utils.db.*;
import com.yongche.framework.api.order.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.util.*;


/**
 * 测试接口: state/createOrder
 */
public class CreateOrderTest {
    public static Logger log = LoggerFactory.getLogger(CreateOrderTest.class);
    private boolean isAccepted;//是否成功接单
    private String str_order_id ;//新创建的订单id

    @BeforeMethod
    public void init(){
        isAccepted=false;
    }
    /**
     * 测试目的：<br>
     * 可以成功创建接机订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.读取xml文件中的请求参数<br>
     * 2.调用createOrder() 创建订单<br>
     * 3.检查user_id,product_type_id,fixed_product_id,car_type_id字段在数据库service_order,service_order_ext中的存在<br>
     *   检查点：以上字段按照xml输入更新在数据库表中<br>
     * 4.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在50秒内变为已接单状态<br>
     * 5.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    @Test
    public void order_createOrder_airportPickup_shouldSucceed_withYoungCarType() throws Exception {
        //get parameters from xml
        Map<String, TestParameter> parameters = XMLUtil.getParameterFromXML("state_createOrder_airport_pickup.xml","state_create_order_airport_pickup_should_succeed_with_valid_parameters");
        String fixed_product_id = parameters.get("fixed_product_id").getValue();
        //构造请求参数。

        RiderInfo riderInfo = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id", riderInfo.getUserId());
        MapUtil.put(inputParameterMap,"passenger_phone", riderInfo.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", riderInfo.getPassengerName());
        MapUtil.put(inputParameterMap,"product_type_id", ParamValue.PRODUCT_TYPE_ID_AIRPORT_PICKUP);
        MapUtil.put(inputParameterMap,"car_type_id", ParamValue.CAR_TYPE_ID_COMFORTABLE);
        MapUtil.put(inputParameterMap,"area_code","PEK");
       // MapUtil.put(inputParameterMap,"fixed_product_id", ParamValue.FIXED_PRODUCT_ID_AIRPORT_PICK_UP_BJ);
        MapUtil.put(inputParameterMap,"start_position","北京首都国际机场");
        MapUtil.put(inputParameterMap,"start_address","北四环西路66号");
        MapUtil.put(inputParameterMap,"end_position","电子城.IT产业园");
        MapUtil.put(inputParameterMap,"end_address","北京市朝阳区酒仙桥北路甲10号");

        String car_type_id = inputParameterMap.get("car_type_id").getValue();

        //run createOrder
        TestParameter order_id = Order.createOrderByProductTypeId(inputParameterMap);
        str_order_id = order_id.getValue();

        String user_id = riderInfo.getUserId();
        String product_type_id = inputParameterMap.get("product_type_id").getValue();
        //check the database
        check_Database(str_order_id,user_id,product_type_id,fixed_product_id,car_type_id);
        //判断是否接单成功
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
    }

    /**
     * 测试目的：<br>
     * 可以成功创建送机订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.读取xml文件中的请求参数<br>
     * 2.调用createOrder() 创建订单<br>
     * 3.检查user_id,product_type_id,fixed_product_id ,car_type_id字段在数据库service_order,service_order_ext中的存在<br>
     *   检查点：以上字段按照xml输入更新在数据库表中<br>
     * 4.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在50秒内变为已接单状态<br>
     * 5.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    @Test
    public void order_createOrder_airportDropOff_shouldSucceed_withComfortableCarType(){
        //构造请求参数
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        Map<String,TestParameter> inputParams = MapUtil.getParameterMap("user_id", rider.getUserId());
        MapUtil.put(inputParams,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParams,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParams,"car_type_id",ParamValue.CAR_TYPE_ID_COMFORTABLE);
        MapUtil.put(inputParams,"car_type_ids",ParamValue.CAR_TYPE_ID_COMFORTABLE);
        MapUtil.put(inputParams,"car_type","舒适车型");//Young!  舒适车型
        MapUtil.put(inputParams,"product_type_id",ParamValue.PRODUCT_TYPE_ID_AIRPORT_DROP_OFF);
        MapUtil.put(inputParams,"fixed_product_id",ParamValue.FIXED_PRODUCT_ID_AIRPORT_DROP_OFF);
        MapUtil.put(inputParams,"start_position","电子城.IT产业园");
        MapUtil.put(inputParams,"start_address","北四环西路66号"); //北京市朝阳区酒仙桥北路甲10号
        MapUtil.put(inputParams,"end_position","北京首都国际机场");
        MapUtil.put(inputParams,"end_address","");
        String str_user_id = rider.getUserId();
        String str_product_type_id = inputParams.get("product_type_id").getValue();
        String str_fixed_product_id = inputParams.get("fixed_product_id").getValue();
        String str_car_type_id = inputParams.get("car_type_id").getValue();

        //run createOrder
        Map<String,TestParameter> order_info = CaseExecutor.runTestCase("state_createOrder_airport_dropOff.xml",inputParams);
        TestParameter order_id = order_info.get("order_id");
        str_order_id = order_id.getValue();

        //check the database
        check_Database(str_order_id,str_user_id,str_product_type_id,str_fixed_product_id,str_car_type_id);

        //循环查询订单是否被接单，超时时间单位:microsecond
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
    }

    /**
     * 测试目的：<br>
     * 可以成功创建接站订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.读取xml文件中的请求参数<br>
     * 2.在送机测试数据基础上构造接站测试数据<br>
     * 3.调用createOrder() 创建订单<br>
     * 4.检查user_id,product_type_id,fixed_product_id ,car_type_id字段在数据库service_order,service_order_ext中的存在<br>
     *   检查点：以上字段按照xml输入更新在数据库表中<br>
     * 5.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在50秒内变为已接单状态<br>
     * 6.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    @Test
    public void order_createOrder_trainStationPickup_shouldSucceed_withComfortableCarType() throws Exception {
        //create test data
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        Map<String,TestParameter> inputParams = MapUtil.getParameterMap("user_id", rider.getUserId());
        MapUtil.put(inputParams,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParams,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParams,"car_type_id",ParamValue.CAR_TYPE_ID_COMFORTABLE);
        MapUtil.put(inputParams,"car_type_ids",ParamValue.CAR_TYPE_ID_COMFORTABLE);
        MapUtil.put(inputParams,"car_type","舒适车型");
        MapUtil.put(inputParams,"product_type_id",ParamValue.PRODUCT_TYPE_ID_AIRPORT_PICKUP);
        MapUtil.put(inputParams,"fixed_product_id",ParamValue.FIXED_PRODUCT_ID_STATION_PICKUP);
        MapUtil.put(inputParams,"start_position","北京火车站");
        MapUtil.put(inputParams,"start_address","北京火车站");
        MapUtil.put(inputParams,"end_position","中关村SOHO大厦");
        MapUtil.put(inputParams,"end_address","中关村SOHO大厦");
        String str_user_id = rider.getUserId();
        String str_product_type_id = inputParams.get("product_type_id").getValue();
        String str_fixed_product_id = inputParams.get("fixed_product_id").getValue();
        String str_car_type_id = inputParams.get("car_type_id").getValue();

        //run createOrder
        Map<String,TestParameter> order_info = CaseExecutor.runTestCase("state_createOrder_airport_pickup.xml",inputParams);
        TestParameter order_id = order_info.get("order_id");
        str_order_id = order_id.getValue();

        //check the database
        check_Database(str_order_id,str_user_id,str_product_type_id,str_fixed_product_id,str_car_type_id);
        //判断是否接单成功
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
    }

    /**
     * 测试目的：<br>
     * 可以成功创建送站订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.读取xml文件中的请求参数<br>
     * 2.在送机测试数据基础上构造接站测试数据<br>
     * 3.调用createOrder() 创建订单<br>
     * 4.检查user_id,product_type_id,fixed_product_id ,car_type_id字段在数据库service_order,service_order_ext中的存在<br>
     *   检查点：以上字段按照xml输入更新在数据库表中<br>
     * 5.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在50秒内变为已接单状态<br>
     * 6.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    @Test
    public void order_createOrder_trainStationDropOff_shouldSucceed_withComfortableCarType() throws Exception {

        //create test data
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        Map<String,TestParameter> inputParams = MapUtil.getParameterMap("user_id", rider.getUserId());
        MapUtil.put(inputParams,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParams,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParams,"product_type_id",ParamValue.PRODUCT_TYPE_ID_AIRPORT_DROP_OFF);
        MapUtil.put(inputParams,"fixed_product_id",ParamValue.FIXED_PRODUCT_ID_STATION_DROPOFF);
        MapUtil.put(inputParams,"car_type_id",ParamValue.CAR_TYPE_ID_COMFORTABLE);
        MapUtil.put(inputParams,"car_type_ids",ParamValue.CAR_TYPE_ID_COMFORTABLE);
        MapUtil.put(inputParams,"start_position","中关村SOHO大厦");
        MapUtil.put(inputParams,"start_address","中关村SOHO大厦");
        MapUtil.put(inputParams,"end_position","北京火车站");
        MapUtil.put(inputParams,"end_address","北京火车站");
        String str_user_id = rider.getUserId();
        String str_product_type_id = inputParams.get("product_type_id").getValue();
        String str_fixed_product_id = inputParams.get("fixed_product_id").getValue();
        String car_type_id = inputParams.get("car_type_id").getValue();

        //run createOrder
        Map<String,TestParameter> order_info = CaseExecutor.runTestCase("state_createOrder_airport_dropOff.xml",inputParams);
        TestParameter order_id = order_info.get("order_id");
        str_order_id = order_id.getValue();

        //check the database
        check_Database(str_order_id,str_user_id,str_product_type_id,str_fixed_product_id,car_type_id);
        //判断是否接单成功
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
    }

    /**
     * 测试目的：<br>
     * 确保可以成功创建半日租订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.创建半日租订单<br>
     * 2.调用确认订单接口/state/confirm，扣除低消
     * 3.检查service_order_id,user_id,passenger_phone,product_type_id,car_type_id字段在数据库service_order,service_order_ext中的存在<br>
     *   检查点：以上字段按照xml输入更新在数据库表中<br>
     * 4.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在50秒内变为已接单状态<br>
     * 5.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     * <br>
     * 测试用例开发人员：罗文平<br>
     */
    @Test
    public void order_createOrder_shouldSucceed_withOrderHalfDayAndYoungType(){
        //构造请求参数
        long time_after_one_hour = System.currentTimeMillis()+2*60*60*1000;
        String time_after_one_hour_str = String.valueOf(time_after_one_hour/1000);
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.ONE_DAY_HALF_DAY_RENT);
        Map<String,TestParameter> inputParams = MapUtil.getParameterMap("user_id", rider.getUserId());
        MapUtil.put(inputParams,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParams,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParams,"expect_start_time",time_after_one_hour_str);//设置预计开始时间是当前时间之后的2小时
        MapUtil.put(inputParams,"car_type", "舒适车型");
        MapUtil.put(inputParams,"car_type_id",ParamValue.CAR_TYPE_ID_COMFORTABLE);
        MapUtil.put(inputParams,"flag", "317829677056");
        MapUtil.put(inputParams,"product_type_id",ParamValue.PRODUCT_TYPE_ID_RENTAL_HALF_DAY);
        MapUtil.put(inputParams,"fixed_product_id",ParamValue.FIXED_PRODUCT_ID_RENTAL_HALF_DAY);

        //创建订单
        String caseFile = "state_createOrder_rental_half_day.xml";
        Map<String,TestParameter> outputParamMap = CaseExecutor.runTestCase(caseFile,inputParams);
        String result = outputParamMap.get(ParamValue.RESPONSE_RESULT).getValue();
        JSONObject responseResult = null;
        try {
            responseResult = JsonUtil.getJSONObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject resultStr1 = responseResult.getJSONObject("result");
        JSONArray service_order_ids = resultStr1.getJSONArray("service_order_ids");
        str_order_id = service_order_ids.get(0).toString();
        log.info("str_order_id: " + str_order_id);

        //确认订单,扣除低消
        Map<String,TestParameter> inputParamMap_confirm = new HashMap<>();
        TestParameter service_order_id_testParam = TestParameter.getTestParameter("service_order_id",str_order_id);
        inputParamMap_confirm.put("service_order_id",service_order_id_testParam);
        CaseExecutor.runTestCase("state_confirm.xml",inputParamMap_confirm);

        String user_id = rider.getUserId();
        String product_type_id = inputParams.get("product_type_id").getValue();
        String fixed_product_id = inputParams.get("fixed_product_id").getValue();
        String car_type_id = inputParams.get("car_type_id").getValue();
        //检查数据库
        check_Database(str_order_id,user_id,product_type_id,fixed_product_id,car_type_id);

        //判断是否接单成功
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
    }

    /**
     * 测试目的：<br>
     * 确保可以成功创建日租订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.创建日租订单<br>
     * 2.调用确认订单接口/state/confirm，扣除低消
     * 3.检查service_order_id,user_id,passenger_phone,product_type_id,car_type_id字段在数据库service_order,service_order_ext中的存在<br>
     *   检查点：以上字段按照xml输入更新在数据库表中<br>
     * 4.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在50秒内变为已接单状态<br>
     * 5.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     * <br>
     * 测试用例开发人员：罗文平<br>
     */
    @Test
    public void order_createOrder_shouldSucceed_withOrderOneDayAndComfortableType(){
        //构造请求参数
        long time_after_one_hour = System.currentTimeMillis()+2*60*60*1000;
        String time_after_one_hour_str = String.valueOf(time_after_one_hour/1000);
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.ONE_DAY_HALF_DAY_RENT);
        Map<String,TestParameter> inputParams = MapUtil.getParameterMap("user_id", rider.getUserId());
        MapUtil.put(inputParams,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParams,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParams,"expect_start_time",time_after_one_hour_str);//设置预计开始时间是当前时间之后的2小时
        MapUtil.put(inputParams,"car_type_id",ParamValue.CAR_TYPE_ID_COMFORTABLE);
        MapUtil.put(inputParams,"car_type", "舒适型");
        MapUtil.put(inputParams,"flag", "317829677056");
        MapUtil.put(inputParams,"product_type_id",ParamValue.PRODUCT_TYPE_ID_RENTAL_ONE_DAY);
        MapUtil.put(inputParams,"fixed_product_id",ParamValue.FIXED_PRODUCT_ID_RENTAL_ONE_DAY);

        //创建订单
        String caseFile = "state_createOrder_rental_one_day.xml";
        Map<String,TestParameter> outputParamMap = CaseExecutor.runTestCase(caseFile,inputParams);
        String result = outputParamMap.get(ParamValue.RESPONSE_RESULT).getValue();
        JSONObject responseResult = null;
        try {
            responseResult = JsonUtil.getJSONObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject resultStr1 = responseResult.getJSONObject("result");
        JSONArray service_order_ids = resultStr1.getJSONArray("service_order_ids");
        str_order_id = service_order_ids.get(0).toString();
        log.info("str_order_id: " + str_order_id);

        //确认订单,扣除低消
        Map<String,TestParameter> inputParamMap_confirm = new HashMap<>();
        TestParameter service_order_id_testParam = TestParameter.getTestParameter("service_order_id",str_order_id);
        inputParamMap_confirm.put("service_order_id",service_order_id_testParam);
        CaseExecutor.runTestCase("state_confirm.xml",inputParamMap_confirm);

        String str_user_id = rider.getUserId();
        String str_product_type_id = inputParams.get("product_type_id").getValue();
        String str_fixed_product_id = inputParams.get("fixed_product_id").getValue();
        String car_type_id = inputParams.get("car_type_id").getValue();
        //检查数据库
        check_Database(str_order_id,str_user_id,str_product_type_id,str_fixed_product_id,car_type_id);

        //判断是否接单成功
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
    }

    /**
     * 测试目的：<br>
     * 确保可以成功创建(本订本乘-马上用车-Young车型-系统选车下单) 订单.<br>
     * <br>
     * 测试步骤：<br>
     * 1.读取xml文件中的请求参数<br>
     * 2.调用createOrder()创建(本订本乘-马上用车-Young车型-系统选车下单)订单<br>
     * 3.检查service_order_id,user_id,passenger_phone,product_type_id,car_type_id字段在数据库service_order,service_order_ext中的存在<br>
     *   检查点：以上字段按照xml输入更新在数据库表中<br>
     * 4.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在50秒内变为已接单状态<br>
     * 5.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     * <br>
     * 测试用例开发人员：苏静<br>
     */
    @Test
    public void order_createOrder_shouldSucceed_withOrderForSelfAndImmediateAndYoungTypeAndSystemDecision(){
        //本订本乘
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.SECOND_USER);
        Map<String,TestParameter> inputParams = MapUtil.getParameterMap("user_id", rider.getUserId());
        MapUtil.put(inputParams,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParams,"passenger_name", rider.getPassengerName());
        //马上用车
        MapUtil.put(inputParams,"product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        //Young车型
        MapUtil.put(inputParams,"car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        //系统选车下单
        MapUtil.put(inputParams,"is_auto_dispatch", "1");

        //创建订单
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParams);
        str_order_id = order_id.getValue();

        String product_type_id = inputParams.get("product_type_id").getValue();
        String car_type_id = inputParams.get("car_type_id").getValue();
        //个人用户下单查数据库
        check_Database_For_Create_Order(str_order_id,rider.getUserId(),rider.getCellphone(),product_type_id,car_type_id);
        //判断是否接单成功
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
    }

    /**
     * 测试目的：<br>
     * 确保可以成功创建(本订本乘-预约用车-Young车型-系统选车下单) 订单.<br>
     * <br>
     * 测试步骤：<br>
     * 1.读取xml文件中的请求参数<br>
     * 2.调用createOrder()创建(本订本乘-预约用车-Young车型-系统选车下单)订单<br>
     * 3.检查service_order_id,user_id,passenger_phone,product_type_id,car_type_id字段在数据库service_order,service_order_ext中的存在<br>
     *   检查点：以上字段按照xml输入更新在数据库表中<br>
     * 4.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在50秒内变为已接单状态<br>
     * 5.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     * <br>
     * 测试用例开发人员：苏静<br>
     */
    @Test
    public void order_createOrder_shouldSucceed_withBookingForSelfAndYoungTypeAndSystemDecision(){
        //本订本乘
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.SECOND_USER);
        Map<String,TestParameter> inputParams = MapUtil.getParameterMap("user_id", rider.getUserId());
        MapUtil.put(inputParams,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParams,"passenger_name", rider.getPassengerName());
        //预约用车
        MapUtil.put(inputParams,"product_type_id", ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR);
        //Young车型
        MapUtil.put(inputParams,"car_type_id", ParamValue.CAR_TYPE_ID_DELUXE);
        //系统选车下单
        MapUtil.put(inputParams,"is_auto_dispatch", "1");

        //创建(本订本乘-预约用车-Young车型-系统选车下单)订单，校验数据库字段，并删除测试数据
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParams);
        str_order_id = order_id.getValue();
        String product_type_id = inputParams.get("product_type_id").getValue();
        String car_type_id = inputParams.get("car_type_id").getValue();
        check_Database_For_Create_Order(str_order_id,rider.getUserId(),rider.getCellphone(),product_type_id,car_type_id);
        //判断是否接单成功
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
    }

    /**
     * 测试目的：<br>
     * 确保可以成功创建(企业有分组下单-本订本乘-马上用车-Young车型-系统选车下单) 订单.<br>
     * <br>
     * 测试步骤：<br>
     * 1.读取xml文件中的请求参数<br>
     * 2.调用createOrder()创建(企业有分组下单-本订本乘-马上用车-Young车型-系统选车下单)订单<br>
     * 3.检查service_order_id,corporate_id，corporate_dept_id，user_id,passenger_phone,product_type_id,car_type_id,corporate_id,
     * corporate_dept_id字段在数据库service_order,service_order_ext中的存在<br>日租
     * 检查点：以上字段按照xml输入更新在数据库表中<br>
     * 4.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     *   注意事项：企业用户下单时，该接口会报ret_code:12025错误，提示car_type_ids[] is empty.
     *             原因：由于app下单car_type_ids订单那边调过来的类型是数组，而web下单传过来的是个字符串。待修正。<br>
     * <br>
     * 测试用例开发人员：苏静<br>
     */
    //@Test
    public void order_createOrder_shouldSucceed_withCorporateWithGroupAndOrderForSelfAndImmediateAndYoungTypeAndSystemDecision(){
        //企业有分组下单
        CorporateInfo corp = CorporateInfo.getCorporateInfo(CorporateInfoConst.CORP_WITH_DEPT);
        Map<String,TestParameter> inputParams = MapUtil.getParameterMap("corporate_id", corp.getId());
        MapUtil.put(inputParams,"corporate_dept_id", corp.getDepartmentId());
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.CORPORATE_USER_WITH_GROUP);
        MapUtil.put(inputParams,"user_id", rider.getUserId());
        MapUtil.put(inputParams,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParams,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParams,"product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        MapUtil.put(inputParams,"car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        MapUtil.put(inputParams,"car_type_ids", ParamValue.CAR_TYPE_ID_YOUNG);
        MapUtil.put(inputParams,"is_auto_dispatch", "1");

        //创建(企业有分组下单-本订本乘-预约用车-Young车型-系统选车下单)订单，校验数据库字段，并删除测试数据
        createAndCheckOrder( inputParams);
    }

    /**
     * 测试目的：<br>
     * 确保可以成功创建(企业无分组下单-本订本乘-马上用车-Young车型-系统选车下单) 订单.<br>
     * <br>
     * 测试步骤：<br>
     * 1.读取xml文件中的请求参数<br>
     * 2.调用createOrder()创建(企业无分组下单-本订本乘-马上用车-Young车型-系统选车下单)订单<br>
     * 3.检查service_order_id,corporate_id，corporate_dept_id，user_id,passenger_phone,product_type_id,car_type_id,corporate_id,
     * corporate_dept_id字段在数据库service_order,service_order_ext中的存在<br>
     * 检查点：以上字段按照xml输入更新在数据库表中<br>
     * 4.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     *   注意事项：企业用户下单时，该接口会报ret_code:12025错误，提示car_type_ids[] is empty.
     *             原因：由于app下单car_type_ids订单那边调过来的类型是数组，而web下单传过来的是个字符串。待修正。<br>
     * <br>
     * 测试用例开发人员：苏静<br>
     */
    //@Test
    public void order_createOrder_shouldSucceed_withCorporateWithoutGroupAndOrderForSelfAndImmediateAndYoungTypeAndSystemDecision(){
        //企业无分组下单
        CorporateInfo corp = CorporateInfo.getCorporateInfo(CorporateInfoConst.CORP_WITHOUT_DEPT);
        Map<String,TestParameter> inputParams = MapUtil.getParameterMap("corporate_id", corp.getId());
        MapUtil.put(inputParams,"corporate_dept_id", corp.getDepartmentId());
        //本订本乘
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.CORPORATE_USER_WITHOUT_GROUP);
        MapUtil.put(inputParams,"user_id", rider.getUserId());
        MapUtil.put(inputParams,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParams,"passenger_name", rider.getPassengerName());
        //马上用车
        MapUtil.put(inputParams,"product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        //Young车型
        MapUtil.put(inputParams,"car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        MapUtil.put(inputParams,"car_type_ids", ParamValue.CAR_TYPE_ID_YOUNG);
        //系统选车下单
        MapUtil.put(inputParams,"is_auto_dispatch", "1");

        //创建(企业无分组下单-本订本乘-预约用车-Young车型-系统选车下单)订单，校验数据库字段，并删除测试数据
        createAndCheckOrder( inputParams );
    }

    /**
     * 测试目的：<br>
     * 确保可以企业账户同一账户同时下多个单。创建(企业有分组下单-本订本乘-马上用车-Young车型-系统选车下单) 订单.<br>
     * <br>
     * 测试步骤：<br>
     * 1.读取xml文件中的请求参数<br>
     * 2.调用createOrder()创建(企业有分组下单-本订本乘-马上用车-Young车型-系统选车下单)订单<br>
     * 3.再下一单<br>
     * 检查点：以上字段按照xml输入更新在数据库表中<br>
     * 4.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     *   注意事项：企业用户下单时，该接口会报ret_code:12025错误，提示car_type_ids[] is empty.
     *             原因：由于app下单car_type_ids订单那边调过来的类型是数组，而web下单传过来的是个字符串。待修正。<br>
     * <br>
     * 测试用例开发人员：苏静<br>
     */
    //@Test
    public void order_createOrder_shouldSucceed_withOneAccountForManyOrdersWithCorporateWithGroupAndOrderForSelfAndImmediateAndYoungTypeAndSystemDecision(){
        //企业有分组下单
        CorporateInfo corp = CorporateInfo.getCorporateInfo(CorporateInfoConst.CORP_WITH_DEPT);
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("corporate_id", corp.getId());
        MapUtil.put(inputParameterMap,"corporate_dept_id", corp.getDepartmentId());
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.CORPORATE_USER_WITH_GROUP);
        MapUtil.put(inputParameterMap,"user_id", rider.getUserId());
        MapUtil.put(inputParameterMap,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParameterMap,"product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        MapUtil.put(inputParameterMap,"fixed_product_id","0");
        MapUtil.put(inputParameterMap,"car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        MapUtil.put(inputParameterMap,"car_type_ids", ParamValue.CAR_TYPE_ID_YOUNG);
        MapUtil.put(inputParameterMap,"is_auto_dispatch", "1");

        //创建(企业有分组下单-本订本乘-预约用车-Young车型-系统选车下单)订单

        String first_order_id = Order.createOrderByProductTypeId(inputParameterMap).getValue();
        //判断第1个订单是否接单成功
        isAccepted = Order.hasDriverResponse(first_order_id);
        Assert.assertTrue(isAccepted,"accept first order failed!!!");
        isAccepted=false;

        //再下一单
        String second_order_id = Order.createOrderByProductTypeId(inputParameterMap).getValue();
        //判断第2个订单是否接单成功
        isAccepted = Order.hasDriverResponse(second_order_id);
        Assert.assertTrue(isAccepted,"accept second order failed!!!");

        String corporate_id = corp.getId();
        String corporate_dept_id = corp.getDepartmentId();
        String user_id = rider.getUserId();
        String product_type_id = inputParameterMap.get("product_type_id").getValue();
        String fixed_product_id = inputParameterMap.get("fixed_product_id").getValue();
        String str_sql = "SELECT COUNT(*) FROM service_order WHERE " +
                " service_order_id in ( " + first_order_id + " , " + second_order_id + " ) " + "AND corporate_id= " + corporate_id+
                " AND corporate_dept_id= " + corporate_dept_id +
                " AND user_id = " + user_id + " AND product_type_id= " + product_type_id +
                " AND fixed_product_id = " + fixed_product_id + " AND status>0 AND status not in ("+
                OrderStatus.FINISHED + "," + OrderStatus.CANCELED + ") ";

        String order_count = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count) == 2);

        //处理两个订单测试数据，第一个订单取消并删除，第二个订单给afterMethod处理
        str_order_id = second_order_id;
        //第一个订单取消并删除
        TestParameter param_order_id = TestParameter.getTestParameter("order_id",first_order_id);
        int current_status = Integer.parseInt(Order.getStatus(first_order_id));
        if(OrderStatus.CANCELED != current_status && OrderStatus.FINISHED != current_status){
            Order.cancelOrderById(param_order_id);
        }
    }

    /**
     * 测试目的：<br>
     * 确保可以成功创建(订乘分离-预约用车-豪华车型-手动选车下单) 订单.<br>
     * <br>
     * 测试步骤：<br>
     * 1.构建接口请求参数<br>
     * 2.调用createOrder()创建(订乘分离-预约用车-豪华车型-手动选车下单)订单<br>
     * 3.检查service_order_id,user_id,passenger_phone,product_type_id,car_type_id字段在数据库service_order,service_order_ext中的存在<br>
     *   检查点：以上字段按照构造的请求参数更新在数据库表中<br>
     * 4.查询接单司机列表，返回列表第一个司机信息<br>
     *   检查点：30秒内有司机抢单，即acceptcar接口能返回接单司机信息<br>
     * 5.调用app-api的order/decisiondriver接口，决策该司机接单<br>
     *   检查点：接口返回200，决策成功<br>
     * 6.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在90秒内变为已接单状态<br>
     * 7.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     * <br>
     * 测试用例开发人员：苏静<br>
     */
    @Test
    public void order_createOrder_shouldSucceed_withBookingForOthersAndDeluxeTypeAndManualChoose(){
        //参数构造
        RiderInfo riderUser = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        RiderInfo passenger = CommonUtil.getRider(RiderCategoryConst.SECOND_USER);
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id",riderUser.getUserId());//订乘分离
        MapUtil.put(inputParameterMap,"passenger_phone", passenger.getCellphone()); //订乘分离
        MapUtil.put(inputParameterMap,"passenger_name", passenger.getPassengerName());
        MapUtil.put(inputParameterMap,"product_type_id", ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR);
        MapUtil.put(inputParameterMap,"car_type_id",ParamValue.CAR_TYPE_ID_DELUXE);  //豪华车型
        MapUtil.put(inputParameterMap,"car_type_ids",ParamValue.CAR_TYPE_ID_DELUXE);
        MapUtil.put(inputParameterMap,"car_type","豪华车型");
        MapUtil.put(inputParameterMap,"is_asap","0");    //预约用车
        MapUtil.put(inputParameterMap,"is_auto_dispatch", "1");    //需要系统自动派单
        MapUtil.put(inputParameterMap,"has_custom_decision", "1");    //支持用户手动选车
        MapUtil.put(inputParameterMap,"is_need_manual_dispatch", "0");    //不需要手动派单
        MapUtil.put(inputParameterMap,"flag", "2");

        //创建(订乘分离-预约用车-舒适车型-手动选车下单)订单，校验数据库字段
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParameterMap);
        str_order_id = order_id.getValue();

        //个人用户下单查数据库
        check_Database_For_Create_Order(str_order_id,riderUser.getUserId(),passenger.getCellphone(),
                inputParameterMap.get("product_type_id").getValue(),inputParameterMap.get("car_type_id").getValue());

        //如果有司机接单则返回第一个接单司机id
        String driver_id = ClientAgent.getAcceptCar(str_order_id, riderUser.getUserId());

        //如果返回driver_d为-1 说明获取接单司机列表失败
        Assert.assertNotEquals(driver_id,"-1","get driver who accepted order failed!!!");

        //获取接单司机成功 则取出第一个司机的driver_id 决策订单给此司机
        boolean ret = ClientAgent.decisionDriver(str_order_id,driver_id);
        Assert.assertTrue(ret,"decision driver failed!!!");

        //判断是否接单成功
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
    }

    /**
     * 测试目的：<br>
     * 确保可以成功创建(本订本乘-马上用车-车型选择-舒适车型-手动选车下单) 订单.<br>
     * <br>
     * 测试步骤：<br>
     * 1.构建接口请求参数<br>
     * 2.调用createOrder()创建(本订本乘-马上用车-车型选择-舒适车型-手动选车下单)订单<br>
     * 3.检查service_order_id,user_id,passenger_phone,product_type_id,car_type_id字段在数据库service_order,service_order_ext中的存在<br>
     *   检查点：以上字段按照构造的请求参数更新在数据库表中<br>
     * 4.查询接单司机列表，返回列表第一个司机信息<br>
     *   检查点：30秒内有司机抢单，即acceptcar接口能返回接单司机信息<br>
     * 5.调用app-api的order/decisiondriver接口，决策该司机接单<br>
     *   检查点：接口返回200，决策成功<br>
     * 6.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在90秒内变为已接单状态<br>
     * 7.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     * <br>
     * 测试用例开发人员：苏静<br>
     */
    @Test
    public void order_createOrder_shouldSucceed_withOrderForSelfAndImmediateAndComfortableTypeAndManualChoose(){
        //构造请求参数
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id",rider.getUserId());//本订本乘
        MapUtil.put(inputParameterMap,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParameterMap,"car_type_id",ParamValue.CAR_TYPE_ID_COMFORTABLE);      //舒适车型
        MapUtil.put(inputParameterMap,"product_type_id",ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        MapUtil.put(inputParameterMap,"is_asap","1");    //马上用车
        MapUtil.put(inputParameterMap,"is_auto_dispatch", "1");    //需要系统自动派单
        MapUtil.put(inputParameterMap,"has_custom_decision", "1");    //支持用户手动选车
        MapUtil.put(inputParameterMap,"is_need_manual_dispatch", "0");    //不需要手动派单
        MapUtil.put(inputParameterMap,"flag", "2");//不支持系统决策


        //创建订单
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParameterMap);
        str_order_id = order_id.getValue();
        String product_type_id = inputParameterMap.get("product_type_id").getValue();
        String car_type_id = inputParameterMap.get("car_type_id").getValue();

        //校验数据库各个字段
        check_Database_For_Create_Order(str_order_id,rider.getUserId(),rider.getCellphone(),product_type_id,car_type_id);

        //循环30秒，每3秒请求一次接单司机列表，如果有司机接单则返回第一个接单司机id，无司机接单返回-1
        String driver_id = ClientAgent.getAcceptCar(str_order_id, rider.getUserId());

        //如果返回driver_d为-1 说明获取接单司机列表失败
        Assert.assertNotEquals(driver_id,"-1","get driver who accepted order failed!!!");

        //获取成功 派单给该司机
        boolean ret = ClientAgent.decisionDriver(str_order_id,driver_id);
        Assert.assertTrue(ret,"decision driver failed!!!");

        //判断是否接单成功
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
    }

    /**
     * 测试目的：<br>
     * 同一个用户可以同时下多个订单.<br>
     * <br>
     * 测试步骤：<br>
     * 1.构造创建订单参数
     * 2.调用createOrder()创建一个订单<br>
     * 3.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在90秒内变为已接单状态<br>
     * 4.再次调用createOrder()创建一个订单<br>
     * 5.循环查询第二个订单的状态，判断是否接单成功<br>
     *   检查点：订单在90秒内变为已接单状态<br>
     * 6.检查数据库<br>
     *   检查点：数据库中是否存在这两个订单，且订单的user_id,product_type_id,car_type_id字段在数据表service_order中的存在<br>
     *      检查两个新建的订单属于同一个user_id，且两个订单的状态字段status 不属于取消、结束和无效状态<br>
     * 7.取消两个订单<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    @Test
    public void order_createOrder_shouldSucceed_whenOneUserCreateSeveralOrders(){
        //构造创建订单参数
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.SECOND_USER);
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id",rider.getUserId());
        MapUtil.put(inputParameterMap,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParameterMap,"product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        MapUtil.put(inputParameterMap,"car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        MapUtil.put(inputParameterMap,"car_type","Young车型");
        MapUtil.put(inputParameterMap,"is_auto_dispatch", "1");
        String fixed_product_id = "0";

        //run createOrder 新建一个订单
        String first_order_id = Order.createOrderByProductTypeId(inputParameterMap).getValue();

        //判断第1个订单是否接单成功
        isAccepted = Order.hasDriverResponse(first_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
        isAccepted=false;

        //第二个订单换一种车型
        TestParameter car_type_id = TestParameter.getTestParameter("car_type_id", ParamValue.CAR_TYPE_ID_COMFORTABLE);
        TestParameter car_type = TestParameter.getTestParameter("car_type","舒适车型");
        inputParameterMap.put("car_type_id",car_type_id);
        inputParameterMap.put("car_type",car_type);

        //run createOrder 新建第二个订单
        String second_order_id = Order.createOrderByProductTypeId(inputParameterMap).getValue();

        //判断第2个订单是否接单成功
        isAccepted = Order.hasDriverResponse(second_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");

        String product_type_id = inputParameterMap.get("product_type_id").getValue();
        //校验上面两个新建的订单属于同一个用户，这两个订单关键字校验，两个单的状态都不是取消和结束状态
        String str_sql = "SELECT COUNT(*) FROM service_order WHERE " +
                " service_order_id in ( " + first_order_id + " , " + second_order_id + " ) " +
                " AND user_id = " + rider.getUserId() + " AND product_type_id= " + product_type_id +
                " AND fixed_product_id = " + fixed_product_id + " AND status>0 AND status not in ("+
                OrderStatus.FINISHED + "," + OrderStatus.CANCELED + ") ";
        String order_count = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count) == 2);

        //处理两个订单测试数据，第一个订单取消并删除，第二个订单给afterMethod处理
        str_order_id = second_order_id;
        //第一个订单取消并删除
        TestParameter param_order_id = TestParameter.getTestParameter("order_id",first_order_id);
        int current_status = Integer.parseInt(Order.getStatus(first_order_id));
        if(OrderStatus.CANCELED != current_status && OrderStatus.FINISHED != current_status){
            Order.cancelOrderById(param_order_id);
        }
        //TearDown.teardownForCreateOrder(first_order_id);
    }

    /**
     * 测试目的：<br>
     * 可以使用优惠券下单成功<br>
     * <br>
     * 测试步骤：<br>
     * 1.构造使用优惠券下单测试数据<br>
     * 2.调用创建优惠券接口给测试账号发一张优惠券<br>
     * 3.构造创建订单所需参数<br>
     * 4.调用createOrder()创建一个订单<br>
     *   检查点：订单的user_id,product_type_id,coupon_member_id字段在数据库service_order,service_order_ext中的存在<br>
     * 5.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在50秒内变为已接单状态<br>
     * 6.删除优惠券数据库中user_coupon_xxx表新插入的测试数据<br>
     * 7.删除数据库中service_order,service_order_ext和order_key_value表新插入的测试数据<br>
     * <br>
     * 测试用例开发人员：许杨<br>
     */
    @Test
    public void order_createOrder_shouldSucceed_whenUseCoupon(){
        //给用户创建优惠券
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        String user_id = rider.getUserId();
        //前提是要已存在一个优惠券模板，此用例的模板id为4460
        String coupon_list = ConfigUtil.getValue(ConfigConst.COUPON_INFO,"coupon_template_id");
        String comment = "auto_test";
        String operator_id = rider.getUserId();  //操作人id  大于0即可
        String coupon_id = Coupon.sendCoupon(user_id,coupon_list,operator_id,comment);
//        String coupon_id = Coupon.getFirstAvailableCoupon(user_id,ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,ParamValue.CAR_TYPE_ID_YOUNG);

        //构造创建订单参数
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id",rider.getUserId()); //本订本乘
        MapUtil.put(inputParameterMap,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParameterMap,"coupon_member_id",coupon_id);
        MapUtil.put(inputParameterMap,"product_type_id",ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        MapUtil.put(inputParameterMap,"car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        MapUtil.put(inputParameterMap,"car_type","Young车型");

        //创建订单
        str_order_id = Order.createOrderByProductTypeId(inputParameterMap).getValue();

        String product_type_id = inputParameterMap.get("product_type_id").getValue();
        //检查订单创建成功 订单表service_order表中有coupon_member_id字段
        String str_sql = "SELECT COUNT(*) FROM service_order WHERE service_order_id =" + str_order_id +
                " AND user_id = " + user_id + " AND product_type_id= " + product_type_id +
                " AND coupon_member_id = " + coupon_id;
        String order_count = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count) > 0);
        //检查service_order_ext中有记录
        str_sql = "SELECT COUNT(*) FROM service_order_ext WHERE service_order_id=" + str_order_id ;
        String order_count_ext = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count_ext) > 0);
        //判断是否接单成功
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
        APIUtil.afterAcceptOrder(str_order_id);

        //删除代金券数据库数据
        TearDown.teardownForCoupon(user_id,coupon_id);
    }

    /**
     * 测试目的：<br>
     * 司机改派.<br>
     * <br>
     * 测试步骤：<br>
     * 1.构造创建订单参数
     * 2.调用createOrder()创建一个订单<br>
     * 3.循环查询订单状态，判断是否接单成功<br>
     *   检查点：订单在90秒内变为已接单状态<br>
     * 4.调用clearCar()改派该订单
     * 5.循环查询订单的状态，判断是否接单成功<br>
     *   检查点：订单在90秒内变为已接单状态<br>
     * 6.检查数据库<br>
     *   检查点：数据库中是否存在这个订单，且订单的user_id,product_type_id,car_type_id字段在数据表service_order中的存在<br>
     *      检查订单driver_id,car_id属性，与第2次派单driver_id,car_id的属性一致<br>
     * 7.取消两个订单<br>
     * <br>
     * 测试用例开发人员：罗文平<br>
     */
    @Test
    public void order_createOrder_reDispatch_should_success(){
        //构造请求参数
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.FIRST_USER);
        Map<String,TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id",rider.getUserId());//本订本乘
        MapUtil.put(inputParameterMap,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParameterMap,"car_type_id",ParamValue.CAR_TYPE_ID_YOUNG);
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

        //改派
        State.clearCar(str_order_id,driverId);

        //判断是否有司机接单,获取改派后的driver_id,car_id
        ret = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(ret);
        String new_driver_id = Order.getFieldsByOrderId(str_order_id).get("driver_id").getValue();
        String new_car_id = Order.getFieldsByOrderId(str_order_id).get("car_id").getValue();
        log.info("改派后：driver id : " + new_driver_id + " car id :"+new_car_id);
        Assert.assertNotEquals(driverId,new_driver_id);
        Assert.assertNotEquals(carId,new_car_id);
        String product_type_id = inputParameterMap.get("product_type_id").getValue();
        String car_type_id = inputParameterMap.get("car_type_id").getValue();
        check_Database(str_order_id,rider.getUserId(),product_type_id,car_type_id,new_driver_id,new_car_id);
    }

    public void check_Database(String new_order_id, String user_id, String product_type_id, String car_type_id,String driver_id,String car_id){
        //check the table service_order
        String str_sql = "SELECT COUNT(*) FROM service_order WHERE service_order_id=" + new_order_id + " AND user_id= " + user_id
                + " AND product_type_id= " + product_type_id  + " AND car_type_id =  " + car_type_id
                +" And driver_id= "+ driver_id + " AND car_id=" + car_id;
        String order_count = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count) > 0);

        //check the table service_order_ext
        String str_sql_ext = "SELECT COUNT(*) FROM service_order_ext WHERE service_order_id=" + new_order_id ;
        String order_count_ext = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql_ext,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count_ext) > 0);
    }

    protected void createAndCheckOrder(Map<String, TestParameter> inputParameterMap) {
        //创建订单
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParameterMap);
        str_order_id = order_id.getValue();
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");

        //校验数据库表字段
        if(inputParameterMap.get("corporate_id")!=null && Integer.parseInt(inputParameterMap.get("corporate_id").getValue())>0)
        {
            //企业用户下单
            check_Database_For_Create_Order(str_order_id,inputParameterMap.get("corporate_id").getValue(),inputParameterMap.get("corporate_dept_id").getValue(),
                    inputParameterMap.get("user_id").getValue(),inputParameterMap.get("passenger_phone").getValue(),
                    inputParameterMap.get("product_type_id").getValue(),inputParameterMap.get("car_type_id").getValue());
        }
        else
        {
            //个人用户下单
            check_Database_For_Create_Order(str_order_id,inputParameterMap.get("user_id").getValue(),inputParameterMap.get("passenger_phone").getValue(),
                    inputParameterMap.get("product_type_id").getValue(),inputParameterMap.get("car_type_id").getValue());
        }
    }

    public void check_Database_For_Create_Order(String new_order_id,String user_id,String passenger_phone,String product_type_id,
                                                String car_type_id){
        //check the table service_order
        String str_sql = "SELECT COUNT(*) FROM service_order WHERE service_order_id=" + new_order_id + " AND user_id= " + user_id
                + " AND passenger_phone= "+passenger_phone + " AND product_type_id= " + product_type_id + " AND car_type_id = " + car_type_id;
        String order_count = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count) > 0);

        //check the table service_order_ext
        String str_sql_ext = "SELECT COUNT(*) FROM service_order_ext WHERE service_order_id=" + new_order_id ;
        String order_count_ext = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql_ext,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count_ext) > 0);
    }

    public void check_Database_For_Create_Order(String new_order_id,String corporate_id,String corporate_dept_id,String user_id,String passenger_phone,String product_type_id,
                                                String car_type_id){
        //check the table service_order
        String str_sql = "SELECT COUNT(*) FROM service_order WHERE service_order_id=" + new_order_id + " AND corporate_id= " + corporate_id
                +" AND corporate_dept_id= " + corporate_dept_id + " AND user_id= " + user_id
                + " AND passenger_phone= "+passenger_phone + " AND product_type_id= " + product_type_id + " AND car_type_id = " + car_type_id;
        String order_count = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count) > 0);

        //check the table service_order_ext
        String str_sql_ext = "SELECT COUNT(*) FROM service_order_ext WHERE service_order_id=" + new_order_id ;
        String order_count_ext = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql_ext,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count_ext) > 0);
    }

    public void check_Database(String new_order_id, String user_id, String product_type_id, String fixed_product_id , String car_type_id){
        //check the table service_order
        String str_sql = "SELECT COUNT(*) FROM service_order WHERE service_order_id=" + new_order_id + " AND user_id= " + user_id
                + " AND product_type_id= " + product_type_id + " AND fixed_product_id = " + fixed_product_id + " AND car_type_id =  " + car_type_id;
        String order_count = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count) > 0);

        /*
        String user_id_db = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql,DbTableColumn.SERVICE_ORDER_USER_ID);
        Assert.assertEquals(user_id,user_id_db);
        String product_type_id_db = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql,DbTableColumn.SERVICE_ORDER_PRODUCT_TYPE_ID);
        Assert.assertEquals(product_type_id,product_type_id_db);
        String fixed_product_id_db = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql,DbTableColumn.SERVICE_ORDER_FIXED_PRODUCT_ID);
        Assert.assertEquals(fixed_product_id,fixed_product_id_db);
        */

        //check the table service_order_ext
        String str_sql_ext = "SELECT COUNT(*) FROM service_order_ext WHERE service_order_id=" + new_order_id ;
        String order_count_ext = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql_ext,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count_ext) > 0);
    }

    @AfterMethod
    public void teardown(ITestResult test){
        CommonUtil.sleep(10000);
        if( null != str_order_id ){
            log.info("================================TEARDOWN==================================");
            log.info("Case name is —— " + test.getName());
            TearDown.cancelAndDeleteOrder(str_order_id);
        }

    }
}
