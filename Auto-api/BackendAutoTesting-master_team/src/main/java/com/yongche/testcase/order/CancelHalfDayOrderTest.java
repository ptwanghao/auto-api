package com.yongche.testcase.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.api.order.Order;
import com.yongche.framework.api.payment.Account;
import com.yongche.framework.core.*;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.MapUtil;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;


public class CancelHalfDayOrderTest {

    public static Logger log = LoggerFactory.getLogger(CancelHalfDayOrderTest.class);
    private boolean isAccepted;//是否成功接单
    private String str_order_id ;//创建的订单id
    private String pay_amount;//低消金额
    private String min_cancel_time;//免费时长
    private String account_id;//账户ID
    private int account_amount_first;//账户余额
    TestParameter expect_start_time;

    @BeforeMethod
    public void init(){
        isAccepted=false;
        str_order_id="0";
    }

    /**
     * 测试目的：<br>
     * 在时长内取消半日租订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.构造创建订单的参数<br>
     * 2.调用createOrder()创建订单<br>
     * 3.循环查询订单是否被接单成功<br>
     *   检查点：订单状态变为4，已接单状态<br>
     * 4.获取当前时间，判断当前时间是否在时长内，如果在时长内则取消订单，扣除低消；如果不在时长内，则计算等待时间，等到达到时间后取消订单
     * 5.调用cancelOrder()取消订单<br>
     *   检查点：检查初始账户余额-取消订单后账户余额是否等于低消金额
     * 6.校验订单是否取消成功<br>
     *   检查点：调用getStatus接口查询订单状态，查出的status=8表示取消成功<br>
     * <br>
     * 测试用例开发人员：罗文平<br>
     */
    @Test
    public void order_cancelHalfDayOrder_shouldSucceed_beforeMinCancelTime() throws Exception{
        createOrder_rentalHalfDayOrder();
        long now_time = System.currentTimeMillis()/1000;//当前时间，单位秒
        long expect_start_time_long = Long.parseLong(expect_start_time.getValue());
        long interval = expect_start_time_long - now_time;
        log.info("距离出发时间还有"+interval+"秒");
        if(interval>Long.parseLong(min_cancel_time)){
            log.info("在免费时长内，等待一段时间");
            Thread.sleep((interval-Long.parseLong(min_cancel_time)+10)*1000);
            log.info("到达时长内，取消订单，不返还低消");
        }

        //构造取消订单的参数
        Map<String,TestParameter> inputParameterMap1 = new HashMap<>();
        MapUtil.put(inputParameterMap1,"order_id",str_order_id);
        MapUtil.put(inputParameterMap1,"user_confirmed","0");
        MapUtil.put(inputParameterMap1,"is_cancel_charge", "1");
        MapUtil.put(inputParameterMap1,"charge_amount", pay_amount+"00");

        //调用cancelOrder接口取消订单
        CaseExecutor.runTestCase("state_cancelOrder_success.xml",inputParameterMap1);
        account_id = Order.getAccountIdByOrderId(str_order_id);
        String account_amount = Account.getAccountAmount(account_id).getValue();
        int balance = account_amount_first-Integer.parseInt(account_amount)/100;

        log.info(" 账号ID:"+account_id);
        log.info("未扣低消前账户余额（元）:"+account_amount_first);
        log.info("扣除低消后账户余额（元）:"+(Integer.parseInt(account_amount))/100);
        log.info("账户差额（元）:"+balance);

        Assert.assertEquals(String.valueOf(balance),pay_amount);

        //校验订单是否取消成功
        check_OrderStatus_For_Cancel_Order(str_order_id);
    }

