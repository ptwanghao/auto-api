package com.yongche.testcase.charge;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.api.APIUtil;
import com.yongche.framework.api.charge.*;
import com.yongche.framework.api.order.*;
import com.yongche.framework.core.*;
import com.yongche.framework.utils.AssertUtil;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.JsonUtil;
import com.yongche.framework.utils.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ChargeDriverTest {
    public static Logger log = LoggerFactory.getLogger(ChargeDriverTest.class);
    private String orderId;

    public void getDriverPrice(String product_type_id,
                               int driver_system_bidding,
                               Map<String, TestParameter> orderInputParameterMap,
                               boolean reDispatch) {

        //构造公共请求参数
        RiderInfo riderInfo = CommonUtil.getRider(RiderCategoryConst.SECOND_USER);
        if (null == orderInputParameterMap) {
            orderInputParameterMap = new HashMap<>();
        }
        MapUtil.put(orderInputParameterMap, "user_id", riderInfo.getUserId());
        MapUtil.put(orderInputParameterMap, "passenger_phone", riderInfo.getCellphone());
        MapUtil.put(orderInputParameterMap, "passenger_name", riderInfo.getPassengerName());

        //1.===创建订单===
        TestParameter orderPara = Order.createOrderByProductTypeId(product_type_id, orderInputParameterMap);
        orderId = orderPara.getValue();

        final int RETRY_INTERVAL_IN_SECOND = 10; // second
        final int RETRY_TIMES = 6;

        if (reDispatch) {
            //判断是否有司机接单
            boolean ret = Order.hasDriverResponse(orderId);
            Assert.assertTrue(ret);

            //司机已接单  获取司机ID
            String driverId = Order.getFieldsByOrderId(orderId).get("driver_id").getValue();
            log.info("driver id : " + driverId);

            //改派
            State.clearCar(orderId, driverId);

            //判断是否有司机接单
            ret = Order.hasDriverResponse(orderId);
            Assert.assertTrue(ret);
        }
        //orderId = "6371656001865532595";

        // 返回的estimate_snap里service_order_id字段不为零，才会含有正确的订单预估时间，预估里程数和预估夜间服务时间，用于后面检查
        Map<String, TestParameter> paraMap = Order.getFieldsByOrderId(orderId, RETRY_INTERVAL_IN_SECOND, RETRY_TIMES);
        paraMap.remove(ParamValue.RESPONSE_RESULT);
        paraMap.remove("driver_id");
        paraMap.remove("car_id");

        if (driver_system_bidding == 0) {
            MapUtil.put(paraMap, "flag", "0");//确保是非一口价的Flag 并且不会受下浮系数影响
            driver_system_bidding = 1;
        } else {
            MapUtil.put(paraMap, "flag", "1099511627776");//一口价的Flag
            MapUtil.put(paraMap, "driver_system_bidding", String.valueOf(driver_system_bidding));
            driver_system_bidding = driver_system_bidding + 1;
        }

        //2.===调用API 获取司机端价格===
        MapUtil.put(paraMap, "service_order_id", orderId);
        Map<String, TestParameter> priceMap = Estimate.getDriverEstimateCostFromExt(paraMap);
        String car_type_ids = paraMap.get("car_type_ids").getValue();
        String price = JsonUtil.getValueFromResult(priceMap, car_type_ids, "订单原始金额");

        //"易到补贴":0,"佣金总金额":499.6,"高速费":0,"停车费":0,"其他费用":0
        String subsidy = JsonUtil.getValueFromResult(priceMap, car_type_ids, "易到补贴");
        String expressWayFee = JsonUtil.getValueFromResult(priceMap, car_type_ids, "高速费");
        String parkFee = JsonUtil.getValueFromResult(priceMap, car_type_ids, "停车费");
        String otherFee = JsonUtil.getValueFromResult(priceMap, car_type_ids, "其他费用");

        log.info("订单原始金额:" + price);
        log.info("car_type_ids: " + car_type_ids);

        //3.===获取免费长途公里数===
        Map<String, TestParameter> baseParamMap = new HashMap<>();

        //下面的三个值必须用代码设置 才能保证不同API是基于同样的车型取数据
        //String product_type_id = paraMap.get("product_type_id").getValue();
        MapUtil.put(baseParamMap, "car_type_id", car_type_ids);
        MapUtil.put(baseParamMap, "product_type_id", product_type_id);
        MapUtil.put(baseParamMap, "city", paraMap.get("city").getValue());

        int freeLongDistance = FreeDistance.getFreeDistance(baseParamMap);


        //4.===获取司机计价基础数据===
        String fixed_product_id = paraMap.get("fixed_product_id").getValue();
        MapUtil.put(baseParamMap, "fixed_product_id", fixed_product_id);

        String product_type_class = Price.getProductTypeClass(product_type_id);
        MapUtil.put(baseParamMap, "product_type_class", product_type_class);


        //5.====根据价格类型 获取基础价格信息====
        EstimatePriceType priceType = EstimatePriceType.ORDER;
        Map<String, TestParameter> basePriceMap = Price.getPrice(priceType, baseParamMap);

        int distance = MapUtil.getInt(priceMap, "预估距离");
        MapUtil.put(basePriceMap, "distance", String.valueOf(distance));
        log.info("预估距离 " + distance);

        int time_length = MapUtil.getInt(priceMap, "预估时间");
        MapUtil.put(basePriceMap, "time_length", String.valueOf(time_length));
        log.info("预估时间 (秒) :" + time_length);

        int night_time_length = MapUtil.getInt(priceMap, "预估夜间时间");
        MapUtil.put(basePriceMap, "night_time_length", String.valueOf(night_time_length));
        log.info("预估夜间时间 (分钟) :" + night_time_length);

        MapUtil.put(basePriceMap, "free_long_distance", String.valueOf(freeLongDistance));
        log.info("免费长途公里数 (米) :" + freeLongDistance);

        MapUtil.put(basePriceMap, "subsidy", subsidy);
        log.info("易到补贴 " + subsidy);

        MapUtil.put(basePriceMap, "expressWayFee", expressWayFee);
        log.info("高速费 " + expressWayFee);

        MapUtil.put(basePriceMap, "parkFee", parkFee);
        log.info("停车费 " + parkFee);

        MapUtil.put(basePriceMap, "otherFee", otherFee);
        log.info("其它费用" + otherFee);

        MapUtil.put(basePriceMap, "apiPrice", price);

        //根据公式计算预估价格
        double calPrice = Estimate.getDriverEstimateCost(basePriceMap);
        calPrice = calPrice * driver_system_bidding;

        log.info("订单基本费用(加价后) " + calPrice);
        log.info("driver_system_bidding " + (driver_system_bidding - 1));

        double sysEstimatedPrice = Double.parseDouble(price);

        AssertUtil.assertDiff(sysEstimatedPrice, calPrice, 3 * driver_system_bidding);
    }

    /**
     * @param car_type_id      车型ID
     * @param product_type_id  产品类型ID
     * @param city             城市
     * @param fixed_product_id 固定产品ID
     * @param reDispatch       是否改派
     * @param useCombo         是否使用套包
     * @param useCoupon        是否使用优惠券
     * @param supercritical    系统记录里程数 为负数时则使用xml里的默认值
     * @param mileage          司机录入里程数 为负数时则使用xml里的默认值
     * @param expressWayFee    高速费 为负数时则使用xml里的默认值
     * @param parkFee          停车费 为负数时则使用xml里的默认值
     */
    public void getDriverPrice(String car_type_id,
                               String product_type_id,
                               String city,
                               String fixed_product_id,
                               boolean reDispatch, // TODO : implement redispatch
                               boolean useCombo,
                               boolean useCoupon,
                               double supercritical,
                               double mileage,
                               double expressWayFee,
                               double parkFee) {

        //套包和优惠券不能同时使用
        Assert.assertFalse(useCoupon && useCombo, "参数输入错误，套包和优惠券不能同时使用");

        Map<String, TestParameter> inputParameterMap = Order.createInputParameterMap(RiderCategoryConst.FIRST_USER,
                product_type_id, fixed_product_id, city, car_type_id, useCoupon);

        orderId = Order.createOrderByProductTypeId(inputParameterMap).getValue();
        CommonUtil.sleep(10000);
        //刚创建的订单有可能为正在派单状态3，也有可能接单较快 为已接单状态4
        int status = Integer.parseInt(Order.getStatus(orderId));
        if (status != OrderStatus.WAIT_FOR_ACCEPT && status != OrderStatus.WAIT_FOR_DRIVER) {
            Assert.assertTrue(false, "status should be 3 or 4!");
        }

        //如果当前为正在派单状态3 则继续判断是否接单成功
        if (status == OrderStatus.WAIT_FOR_ACCEPT) {
            boolean isAccepted = Order.hasDriverResponse(orderId);
            Assert.assertTrue(isAccepted, "accept order failed!!!");
        }

        //接单后,且订单状态为4
        status = Integer.parseInt(Order.getStatus(orderId));
        Assert.assertEquals(status, OrderStatus.WAIT_FOR_DRIVER, "status should be 4 - WAIT_FOR_DRIVER !");

        //使用套包
        if (useCombo) {
            Combo.updateCombo(orderId);
        }

        //完成订单并确认付款
        Map<OrderStage, Map<String, TestParameter>> responseAfterEndTiming =
                APIUtil.afterAcceptOrder(orderId, supercritical, mileage, expressWayFee, parkFee);

        // 获取司机端快照
        ChargeSnap driverSnap = ChargeSnap.construct(ChargeSnap.SnapType.Driver);
        Snapshot snap = driverSnap.getLastSnapFromMongo(orderId);


        double originalOrderFeeActual = snap.getOutput().getDouble("订单原始金额");
        Map<String, TestParameter> feeDuringEndTiming = CommonUtil.OutputResult(responseAfterEndTiming.get(OrderStage.EndTiming));
        log.info("检查司机端订单原始金额");
        double originalOrderFeeExpected = FaresCalculator.getDriverFare(snap, feeDuringEndTiming);
        Assert.assertEquals(originalOrderFeeActual, originalOrderFeeExpected, "司机端订单原始金额不正确");
        log.info("订单原始金额(实际) : " + originalOrderFeeActual + " ,订单原始金额(期待) : " + originalOrderFeeExpected);
        log.info("Test passed");
        CommonUtil.sleep(2000);
    }

    public void getDriverPrice(String car_type_id,
                               String product_type_id,
                               String city,
                               String fixed_product_id,
                               boolean reDispatch, // TODO : implement redispatch
                               boolean useCombo,
                               boolean useCoupon) {

        getDriverPrice(car_type_id,
                product_type_id,
                city,
                fixed_product_id,
                reDispatch,
                useCombo,
                useCoupon,
                ParamValue.NO_OVERRIDE_SUPERCRITAL,
                ParamValue.NO_OVERRIDE_MILEAGE,
                ParamValue.NO_OVERRIDE_HIGHWAY_AMOUNT,
                ParamValue.NO_OVERRIDE_PARKING_AMOUNT);
    }

    /**
     * 测试目的：<br>
     * 司机能获取正确获取 非定价产品-预约用车-舒适车型 的预估价格<br>
     * <p>
     * 测试步骤：<br>
     * 1.获取免费长途公里数<br>
     * 2.获取计算预估计价格需要的基础价格数据 如:起步价,最低消费,每公里费用,每分钟费用等<br>
     * 3.获取系统的预估总价<br>
     * 4.根据基础价格表和司机价格预估公式 计算预估总价<br>
     * 检查点：预约用车-舒适车型 系统预估总价和计算所有预估总价差值在3以内<br>
     * <p>
     * 测试用例开发人员:马毅<br>
     */
    @Test
    public void charge_getPrice_hourRent_shouldSucceed_comfortableCar() {
        Map<String, TestParameter> inputMap = MapUtil.getParameterMap("car_type_id", "2");
        MapUtil.put(inputMap, "car_type_ids", "2");
        MapUtil.put(inputMap, "expect_start_time", String.valueOf(Order.get_expect_start_time_for_night_service()));
        getDriverPrice(ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR, 0, inputMap, false);
    }

    /**
     * 测试目的：<br>
     * 司机能获取正确获取 非定价产品-预约用车-舒适车型 的预估价格<br>
     * <p>
     * 测试步骤：<br>
     * 创建订单
     * 1.获取免费长途公里数<br>
     * 2.获取计算预估计价格需要的基础价格数据 如:起步价,最低消费,每公里费用,每分钟费用等<br>
     * 3.获取系统的预估总价<br>
     * 4.系统加价1倍<br>
     * 5.根据基础价格表和司机价格预估公式 计算预估总价<br>
     * 检查点：预约用车-舒适车型 系统预估总价和计算所有预估总价差值在3以内<br>
     * <p>
     * 测试用例开发人员:马毅<br>
     */
    @Test
    public void charge_getPrice_hourRent_withBidding_shouldSucceed_comforatCar() {
        log.info("casename: charge_getPrice_hourRent_withBidding_shouldSucceed_comforatCar");
        Map<String, TestParameter> inputMap = MapUtil.getParameterMap("car_type_id", "2");
        MapUtil.put(inputMap, "car_type_ids", "2");
        MapUtil.put(inputMap, "expect_start_time", String.valueOf(Order.get_expect_start_time_for_night_service()));
        getDriverPrice(ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR, 1, inputMap, false);
    }

    /**
     * 测试目的：<br>
     * 司机能获取正确获取 非定价产品-预约用车-舒适车型 的预估价格<br>
     * <p>
     * 测试步骤：<br>
     * 创建订单
     * 1.系统改派车辆<br>
     * 2.获取免费长途公里数<br>
     * 3.获取计算预估计价格需要的基础价格数据 如:起步价,最低消费,每公里费用,每分钟费用等<br>
     * 4.获取系统的预估总价<br>
     * 5.根据基础价格表和司机价格预估公式 计算预估总价<br>
     * 检查点：预约用车-舒适车型 系统预估总价和计算所有预估总价差值在3以内<br>
     * <p>
     * 测试用例开发人员:马毅<br>
     */
    @Test
    public void charge_getPrice_hourRent_reDispatch_shouldSucceed_comfortableCar() {
        Map<String, TestParameter> inputMap = MapUtil.getParameterMap("car_type_id", "3");
        MapUtil.put(inputMap, "car_type_ids", "3");
        MapUtil.put(inputMap, "expect_start_time", String.valueOf(Order.get_expect_start_time_for_night_service()));
        getDriverPrice(ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR, 0, inputMap, false);
    }

    /**
     * 测试目的：<br>
     * 乘客端能正确使用优惠券 使用优惠券后 计费正确<br>
     * <p>
     * 测试步骤：<br>
     * 1.创建订单
     * 1.获取用户可用的第一个优惠券<br>
     * 2.乘客使用优惠券<br>
     * 3.司机接单<br>
     * 4.完成行程<br>
     * 检查点：乘客使用优惠券后 计费正确<br>
     * TODO:涉及到结算的部分 还没有实现
     * 测试用例开发人员:马毅<br>
     */
    //@Test
    public void charge_withCoupon_shouldSucceed_deluxeCar() {
        RiderInfo rider = CommonUtil.getRider(RiderCategoryConst.FOR_PAYMENT_TESTING);
        String user_id = rider.getUserId();

        String car_type_id = "3";
        String product_type_id = "1";
        String passenger_phone = rider.getCellphone();

        Map<String, TestParameter> baseParameterMap = MapUtil.getParameterMap("user_id", user_id);
        MapUtil.put(baseParameterMap, "car_type_id", car_type_id);
        MapUtil.put(baseParameterMap, "car_type_ids", car_type_id);
        MapUtil.put(baseParameterMap, "product_type_id", product_type_id);
        MapUtil.put(baseParameterMap, "passenger_phone", passenger_phone);
        MapUtil.put(baseParameterMap, "flag", "0");
        MapUtil.put(baseParameterMap, "passenger_name", rider.getPassengerName());

        //获取第一个有效优惠券
        String coupon_member_id = Coupon.getFirstAvailableCoupon(user_id, product_type_id, car_type_id);

        //创建订单
        TestParameter orderPara = Order.createOrderByProductTypeId(product_type_id, baseParameterMap);
        orderId = orderPara.getValue();

        MapUtil.put(baseParameterMap, "service_order_id", orderId);
        MapUtil.put(baseParameterMap, "coupon_member_id", coupon_member_id);
        MapUtil.put(baseParameterMap, "user_cellphone", passenger_phone);

        //使用优惠券
        CaseExecutor.runTestCase("charge/couponMember_useCoupon.xml", baseParameterMap);

        boolean ret = APIUtil.acceptOrder(orderId);
        //boolean ret = APIUtil.acceptOrder(orderId,baseParameterMap);
        Assert.assertTrue(ret);
    }

    /**
     * 测试目的：<br>
     * 一口价订单完成后，司机端计费正确<br>
     * <p>
     * 测试步骤：<br>
     * 1.创建舒适车ASAP订单并使用优惠券 （确保ERP-基础数据管理-司机一口价配置管理-时租-马上用车， 舒适车型为启用状态）<br>
     * 2.司机接单<br>
     * 3.完成行程<br>
     * 检查点：订单原始金额 计费正确<br>
     * <p>
     * 测试用例开发人员:戴宝春<br>
     */
    @Test
    public void charge_withCoupon_shouldSucceed_ComforatCar_ASAP_FixedFee() {
        log.info("===测试用例：非定价产品-立即用车-舒适车型（一口价范围内）-乘客使用优惠券 订单结束后司机端订单原始金额计费正确===");
        boolean reDispatch = false; //不改派
        boolean useCombo = false;   //是否使用套包 true ：使用，false：不使用
        boolean useCoupon = true;   //使用优惠券

        getDriverPrice(ParamValue.CAR_TYPE_ID_COMFORTABLE,
                ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,
                "bj",
                null,
                reDispatch,
                useCombo,
                useCoupon);
    }

    /**
     * 测试目的：<br>
     * 一口价订单完成后，实际价格高于一口价阈值，司机端计费正确<br>
     * <p>
     * 测试步骤：<br>
     * 1.创建舒适车型ASAP订单并使用套包 （确保ERP-基础数据管理-司机一口价配置管理-时租-马上用车， 舒适车型为启用状态）<br>
     * 2.司机接单<br>
     * 3.完成行程<br>
     * 检查点：司机端使用订单原始金额而非一口价金额计费<br>
     * <p>
     * 测试用例开发人员:戴宝春<br>
     */
    @Test
    public void charge_withCombo_shouldSucceed_ComforatCar_ASAP_FixedFee_Beyond_Threshold() {
        log.info("===测试用例：非定价产品-立即用车-舒适车型（高于一口价）-乘客使用套包 订单结束后司机端订单原始金额计费正确===");
        boolean reDispatch = false;         //不改派
        boolean useCombo = true;           //是否使用套包 true ：使用，false：不使用
        boolean useCoupon = !useCombo;     //是否使用优惠券

        getDriverPrice(ParamValue.CAR_TYPE_ID_COMFORTABLE,
                ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,
                "bj",
                null,
                reDispatch,
                useCombo,
                useCoupon,
                25,
                25,
                10,
                15);
    }

    /**
     * 测试目的：<br>
     * 一口价订单完成后，实际价格低于一口价阈值，司机端计费正确<br>
     * <p>
     * 测试步骤：<br>
     * 1.创建舒适车型ASAP订单并使用优惠券 （确保ERP-基础数据管理-司机一口价配置管理-时租-马上用车， 舒适车型为启用状态）<br>
     * 2.司机接单<br>
     * 3.完成行程<br>
     * 检查点：司机端使用订单原始金额而非一口价金额计费<br>
     * <p>
     * 测试用例开发人员:戴宝春<br>
     */
    @Test
    public void charge_withCoupon_shouldSucceed_ComforatCar_ASAP_FixedFee_Lessthan_Threshold() {
        log.info("===测试用例：非定价产品-立即用车-舒适车型（低于一口价）-乘客使用优惠券 订单结束后司机端订单原始金额计费正确===");
        boolean reDispatch = false;      //不改派
        boolean useCombo = false;        //是否使用套包 true ：使用，false：不使用
        boolean useCoupon = !useCombo;   //是否使用优惠券

        getDriverPrice(ParamValue.CAR_TYPE_ID_COMFORTABLE,
                ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,
                "bj",
                null,
                reDispatch,
                useCombo,
                useCoupon,
                5,
                5,
                0,
                0);
    }

    /**
     * 测试目的：<br>
     * 非一口价订单完成后，司机端计费正确<br>
     * <p>
     * 测试步骤：<br>
     * 1.创建豪华车型ASAP订单并使用优惠券 （确保ERP-基础数据管理-司机一口价配置管理-时租-马上用车， 豪华车型为禁止状态）<br>
     * 2.司机接单<br>
     * 3.完成行程<br>
     * 检查点：订单原始金额 计费正确<br>
     * <p>
     * 测试用例开发人员:戴宝春<br>
     */
    @Test
    public void charge_withCoupon_shouldSucceed_DELUXE_ASAP_NonFixedFee() {
        log.info("===测试用例：非定价产品-立即用车-豪华车型（非一口价）-乘客使用优惠券 订单结束后司机端订单原始金额计费正确===");
        boolean reDispatch = false;         //不改派
        boolean useCombo = false;           //是否使用套包 true ：使用，false：不使用
        boolean useCoupon = !useCombo;      //使用优惠券

        getDriverPrice(ParamValue.CAR_TYPE_ID_DELUXE,
                ParamValue.PRODUCT_TYPE_ID_IMMEDIATE,
                "bj",
                null,
                reDispatch,
                useCombo,
                useCoupon);
    }

    @BeforeMethod
    public void init() {
        orderId = null;
    }

    @AfterMethod
    public void tearDown() {
        if (null != orderId) {
            log.info("Tear Down: 如果订单状态不为结束或取消 取消订单释放司机");
            int current_status = Integer.parseInt(Order.getStatus(orderId));
            //如果状态不为结束或取消 取消订单释放司机
            if (OrderStatus.CANCELED != current_status && OrderStatus.FINISHED != current_status) {
                Order.cancelOrderById(new TestParameter("order_id", orderId));
            }
            log.info("Order id: " + orderId);
        }
    }

}
