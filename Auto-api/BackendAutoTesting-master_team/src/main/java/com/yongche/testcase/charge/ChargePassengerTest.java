package com.yongche.testcase.charge;


import com.yongche.framework.api.APIUtil;
import com.yongche.framework.api.order.Combo;
import com.yongche.framework.api.order.Order;
import com.yongche.framework.core.*;
import com.yongche.framework.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import com.yongche.framework.api.charge.*;

/**
 * 测试预估价格API<BR>
 * http://zhengyun.dev-charge.yongche.org/test-platform/test.php <BR>
 * 计费公式: https://phab.yongche.org/F4656 <BR>
 * 补充说明:<BR>
 *     乘客端  预约和马上用车(随叫随到)属于非定价产品 不需要fixed_product_id<BR>
 *     乘客端  其它类型均为定价产品 需要fixed_product_id<BR>
 */
public class ChargePassengerTest {
    public static Logger log = LoggerFactory.getLogger(ChargePassengerTest.class);
    private String orderId;

    public void getPrice(String car_type_id,
                                String product_type_id,
                                String city,
                                String fixed_product_id){

        //下面的三个值必须用代码设置 才能保证不同API是基于同样的车型取数据
        Map<String,TestParameter> baseParamMap = new HashMap<>();
        MapUtil mapUtil = MapUtil.getInstance(baseParamMap);

        mapUtil.put("car_type_id",car_type_id);
        mapUtil.put("product_type_id",product_type_id);
        mapUtil.put("city",city);
        //mapUtil.put("start_time","1549479240");//用于调试夜间服务费

        //获取免费长途公里数
        int freeLongDistance = FreeDistance.getFreeDistance(baseParamMap);
        log.info("免费长途公里数 (米) :" + freeLongDistance);

        if(null != fixed_product_id){
            MapUtil.put(baseParamMap,"fixed_product_id",fixed_product_id);
            MapUtil.put(baseParamMap,"fix_pro_id",fixed_product_id);//在API getIntervalEstimateCost()中 fix_pro_id 代表 fixed_product_id
        }

        String product_type_class = Price.getProductTypeClass(product_type_id);
        MapUtil.put(baseParamMap,"product_type_class",product_type_class);

        //根据价格类型 获取基础价格信息
        EstimatePriceType priceType = EstimatePriceType.PASSENGER;
        Map<String,TestParameter> basePriceMap = Price.getPrice(priceType,baseParamMap);

        //获取系统的预估总价
        Map<String,TestParameter> estimateMap = Estimate.getIntervalEstimateCost(baseParamMap);

        //根据基础价格表和乘客价格预估公式 计算预估总价
        double calPrice = Estimate.getPassengerEstimateCost(basePriceMap,estimateMap,freeLongDistance);

        //校验系统预估价格与计算所得的价格是否在允许的差值范围内
        String baseFareStr = JsonUtil.getValueFromResult(estimateMap,car_type_id,"原始订单基本费用");
        double sysEstimatedPrice = Double.parseDouble(baseFareStr);

        AssertUtil.assertDiff(sysEstimatedPrice,calPrice,2);
    }

    /**
     * 测试订单完成后乘客端计费
     * @param car_type_id
     * @param product_type_id
     * @param city
     * @param fixed_product_id
     * @param reDispatch  TODO: implement redispatch
     * @param useCombo
     * @param useCoupon
     */
    public  void getPrice(String car_type_id,
                                String product_type_id,
                                String city,
                                String fixed_product_id,
                                boolean reDispatch, // TODO : implement redispatch
                                boolean useCombo,
                                boolean useCoupon) {

        //套包和优惠券不能同时使用
        Assert.assertFalse(useCoupon && useCombo , "参数输入错误，套包和优惠券不能同时使用");

        Map<String,TestParameter> inputParameterMap = Order.createInputParameterMap(RiderCategoryConst.FIRST_USER,
                product_type_id,fixed_product_id,city,car_type_id,useCoupon);

        orderId = Order.createOrderByProductTypeId(inputParameterMap).getValue();
        CommonUtil.sleep(10000);
        //刚创建的订单有可能为正在派单状态3，也有可能接单较快 为已接单状态4
        int status = Integer.parseInt(Order.getStatus(orderId));
        if (status!= OrderStatus.WAIT_FOR_ACCEPT && status!= OrderStatus.WAIT_FOR_DRIVER){
            Assert.assertTrue(false,"status should be 3 or 4!");
        }

        //如果当前为正在派单状态3 则继续判断是否接单成功
        if (status == OrderStatus.WAIT_FOR_ACCEPT ) {
            boolean isAccepted = Order.hasDriverResponse(orderId);
            Assert.assertTrue(isAccepted, "accept order failed!!!");
        }

        //接单后,且订单状态为4
        status = Integer.parseInt(Order.getStatus(orderId));
        Assert.assertEquals(status,OrderStatus.WAIT_FOR_DRIVER,"status should be4 - WAIT_FOR_DRIVER !");

        //使用套包
        if (useCombo) {
            Combo.updateCombo(orderId);
        }

        //完成订单并确认付款
        Map<OrderStage,Map<String, TestParameter>> responseAfterEndTiming = APIUtil.afterAcceptOrder(orderId);

        Map<String, TestParameter> feeDuringEndTiming = CommonUtil.OutputResult(responseAfterEndTiming.get(OrderStage.EndTiming));
        Map<String, TestParameter> feeDuringConfirmCharge = CommonUtil.OutputResult(responseAfterEndTiming.get(OrderStage.ConfirmCharge));
        // 应支付总金额
        double passengerFeeActual = MapUtil.getDouble(feeDuringConfirmCharge,"total_amount");
        log.info("检查用户应付金额");
        double passengerFeeExpected = FaresCalculator.getPassengerFare(orderId,feeDuringEndTiming);
        Assert.assertEquals(passengerFeeActual,passengerFeeExpected,"passenger fee to be charged is not correct");
        log.info("actual passenger fee : " + passengerFeeActual + " , expected passenger fee : " + passengerFeeExpected);
        log.info("Test passed");
        CommonUtil.sleep(2000);

    }

