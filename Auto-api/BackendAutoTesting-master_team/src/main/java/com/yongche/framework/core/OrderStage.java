package com.yongche.framework.core;


/**
 * The enum describes the stages of an order from driver accepted the order to the order ended or was cancelled.
 */
public enum OrderStage {
    DriverAccepted,DriverDepart, DriverArrived, StartTiming, EndTiming, ConfirmCharge, GetLastStatus;
}