    /**
     * 测试目的：<br>
     * 在时长外取消半日租订单<br>
     * <br>
     * 测试步骤：<br>
     * 1.构造创建订单的参数<br>
     * 2.调用createOrder()创建订单<br>
     * 3.循环查询订单是否被接单成功<br>
     *   检查点：订单状态变为4，已接单状态<br>
     * 4.获取当前时间，判断当前时间是否在免费时长内，如果在时长内则取消订单，返还低消<br>
     * 5.调用cancelOrder()取消订单<br>
     *   检查点：检查取消订单后账户余额与初始账户余额是否一致
     * 6.校验订单是否取消成功<br>
     *   检查点：调用getStatus接口查询订单状态，查出的status=8表示取消成功<br>
     * <br>
     * 测试用例开发人员：罗文平<br>
     */
    @Test
    public void order_cancelHalfDayOrder_shouldSucceed_afterMinCancelTime() throws Exception{
        createOrder_rentalHalfDayOrder();
        long now_time = System.currentTimeMillis()/1000;//当前时间，单位秒
        long expect_start_time_long = Long.parseLong(expect_start_time.getValue());
        long interval = expect_start_time_long - now_time;
        log.info("距离出发时间还有"+interval+"秒");
        if(interval>Long.parseLong(min_cancel_time)){
            log.info("在免费时长内，取消订单，返还低消");
        }

        Map<String,TestParameter> inputParameterMap1 = new HashMap<>();
        MapUtil.put(inputParameterMap1,"order_id",str_order_id);
        MapUtil.put(inputParameterMap1,"user_confirmed","0");
        MapUtil.put(inputParameterMap1,"return_min","1");

        //调用cancelOrder接口取消订单
        CaseExecutor.runTestCase("state_cancelOrder_success.xml",inputParameterMap1);
        String account_id = Order.getAccountIdByOrderId(str_order_id);
        String account_amount = Account.getAccountAmount(account_id).getValue();

        log.info(" 账号ID:"+account_id);
        log.info("账户初始余额（元）:"+account_amount_first);
        log.info("时长外取消订单后，账户余额（元）:"+(Integer.parseInt(account_amount))/100);

        Assert.assertEquals(String.valueOf(account_amount_first*100),account_amount);

        //校验订单是否取消成功
        check_OrderStatus_For_Cancel_Order(str_order_id);
    }

    public void createOrder_rentalHalfDayOrder(){
        //构建创建订单的输入参数
        Map<String,TestParameter> inputParam = new HashMap<>();
        long time_after_one_hour = System.currentTimeMillis()+61*60*1000;
        String time_after_one_hour_str = String.valueOf(time_after_one_hour/1000);
        expect_start_time = TestParameter.getTestParameter("expect_start_time",time_after_one_hour_str);//设置预计开始时间是当前时间之后的61分钟
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.ONE_DAY_HALF_DAY_RENT);
        MapUtil.put(inputParam,"user_id", rider.getUserId());    //本订本乘
        MapUtil.put(inputParam,"passenger_phone", rider.getCellphone());
        MapUtil.put(inputParam,"passenger_name", rider.getPassengerName());
        MapUtil.put(inputParam,"expect_start_time",time_after_one_hour_str);
        MapUtil.put(inputParam,"car_type_id",ParamValue.CAR_TYPE_ID_COMFORTABLE);
        MapUtil.put(inputParam,"car_type","舒适车型");
        MapUtil.put(inputParam,"flag","317829677056");
        MapUtil.put(inputParam,"product_type_id",ParamValue.PRODUCT_TYPE_ID_RENTAL_HALF_DAY);
        MapUtil.put(inputParam,"fixed_product_id",ParamValue.FIXED_PRODUCT_ID_RENTAL_HALF_DAY);

        //创建订单
        String caseFile = "state_createOrder_rental_half_day.xml";
        Map<String,TestParameter> outputParamMap = CaseExecutor.runTestCase(caseFile,inputParam);
        String service_order_ids = outputParamMap.get("order_id").getValue();
        JSONArray orderList = JSONArray.fromObject(service_order_ids);
        str_order_id = String.valueOf(orderList.get(0));//如果返回多个order_id，默认取第一个
        pay_amount = outputParamMap.get("pay_amount").getValue();
        min_cancel_time = outputParamMap.get("min_cancel_time").getValue();
        account_amount_first = Integer.valueOf(outputParamMap.get("account_amount").getValue());

        //确认订单,扣除低消
        Map<String,TestParameter> inputParamMap_confirm = new HashMap<>();
        MapUtil.put(inputParamMap_confirm,"service_order_id",str_order_id);
        CaseExecutor.runTestCase("state_confirm.xml",inputParamMap_confirm);

        //判断是否接单成功
        isAccepted = Order.hasDriverResponse(str_order_id);
        Assert.assertTrue(isAccepted,"accept order failed!!!");
    }

    public void check_OrderStatus_For_Cancel_Order(String str_order_id){
        //check status
        String order_status = Order.getStatus(str_order_id);
        Assert.assertTrue(Integer.parseInt(order_status) == OrderStatus.CANCELED);
    }

    //处理未取消成功的订单
    @AfterMethod
    public void teardown(ITestResult test){
        CommonUtil.sleep(10000);
        log.info("================================TEARDOWN==================================");
        log.info("Case name is —— " + test.getName());
        TearDown.cancelAndDeleteOrder(str_order_id);
    }
}
