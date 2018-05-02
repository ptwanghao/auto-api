package com.yongche.framework.api.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.api.charge.Price;
import com.yongche.framework.config.ConfigUtil;
import com.yongche.framework.core.*;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.JsonUtil;
import com.yongche.framework.utils.MapUtil;
import com.yongche.framework.utils.OrderStartEndTime.OrderPhaseEnum;
import com.yongche.framework.utils.OrderStartEndTime.OrderTimeSpan;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 *
 */
public class Order {
    public static Logger log = LoggerFactory.getLogger(Order.class);

    /**
     * 获取订单ID<br>
     *
     * @return 由订单号和"order_id"创建的{@link TestParameter}对象
     */
    public static TestParameter getOrderId(String xmlFileName, Map<String, TestParameter> inputParameterMap, Map<String, TestParameter> expectedResultMap) {
        Map<String, TestParameter> expectedParams = CaseExecutor.runTestCase(xmlFileName, inputParameterMap, expectedResultMap);
        return expectedParams.get("order_id");
    }

    public static Map<String, TestParameter> createInputParameterMap(int passengerIndex,
                                                                     String product_type_id,
                                                                     String fixed_product_id,
                                                                     String city,
                                                                     String car_type_id,
                                                                     boolean useCoupon) {

        RiderInfo rider = CommonUtil.getRider(passengerIndex);
        String user_id = rider.getUserId();
        //新创建订单 生成订单号
        Map<String, TestParameter> inputParameterMap = MapUtil.getParameterMap("user_id", rider.getUserId());
        MapUtil.put(inputParameterMap, "passenger_phone", rider.getCellphone());
        MapUtil.put(inputParameterMap, "passenger_name", rider.getPassengerName());
        if (null != product_type_id) {
            MapUtil.put(inputParameterMap, "product_type_id", product_type_id);
            String product_type_class = Price.getProductTypeClass(product_type_id);
            MapUtil.put(inputParameterMap, "product_type_class", product_type_class);
        }
        if (null != car_type_id) {
            MapUtil.put(inputParameterMap, "car_type_id", car_type_id);
        }
        if (null != city) {
            MapUtil.put(inputParameterMap, "city", city);
        }

        if (null != fixed_product_id) {
            MapUtil.put(inputParameterMap, "fixed_product_id", fixed_product_id);
        }

        if (useCoupon) { //给用户创建优惠券
            log.info("使用优惠券");
            String coupon_list = ConfigUtil.getValue(ConfigConst.COUPON_INFO, "coupon_template_id");
            String comment = "auto_test";
            String operator_id = user_id;  //操作人id  大于0即可
            String coupon_id = Coupon.sendCoupon(user_id, coupon_list, operator_id, comment);
            MapUtil.put(inputParameterMap, "coupon_member_id", coupon_id);
        }

        return inputParameterMap;
    }

