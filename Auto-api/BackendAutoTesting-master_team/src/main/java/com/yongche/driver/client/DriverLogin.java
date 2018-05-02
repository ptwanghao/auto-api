package com.yongche.driver.client;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.yongche.driver.common.WorkerClient;
import com.yongche.driver.message.AcknowledgeMessage;
import com.yongche.driver.message.GroundhogMessage;
import com.yongche.driver.message.PingMessage;
import com.yongche.driver.message.PushRequestMessage;
import com.yongche.driver.push.MCConnection;
import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.config.ConfigUtil;
import net.sf.json.*;


import com.yongche.driver.common.PSFClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverLogin implements Runnable {
    public  Logger log = LoggerFactory.getLogger(DriverLogin.class);
    
    private MCConnection persistentConnection;
    private PSFClient psf = null;
    private PSFClient.PSFRPCRequestData request = null;
    private DriverInfo driverInfo;

    public DriverLogin(DriverInfo driverInfo) {
        this.driverInfo = driverInfo;
        final String[] hostList = ConfigUtil.getValue(ConfigConst.DRIVER_INFO, "driver_host").split(",");
        ArrayList<String> managerHosts = new ArrayList<>();

        for (String host : hostList) {
                managerHosts.add(host);
        }
        
        JSONArray arr = JSONArray.fromObject(managerHosts);
        String hosts = arr.toString();
        log.info("Driver hosts: " + hosts);

        // 没有传service 和 strage。 strage的内容直接用setSessionInfo传了
        // persistentConnection = new McPersistentConnection(null, null);
        persistentConnection = new MCConnection(null);
        persistentConnection.setSessionInfo(hosts, System.currentTimeMillis(), driverInfo.getUserType(), driverInfo.getUserId(), driverInfo.getDeviceId(), driverInfo.getToken());
        
        String[] serviceCenter = CommonUtil.getServiceCenter();
        //{ "10.0.11.71:5201", "10.0.11.72:5201" };           // 测试环境
        //{ "172.17.0.77:5201", "172.17.0.78:5201" };         // 线上环境
        try {
            psf = new PSFClient(serviceCenter);
            request = new PSFClient.PSFRPCRequestData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        LoginMC();
    }

    public void LoginMC() {
        try {
            persistentConnection.retrieveWorkerInfo();
            persistentConnection.makeWorkerConnection();

            // 保持心跳，50秒发一次
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        long threadId = Thread.currentThread().getId();

                        try {
                            //log.info("Send ping message " + threadId);
                            persistentConnection.connection.ping(60);
                            Thread.sleep(50000);
                        } catch (Exception e) {
                            log.info("Thread " + threadId + " fail to send ping message");
                        }
                    }
                }
            }).start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        loopRec();
    }

    /**
     * 收消息，不停
     */
    public void loopRec() {
        WorkerClient wc = persistentConnection.connection;

        while (wc.isConnected()) {
            try {
                GroundhogMessage recvMsg = wc.getMsg();
               //log.info("Message type：" + recvMsg.getClass());

                if (recvMsg instanceof PushRequestMessage) {
                    log.info("PushRequestMessage coming:");
                    PushRequestMessage pushMsg = (PushRequestMessage) recvMsg;
                    wc.sendAckForMessage(pushMsg);

                    if (pushMsg.messageType == GroundhogMessage.MESSAGE_TYPE_DISPATCH) {
                        JSONObject jobj = JSONObject.fromObject(pushMsg.pushMessage);
                        final int round = jobj.getJSONObject("params").getJSONObject("order").getInt("round");
                        final String orderNo = jobj.getJSONObject("params").getJSONObject("order").getString("order_id");
                        final int batch = jobj.getJSONObject("params").getJSONObject("order").getInt("batch");
                        String dispatchInfo = jobj.toString();

                        int index = dispatchInfo.indexOf("test-dispatch");
                        String nameStr = "passenger_name";
                        int indexName = dispatchInfo.indexOf(nameStr) ;
                        log.info("司机 : " + driverInfo.getUserId()+ " 收到派单信息 " + orderNo + " round : " + round + " batch : " + batch + " " + dispatchInfo.substring(indexName,indexName + nameStr.length() + 28));

                        //if (round == 1 && (batch == 1) && (-1 != index)) {
                        if (round <= 2 && (batch <= 2) && (-1 != index)) {
                            log.info("司机 : " + driverInfo.getUserId()+ " 派单信息  " + dispatchInfo);
                            grabOrder(orderNo, String.valueOf(batch), String.valueOf(round));
                        }

                    }
                } else if (recvMsg instanceof PingMessage) {
                    log.info("Ping message");// 这个是测试心跳是否回包的
                } else if (recvMsg instanceof AcknowledgeMessage) {
                    wc.sendAckForMessage(recvMsg);
                    //log.info("AcknowledgeMessage ack");
                }
            }catch(Exception e){
                log.info(e.getMessage());
                log.info(String.valueOf("Driver id: " + driverInfo.getUserId()) + " caught exception while receiving message");
                wc.close();
            }
        }

        log.info("Notice: Driver : " + driverInfo.getUserId()+ " is not connected to MC!!!\n");
    }


    /**
     * 司机抢单
     *
     * @param orderNo
     * @param batch
     * @param round
     */
    private void grabOrder(String orderNo, String batch, String round) {
        // log.info("=========司机接单.订单id：" + orderNo + " 司机driverId" +
        // driverInfo.getUserId());
        request.data = "";
        String params = "order_id=" + orderNo + "&in_coord_type=baidu&car_id=" + driverInfo.getCarId() + "&latitude=36.902685&longitude=100.152054&drive_time=1800&batch=" + batch + "&round=" + round + "&is_auto=0&distance=1000";
        request.service_uri = "/Dispatch/driverResponse?" + params;
        String response = "";
        long start = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        String dateStr = sdf.format(start);

        try {
            response = psf.call("dispatch", request);
           // log.info("司机 " + driverInfo.getUserId() + " 抢单结果：" + response);// 打印出来测试方法，测试时注掉
            log.info("=========司机接单 " + driverInfo.getUserId() + " 订单id：" + orderNo  + " 时间: " + dateStr + " Dispatch response : " + response);
        } catch (Exception e) {
            log.info("司机 " + driverInfo.getUserId() + " 接单失败! Order ID: " + orderNo);
            e.printStackTrace();
        }
    }

    public void Close() {
        persistentConnection.close();
    }

}
