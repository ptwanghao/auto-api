package com.yongche.driver.client;

/**
 *
 */
public class DriverInfo {
    //司机ID
    private long userId;
    //设备号
    private long deviceId;
    //用户类型 默认都是DR
    private String userType;
    //TOKEN
    private String token;
    //车号
    private String carId;


    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString(){
        return "\n" + "client user id :" + userId + "\n" +
                "user type : " + userType +"\n" +
                "device id : " + deviceId +"\n" +
                "token : " + token +"\n" +
                "car id : " + carId;
    }
}
