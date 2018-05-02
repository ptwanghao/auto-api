package com.yongche.framework.api.charge;

public class DriverSnap extends ChargeSnap{

    public static final String XML_FILE_GET_LAST_DRIVER_SNAP = "charge/charge_getLastDriverSnapFromMongo.xml";
    public static final String XML_FILE_GET_DRIVER_SNAP = "charge/charge_getDriverSnapFromMongo.xml";

    @Override
    public String getLastSnapXmlFileName() {
        return XML_FILE_GET_LAST_DRIVER_SNAP;
    }

    @Override
    public String getSnapListXmlFileName() {
        return XML_FILE_GET_DRIVER_SNAP;
    }
}
