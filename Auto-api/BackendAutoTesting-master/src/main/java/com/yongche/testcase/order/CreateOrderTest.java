package com.yongche.testcase.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.*;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.XMLUtil;
import com.yongche.framework.utils.db.*;
import com.yongche.framework.api.order.*;
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
    private String str_order_id;//新创建的订单id

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
        String user_id = parameters.get("user_id").getValue();
        String product_type_id = parameters.get("product_type_id").getValue();
        String fixed_product_id = parameters.get("fixed_product_id").getValue();
        String car_type_id = parameters.get("car_type_id").getValue();

        //run createOrder
        TestParameter order_id = Order.createOrderByProductTypeId(ParamValue.PRODUCT_TYPE_ID_AIRPORT_PICKUP);
        str_order_id = order_id.getValue();

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
        //get parameters from xml
        Map<String, TestParameter> parameters = XMLUtil.getParameterFromXML("state_createOrder_airport_dropOff.xml","state_create_order_airport_dropOff_should_succeed_with_valid_parameters");
        String user_id = parameters.get("user_id").getValue();
        String product_type_id = parameters.get("product_type_id").getValue();
        String fixed_product_id = parameters.get("fixed_product_id").getValue();
        String car_type_id = parameters.get("car_type_id").getValue();

        //run createOrder
        TestParameter order_id = Order.createOrderByProductTypeId(ParamValue.PRODUCT_TYPE_ID_AIRPORT_DROP_OFF);
        str_order_id = order_id.getValue();

        //check the database
        check_Database(str_order_id,user_id,product_type_id,fixed_product_id,car_type_id);

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
        //get parameters from xml，case train_station_pickup can share the same parameters with case airport_dropOff
        Map<String, TestParameter> parameters = XMLUtil.getParameterFromXML("state_createOrder_airport_dropOff.xml","state_create_order_airport_dropOff_should_succeed_with_valid_parameters");
        String user_id = parameters.get("user_id").getValue();
        String car_type_id = parameters.get("car_type_id").getValue();

        //create test data
        Map<String, TestParameter> inputParams = new HashMap<>();
        TestParameter product_type_id = new TestParameter("product_type_id",ParamValue.PRODUCT_TYPE_ID_AIRPORT_PICKUP);
        TestParameter fixed_product_id = new TestParameter("fixed_product_id",ParamValue.FIXED_PRODUCT_ID_STATION_PICKUP);
        TestParameter start_position = new TestParameter("start_position","北京西火车站");
        TestParameter start_address = new TestParameter("start_address","北京西火车站");
        TestParameter end_position = new TestParameter("end_position","海龙大厦");
        TestParameter end_address = new TestParameter("end_address","中关村大街1号-1506-g");
        inputParams.put("product_type_id",product_type_id);
        inputParams.put("fixed_product_id",fixed_product_id);
        inputParams.put("start_position",start_position);
        inputParams.put("start_address",start_address);
        inputParams.put("end_position",end_position);
        inputParams.put("end_address",end_address);
        String str_product_type_id = product_type_id.getValue();
        String str_fixed_product_id = fixed_product_id.getValue();

        //run createOrder
        Map<String,TestParameter> order_info = CaseExecutor.runTestCase("state_createOrder_airport_dropOff.xml",inputParams);
        TestParameter order_id = order_info.get("order_id");
        str_order_id = order_id.getValue();

        //check the database
        check_Database(str_order_id,user_id,str_product_type_id,str_fixed_product_id,car_type_id);
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
        //get parameters from xml，case train_station_pickup can share the same parameters with case airport_dropOff
        Map<String, TestParameter> parameters = XMLUtil.getParameterFromXML("state_createOrder_airport_dropOff.xml","state_create_order_airport_dropOff_should_succeed_with_valid_parameters");
        String user_id = parameters.get("user_id").getValue();
        String car_type_id = parameters.get("car_type_id").getValue();

        //create test data
        Map<String, TestParameter> inputParams = new HashMap<>();
        TestParameter product_type_id = new TestParameter("product_type_id",ParamValue.PRODUCT_TYPE_ID_AIRPORT_DROP_OFF);
        TestParameter fixed_product_id = new TestParameter("fixed_product_id",ParamValue.FIXED_PRODUCT_ID_STATION_DROPOFF);
        TestParameter start_position = new TestParameter("start_position","中关村SOHO大厦");
        TestParameter start_address = new TestParameter("start_address","中关村SOHO大厦");
        TestParameter end_position = new TestParameter("end_position","北京火车站");
        TestParameter end_address = new TestParameter("end_address","北京火车站");
        inputParams.put("product_type_id",product_type_id);
        inputParams.put("fixed_product_id",fixed_product_id);
        inputParams.put("start_position",start_position);
        inputParams.put("start_address",start_address);
        inputParams.put("end_position",end_position);
        inputParams.put("end_address",end_address);
        String str_product_type_id = product_type_id.getValue();
        String str_fixed_product_id = fixed_product_id.getValue();

        //run createOrder
        Map<String,TestParameter> order_info = CaseExecutor.runTestCase("state_createOrder_airport_dropOff.xml",inputParams);
        TestParameter order_id = order_info.get("order_id");
        str_order_id = order_id.getValue();

        //check the database
        check_Database(str_order_id,user_id,str_product_type_id,str_fixed_product_id,car_type_id);
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
     * 2.取消订单<br>
     */
    //@Test
    public void createOrder_HalfDay(){
        TestParameter order_id =  Order.createOrderByProductTypeId(ParamValue.PRODUCT_TYPE_ID_RENTAL_HALF_DAY);
        //TODO: add comfirm()
        Order.cancelOrderById(order_id);
    }

    /**
     * 测试目的：<br>
     * 确保可以成功创建日租订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.创建半租订单<br>
     * 2.取消订单<br>
     */
   // @Test
    public void createOrder_OneDay(){
        TestParameter order_id =  Order.createOrderByProductTypeId(ParamValue.PRODUCT_TYPE_ID_RENTAL_ONE_DAY);
        //TODO: add comfirm()
        Order.cancelOrderById(order_id);
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
        TestParameter user_id = TestParameter.getTestParameter("user_id", "13024677");
        TestParameter passenger_phone = TestParameter.getTestParameter("passenger_phone", "16801355462");
        //马上用车
        TestParameter product_type_id = TestParameter.getTestParameter("product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        //Young车型
        TestParameter car_type_id = TestParameter.getTestParameter("car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        //系统选车下单
        TestParameter is_auto_dispatch = TestParameter.getTestParameter("is_auto_dispatch", "1");

        Map<String,TestParameter> inputParameterMap = new HashMap<>();
        inputParameterMap.put("user_id",user_id);
        inputParameterMap.put("passenger_phone",passenger_phone);
        inputParameterMap.put("product_type_id",product_type_id);
        inputParameterMap.put("car_type_id",car_type_id);
        inputParameterMap.put("is_auto_dispatch",is_auto_dispatch);

        //创建订单
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParameterMap);
        str_order_id = order_id.getValue();

        //个人用户下单查数据库
        check_Database_For_Create_Order(str_order_id,user_id.getValue(),passenger_phone.getValue(),product_type_id.getValue(),car_type_id.getValue());
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
        TestParameter user_id = TestParameter.getTestParameter("user_id", "13024677");
        TestParameter passenger_phone = TestParameter.getTestParameter("passenger_phone", "16801355462");
        //预约用车
        TestParameter product_type_id = TestParameter.getTestParameter("product_type_id", ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR);
        //Young车型
        TestParameter car_type_id = TestParameter.getTestParameter("car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        //系统选车下单
        TestParameter is_auto_dispatch = TestParameter.getTestParameter("is_auto_dispatch", "1");

        Map<String,TestParameter> inputParameterMap = new HashMap<>();
        inputParameterMap.put("user_id",user_id);
        inputParameterMap.put("passenger_phone",passenger_phone);
        inputParameterMap.put("product_type_id",product_type_id);
        inputParameterMap.put("car_type_id",car_type_id);
        inputParameterMap.put("is_auto_dispatch",is_auto_dispatch);

        //创建(本订本乘-预约用车-Young车型-系统选车下单)订单，校验数据库字段，并删除测试数据
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParameterMap);
        str_order_id = order_id.getValue();
        check_Database_For_Create_Order(str_order_id,user_id.getValue(),passenger_phone.getValue(),product_type_id.getValue(),car_type_id.getValue());
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
     * corporate_dept_id字段在数据库service_order,service_order_ext中的存在<br>
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
        TestParameter corporate_id = TestParameter.getTestParameter("corporate_id", "26020768");
        TestParameter corporate_dept_id = TestParameter.getTestParameter("corporate_dept_id", "1920");
        //本订本乘
        TestParameter user_id = TestParameter.getTestParameter("user_id", "26021162");
        TestParameter passenger_phone = TestParameter.getTestParameter("passenger_phone", "16801355300");
        //马上用车
        TestParameter product_type_id = TestParameter.getTestParameter("product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        //Young车型
        TestParameter car_type_id = TestParameter.getTestParameter("car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        TestParameter car_type_ids = TestParameter.getTestParameter("car_type_ids", ParamValue.CAR_TYPE_ID_YOUNG);
        //系统选车下单
        TestParameter is_auto_dispatch = TestParameter.getTestParameter("is_auto_dispatch", "1");

        Map<String,TestParameter> inputParameterMap = new HashMap<>();
        inputParameterMap.put("product_type_id",product_type_id);
        inputParameterMap.put("car_type_id",car_type_id);
        inputParameterMap.put("car_type_ids",car_type_ids);
        inputParameterMap.put("is_auto_dispatch",is_auto_dispatch);
        inputParameterMap.put("corporate_id",corporate_id);
        inputParameterMap.put("corporate_dept_id",corporate_dept_id);
        inputParameterMap.put("user_id",user_id);
        inputParameterMap.put("passenger_phone",passenger_phone);

        //创建(企业有分组下单-本订本乘-预约用车-Young车型-系统选车下单)订单，校验数据库字段，并删除测试数据
        createAndCheckOrder( inputParameterMap);
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
        TestParameter corporate_id = TestParameter.getTestParameter("corporate_id", "26020768");
        TestParameter corporate_dept_id = TestParameter.getTestParameter("corporate_dept_id", "0");
        //本订本乘
        TestParameter user_id = TestParameter.getTestParameter("user_id", "26020774");
        TestParameter passenger_phone = TestParameter.getTestParameter("passenger_phone", "16801355100");
        //马上用车
        TestParameter product_type_id = TestParameter.getTestParameter("product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        //Young车型
        TestParameter car_type_id = TestParameter.getTestParameter("car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        TestParameter car_type_ids = TestParameter.getTestParameter("car_type_ids", ParamValue.CAR_TYPE_ID_YOUNG);
        //系统选车下单
        TestParameter is_auto_dispatch = TestParameter.getTestParameter("is_auto_dispatch", "1");

        Map<String,TestParameter> inputParameterMap = new HashMap<>();
        inputParameterMap.put("product_type_id",product_type_id);
        inputParameterMap.put("car_type_id",car_type_id);
        inputParameterMap.put("car_type_ids",car_type_ids);
        inputParameterMap.put("is_auto_dispatch",is_auto_dispatch);
        inputParameterMap.put("corporate_id",corporate_id);
        inputParameterMap.put("corporate_dept_id",corporate_dept_id);
        inputParameterMap.put("user_id",user_id);
        inputParameterMap.put("passenger_phone",passenger_phone);

        //创建(企业无分组下单-本订本乘-预约用车-Young车型-系统选车下单)订单，校验数据库字段，并删除测试数据
        createAndCheckOrder( inputParameterMap);
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
        TestParameter corporate_id = TestParameter.getTestParameter("corporate_id", "26020768");
        TestParameter corporate_dept_id = TestParameter.getTestParameter("corporate_dept_id", "1920");
        //本订本乘
        TestParameter user_id = TestParameter.getTestParameter("user_id", "26021162");
        TestParameter passenger_phone = TestParameter.getTestParameter("passenger_phone", "16801355300");
        //马上用车
        TestParameter product_type_id = TestParameter.getTestParameter("product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        //Young车型
        TestParameter car_type_id = TestParameter.getTestParameter("car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        TestParameter car_type_ids = TestParameter.getTestParameter("car_type_ids", ParamValue.CAR_TYPE_ID_YOUNG);
        //系统选车下单
        TestParameter is_auto_dispatch = TestParameter.getTestParameter("is_auto_dispatch", "1");

        Map<String,TestParameter> inputParameterMap = new HashMap<>();
        inputParameterMap.put("product_type_id",product_type_id);
        inputParameterMap.put("car_type_id",car_type_id);
        inputParameterMap.put("car_type_ids",car_type_ids);
        inputParameterMap.put("is_auto_dispatch",is_auto_dispatch);
        inputParameterMap.put("corporate_id",corporate_id);
        inputParameterMap.put("corporate_dept_id",corporate_dept_id);
        inputParameterMap.put("user_id",user_id);
        inputParameterMap.put("passenger_phone",passenger_phone);

        //创建(企业有分组下单-本订本乘-预约用车-Young车型-系统选车下单)订单
        String order_id = Order.createOrderByProductTypeId(inputParameterMap).getValue();

        //再下一单
        String second_order_id = Order.createOrderByProductTypeId(inputParameterMap).getValue();

        String str_sql = "SELECT COUNT(*) FROM service_order WHERE  corporate_id= " + corporate_id.getValue()
                +" AND corporate_dept_id= " + corporate_dept_id.getValue() + " AND user_id= " + user_id.getValue()
                + " AND passenger_phone= "+passenger_phone.getValue() + " AND product_type_id= " + product_type_id.getValue() + " AND car_type_id = " + car_type_id.getValue() + " AND status = 2";
        String order_count = DBUtil.getDataFromLastRow(ConfigConst.YC_ORDER,str_sql,DbTableColumn.COUNT);
        Assert.assertTrue(Integer.parseInt(order_count) == 2);

        //删除订单
        //TearDown.teardownForCreateOrder(order_id);
        //TearDown.teardownForCreateOrder(second_order_id);
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
        TestParameter user_id = TestParameter.getTestParameter("user_id", "26016450");    //订乘分离
        TestParameter passenger_phone = TestParameter.getTestParameter("passenger_phone", "16888800000");
        TestParameter passenger_name = TestParameter.getTestParameter("passenger_name", "test-dispatch-16888800000");
        TestParameter product_type_id = TestParameter.getTestParameter("product_type_id", ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR);

        TestParameter car_type_id = TestParameter.getTestParameter("car_type_id",ParamValue.CAR_TYPE_ID_DELUXE);  //豪华车型
        TestParameter car_type_ids = TestParameter.getTestParameter("car_type_ids",ParamValue.CAR_TYPE_ID_DELUXE);
        TestParameter car_type = TestParameter.getTestParameter("car_type","豪华车型");
        TestParameter is_asap = TestParameter.getTestParameter("is_asap","0");    //预约用车
        TestParameter is_auto_dispatch = TestParameter.getTestParameter("is_auto_dispatch", "1");    //需要系统自动派单
        TestParameter has_custom_decision = TestParameter.getTestParameter("has_custom_decision", "1");    //支持用户手动选车
        TestParameter is_need_manual_dispatch = TestParameter.getTestParameter("is_need_manual_dispatch", "0");    //不需要手动派单
        TestParameter flag = TestParameter.getTestParameter("flag", "2");

        Map<String,TestParameter> inputParameterMap = new HashMap<>();
        inputParameterMap.put("user_id",user_id);
        inputParameterMap.put("passenger_phone",passenger_phone);
        inputParameterMap.put("passenger_name",passenger_name);
        inputParameterMap.put("product_type_id",product_type_id);
        inputParameterMap.put("car_type_id",car_type_id);
        inputParameterMap.put("car_type_ids",car_type_ids);
        inputParameterMap.put("car_type",car_type);
        inputParameterMap.put("is_asap",is_asap);
        inputParameterMap.put("is_auto_dispatch",is_auto_dispatch);
        inputParameterMap.put("has_custom_decision",has_custom_decision);
        inputParameterMap.put("is_need_manual_dispatch",is_need_manual_dispatch);
        inputParameterMap.put("flag",flag);

        //创建(订乘分离-预约用车-舒适车型-手动选车下单)订单，校验数据库字段
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParameterMap);
        str_order_id = order_id.getValue();

        //个人用户下单查数据库
        check_Database_For_Create_Order(str_order_id,user_id.getValue(),passenger_phone.getValue(),product_type_id.getValue(),car_type_id.getValue());

        //如果有司机接单则返回第一个接单司机id
        String driver_id = ClientAgent.getAcceptCar(str_order_id, user_id.getValue());

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
        TestParameter user_id = TestParameter.getTestParameter("user_id", "26016450");    //本订本乘
        TestParameter passenger_phone = TestParameter.getTestParameter("passenger_phone", "16888800000");
        TestParameter passenger_name = TestParameter.getTestParameter("passenger_name", "test-dispatch-16888800000");
        TestParameter car_type_id = TestParameter.getTestParameter("car_type_id",ParamValue.CAR_TYPE_ID_COMFORTABLE);      //舒适车型
        TestParameter product_type_id = TestParameter.getTestParameter("product_type_id",ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        TestParameter is_asap = TestParameter.getTestParameter("is_asap","1");    //马上用车
        TestParameter is_auto_dispatch = TestParameter.getTestParameter("is_auto_dispatch", "1");    //需要系统自动派单
        TestParameter has_custom_decision = TestParameter.getTestParameter("has_custom_decision", "1");    //支持用户手动选车
        TestParameter is_need_manual_dispatch = TestParameter.getTestParameter("is_need_manual_dispatch", "0");    //不需要手动派单
        TestParameter flag = TestParameter.getTestParameter("flag", "2");//不支持系统决策

        Map<String,TestParameter> inputParameterMap = new HashMap<>();
        inputParameterMap.put("passenger_phone",passenger_phone);
        inputParameterMap.put("user_id",user_id);
        inputParameterMap.put("passenger_name",passenger_name);
        inputParameterMap.put("car_type_id",car_type_id);
        inputParameterMap.put("product_type_id",product_type_id);
        inputParameterMap.put("is_asap",is_asap);
        inputParameterMap.put("is_auto_dispatch",is_auto_dispatch);
        inputParameterMap.put("has_custom_decision",has_custom_decision);
        inputParameterMap.put("is_need_manual_dispatch",is_need_manual_dispatch);
        inputParameterMap.put("flag",flag);

        //创建订单
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParameterMap);
        str_order_id = order_id.getValue();

        //校验数据库各个字段
        check_Database_For_Create_Order(str_order_id,user_id.getValue(),passenger_phone.getValue(),product_type_id.getValue(),car_type_id.getValue());

        //循环30秒，每3秒请求一次接单司机列表，如果有司机接单则返回第一个接单司机id，无司机接单返回-1
        String driver_id = ClientAgent.getAcceptCar(str_order_id, user_id.getValue());

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
        TestParameter user_id = TestParameter.getTestParameter("user_id", "13024677");
        TestParameter passenger_phone = TestParameter.getTestParameter("passenger_phone", "16801355462");
        TestParameter product_type_id = TestParameter.getTestParameter("product_type_id", ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        TestParameter car_type_id = TestParameter.getTestParameter("car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        TestParameter car_type = TestParameter.getTestParameter("car_type","Young车型");
        TestParameter is_auto_dispatch = TestParameter.getTestParameter("is_auto_dispatch", "1");
        Map<String,TestParameter> inputParameterMap = new HashMap<>();
        inputParameterMap.put("user_id",user_id);
        inputParameterMap.put("passenger_phone",passenger_phone);
        inputParameterMap.put("product_type_id",product_type_id);
        inputParameterMap.put("car_type_id",car_type_id);
        inputParameterMap.put("car_type",car_type);
        inputParameterMap.put("is_auto_dispatch",is_auto_dispatch);
        String fixed_product_id = "0";

        //run createOrder 新建一个订单
        String first_order_id = Order.createOrderByProductTypeId(inputParameterMap).getValue();

        //判断第1个订单是否接单成功
        isAccepted = Order.hasDriverResponse(first_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
        isAccepted=false;

        //第二个订单换一种车型
        car_type_id = TestParameter.getTestParameter("car_type_id", ParamValue.CAR_TYPE_ID_COMFORTABLE);
        car_type = TestParameter.getTestParameter("car_type","舒适车型");
        inputParameterMap.put("car_type_id",car_type_id);
        inputParameterMap.put("car_type",car_type);

        //run createOrder 新建第二个订单
        String second_order_id = Order.createOrderByProductTypeId(inputParameterMap).getValue();

        //判断第2个订单是否接单成功
        isAccepted = Order.hasDriverResponse(second_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");

        //校验上面两个新建的订单属于同一个用户，这两个订单关键字校验，两个单的状态都不是取消和结束状态
        String str_sql = "SELECT COUNT(*) FROM service_order WHERE " +
                " service_order_id in ( " + first_order_id + " , " + second_order_id + " ) " +
                " AND user_id = " + user_id.getValue() + " AND product_type_id= " + product_type_id.getValue() +
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
        String user_id = "26016450";
        String coupon_list = "4460:1"; //前提是要已存在一个优惠券模板，此用例的模板id为4460
        String comment = "auto_test";
        String operator_id = "99999";  //操作人id  大于0即可
        String coupon_id = Coupon.sendCoupon(user_id,coupon_list,operator_id,comment);

        //构造创建订单参数
        Map<String,TestParameter> inputParameterMap = new HashMap<>();
        TestParameter coupon_member_id = TestParameter.getTestParameter("coupon_id",coupon_id);
        TestParameter userId = TestParameter.getTestParameter("user_id", user_id);
        TestParameter passenger_phone = TestParameter.getTestParameter("passenger_phone", "16888800000");
        TestParameter passenger_name = TestParameter.getTestParameter("passenger_name", "test-dispatch-16888800000");
        TestParameter product_type_id = TestParameter.getTestParameter("product_type_id",ParamValue.PRODUCT_TYPE_ID_IMMEDIATE);
        TestParameter car_type_id = TestParameter.getTestParameter("car_type_id", ParamValue.CAR_TYPE_ID_YOUNG);
        TestParameter car_type = TestParameter.getTestParameter("car_type","Young车型");
        inputParameterMap.put("coupon_member_id",coupon_member_id);
        inputParameterMap.put("product_type_id",product_type_id);
        inputParameterMap.put("user_id",userId);
        inputParameterMap.put("passenger_phone",passenger_phone);
        inputParameterMap.put("passenger_name",passenger_name);
        inputParameterMap.put("car_type_id",car_type_id);
        inputParameterMap.put("car_type",car_type);

        //创建订单
        str_order_id = Order.createOrderByProductTypeId(inputParameterMap).getValue();

        //检查订单创建成功 订单表service_order表中有coupon_member_id字段
        String str_sql = "SELECT COUNT(*) FROM service_order WHERE service_order_id =" + str_order_id +
                " AND user_id = " + user_id + " AND product_type_id= " + product_type_id.getValue() +
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

        //删除代金券数据库数据
        TearDown.teardownForCoupon(user_id,coupon_id);
    }

    protected void createAndCheckOrder(Map<String, TestParameter> inputParameterMap) {
        //创建订单
        TestParameter order_id =  Order.createOrderByProductTypeId(inputParameterMap);
        String new_order_id = order_id.getValue();

        //校验数据库表字段
        if(inputParameterMap.get("corporate_id")!=null && Integer.parseInt(inputParameterMap.get("corporate_id").getValue())>0)
        {
            //企业用户下单
            check_Database_For_Create_Order(new_order_id,inputParameterMap.get("corporate_id").getValue(),inputParameterMap.get("corporate_dept_id").getValue(),
                    inputParameterMap.get("user_id").getValue(),inputParameterMap.get("passenger_phone").getValue(),
                    inputParameterMap.get("product_type_id").getValue(),inputParameterMap.get("car_type_id").getValue());
        }
        else
        {
            //个人用户下单
            check_Database_For_Create_Order(new_order_id,inputParameterMap.get("user_id").getValue(),inputParameterMap.get("passenger_phone").getValue(),
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
        log.info("================================TEARDOWN==================================");
        log.info("Case name is —— " + test.getName());
        TearDown.cancelAndDeleteOrder(str_order_id);
    }
}
