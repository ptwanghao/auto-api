package com.yongche.framework.utils;


import com.yongche.driver.client.DriverInfo;
import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.core.RiderInfo;
import com.yongche.framework.core.TestCase;
import com.yongche.framework.core.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 */
public class CommonUtil {
    public static Logger log = LoggerFactory.getLogger(CommonUtil.class);
    private static ArrayList<RiderInfo> riderList;
    private static AtomicInteger value = new AtomicInteger(-1);

    static {
        riderList = new ArrayList<>();// 乘客信息列表
        String riderData = ConfigUtil.getValue(ConfigConst.RIDER_INFO, "rider_info_data");

        try {
            File file = new File(riderData);
            FileReader reader = new FileReader(file);
            BufferedReader buffer = new BufferedReader(reader);
            String line;

            while ((line = buffer.readLine()) != null) {
                RiderInfo riderInfo = new RiderInfo();
                if (line.charAt(0) == '#')
                    continue;
                String[] values = line.split(",");
                riderInfo.setUserId(values[0]);
                riderInfo.setCellphone(values[1]);

                riderList.add(riderInfo);
            }
        }catch (Exception e){
            log.info("Failt to read rider data info. " + e.getMessage());
        }
    }

    public static String getHttpUrl(TestSuite suite, TestCase testcase){
        String host = suite.getHost();
        host = ConfigUtil.getValue(ConfigConst.HOST,host.toUpperCase());

        return "http://" + host + "/" + testcase.getModule();
    }

    public static String[] getServiceCenter(){
        String serviceCenter = ConfigUtil.getValue(ConfigConst.TEST_EXECUTION_CONFIG,ConfigConst.SERVICE_CENTER);
        serviceCenter = serviceCenter.trim().replace(" ","");
        return serviceCenter.split("\\|");
    }

    public static double round(double value){
        String temp = String.format("%.1f",value);
        value = Double.parseDouble(temp);
        return value;
    }

    public static void sleep(int microsecond){
        try {
            Thread.sleep(microsecond);
        } catch (InterruptedException e) {
            log.info(e.getMessage());
        }
    }

    public static ArrayList<DriverInfo> getDriverList(String data){
        ArrayList<DriverInfo> driverList = new ArrayList<>();// 司机信息列表

        try {
            //File file = new File("driverinfo.dat");
            File file = new File(data);
            FileReader reader = new FileReader(file);
            BufferedReader buffer = new BufferedReader(reader);
            String line;

            while ((line = buffer.readLine()) != null) {
                DriverInfo driverInfo = new DriverInfo();
                if (line.charAt(0) == '#')
                    continue;
                String[] values = line.split(",");
                driverInfo.setUserId(Long.parseLong(values[0]));
                driverInfo.setDeviceId(Long.parseLong(values[1]));
                driverInfo.setUserType(values[2]);
                driverInfo.setToken(values[3]);
                driverInfo.setCarId(values[4]);
                driverList.add(driverInfo);
            }
        }catch (Exception e){
            log.info("Fail to read driver info" + e.getMessage());
        }

        return driverList;
    }

    //逐行返回riderinfo里的数据
    public static RiderInfo getRider(){
        int index = value.incrementAndGet();
        int length = riderList.size();
        index = index % length;

        return  riderList.get(index);
    }
}
