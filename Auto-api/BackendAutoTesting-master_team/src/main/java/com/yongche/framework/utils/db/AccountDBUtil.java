package com.yongche.framework.utils.db;

import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.config.ConfigUtil;

public class AccountDBUtil extends DBUtil{

    private AccountDBUtil(int DBIndex){
        connectToDB(DBIndex);
    }

    /**根据 DBName 创建DBUtil的实例
     * @param DBIndex 数据库的名字
     * @return DBUtil实例
     */
    public static AccountDBUtil getDBInstance(int DBIndex){
        return new AccountDBUtil(DBIndex);
    }

    /**连接数据库并返回SQL执行最后一行指定列的结果
     * @param DBIndex 数据库的名字
     * @param sql 要执行的SQL语句
     * @param column 返回结果中指定的列
     * @return SQL执行结果中最后一行中指定的列的值
     */
    public static String getDataFromLastRow(int DBIndex,String sql,int column){
        AccountDBUtil db = AccountDBUtil.getDBInstance(DBIndex);
        String lastResult = db.getDataFromLastRow(sql,column);
        db.closeDB();
        return lastResult;
    }

    /**连接数据库并执行SQL更新数据库
     * @param dbIndex 数据库分库后缀索引
     * @param sql 要执行的SQL语句
     * @return 如果是SQL Data Manipulation Language语句返回更数的行数 否则返回0
     */
    public static int updateAccountDB(int dbIndex,String sql){
        AccountDBUtil db = AccountDBUtil.getDBInstance(dbIndex);
        int res = db.updateDB(sql);
        db.closeDB();

        return res;
    }

    /**连接到数据库
     * @param dbIndex  数据库分库后缀索引
     */
    public void connectToDB(int dbIndex){
        String configSection = dbIndex<=128 ? ConfigConst.YC_ACCOUNT1 : ConfigConst.YC_ACCOUNT2;
        String host= ConfigUtil.getValue(configSection, ConfigConst.HOSTNAME);
        String databaseName = ConfigUtil.getValue(configSection,ConfigConst.DATABASE_NAME) + dbIndex;
        String user= ConfigUtil.getValue(configSection, ConfigConst.USER);
        String password= ConfigUtil.getValue(configSection, ConfigConst.PASSWORD);

        connectToDB(host,databaseName,user,password);
    }
}