    /**
     * 根据不同的订单类型创建订单<br>
     *
     * @param inputParameterMap 必须包含product_type_id的TestParameter对象
     * @return 由订单号和"order_id"创建的{@link TestParameter}对象
     */
    //TODO: Add database check for order ID
    public static TestParameter createOrderByProductTypeId(Map<String, TestParameter> inputParameterMap) {
        String fixedProductIdVal = "1";//0代表非固定价格 1 代表固定价格
        String typeId = inputParameterMap.get("product_type_id").getValue();

        //预约用车 和 随叫随到 要求fixed_product_id = 0,接机类型为38,送机35,其它为1
        switch (typeId) {
            case ParamValue.PRODUCT_TYPE_ID_AIRPORT_PICKUP:
                fixedProductIdVal = "792";//fixedProductIdVal = "38"
                break;
            case ParamValue.PRODUCT_TYPE_ID_AIRPORT_DROP_OFF:
                fixedProductIdVal = "35";
                break;
            case ParamValue.PRODUCT_TYPE_ID_HOUR_RENT_HOUR:
            case ParamValue.PRODUCT_TYPE_ID_IMMEDIATE:
                fixedProductIdVal = "0";
                break;
        }

        //TestParameter product_type_id = TestParameter.getTestParameter("product_type_id", typeId);
        //TestParameter fixedProductId = TestParameter.getTestParameter("fixed_product_id",fixedProductIdVal);
        //Map<String,TestParameter> inputParameterMap = new HashMap<>();
        //inputParameterMap.put("product_type_id",product_type_id);
        //inputParameterMap.put("fixed_product_id",fixedProductId);
        MapUtil.put(inputParameterMap, "fixed_product_id", fixedProductIdVal);
        String caseFile = "state_createOrder_immediate.xml";

        //日租和半日租 用state_createOrder_rental_one_day.xml 且返回后需要调用confirm()接口
        //其它类型的order 用state_createOrder_immediate.xml
        switch (typeId) {
            case ParamValue.PRODUCT_TYPE_ID_AIRPORT_PICKUP:
                caseFile = "state_createOrder_airport_pickup.xml";
                break;
            case ParamValue.PRODUCT_TYPE_ID_AIRPORT_DROP_OFF:
                caseFile = "state_createOrder_airport_dropOff.xml";
                break;
            case ParamValue.PRODUCT_TYPE_ID_RENTAL_HALF_DAY:
            case ParamValue.PRODUCT_TYPE_ID_RENTAL_ONE_DAY:
                caseFile = "state_createOrder_rental_one_day.xml";
                break;
        }

        return getOrderId(caseFile, inputParameterMap, null);
    }

    /**
     * 根据不同的订单类型创建订单<br>
     *
     * @param typeId 订单类型 {@link TestParameter}
     * @return 由订单号和"order_id"创建的{@link TestParameter}对象
     */
    public static TestParameter createOrderByProductTypeId(String typeId) {
        Map<String, TestParameter> inputParameterMap = MapUtil.getParameterMap("product_type_id", typeId);
        return createOrderByProductTypeId(inputParameterMap);
    }

    public static TestParameter createOrderByProductTypeId(String typeId, Map<String, TestParameter> inputParameterMap) {

        if (null == inputParameterMap) {
            inputParameterMap = MapUtil.getParameterMap("product_type_id", typeId);
        } else {
            MapUtil.put(inputParameterMap, "product_type_id", typeId);
        }

        return createOrderByProductTypeId(inputParameterMap);
    }

    public static void cancelOrder(Map<String, TestParameter> inputParameterMap) {
        CaseExecutor.runTestCase("state_cancelOrder_success.xml", inputParameterMap);
    }

    public static void cancelOrderById(TestParameter order_id) {
        Map<String, TestParameter> inputParamMap = MapUtil.getParameterMap("order_id", order_id.getValue());
        cancelOrder(inputParamMap);
    }

    public static Map<String, TestParameter> setDriverDepart(String orderId) {
        /**
         * parameter where is a 2-dimension array. below is the PHP code snippet :
         *
         * 1342         if (isset($params['where']['order_id'])) {
         * 1343             $orderId = $params['where']['order_id'];
         * 1344         } else {
         * 1345             return Common::fail(Common::RET_CODE_PARAM, "order_id is null");
         * 1346         }
         *
         */
        Map<String, TestParameter> departInputMap = MapUtil.getParameterMap("where[order_id]", orderId);
        return CaseExecutor.runTestCase("order_setDriverDepart.xml", departInputMap);
    }

    public static boolean hasDriverResponse(String orderId, int timeout) {
        boolean ret = false;

        //每10秒循环一次
        int interval = 10 * 1000;
        int loopCount = timeout / interval;

        int status = -1;

        while (status < 4 && loopCount > 0) {
            loopCount--;
            String value = getStatus(orderId);
            status = Integer.parseInt(value);
            CommonUtil.sleep(interval);
        }

        if (status == 4) {
            ret = true;
        }
        //Assert.assertEquals(status , 4,"status is " + status);

        return ret;
    }

    public static boolean hasDriverResponse(String orderId) {
        int timeout = ParamValue.DRIVER_RESPONSE_TIMEOUT;
        return hasDriverResponse(orderId, timeout);
    }

