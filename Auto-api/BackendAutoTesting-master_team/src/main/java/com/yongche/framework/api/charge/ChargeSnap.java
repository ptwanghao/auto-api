package com.yongche.framework.api.charge;


import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.ParamValue;
import com.yongche.framework.core.Snapshot;
import com.yongche.framework.core.TestParameter;
import com.yongche.framework.utils.MapUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract public class ChargeSnap {
    public static Logger log = LoggerFactory.getLogger(ChargeSnap.class);

    public abstract String getLastSnapXmlFileName();
    public abstract String getSnapListXmlFileName();

    /**
     * Get last driver or passenger snap from Mongo DB
     * @param orderId
     * @return
     */
    public Snapshot getLastSnapFromMongo(String orderId){
        Map<String, TestParameter> outputParameters = execute(getLastSnapXmlFileName(), orderId);
        String response = MapUtil.getString(outputParameters, ParamValue.RESPONSE_RESULT);
        String resultOfResponse = JSONObject.fromObject(response).get("result").toString();
        JSONObject snapshotJsonObject = JSONObject.fromObject(resultOfResponse);
        return Snapshot.fromJsonObject(snapshotJsonObject);
    }

    /**
     * Get driver or passenger's snap list from Mongo DB
     * @param orderId
     * @return
     */
    public List<Snapshot> getSnapListFromMongo(String orderId){
        List<Snapshot> snapshotList = new ArrayList<>();

        Map<String, TestParameter> outputParameters = execute(getSnapListXmlFileName(),orderId);

        /**
         * 接口返回结果是数组 {ret_code:200,result:[{},{},...{}]}
         */

        String response = MapUtil.getString(outputParameters, ParamValue.RESPONSE_RESULT);
        String resultOfResponse = JSONObject.fromObject(response).get("result").toString();
        JSONArray snapshots = JSONArray.fromObject(resultOfResponse);
        // 遍历数组每个元素，创建Snapshot对象并添加到列表中
        snapshots.forEach(snapshotJsonObject -> snapshotList.add(Snapshot.fromJsonObject((JSONObject) snapshotJsonObject)) );

        return snapshotList;
    }

    /**
     * Execute the backend API invoking
     * @param xmlFileName
     * @param orderId
     * @return
     */
    protected Map<String,TestParameter> execute(String xmlFileName, String orderId){
        Map<String, TestParameter> inputParameterMap = MapUtil.getParameterMap("service_order_id", orderId);
        return CaseExecutor.runTestCase(xmlFileName,inputParameterMap);
    }

    /**
     * Construct the DriverSnap or RiderSnap object
     * @param snapType
     * @return
     */
    public static ChargeSnap construct (SnapType snapType) {
        ChargeSnap snap = null;
        switch (snapType) {
            case Passenger:
                snap = new RiderSnap();
                break;
            case Driver:
                snap = new DriverSnap();
                break;
            default:
                break;

        }
        return snap;
    }

    public enum SnapType{
        Passenger, Driver
    }

    /**
     * Self-host testing entrance
     * @param args
     */
    public static void main(String[] args){
        test(SnapType.Driver);
        test(SnapType.Passenger);
    }

    /**
     * self-host testing method
     * @param snapType
     */
    private static void test(SnapType snapType){
        s_count = 0;
        log.info("=======Test " + snapType.name() + "==========");
        ChargeSnap chargeSnap = ChargeSnap.construct(snapType);
        log.info("getLastSnapXmlFileName : " + chargeSnap.getLastSnapXmlFileName());
        log.info("getSnapListXmlFileName" + chargeSnap.getSnapListXmlFileName());

        Snapshot snap = chargeSnap.getLastSnapFromMongo("6428722985509621092");
        show(snap);

        List<Snapshot> snapshotList = chargeSnap.getSnapListFromMongo( "6428722985509621092" );
        snapshotList.forEach( a ->show(a) );
    }

    private static int s_count = 0; // self-host testing counter

    /**
     * self-host testing method , output message
     * @param snap
     */
    private static void show(Snapshot snap){
        log.info("---------------");
        log.info("Count : " + s_count);
        s_count ++;
        log.info(snap.getId());
        log.info(snap.getTimeStamp());
        log.info(snap.getCreateTime().getString("sec"));
        log.info(snap.getData().getString("产品类型ID"));
        log.info(snap.getForm().toString());
        log.info(snap.getPrice().getString("长途服务公里单价"));
        log.info(snap.getOutput().getString("订单原始金额"));
        log.info("---------------");
    }
}
