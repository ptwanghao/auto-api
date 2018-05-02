package com.yongche.framework.core;

import com.yongche.framework.utils.ConfigUtil;

/**
 * API参数取值列表
 */
public class ParamValue {

    public static final String RESPONSE_RESULT = "RESPONSE_RESULT";

    //Product Type ID
    public static final String PRODUCT_TYPE_ID_HOUR_RENT_HOUR = "1"; //时租-包时
    public static final String PRODUCT_TYPE_ID_IMMEDIATE = "17"; //时租-马上用车
    public static final String PRODUCT_TYPE_ID_AIRPORT_PICKUP = "7"; //定价-接机
    public static final String PRODUCT_TYPE_ID_AIRPORT_DROP_OFF = "8"; //定价-送机
    public static final String PRODUCT_TYPE_ID_RENTAL_HALF_DAY = "11"; //定价-半日租
    public static final String PRODUCT_TYPE_ID_RENTAL_ONE_DAY = "12"; //定价-日租

    public static final String FIXED_PRODUCT_ID_STATION_PICKUP = "464";//接站
    public static final String FIXED_PRODUCT_ID_STATION_DROPOFF = "467";//送站

    //Car_type_id
    public static final String CAR_TYPE_ID_COMFORTABLE = "2"; //舒适型
    public static final String CAR_TYPE_ID_DELUXE = "3"; //豪华型
    public static final String CAR_TYPE_ID_LUXURY = "4"; //奢侈型
    public static final String CAR_TYPE_ID_YOUNG = "37"; //Young型

    //City
    public static final String CITY_BEIJING = "bj";

    //Source
    public static final String SOURCE_ERP = "4000001";
    public static final String SOURCE_ANDROIDAPP = "3000001";

    //Fixed product id
    public static final String FIXED_PRODUCT_ID_RENTAL_ONE_DAY = "84";
    public static final String FIXED_PRODUCT_ID_AIRPORT_DROP_OFF_BJ = "847";

    //cancelOrder reason_id
    public static final String FORCE_CANCEL_ORDER = "69";

    public static final int DRIVER_RESPONSE_TIMEOUT = Integer.parseInt( ConfigUtil.getValue(ConfigConst.DRIVER_INFO,"driver_response_timeout") )*1000;

    //app-api
    public static final int GET_ACCEPT_CAR_TIMEOUT = 30*1000;
    public static final String APP_API_SECRET_KEY = "1bW4KASQPawc2G8DNBtIJmr35SzRcBCb";
    public static final String ACCESS_TOKEN = "Bearer b595a4a2450393212ca5109eff781a90612c1048";

    //帐户类型
    public static final String BALANCE_TYPE_MAIN = "1"; //主帐户
    public static final String BALANCE_TYPE_SUB = "2"; // 一级副帐户
    public static final String BALANCE_TYPE_BUCKET = "4"; //

}
