package com.yongche.testcase.charge;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.api.APIUtil;
import com.yongche.framework.api.charge.Estimate;
import com.yongche.framework.api.charge.FreeDistance;
import com.yongche.framework.api.charge.Price;
import com.yongche.framework.api.order.Coupon;
import com.yongche.framework.api.order.Order;
import com.yongche.framework.api.order.State;
import com.yongche.framework.core.*;
import com.yongche.framework.utils.AssertUtil;
import com.yongche.framework.utils.ConfigUtil;
import com.yongche.framework.utils.JsonUtil;
import com.yongche.framework.utils.MapUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
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
                               boolean reDispatch){

        //1.===创建订单===
        TestParameter orderPara = Order.createOrderByProductTypeId(product_type_id,orderInputParameterMap);
        orderId = orderPara.getValue();

        if(reDispatch){
            //判断是否有司机接单
            boolean ret = Order.hasDriverResponse(orderId);
            Assert.assertTrue(ret);

            //司机已接单  获取司机ID
            String driverId = Order.getFieldsByOrderId(orderId).get("driver_id").getValue();
            log.info("driver id : " + driverId);

            //改派
            State.clearCar(orderId,driverId);

            //判断是否有司机接单
            ret = Order.hasDriverResponse(orderId);
            Assert.assertTrue(ret);
        }
        //orderId = "6371656001865532595";

        Map<String, TestParameter> paraMap = Order.getFieldsByOrderId(orderId);
        paraMap.remove(ParamValue.RESPONSE_RESULT);
        paraMap.remove("driver_id");
        paraMap.remove("car_id");

        if(driver_system_bidding == 0) {
            MapUtil.put(paraMap, "flag", "0");//确保是非一口价的Flag 并且不会受下浮系数影响
            driver_system_bidding = 1;
        }else{
            MapUtil.put(paraMap, "flag", "1099511627776");//一口价的Flag
            MapUtil.put(paraMap, "driver_system_bidding", String.valueOf(driver_system_bidding));
            driver_system_bidding = driver_system_bidding  + 1;
        }

        //2.===调用API 获取司机端价格===
        MapUtil.put(paraMap,"service_order_id",orderId);
        Map<String, TestParameter> priceMap = Estimate.getDriverEstimateCostFromExt(paraMap);
        String car_type_ids = paraMap.get("car_type_ids").getValue();
        String price = JsonUtil.getValueFromResult(priceMap,car_type_ids,"订单原始金额") ;

        //"易到补贴":0,"佣金总金额":499.6,"高速费":0,"停车费":0,"其他费用":0
        String subsidy = JsonUtil.getValueFromResult(priceMap,car_type_ids,"易到补贴") ;
        String expressWayFee = JsonUtil.getValueFromResult(priceMap,car_type_ids,"高速费") ;
        String parkFee = JsonUtil.getValueFromResult(priceMap,car_type_ids,"停车费") ;
        String otherFee = JsonUtil.getValueFromResult(priceMap,car_type_ids,"其他费用") ;

        log.info("订单原始金额:" + price);
        log.info("car_type_ids: " + car_type_ids);

        //3.===获取免费长途公里数===
        Map<String,TestParameter> baseParamMap = new HashMap<>();

        //下面的三个值必须用代码设置 才能保证不同API是基于同样的车型取数据
        //String product_type_id = paraMap.get("product_type_id").getValue();
        MapUtil.put(baseParamMap,"car_type_id",car_type_ids);
        MapUtil.put(baseParamMap,"product_type_id", product_type_id);
        MapUtil.put(baseParamMap,"city",paraMap.get("city").getValue());

        int freeLongDistance = FreeDistance.getFreeDistance(baseParamMap);


        //4.===获取司机计价基础数据===
        String fixed_product_id = paraMap.get("fixed_product_id").getValue();
        MapUtil.put(baseParamMap,"fixed_product_id",fixed_product_id);

        String product_type_class = Price.getProductTypeClass(product_type_id);
        MapUtil.put(baseParamMap,"product_type_class",product_type_class);


        //5.====根据价格类型 获取基础价格信息====
        EstimatePriceType priceType = EstimatePriceType.ORDER;
        Map<String,TestParameter> basePriceMap = Price.getPrice(priceType,baseParamMap);

        int distance = MapUtil.getInt(priceMap,"预估距离");
        MapUtil.put(basePriceMap,"distance",String.valueOf(distance));
        log.info("预估距离 " + distance);

        int time_length = MapUtil.getInt(priceMap,"预估时间");
        MapUtil.put(basePriceMap,"time_length",String.valueOf(time_length));
        log.info("预估时间 " + time_length);

        int night_time_length =  MapUtil.getInt(priceMap,"预估夜间时间");
        MapUtil.put(basePriceMap,"night_time_length",String.valueOf(night_time_length));
        log.info("预估夜间时间 " + night_time_length);

        MapUtil.put(basePriceMap,"free_long_distance",String.valueOf(freeLongDistance));
        log.info("免费长途公里数 (米) :" + freeLongDistance);

        MapUtil.put(basePriceMap,"subsidy",subsidy);
        log.info("易到补贴 " + subsidy);

        MapUtil.put(basePriceMap,"expressWayFee",expressWayFee);
        log.info("高速费 " + expressWayFee);

        MapUtil.put(basePriceMap,"parkFee",parkFee);
        log.info("停车费 " + parkFee);

        MapUtil.put(basePriceMap,"otherFee",otherFee);
        log.info("其它费用" + otherFee);

        MapUtil.put(basePriceMap,"apiPrice",price);

        //根据公式计算预估价格
        double calPrice = Estimate.getDriverEstimateCost(basePriceMap);
        calPrice = calPrice * driver_system_bidding;

        log.info("订单基本费用(加价后) " + calPrice);
        log.info("driver_system_bidding " + (driver_system_bidding -1));

        double sysEstimatedPrice = Double.parseDouble(price);

        AssertUtil.assertDiff(sysEstimatedPrice,calPrice,3*driver_system_bidding);
    }

    /**
     * 测试目的：<br>
     * 司机能获取正确获取 非定价产品-预约用车-舒适车型 的预估价格<br>
     *
     * 测试步骤：<br>
     * 1.获取免费长途公里数<br>
     * 2.获取计算预估计价格需要的基础价格数据 如:起步价,最低消费,每公里费用,每分钟费用等<br>
     * 3.获取系统的预估总价<br>
     * 4.根据基础价格表和司机价格预估公式 计算预估总价<br>
     *   检查点：预约用车-舒适车型 系统预估总价和计算所有预估总价差值在3以内<br>
     *
     * 测试用例开发人员:马毅<br>
     */
    @Test
    public void charge_getPrice_hourRent_shouldSucceed_comfortableCar(){
        Map<String,TestParameter> inputMap = MapUtil.getParameterMap("car_type_id","2");
        MapUtil.put(inputMap,"car_type_ids","2");
        getDriverPrice(ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR,0,inputMap,false);
    }

    /**
     * 测试目的：<br>
     * 司机能获取正确获取 非定价产品-预约用车-舒适车型 的预估价格<br>
     *
     * 测试步骤：<br>
     * 创建订单
     * 1.获取免费长途公里数<br>
     * 2.获取计算预估计价格需要的基础价格数据 如:起步价,最低消费,每公里费用,每分钟费用等<br>
     * 3.获取系统的预估总价<br>
     * 4.系统加价1倍<br>
     * 5.根据基础价格表和司机价格预估公式 计算预估总价<br>
     *   检查点：预约用车-舒适车型 系统预估总价和计算所有预估总价差值在3以内<br>
     *
     * 测试用例开发人员:马毅<br>
     */
    @Test
    public void charge_getPrice_hourRent_withBidding_shouldSucceed_comforatCar(){
        Map<String,TestParameter> inputMap = MapUtil.getParameterMap("car_type_id","2");
        MapUtil.put(inputMap,"car_type_ids","2");
        getDriverPrice(ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR,1,inputMap,false);
    }

    /**
     * 测试目的：<br>
     * 司机能获取正确获取 非定价产品-预约用车-舒适车型 的预估价格<br>
     *
     * 测试步骤：<br>
     * 创建订单
     * 1.系统改派车辆<br>
     * 2.获取免费长途公里数<br>
     * 3.获取计算预估计价格需要的基础价格数据 如:起步价,最低消费,每公里费用,每分钟费用等<br>
     * 4.获取系统的预估总价<br>
     * 5.根据基础价格表和司机价格预估公式 计算预估总价<br>
     *   检查点：预约用车-舒适车型 系统预估总价和计算所有预估总价差值在3以内<br>
     *
     * 测试用例开发人员:马毅<br>
     */
    @Test
    public void charge_getPrice_hourRent_reDispatch_shouldSucceed_comfortableCar(){
        Map<String,TestParameter> inputMap = MapUtil.getParameterMap("car_type_id","3");
        MapUtil.put(inputMap,"car_type_ids","3");
        getDriverPrice(ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR,0,inputMap,false);
    }

 /**
  * 测试目的：<br>
  * 乘客端能正确使用优惠券 使用优惠券后 计费正确<br>
  *
  * 测试步骤：<br>
  * 1.创建订单
  * 1.获取用户可用的第一个优惠券<br>
  * 2.乘客使用优惠券<br>
  * 3.司机接单<br>
  * 4.完成行程<br>
  *   检查点：乘客使用优惠券后 计费正确<br>
  *   TODO:涉及到结算的部分 还没有实现
  * 测试用例开发人员:马毅<br>
  */
    //@Test
    public void charge_withCoupon_shouldSucceed_deluxeCar(){
        String user_id = "26007737";
        String car_type_id = "3";
        String product_type_id = "1";
        String passenger_phone="16811070001";

        Map<String, TestParameter> baseParameterMap = MapUtil.getParameterMap("user_id",user_id);
        MapUtil.put(baseParameterMap,"car_type_id",car_type_id);
        MapUtil.put(baseParameterMap,"car_type_ids",car_type_id);
        MapUtil.put(baseParameterMap,"product_type_id",product_type_id);
        MapUtil.put(baseParameterMap,"passenger_phone",passenger_phone);
        MapUtil.put(baseParameterMap,"flag","0");
        MapUtil.put(baseParameterMap,"passenger_name","test-dispatch-" + passenger_phone);

        //获取第一个有效优惠券
        String coupon_member_id = Coupon.getFirstAvailableCoupon(user_id,product_type_id,car_type_id);

        //创建订单
        TestParameter orderPara = Order.createOrderByProductTypeId(product_type_id,baseParameterMap);
        orderId = orderPara.getValue();

        MapUtil.put(baseParameterMap,"service_order_id",orderId);
        MapUtil.put(baseParameterMap,"coupon_member_id",coupon_member_id);
        MapUtil.put(baseParameterMap,"user_cellphone",passenger_phone);

        //使用优惠券
        CaseExecutor.runTestCase("charge/couponMember_useCoupon.xml",baseParameterMap);

        boolean ret = APIUtil.acceptOrder(orderId);
        //boolean ret = APIUtil.acceptOrder(orderId,baseParameterMap);
        Assert.assertTrue(ret);
    }

/*
    public static boolean acceptOrder(String orderId,Map<String,TestParameter> baseParameterMap){
        String value = ConfigUtil.getValue(ConfigConst.DRIVER_INFO,"driver_response_timeout");
        int timeout = Integer.parseInt(value)*1000;

        boolean ret = Order.hasDriverResponse(orderId,timeout);

        if(ret) {
            afterAcceptOrder(orderId,baseParameterMap);
        }

        return ret;
    }

    public static void afterAcceptOrder(String orderId, Map<String,TestParameter> baseParameterMap){

        Order.setDriverDepart(orderId);

        State.driverArrived(orderId);

        State.startTiming(orderId);

        State.endTiming(orderId);

        State.confirmCharge(orderId);

        Order.getStatus(orderId);
    }
*/

    @AfterMethod
    public void tearDown(){
        int current_status = Integer.parseInt(Order.getStatus(orderId));

        //如果状态不为结束或取消 取消订单释放司机
        if(OrderStatus.CANCELED != current_status && OrderStatus.FINISHED != current_status){
            Order.cancelOrderById(new TestParameter("order_id",orderId));
        }
        log.info("Order id: " + orderId);
    }

}
