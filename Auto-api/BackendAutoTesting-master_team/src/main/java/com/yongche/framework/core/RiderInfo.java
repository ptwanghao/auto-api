package com.yongche.framework.core;

/**
 *
 */
public class RiderInfo {
    private String userId;

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    private String cellphone;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String passengerName;

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    @Override
    public String toString(){
        return "User Id : " + userId + " phone : " + cellphone + " name : " + getPassengerName();
    }

}

