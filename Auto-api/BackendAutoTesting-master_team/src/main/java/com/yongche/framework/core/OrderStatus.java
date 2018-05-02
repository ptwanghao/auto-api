package com.yongche.framework.core;

/**
 * 订单状态列表
 */
public class OrderStatus {

    public static final int UNVALID = -1; //无效订单状态
    public static final int UNINIT = 0;   //未初始化订单状态
    public static final int WAIT_FOR_DISPATCH = 2; //等待派单
    public static final int WAIT_FOR_ACCEPT = 3; //等待接单
    public static final int WAIT_FOR_DRIVER = 4;//司机已接单，等待司机就位
    public static final int SERVICE_START =5; //服务开始
    public static final int IN_SERVICE =6; //服务中，等待结束
    public static final int FINISHED = 7; //服务结束
    public static final int CANCELED = 8; //订单已取消状态

    public static String toString(int status){
        String value ;
        switch (status) {
            case UNINIT:
                value = "Uninitialized";
                break;
            case WAIT_FOR_DISPATCH:
                value = "WAIT_FOR_DISPATCH";
                break;
            case WAIT_FOR_ACCEPT:
                value = "WAIT_FOR_ACCEPT";
                break;
            case WAIT_FOR_DRIVER:
                value = "WAIT_FOR_DRIVER";
                break;
            case SERVICE_START:
                value = "SERVICE_START";
                break;
            case IN_SERVICE:
                value = "IN_SERVICE";
                break;
            case FINISHED:
                value = "FINISHED";
                break;
            case CANCELED:
                value = "CANCELED";
                break;
            case UNVALID:
                default:
                    value = "Invalid";
                    break;

        }
        return value;
    }

}