    public static String getStatus(String orderId) {
        Map<String, TestParameter> paraMap = MapUtil.getParameterMap("order_id", orderId);
        Map<String, TestParameter> outputParamMap = CaseExecutor.runTestCase("order_getStatus.xml", paraMap);
        String status = outputParamMap.get("status").getValue();

        log.info("order status: " + status + " ( " + OrderStatus.toString(Integer.parseInt(status)) + " )");
        return status;
    }

    public static String getDriverIdByOrderId(String orderId) {
        Map<String, TestParameter> outputParamMap = getFieldsByOrderId(orderId);
        return outputParamMap.get("driver_id").getValue();
    }

    public static String getCarIdByOrderId(String orderId) {
        Map<String, TestParameter> outputParamMap = getFieldsByOrderId(orderId);
        return outputParamMap.get("car_id").getValue();
    }

    public static String getAccountIdByOrderId(String orderId) {
        Map<String, TestParameter> outputParamMap = getFieldsByOrderId(orderId);
        return outputParamMap.get("account_id").getValue();
    }

    public static Map<String, TestParameter> getFieldsByOrderId(String orderId) {
        Map<String, TestParameter> paraMap = MapUtil.getParameterMap("service_order_id", orderId);
        Map<String, TestParameter> outputParamMap = CaseExecutor.runTestCase("order_getFieldsByOrderId.xml", paraMap);
        return outputParamMap;
    }

    /**
     * 反复调用getFieldsByOrderId，直到订单预估计费完成，即返回的estimate_snap字段里包含的service_order_id值为非空
     *
     * @param orderId                期待查询的订单ID
     * @param retryIntervalInSeconds 两次调用getFieldsByOrderId的时间间隔，即休眠时间，单位为秒。小于0时默认值为10秒
     * @param retryTimes             重试次数。小于0时默认值为15次
     * @return
     */
    public static Map<String, TestParameter> getFieldsByOrderId(String orderId, int retryIntervalInSeconds, int retryTimes) {
        if (retryIntervalInSeconds < 0) {
            retryIntervalInSeconds = 10; // in seconds
        }

        if (retryTimes < 0) {
            retryTimes = 15;
        }

        Map<String, TestParameter> outputParamMap = getFieldsByOrderId(orderId);
        while (retryTimes-- > 0) {

            try {
                String estimate_Snap = MapUtil.getString(outputParamMap, "estimate_snap");
                //estimate_Snap字符串没有已“}”结尾，不是标准的json格式，转换为jsonObject时报错，因此需要在结尾加入两个“}”
                if(!estimate_Snap.endsWith("}")){
                    estimate_Snap = estimate_Snap + "}"+"}";
                }
                String serviceOrderId = JsonUtil.getJSONObject(estimate_Snap).getString("service_order_id");
                //String serviceOrderId = JsonUtil.getJSONObject(MapUtil.getString(outputParamMap, "estimate_snap")).getString("service_order_id");

                log.info("service_order_id returned by getFieldsByOrderId : " + serviceOrderId);
                if (null != serviceOrderId && !serviceOrderId.equalsIgnoreCase("null")) {
                    break;
                }

                log.info("Sleep for " + retryIntervalInSeconds + " second(s} and will retry");

                Thread.sleep(retryIntervalInSeconds * 1000);

                outputParamMap = getFieldsByOrderId(orderId);


            } catch (InterruptedException e) {
                e.printStackTrace(); // To handle with exception raised by sleep()
            } catch (Exception e) {
                e.printStackTrace(); // To handle with exception raised by getJSONObject()
            }
        }

        if (retryTimes < 0) {
            throw new SkipException("没有获得争取的预估数据");
        }

        return outputParamMap;
    }

    /**
     * 用于处理几个查询接口的返回结果，将f返回内容格式化为json格式
     */
    public static JSONObject getJsonResult(Map<String, TestParameter> result) {
        //取出 response中RESPONSE_RESULT字段的value
        String res = result.get("RESPONSE_RESULT").getValue();
        /*
        //要解析出json需要在返回值的success字段两边添加双引号
        int start = res.indexOf("success");
        int len = "success".length();
        int end = start + len + 1;
        StringBuilder res_sb = new StringBuilder(res);//构造一个StringBuilder对象
        res_sb.insert(start, "\"");
        res_sb.insert(end, "\"");
        res = res_sb.toString();
        //解析json 获取当前订单列表
        */
        JSONObject jsonObject = JSONObject.fromObject(res);
        return jsonObject;
    }

