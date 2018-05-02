package com.yongche.framework.api.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.ParamValue;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.MapUtil;
import com.yongche.framework.utils.OrderStartEndTime.OrderPhaseEnum;
import com.yongche.framework.utils.OrderStartEndTime.OrderTimeSpan;
import com.yongche.framework.utils.OrderStartEndTime.OrderTimeSpanDuringConfirmCharging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.Map;

/**
 *
 */
public class State {
    public static Logger log = LoggerFactory.getLogger(State.class);

    public static Map<String,TestParameter> driverArrived(String orderId){
        Map<String,TestParameter> paraMap = MapUtil.getParameterMap("order_id",orderId);

        //car_id
        String carId =  Order.getCarIdByOrderId(orderId);
        MapUtil.put(paraMap,"car_id",carId);

        //arrive_time
        long arriveTime = (new Date()).getTime()/1000;
        MapUtil.put(paraMap,"arrive_time",String.valueOf(arriveTime));

        return CaseExecutor.runTestCase("state_driveArrived.xml",paraMap);
    }

    public static Map<String,TestParameter> endTiming(String orderId){
        //根据订单实际开始时间，设置服务结束时间为其8分钟后
        OrderTimeSpan orderTimeSpanDuringEndTiming = OrderTimeSpan.construct(OrderPhaseEnum.EndTiming,orderId);
        // orderTimeSpanDuringEndTiming.setTimeLength(8); // no need to invoke explicitly, default timeLength is 8 min
        long endTime = orderTimeSpanDuringEndTiming.getEndTime();
        return endTiming(orderId,endTime);
    }

    public static Map<String,TestParameter> endTiming(String orderId, long endTime){
        return endTiming(orderId,endTime,ParamValue.NO_OVERRIDE_SUPERCRITAL);
    }

    /**
     * 调用State/endTiming接口结束行程，并设置系统记录记录里程数。系统记录里程数为负数时，取state_startTiming.xml里的默认里程数。
     * @param orderId 订单ID
     * @param supercritical 系统记录里程数
     * @return
     */
    public static Map<String,TestParameter> endTiming(String orderId,double supercritical){
        //根据订单实际开始时间，设置服务结束时间为其8分钟后
        OrderTimeSpan orderTimeSpanDuringEndTiming = OrderTimeSpan.construct(OrderPhaseEnum.EndTiming,orderId);
        // orderTimeSpanDuringEndTiming.setTimeLength(8); // no need to invoke explicitly, default timeLength is 8 min
        long endTime = orderTimeSpanDuringEndTiming.getEndTime();

        return endTiming(orderId, endTime,supercritical);
    }

    /**
     * 调用State/endTiming接口结束行程，并设置结束时间和系统记录记录里程数。系统记录里程数为负数时，取state_startTiming.xml里的默认里程数。
     * @param orderId 订单ID
     * @param endTime 订单结束时间
     * @param supercritical 系统记录里程数
     * @return
     */
    public static Map<String,TestParameter> endTiming(String orderId,long endTime, double supercritical){

        Map<String,TestParameter> paraMap = MapUtil.getParameterMap("order_id",orderId);

        //car_id
        String carId =  Order.getCarIdByOrderId(orderId);
        MapUtil.put(paraMap,"car_id",carId);

        //end_time
        MapUtil.put(paraMap,"end_time",String.valueOf(endTime));

        //系统记录里程数
        if ( supercritical >= 0) {
            MapUtil.put(paraMap,"supercritical",String.valueOf(supercritical));
        }

        Map<String,TestParameter> responseMap = CaseExecutor.runTestCase("state_endTiming.xml",paraMap);

        // For show
        CommonUtil.OutputResult(responseMap.get(ParamValue.RESPONSE_RESULT).getValue());

        return responseMap;
    }

    public static Map<String,TestParameter> startTiming(String orderId){
        Map<String,TestParameter> paraMap = MapUtil.getParameterMap("order_id",orderId);

        //car_id
        String carId =  Order.getCarIdByOrderId(orderId);
        MapUtil.put(paraMap,"car_id",carId);

        //start_time
        long startTime = (new Date()).getTime()/1000;
        MapUtil.put(paraMap,"start_time",String.valueOf(startTime));

        return CaseExecutor.runTestCase("state_startTiming.xml",paraMap);
    }

