package com.yongche.driver.common;

import com.yongche.driver.client.DriverInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 *
 */
public class PropertyUtil {
    /*
    static {
        //initProperties("driver.ini");
    }

    public static Logger log = LoggerFactory.getLogger(PropertyUtil.class);
    private static Map<String, String> properties;

    private static void initProperties(String file) {
        properties = new HashMap<>();
        Properties p = new Properties();

        try {
            FileReader reader = new FileReader(new File(file));
            p.load(reader);
        }catch (Exception e){
            log.error("Fail to read " + file + " due to error :" +e.getMessage());
        }

        Set<Object> keySet = p.keySet();
        for (Object object : keySet) {
            String key = (String) object;
            properties.put(key, p.getProperty(key));
        }
    }

    //public static String getProperty(String key) {
    //    return properties.get(key);
    //}
    */
/*
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
            e.printStackTrace();
        }

        return driverList;
    }
*/

}
