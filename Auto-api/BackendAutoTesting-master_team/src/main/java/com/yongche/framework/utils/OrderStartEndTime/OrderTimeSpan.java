package com.yongche.framework.utils.OrderStartEndTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class OrderTimeSpan {

    public static Logger log = LoggerFactory.getLogger(OrderTimeSpan.class);

    protected static int DEFAULT_TIME_LENGTH_IN_MINUTE = 5; // in minute

    long startTime;
    long endTime;
    int timeLength;

    /**
     * 设置订单的开始时间和时长。由子类实现
     */
    abstract public OrderTimeSpan setStartTimeAndLength();

    public long getStartTime() {
        return startTime;
    }

    public OrderTimeSpan setStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * 订单结束时间，用于State/EndTiming,State/ConfirmCharging接口调用。<br>
     * 当timeLength大于0时，结束时间=开始时间+时长 <br>
     * @return
     */
    public long getEndTime() {

        if ( timeLength > 0) {
            endTime = startTime + timeLength * 60;
        }
        return endTime;
    }

    /**
     * 设置结束时间，用于State/EndTiming,State/ConfirmCharging接口调用。<br>
     * 同时将时长设置为0，当调用getEndTime()时，将直接获得此处设置的结束时间值。 <br>
     * @param endTime
     * @return
     */
    public OrderTimeSpan setEndTime(long endTime) {
        this.endTime = endTime;
        this.timeLength = 0;
        return this;
    }

    public int getTimeLength(){
        return this.timeLength;
    }

    /**
     * 设置时长。当时长>0时，结束时间=开始时间+时长
     * @param timeLengthInMinute 时长，单位为分钟
     * @return
     */
    public OrderTimeSpan setTimeLength(int timeLengthInMinute){
        this.timeLength = timeLengthInMinute;
        return this;
    }

    /**
     * 工厂方法。构造用于不同阶段（state/endTiming, state/confirmCharging)的订单起止时间对象。
     * @param phase
     * @param orderId
     * @return
     */
    public static OrderTimeSpan construct(OrderPhaseEnum phase, String orderId){

        OrderTimeSpan orderTimeSpan = null;

        switch (phase){
            case EndTiming:
                orderTimeSpan = new OrderTimeSpanDuringEndTiming(orderId);
                break;
            case ConfirmCharging:
                orderTimeSpan = new OrderTimeSpanDuringConfirmCharging();
                break;
            default:
                log.info("Invalid OrderPhaseEnum value, exception will be raised");
                break;
        }

        return orderTimeSpan.setStartTimeAndLength();
    }

}