    public static Map<String,TestParameter> confirmCharge(String orderId){
        return confirmCharge(orderId,
                ParamValue.NO_OVERRIDE_SUPERCRITAL,
                ParamValue.NO_OVERRIDE_MILEAGE,
                ParamValue.NO_OVERRIDE_HIGHWAY_AMOUNT,
                ParamValue.NO_OVERRIDE_PARKING_AMOUNT);

    }

    public static Map<String,TestParameter> confirmCharge(String orderId, long startTime, long endTime){
        return confirmCharge(orderId,
                startTime,
                endTime,
                ParamValue.NO_OVERRIDE_SUPERCRITAL,
                ParamValue.NO_OVERRIDE_MILEAGE,
                ParamValue.NO_OVERRIDE_HIGHWAY_AMOUNT,
                ParamValue.NO_OVERRIDE_PARKING_AMOUNT);
    }

    /**
     * 在司机端确认账单，并设置系统记录里程数，司机录入里程数，高速费，停车费
     * @param orderId 订单ID
     * @param supercritical 系统记录里程数。为负数时，取state_confirmCharge.xml里的默认值
     * @param mileage 司机录入里程数。为负数时，取state_confirmCharge.xml里的默认值
     * @param highway_amount 高速费。 为负数时，取state_confirmCharge.xml里的默认值
     * @param parking_amount 停车费。为负数时，取state_confirmCharge.xml里的默认值
     * @return
     */
    public static Map<String,TestParameter> confirmCharge(String orderId,
                                                          double supercritical,
                                                          double mileage,
                                                          double highway_amount,
                                                          double parking_amount) {
        //避免被系统判定为异常订单，将开始时间提前三分钟
        OrderTimeSpanDuringConfirmCharging orderStartEndTime =
                (OrderTimeSpanDuringConfirmCharging)OrderTimeSpan.construct(OrderPhaseEnum.ConfirmCharging,null);
        // orderStartEndTime.setStartTimeEarlier(3);// No need to invoke explicitly,  默认设置提前三分钟

        long startTime = orderStartEndTime.getStartTime();

        //orderStartEndTime.setTimeLength(8);// no need to invoke explicitly, default timeLength is 8 min
        long endTime = orderStartEndTime.getEndTime(); //结束时间为开始时间后8分钟

        return confirmCharge(orderId,startTime,endTime, supercritical, mileage,highway_amount,parking_amount);
    }

    /**
     * 在司机端确认账单。并设置订单起止时间，系统记录里程数，司机录入里程数，高速费，停车费
     * @param orderId 订单ID
     * @param startTime 服务实际开始时间
     * @param endTime 服务实际结束时间
     * @param supercritical 系统记录里程数。为负数时，取state_confirmCharge.xml里的默认值
     * @param mileage 司机录入里程数。 为负数时，取state_confirmCharge.xml里的默认值
     * @param highway_amount 高速费。 为负数时，取state_confirmCharge.xml里的默认值
     * @param parking_amount 停车费。 为负数时，取state_confirmCharge.xml里的默认值
     * @return
     */
    public static Map<String,TestParameter> confirmCharge(
            String orderId,
            long startTime,
            long endTime,
            double supercritical,
            double mileage,
            double highway_amount,
            double parking_amount){
        Map<String,TestParameter> paraMap = MapUtil.getParameterMap("order_id",orderId);
        MapUtil.put(paraMap,"start_time",String.valueOf(startTime));
        MapUtil.put(paraMap,"end_time",String.valueOf(endTime));
        if (supercritical >= 0) {
            MapUtil.put(paraMap, "supercritical", String.valueOf(supercritical));
        }
        if(mileage >= 0) {
            MapUtil.put(paraMap, "mileage", String.valueOf(mileage));
        }
        if ( highway_amount > 0) {
            MapUtil.put(paraMap, "highway_amount", String.valueOf(highway_amount));
        }
        if ( parking_amount > 0) {
            MapUtil.put(paraMap, "parking_amount", String.valueOf(parking_amount));
        }

        return CaseExecutor.runTestCase("state_confirmCharge.xml",paraMap);
    }

    public static Map<String,TestParameter> clearCar(String orderId,String driverId){
        Map<String,TestParameter> paraMap = MapUtil.getParameterMap("order_id",orderId);
        MapUtil.put(paraMap,"driver_id",driverId);

        return CaseExecutor.runTestCase("state_clearCar.xml",paraMap);
    }
}
