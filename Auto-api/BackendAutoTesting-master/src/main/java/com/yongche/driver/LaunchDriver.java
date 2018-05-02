package com.yongche.driver;


import com.yongche.driver.client.DriverModel;
import com.yongche.driver.client.Thrift;

/**
 *
 */



public class LaunchDriver {
    //public static Logger log = LoggerFactory.getLogger(LaunchDriver.class);

    public static void main(String []args){
        DriverModel.run();
        Thrift.run();
    }
}
