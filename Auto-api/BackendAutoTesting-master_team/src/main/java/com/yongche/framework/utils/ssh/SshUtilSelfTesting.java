package com.yongche.framework.utils.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class SshUtilSelfTesting {
    public static Logger log = LoggerFactory.getLogger(SshUtilSelfTesting.class);
    /**
     * self-host testing purpose
     * @param databaseName
     * @return
     */
    private static int testDBUtil(String databaseName){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>Self-testing starts : " + databaseName + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        int pass = 0;
        com.yongche.framework.utils.db.DBUtil dbUtil = null;
        try {
            dbUtil = com.yongche.framework.utils.db.DBUtil.getDBInstance(databaseName);
            log.info(dbUtil.getConn().toString());
            log.info("Success : db connection to : " + databaseName  + " is established");
            pass = 1;
        }catch(Exception e){
            log.error("Failure : db connection to : " + databaseName + " is not established, exp = " + e.getMessage());
            e.printStackTrace();
        }finally {
            if (null != dbUtil) {
                dbUtil.closeDB();
            }
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>Self-testing ends : " + databaseName + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        return pass;

    }

    /**
     * self-host testing purpose
     * @param dbIndex
     * @return
     */
    private static int testAccountDBUtil(int dbIndex){
        int pass = 0;
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>Self-testing starts : yc_account" + dbIndex + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        String result = com.yongche.framework.utils.db.AccountDBUtil.getDataFromLastRow(100,"select * from account", 4);
        if ( null != result ) {
            log.info("Result fetched by AccountDBUtil : " + result);
            pass = 1;
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>Self-testing ends : yc_account" + dbIndex + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return pass;
    }


    /**
     * self-host testing
     * @param args
     */
    public static void main(String []args) {
        List<String> dbList = new ArrayList<>();
        dbList.add("YC_ORDER");
        dbList.add("YC_ORDER_GLOBAL");
        dbList.add("YC_CRM_COMMON");
        dbList.add("YC_COUPON");

        final int[] passCount = {0,0};

        dbList.forEach(db -> { passCount[0] += testDBUtil(db);});

        int[] dbIndexList= {100,128,129};
        for(int index : dbIndexList){
            passCount[1] += testAccountDBUtil(index);
        }

        log.info("======================================");
        log.info("Test DBUtil");
        log.info("Passed count : " + passCount[0] + " expected count : " + dbList.size());
        Assert(passCount[0], dbList.size(), "Test DBUtil");
        log.info("======================================");

        log.info("======================================");
        log.info("Test AcccountDBUtil");
        log.info("Passed count : " + passCount[1] + " expected count : " + dbIndexList.length);
        Assert(passCount[1], dbIndexList.length, "Test AcccountDBUtil");
        log.info("======================================");
    }

    private static void Assert(int actual, int expect, String message){
        if(actual == expect) {
            log.info(message + " >>>Passed<<<");
        }else {
            log.error(message + " >>>Failed<<<");
        }
    }
}
