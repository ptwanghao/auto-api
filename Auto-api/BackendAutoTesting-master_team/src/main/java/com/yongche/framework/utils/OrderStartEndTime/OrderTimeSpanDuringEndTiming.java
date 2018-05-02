package com.yongche.framework.utils.OrderStartEndTime;

import com.yongche.framework.api.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取订单的实际开始时间，将结束时间设置为OrderTimeSpan.DEFAULT_TIME_LENGTH_IN_MINUTE分鐘之後
 */
public class OrderTimeSpanDuringEndTiming extends OrderTimeSpan {
    public static Logger log = LoggerFactory.getLogger(OrderTimeSpanDuringEndTiming.class);

    protected long actualStartTime;

    public OrderTimeSpanDuringEndTiming(String orderId){
        //根据订单ID，获取订单开始时间
        actualStartTime = Order.getStartTimeByOrderId(orderId);
    }

    @Override
    public OrderTimeSpan setStartTimeAndLength(){
        this.setStartTime(actualStartTime);
        this.setTimeLength(OrderTimeSpan.DEFAULT_TIME_LENGTH_IN_MINUTE);
        log.info("Set start_time and time length for state/endTiming");
        log.info("    Start time  : " + this.startTime );
        log.info("    Time length : " + this.timeLength + " minutes");
        return this;
    }
}
