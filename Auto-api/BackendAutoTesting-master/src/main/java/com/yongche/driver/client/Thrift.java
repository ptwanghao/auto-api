package com.yongche.driver.client;


import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.yongche.LocationServiceThriftClient;
import com.yongche.driver.common.PropertyUtil;
import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Thrift {
    public static Logger log = LoggerFactory.getLogger(Thrift.class);

    private static List<DriverInfo> driverList = new LinkedList<>();

    private static ExecutorService fixedThreadPool;

    static {
        try {
            String driverinfo = ConfigUtil.getValue(ConfigConst.DRIVER_INFO, "driver_info_data");
            driverList = CommonUtil.getDriverList(driverinfo);
            int threadCount = driverList.size();
            fixedThreadPool = Executors.newFixedThreadPool(threadCount);//(Integer.parseInt(PropertyUtil.getProperty("threadcount")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void run(){
        // 上传位置 司机坐标，间隔3秒。
        fixedThreadPool.execute(new Runnable() {
            public void run() {

                String latitude = ConfigUtil.getValue(ConfigConst.DRIVER_INFO, "latitude");;
                String longitude = ConfigUtil.getValue(ConfigConst.DRIVER_INFO, "longitude");
                log.info("latitude : " + latitude);
                log.info("longitude : " + longitude);

                while (true) {
                    for (final DriverInfo driverInfo : driverList) {

                        final String[] IpPort = setIPAndPort(driverInfo.getUserId());
                        LocationServiceThriftClient client = new LocationServiceThriftClient(IpPort[0], Integer.parseInt(IpPort[1]));

                        try {
                            client.open();
                            double lat = Double.valueOf(latitude + RandomNum(3));//38.77
                            double lon = Double.valueOf(longitude + RandomNum(3));//82.68
                            //log.info("latitude: " + lat + " longitude: " + lon);

                            client.add(String.valueOf(driverInfo.getUserId()), lat, lon);
                            //log.info(lat + "---" + lon + "---drivers thrift updated success. IP:" + IpPort[0] + ":" + IpPort[1] + "  driverID:" + String.valueOf(driverInfo.getUserId()));
                            //client.add(String.valueOf(driverInfo.getUserId()), Double.valueOf("38.297" + RandomNum(3)), Double.valueOf("97.584" + RandomNum(3)));

                            Thread.sleep(5000);
                        } catch (Exception e) {
                            log.info("Fail to upload data to LBS Driver: " + driverInfo);
                            e.printStackTrace();
                        } finally {
                            client.close();
                        }
                    }
                }
            }
        });
    }

    private static String RandomNum(int length) {
        String base = "0123456789";

        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    private static String[] setIPAndPort(long userId) {
        String[] ipAndPort = new String[2];

        ipAndPort[0] = ConfigUtil.getValue(ConfigConst.DRIVER_INFO, "ip_lbs");

        if (userId % 2 == 0){
            ipAndPort[1] = ConfigUtil.getValue(ConfigConst.DRIVER_INFO, "port2");
        } else {
            ipAndPort[1] = ConfigUtil.getValue(ConfigConst.DRIVER_INFO, "port1");
        }

        return ipAndPort;
    }
}
