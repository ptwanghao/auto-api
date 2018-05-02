package com.yongche.driver.push;

final public class SubscribeInfo {
    public long driverId = 0;
    public long orderId;
    public short messageType;
    public long retryTimeout;
    public long lastRecvPkgTime;
}
