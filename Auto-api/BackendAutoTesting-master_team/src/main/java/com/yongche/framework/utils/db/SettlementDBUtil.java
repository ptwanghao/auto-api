package com.yongche.framework.utils.db;

import com.yongche.framework.config.ConfigUtil;
import com.yongche.framework.core.ConfigConst;

import java.util.List;
import java.util.Map;

public class SettlementDBUtil extends DBUtil{
    private SettlementDBUtil(int DBIndex){
        connectToDB(DBIndex);
    }

    /**根据 DBName 创建DBUtil的实例
     * @param DBIndex 数据库的名字
     * @return DBUtil实例
     */
    public static SettlementDBUtil getDBInstance(int DBIndex){
        return new SettlementDBUtil(DBIndex);
    }

    /**连接数据库并返回SQL执行最后一行指定列的结果
     * @param DBIndex 数据库的名字
     * @param sql 要执行的SQL语句
     * @param column 返回结果中指定的列
     * @return SQL执行结果中最后一行中指定的列的值
     */
    public static String getDataFromLastRow(int DBIndex,String sql,int column){
        SettlementDBUtil db = SettlementDBUtil.getDBInstance(DBIndex);
        String lastResult = db.getDataFromLastRow(sql,column);
        db.closeDB();
        return lastResult;
    }

    public static List<Map<String,String>> getResultSet(int DBIndex,String sql){
        SettlementDBUtil db = SettlementDBUtil.getDBInstance(DBIndex);
        List<Map<String,String>> resultList = db.getResultSet(sql);
        return resultList;
    }


    /**连接到数据库
     * @param dbIndex  数据库分库后缀索引
     */
    public void connectToDB(int dbIndex){
        String configSection = ConfigConst.YC_DRIVER_SETTLEMENT;
        String host= ConfigUtil.getValue(configSection, ConfigConst.HOSTNAME);
        String databaseName = ConfigUtil.getValue(configSection,ConfigConst.DATABASE_NAME) + dbIndex;
        String user= ConfigUtil.getValue(configSection, ConfigConst.USER);
        String password= ConfigUtil.getValue(configSection, ConfigConst.PASSWORD);
        connectToDB(host,databaseName,user,password);
    }
}
