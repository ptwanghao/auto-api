package com.yongche.framework.core;

import com.yongche.framework.utils.TestParameterMap;
import net.sf.json.JSONObject;

/**
 * The class describes the data structure of an order snapshot which is usually returned by below interface :
 *      getLastDriverSnapFromMongo
 *      getLastPassengerSnapFromMongo
 */
public class Snapshot {
    private  static final String KEY_DATA = "data";
    private  static final String KEY_PRICE = "price";
    private  static final String KEY_FORM = "form";
    private  static final String KEY_OUTPUT = "output";
    private  static final String KEY_CREATE_TIME = "create_time";
    private  static final String KEY_TYPE = "快照类型";
    private  static final String KEY_TIMESTAMP = "快照时间";
    private  static final String KEY_ID = "_id";

    private TestParameterMap data;
    private TestParameterMap price;
    private TestParameterMap form;
    private TestParameterMap output;
    private TestParameterMap createTime;
    private int snapshotType;
    private String timeStamp;
    private String id;


    public TestParameterMap getData() {
        return data;
    }

    public void setData(TestParameterMap data) {
        this.data = data;
    }

    public TestParameterMap getPrice() {
        return price;
    }

    public void setPrice(TestParameterMap price) {
        this.price = price;
    }

    public TestParameterMap getForm() {
        return form;
    }

    public void setForm(TestParameterMap form) {
        this.form = form;
    }

    public TestParameterMap getOutput() {
        return output;
    }

    public void setOutput(TestParameterMap output) {
        this.output = output;
    }

    public TestParameterMap getCreateTime() {
        return createTime;
    }

    public void setCreateTime(TestParameterMap createTime) {
        this.createTime = createTime;
    }

    public int getSnapshotType() {
        return snapshotType;
    }

    public void setSnapshotType(int snapshotType) {
        this.snapshotType = snapshotType;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    /**
     * Constuct a Snapshot object by json object
     * @param snapshotJsonObj
     * @return
     */
    public static Snapshot fromJsonObject (JSONObject snapshotJsonObj){
        Snapshot snap = new Snapshot();
        snap.setData( getData(snapshotJsonObj) );
        snap.setPrice( getPrice(snapshotJsonObj));
        snap.setForm( getform(snapshotJsonObj));
        snap.setOutput( getOutput(snapshotJsonObj));
        snap.setCreateTime( getCreateTime(snapshotJsonObj));
        snap.setId( getId(snapshotJsonObj));
        snap.setTimeStamp( getTimeStamp(snapshotJsonObj));
        snap.setSnapshotType( getSnapshotType(snapshotJsonObj));
        return snap;
    }

    private static TestParameterMap getData(JSONObject snapshotJsonObj){
        return fromJsonObject(snapshotJsonObj, KEY_DATA);
    }

    private static TestParameterMap getPrice(JSONObject snapshotJsonObj){
        return fromJsonObject(snapshotJsonObj, KEY_PRICE);
    }

    private static TestParameterMap getform(JSONObject snapshotJsonObj){
        TestParameterMap testParameterMap = new TestParameterMap();
        String value = snapshotJsonObj.get(KEY_FORM).toString();
        testParameterMap.put(KEY_FORM, value);
        return testParameterMap;
    }

    private static TestParameterMap getOutput(JSONObject snapshotJsonObj){
        return fromJsonObject(snapshotJsonObj, KEY_OUTPUT);
    }

    private static TestParameterMap getCreateTime(JSONObject snapshotJsonObj){
        return fromJsonObject(snapshotJsonObj, KEY_CREATE_TIME);
    }

    private static String getId(JSONObject snapshotJsonObj){
        return snapshotJsonObj.getString(KEY_ID);
    }

    private static String getTimeStamp(JSONObject snapshotJsonObj){
        return snapshotJsonObj.getString(KEY_TIMESTAMP);
    }

    private static int getSnapshotType(JSONObject snapshotJsonObj) {
        return snapshotJsonObj.getInt(KEY_TYPE);
    }

    private static TestParameterMap fromJsonObject(JSONObject snapshotJsonObj, String key){
        TestParameterMap testParameterMap = new TestParameterMap();
        JSONObject fields = snapshotJsonObj.getJSONObject(key);
        try {
            fields.forEach(
                    (k,v) -> {
                        testParameterMap.put((String)k,v);
                    }
            );
        }catch(Exception e){
            e.printStackTrace();
        }

        return testParameterMap;
    }

}
