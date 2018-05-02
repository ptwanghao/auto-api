package com.yongche.framework.utils.OrderStartEndTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 避免被系统判定为异常订单，将开始时间提前, 默认提前三分钟 <br>
 * 结束时间为开始时间 + OrderTimeSpan.DEFAULT_TIME_LENGTH_IN_MINUTE <br>
 */
public class OrderTimeSpanDuringConfirmCharging extends OrderTimeSpan {

    public static Logger log = LoggerFactory.getLogger(OrderTimeSpanDuringConfirmCharging.class);

    private static int START_TIME_EARLIER = 3 * 60; // in seconds

    private  int startTimeEarlier = START_TIME_EARLIER ;

    public int getStartTimeEarlier() {
        return startTimeEarlier;
    }

    public OrderTimeSpanDuringConfirmCharging setStartTimeEarlier(int startTimeEarlier) {
        this.startTimeEarlier = startTimeEarlier;
        return (OrderTimeSpanDuringConfirmCharging)setStartTimeAndLength();
    }

    @Override
    public OrderTimeSpan setStartTimeAndLength(){
        long now = new Date().getTime() / 1000;
        this.setStartTime(now - startTimeEarlier); //避免被系统判定为异常订单，将开始时间提前。默认提前三分钟
        this.setTimeLength(OrderTimeSpan.DEFAULT_TIME_LENGTH_IN_MINUTE);
        log.info("Set start_time and time length for state/confirmCharging");
        log.info("    当前时间 : " + now);
        log.info("    提前 " + this.startTimeEarlier + " seconds");
        log.info("    Start time  : " + this.startTime );
        log.info("    Time length : " + this.timeLength + " minutes");
        return this;
    }

}
