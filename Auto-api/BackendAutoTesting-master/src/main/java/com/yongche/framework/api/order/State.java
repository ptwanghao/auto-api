package com.yongche.framework.api.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.api.APIUtil;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.Map;

/**
 *
 */
public class State {
    public static Logger log = LoggerFactory.getLogger(State.class);

    public static void driverArrived(String orderId){
        Map<String,TestParameter> paraMap = MapUtil.getParameterMap("order_id",orderId);

        //car_id
        String carId =  Order.getCarIdByOrderId(orderId);
        MapUtil.put(paraMap,"car_id",carId);

        //arrive_time
        long arriveTime = (new Date()).getTime()/1000;
        MapUtil.put(paraMap,"arrive_time",String.valueOf(arriveTime));

        CaseExecutor.runTestCase("state_driveArrived.xml",paraMap);
    }

    public static void endTiming(String orderId){
        Map<String,TestParameter> paraMap = MapUtil.getParameterMap("order_id",orderId);

        //car_id
        String carId =  Order.getCarIdByOrderId(orderId);
        MapUtil.put(paraMap,"car_id",carId);

        //end_time
        long endTime = (new Date()).getTime()/1000;
        MapUtil.put(paraMap,"end_time",String.valueOf(endTime));

        CaseExecutor.runTestCase("state_endTiming.xml",paraMap);
    }

    public static void startTiming(String orderId){
        Map<String,TestParameter> paraMap = MapUtil.getParameterMap("order_id",orderId);

        //car_id
        String carId =  Order.getCarIdByOrderId(orderId);
        MapUtil.put(paraMap,"car_id",carId);

        //start_time
        long startTime = (new Date()).getTime()/1000;
        MapUtil.put(paraMap,"start_time",String.valueOf(startTime));

        CaseExecutor.runTestCase("state_startTiming.xml",paraMap);
    }

    public static void confirmCharge(String orderId){
        Map<String,TestParameter> paraMap = MapUtil.getParameterMap("order_id",orderId);

        long startTime = (new Date()).getTime()/1000 - 180;//避免被系统判定为异常订单，将开始时间提前三分钟
        MapUtil.put(paraMap,"start_time",String.valueOf(startTime));

        long endTime = (new Date()).getTime()/1000 + 300;
        MapUtil.put(paraMap,"end_time",String.valueOf(endTime));

        CaseExecutor.runTestCase("state_confirmCharge.xml",paraMap);
    }

    public static void clearCar(String orderId,String driverId){
        Map<String,TestParameter> paraMap = MapUtil.getParameterMap("order_id",orderId);
        MapUtil.put(paraMap,"driver_id",driverId);

        CaseExecutor.runTestCase("state_clearCar.xml",paraMap);
    }
}
