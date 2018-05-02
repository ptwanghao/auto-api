package com.yongche.framework.api;


import com.yongche.framework.api.order.Order;
import com.yongche.framework.api.order.State;
import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.config.ConfigUtil;
import com.yongche.framework.core.OrderStage;
import com.yongche.framework.core.ParamValue;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.MapUtil;
import org.slf4j.*;

import java.util.HashMap;
import java.util.Map;


/**
 * 对API调用的一些封装 方便编写测试用例
 */
public class APIUtil {
    public static Logger log = LoggerFactory.getLogger(APIUtil.class);

    public static boolean acceptOrder(String orderId){
        String value = ConfigUtil.getValue(ConfigConst.DRIVER_INFO,"driver_response_timeout");
        int timeout = Integer.parseInt(value)*1000;

        boolean ret = Order.hasDriverResponse(orderId,timeout);

        if(ret) {
            afterAcceptOrder(orderId);
        }

        return ret;
    }

    /**
     * 司机接单后完成订单后续流程
     * @param orderId 订单ID
     * @return
     */
    public static Map<OrderStage, Map<String,TestParameter>> afterAcceptOrder(String orderId){
        return afterAcceptOrder(orderId,
                ParamValue.NO_OVERRIDE_SUPERCRITAL,
                ParamValue.NO_OVERRIDE_MILEAGE,
                ParamValue.NO_OVERRIDE_HIGHWAY_AMOUNT,
                ParamValue.NO_OVERRIDE_PARKING_AMOUNT);
    }

    /**
     * 司机接单后完成订单后续流程
     * @param orderId 订单ID
     * @param supercritical 系统记录里程数
     * @param mileage 司机录入里程数
     * @param highway_amount 高速费
     * @param parking_amount 停车费
     * @return
     */
    public static Map<OrderStage, Map<String,TestParameter>> afterAcceptOrder(String orderId,
                                                                              double supercritical,
                                                                              double mileage,
                                                                              double highway_amount,
                                                                              double parking_amount){
        Map<String,TestParameter> departMap, arrivedMap,startTimingMap,endTimingMap, confirmChargeMap, lastStatusMap;
        Map<OrderStage, Map<String,TestParameter>> results = new HashMap<>();
        log.info("司机出发");
        departMap = Order.setDriverDepart(orderId);
        results.put(OrderStage.DriverDepart,departMap);

        log.info("司机到达");
        arrivedMap = State.driverArrived(orderId);
        results.put(OrderStage.DriverArrived,arrivedMap);

        log.info("开始行程");
        startTimingMap = State.startTiming(orderId);
        results.put(OrderStage.StartTiming,startTimingMap);

        log.info("结束行程");
        endTimingMap = State.endTiming(orderId,supercritical);
        results.put(OrderStage.EndTiming,endTimingMap);

        log.info("确认账单");
        confirmChargeMap = State.confirmCharge(orderId,supercritical,mileage,highway_amount,parking_amount);
        results.put(OrderStage.ConfirmCharge,confirmChargeMap);

        log.info("获取订单状态");
        String status = Order.getStatus(orderId);
        lastStatusMap = MapUtil.getParameterMap("Status",status );
        results.put(OrderStage.GetLastStatus,lastStatusMap);

        return results;
    }
}