    /**
     * 用于处理getJsonResult方法返回的数据，返回其中某个节点
     */
    public static JSONArray getOrderList(Map<String, TestParameter> result, String list_name) {
        JSONObject json_result = (JSONObject) getJsonResult(result).get("result");
        JSONArray order_list = (JSONArray) json_result.get(list_name);
        return order_list;
    }

    public static JSONArray getOrderList(Map<String, TestParameter> result) {
        Object raw_order_history_list = getJsonResult(result).get("result");

        log.info("raw_order_history_list is a type of : " + raw_order_history_list.getClass());

        JSONArray history_list = new JSONArray();

        if (raw_order_history_list.getClass() == JSONArray.class) {
            history_list.addAll((JSONArray) raw_order_history_list);
        } else if (raw_order_history_list.getClass() == JSONObject.class) {

            ((JSONObject) raw_order_history_list).forEach(
                    (k, v) -> {
                        history_list.add(v);
                    }
            );

        }

        return history_list;
    }

    public static boolean checkOrderInList(String order_id, JSONArray order_list) {
        boolean flag = false;
        for (int i = 0; i < order_list.size(); i++) {
            JSONObject json_order = (JSONObject) order_list.get(i);
            String service_order_id = json_order.get("service_order_id").toString();
            if (order_id.equals(service_order_id)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 计算用于夜间服务的期待开始时间。
     * 如果当前时间在夜间服务时段之外，设置为当天的22:50，用于计算夜间服务费。要求预计总时间超过10分钟。<br>
     * 如果当前时间在夜间服务时段之内，返回当前时间
     * 夜间服务时段从23点开始计时，到凌晨5点结束。<br>
     *
     * @return 自1970年1月1日零时以来消逝的秒数，long型
     */
    public static long get_expect_start_time_for_night_service() {

        // 如果当前处于夜间服务时段之外，设置期待开始时间为22点50， 要求预估总时间超过10分钟，才能够测试夜间服务费等内容。
        return get_expect_start_time_for_night_service(22, 50, 00);
    }

    /**
     * 计算用于夜间服务的期待开始时间。
     * 如果当前时间在夜间服务时段之外，设置期待开始时间为当天的@hour:@minute:@second的时间点。<br>
     * 如果当前时间在夜间服务时段之内，返回当前时间.<br>
     *
     * @param Hour   夜间服务时间段开始点之小时
     * @param minute 夜间服务时间段开始点之分钟
     * @param second 夜间服务时间段开始点之秒
     * @return 自1970年1月1日零时以来消逝的秒数，long型
     */
    public static long get_expect_start_time_for_night_service(int Hour, int minute, int second) {
        if (!isWithinNightServicePeriod()) {
            // 如果当前处于夜间服务时段之外，
            return get_expect_start_time(Hour, minute, second);

        } else {
            // 如果当前处于夜间服务时段中，返回当前时间
            Date now = new Date();
            long unixTime = now.getTime() / 1000;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.info("Night service start time : " + formatter.format(now) + " - unix time : " + unixTime);

            return unixTime;
        }
    }

    /**
     * 计算订单期待开始时间，日期为运行时的日期。<br>
     *
     * @param hour_of_day 小时， 24小时制
     * @param minute      分钟
     * @param second      秒数
     * @return 自1970年1月1日零时以来消逝的秒数，long型
     */
    public static long get_expect_start_time(int hour_of_day, int minute, int second) {

        Calendar current = Calendar.getInstance();

        current.set(Calendar.HOUR_OF_DAY, hour_of_day);
        current.set(Calendar.MINUTE, minute);
        current.set(Calendar.SECOND, second);

        Date today = current.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long unixTime = today.getTime() / 1000;
        log.info("Night service start time : " + formatter.format(today) + " - unix time : " + unixTime);

        return unixTime;
    }

    /**
     * 检查当前时间是否处于夜间服务时段，默认夜间服务时段起始于夜里23点，结束于第二天凌晨5点。
     *
     * @return true 如果当前时间晚于23点早于5点，false otherwise
     */
    public static boolean isWithinNightServicePeriod() {
        final int Night_Service_Time_Start = 23;
        final int Night_Service_Time_End = 5;

        return isWithinNightServicePeriod(Night_Service_Time_Start, Night_Service_Time_End);
    }

    /**
     * 检查当前时间是否处于夜间服务时段
     *
     * @param start 夜间服务时段起始点，单位：小时。如果start<0或者>=24,则默认值为23
     * @param end   夜间服务时段结束点，单位：小时。如果end<0或者>=24，则默认值为5
     * @return true 如果当前时间晚于start早于end，false otherwise
     */
    public static boolean isWithinNightServicePeriod(int start, int end) {
        if (start < 0 || start >= 24) {
            start = 23;
        }
        if (end < 0 || end >= 24) {
            end = 5;
        }

        Calendar current = Calendar.getInstance();
        int hour_of_day = current.get(Calendar.HOUR_OF_DAY); // 24-hours

        // return true 如果当前时间晚于晚上的起始时间点，早于凌晨的结束时间点，false otherwise
        return !(hour_of_day < start && hour_of_day > end);
    }


    /**
     * 根据订单ID，获取订单实际开始时间
     *
     * @param orderId 订单ID
     * @return 返回自1970-1-1 00:00:00时开始的秒数
     */
    public static long getStartTimeByOrderId(String orderId) {
        //获取开始时间
        Map<String, TestParameter> fields = Order.getFieldsByOrderId(orderId);
        long startTime = Long.parseLong(fields.get("start_time").getValue());
        return startTime;
    }

    /**
     * 调整订单金额
     *
     * @param orderId 订单ID
     * @return
     */
    public static Map<String, TestParameter> adjustAmount(String orderId) {

        OrderTimeSpan orderStartEndTime = OrderTimeSpan.construct(OrderPhaseEnum.EndTiming, orderId);
        String startTime = String.valueOf(orderStartEndTime.getStartTime());
        String endTime = String.valueOf(orderStartEndTime.getEndTime());

        return adjustAmount(orderId, startTime, endTime, "15", "0", "0", "0");
    }

    /**
     * 调整订单金额
     *
     * @param orderId                   订单ID
     * @param startTime                 订单开始时间 单位：秒
     * @param endTime                   订单结束时间 单位：秒
     * @param dependableDistanceInMeter 计费里程 单位：米
     * @param deadheadDistanceInMeter   长途里程 单位：米
     * @param parkingAmount             停车费 单位：分
     * @param highway_amount            高速费 单位：分
     * @return
     */
    public static Map<String, TestParameter> adjustAmount(
            String orderId,
            String startTime,
            String endTime,
            String dependableDistanceInMeter,
            String deadheadDistanceInMeter,
            String parkingAmount,
            String highway_amount) {

        log.info("Adjust amount for order : " + orderId);
        log.info("    start_time : " + startTime);
        log.info("    end_time : " + endTime);
        log.info("    dependable_distance : " + dependableDistanceInMeter + " meters");
        log.info("    deadhead_distance : " + deadheadDistanceInMeter);
        log.info("    parking_amount : " + parkingAmount);
        log.info("    highway_amount : " + highway_amount);

        Map<String, TestParameter> inputParameterMap = MapUtil.getParameterMap("order_id", orderId);
        MapUtil.put(inputParameterMap, "start_time", startTime);
        MapUtil.put(inputParameterMap, "end_time", endTime);
        MapUtil.put(inputParameterMap, "dependable_distance", dependableDistanceInMeter);
        MapUtil.put(inputParameterMap, "deadhead_distance", deadheadDistanceInMeter);
        MapUtil.put(inputParameterMap, "parking_amount", parkingAmount);
        MapUtil.put(inputParameterMap, "highway_amount", highway_amount);
        MapUtil.put(inputParameterMap, "regulatedri_reason", "测试订单");
        MapUtil.put(inputParameterMap, "username", CommonUtil.getRider(RiderCategoryConst.FIRST_USER).getUserId());

        return CaseExecutor.runTestCase("state_adjustAmount.xml", inputParameterMap);
    }
}