    /**
     * 测试目的：<br>
     * 乘客能获取正确获取 非定价产品-预约用车-Young车型 的预估价格<br>
     *
     * 测试步骤：<br>
     * 1.获取免费长途公里数<br>
     * 2.获取计算预估计价格需要的基础价格数据 如:起步价,最低消费,每公里费用,每分钟费用等<br>
     * 3.获取系统的预估总价<br>
     * 4.根据基础价格表和乘客价格预估公式 计算预估总价<br>
     *   检查点：预约用车-Young车型 系统预估总价和计算所有预估总价差值在2以内<br>
     *
     * 测试用例开发人员:马毅<br>
     */
    @Test
    public  void charge_getPrice_hourRent_shouldSucceed_youngCar(){
        getPrice(ParamValue.CAR_TYPE_ID_YOUNG,
                ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR,
                ParamValue.CITY_BEIJING,null);
    }

    /**
     * 测试目的：<br>
     * 乘客能获取正确获取 定价产品-送机-Young车型 的预估价格<br>
     *
     * 测试步骤：<br>
     * 1.获取免费长途公里数<br>
     * 2.获取计算预估计价格需要的基础价格数据 如:起步价,最低消费,每公里费用,每分钟费用等<br>
     * 3.获取系统的预估总价<br>
     * 4.根据基础价格表和乘客价格预估公式 计算预估总价<br>
     *   检查点：送机-Young车型 系统预估总价和计算所有预估总价差值在2以内<br>
     *
     * 测试用例开发人员:马毅<br>
     */
    @Test
    public  void charge_getPrice_airportDropOff_shouldSucceed_youngCar(){
        getPrice(ParamValue.CAR_TYPE_ID_YOUNG,
                ParamValue.PRODUCT_TYPE_ID_AIRPORT_DROP_OFF,
                ParamValue.CITY_BEIJING,
                ParamValue.FIXED_PRODUCT_ID_AIRPORT_DROP_OFF);
    }

    //@Test
    public  void charge_getPrice_asap_shouldSucceed_luxuryCar(){
        getPrice(ParamValue.CAR_TYPE_ID_LUXURY,
                ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,
                ParamValue.CITY_BEIJING,null);
    }

    //@Test
    public  void charge_getPrice_oneDayRent_shouldSucceed_comfortableCar(){
        getPrice(ParamValue.CAR_TYPE_ID_COMFORTABLE,
                ParamValue.PRODUCT_TYPE_ID_RENTAL_ONE_DAY,
                ParamValue.CITY_BEIJING,
                ParamValue.FIXED_PRODUCT_ID_RENTAL_ONE_DAY);
    }

    /**
     * 测试目的：<br>
     * 非定价产品-立即用车-舒适车型（一口价范围内）订单结束后， 使用优惠券的乘客计费正确<br>
     *
     * 测试步骤：<br>
     * 1.给用户发放优惠券<br>
     * 2.创建订单 （确保ERP-基础数据管理-司机一口价配置管理-时租-马上用车， 舒适车型为启用状态）<br>
     * 3.正常结束订单<br>
     * 4.获取订单乘客端最后一张快照<br>
     * 5.计算乘客应付金额<br>
     *     检查点：乘客应付金额与实付金额相等
     *
     * 测试用例开发人员:戴宝春<br>
     */
    @Test
    public void charge_withCoupon_AfterOrderEnd_shouldSucceed_comfortableCar_ASAP_FixedFee(){
        log.info("===测试用例：非定价产品-立即用车-舒适车型（一口价范围内）订单结束后， 使用优惠券的乘客计费正确===");
        final boolean reDispatch = false;
        final boolean useCombo = false; // 是否使用套包
        final boolean useCoupon = !useCombo; // 是否使用优惠券。优惠券和套包不可同时使用


        getPrice(ParamValue.CAR_TYPE_ID_COMFORTABLE,
                ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,
                "bj",
                null,
                reDispatch,
                useCombo,
                useCoupon);
    }

