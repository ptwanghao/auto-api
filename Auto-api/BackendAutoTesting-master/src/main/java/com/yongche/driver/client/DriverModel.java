package com.yongche.driver.client;

/**
 *
 */

import com.yongche.driver.common.PropertyUtil;
import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.utils.CommonUtil;
import com.yongche.framework.utils.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;


public class DriverModel {
    public static Logger log = LoggerFactory.getLogger(DriverModel.class);

    public static void run() {
        String driverInfoData = ConfigUtil.getValue(ConfigConst.DRIVER_INFO, "driver_info_data");
        log.info(driverInfoData);
        ArrayList<DriverInfo> driverList  = CommonUtil.getDriverList(driverInfoData);

        int threadCount = driverList.size();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadCount);
        log.info("Drive Model Thread count " + threadCount);

        for (final DriverInfo driverInfo : driverList) {
            fixedThreadPool.execute(new DriverLogin(driverInfo));
        }
    }
}
