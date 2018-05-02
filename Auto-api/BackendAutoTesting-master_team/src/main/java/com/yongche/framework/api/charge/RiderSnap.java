package com.yongche.framework.api.charge;

public class RiderSnap extends ChargeSnap{

    public static final String XML_FILE_GET_LAST_PASSENGER_SNAP = "charge/charge_getLastPassengerSnapFromMongo.xml";
    public static final String XML_FILE_GET_PASSENGER_SNAP_LIST = "charge/charge_getPassengerSnapFromMongo.xml";

    @Override
    public String getLastSnapXmlFileName() {
        return XML_FILE_GET_LAST_PASSENGER_SNAP;
    }

    @Override
    public String getSnapListXmlFileName() {
        return XML_FILE_GET_PASSENGER_SNAP_LIST;
    }


}