    /**
     * 测试目的：<br>
     * 非定价产品-立即用车-豪华车型（非一口价）订单结束后， 使用优惠券的乘客计费正确<br>
     *
     * 测试步骤：<br>
     * 1.创建订单 （确保ERP-基础数据管理-司机一口价配置管理-时租-马上用车， 豪华车型为禁止状态）<br>
     * 2.司机接单后给用户发放套包<br>
     * 3.正常结束订单<br>
     * 4.获取订单乘客端最后一张快照<br>
     * 5.计算乘客应付金额<br>
     *     检查点：乘客应付金额与实付金额相等
     *
     * 测试用例开发人员:戴宝春<br>
     */
    @Test
    public void charge_withCoupon_AfterOrderEnd_shouldSucceed_Deluxe_ASAP_NonFixedFee(){
        log.info("===测试用例：非定价产品-立即用车-豪华车型（非一口价）订单结束后， 使用优惠券的乘客计费正确===");
        final boolean reDispatch = false;
        final boolean useCombo = false;      // 是否使用套包
        final boolean useCoupon = !useCombo; // 是否使用优惠券。优惠券和套包不可同时使用

        getPrice(ParamValue.CAR_TYPE_ID_DELUXE,
                ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,
                "bj",
                null,
                reDispatch,
                useCombo,
                useCoupon);
    }

    /**
     * 测试目的：<br>
     * 非定价产品-立即用车-舒适车型（一口价范围内）订单结束后， 使用套包的乘客计费正确<br>
     *
     * 测试步骤：<br>
     * 1.创建订单 （确保ERP-基础数据管理-司机一口价配置管理-时租-马上用车， 舒适车型为启用状态）<br>
     * 2.司机接单后给用户发放套包<br>
     * 3.正常结束订单<br>
     * 4.获取订单乘客端最后一张快照<br>
     * 5.计算乘客应付金额<br>
     *     检查点：乘客应付金额与实付金额相等
     *
     * 测试用例开发人员:戴宝春<br>
     */
    @Test
    public void charge_withCombo_AfterOrderEnd_shouldSucceed_comfortableCar_ASAP_FixedFee(){
        log.info("===测试用例：非定价产品-立即用车-舒适车型（一口价范围内）订单结束后， 使用套包的乘客计费正确===");
        final boolean reDispatch = false;
        final boolean useCombo = true; // 是否使用套包
        final boolean useCoupon = !useCombo; // 是否使用优惠券。优惠券和套包不可同时使用


        getPrice(ParamValue.CAR_TYPE_ID_COMFORTABLE,
                ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,
                "bj",
                null,
                reDispatch,
                useCombo,
                useCoupon);
    }

    /**
     * 测试目的：<br>
     * 非定价产品-立即用车-豪华车型（非一口价）订单结束后， 使用套包的乘客计费正确<br>
     *
     * 测试步骤：<br>
     * 1.给用户发放套包<br>
     * 2.创建订单 （确保ERP-基础数据管理-司机一口价配置管理-时租-马上用车， Young车型为禁止状态）<br>
     * 3.正常结束订单<br>
     * 4.获取订单乘客端最后一张快照<br>
     * 5.计算乘客应付金额<br>
     *     检查点：乘客应付金额与实付金额相等
     *
     * 测试用例开发人员:戴宝春<br>
     */
    @Test
    public void charge_withCombo_AfterOrderEnd_shouldSucceed_Young_ASAP_NonFixedFee(){
        log.info("===测试用例：非定价产品-立即用车-Young车型（非一口价）订单结束后， 使用套包的乘客计费正确===");
        final boolean reDispatch = false;
        final boolean useCombo = true; // 是否使用套包
        final boolean useCoupon = !useCombo; // 是否使用优惠券。优惠券和套包不可同时使用

        getPrice(ParamValue.CAR_TYPE_ID_YOUNG,
                ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,
                "bj",
                null,
                reDispatch,
                useCombo,
                useCoupon);
    }

    @BeforeMethod
    public void init(){
        orderId = null;
    }

    @AfterMethod
    public void tearDown(){
        if ( null != orderId ){
            log.info("Tear Down: 如果订单状态不为结束或取消 取消订单释放司机");
            int current_status = Integer.parseInt(Order.getStatus(orderId));
            //如果状态不为结束或取消 取消订单释放司机
            if(OrderStatus.CANCELED != current_status && OrderStatus.FINISHED != current_status){
                Order.cancelOrderById(new TestParameter("order_id",orderId));
            }
            log.info("Order id: " + orderId);
        }
    }

}
