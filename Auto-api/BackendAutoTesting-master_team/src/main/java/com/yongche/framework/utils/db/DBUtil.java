package com.yongche.framework.utils.db;


import com.jcraft.jsch.Session;
import com.yongche.framework.core.ConfigConst;
import com.yongche.framework.config.ConfigUtil;
import com.yongche.framework.utils.ssh.DatabaseOverSSH;
import com.yongche.framework.utils.ssh.SshUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtil {
    private static Logger log = LoggerFactory.getLogger(DBUtil.class);
    protected Connection conn;
    protected final int CONNECTION_TIMEOUT = 20;
    protected Session session; // SSH session

    public Connection getConn(){return conn;}

    protected DBUtil(){}

    private  DBUtil(String DBName){
            connectToDB(DBName);
    }

    /**根据 DBName 创建DBUtil的实例
     * @param DBName 数据库的名字
     * @return DBUtil实例
     */
    public static DBUtil getDBInstance(String DBName){
        return new DBUtil(DBName);
    }

    /**连接数据库并返回SQL执行最后一行指定列的结果
     * @param DBName 数据库的名字
     * @param sql 要执行的SQL语句
     * @param column 返回结果中指定的列
     * @return SQL执行结果中最后一行中指定的列的值
    */
    public static String getDataFromLastRow(String DBName,String sql,int column){
        DBUtil db = DBUtil.getDBInstance(DBName);
        String lastResult = db.getDataFromLastRow(sql,column);
        db.closeDB();

        return lastResult;
    }

    /**连接数据库并执行SQL更新数据库
     * @param DBName 数据库的名字
     * @param sql 要执行的SQL语句
     * @return 如果是SQL Data Manipulation Language语句返回更数的行数 否则返回0
     */
    public static int updateDB(String DBName,String sql){
        DBUtil db = DBUtil.getDBInstance(DBName);
        int res = db.updateDB(sql);
        db.closeDB();

        return res;
    }

    /**执行SQL更新数据库
     * @param sql 要执行的SQL语句
     * @return 如果是SQL Data Manipulation Language语句返回更数的行数 否则返回0
     */
    protected int updateDB(String sql){
        int res = -1;
        PreparedStatement pst = null;

        try {
            conn.prepareStatement(sql);
            pst = conn.prepareStatement(sql);
            res  = pst.executeUpdate();

        } catch (Exception e) {
            log.error("Fail to update database with SQL: " + sql);
            e.printStackTrace();
        }finally {
            try {
                if (null != pst) {
                    pst.close();
                }
            }catch (Exception e){
                log.error("Fail to close prepareStatement");
            }
        }

        return res;
    }

    /**返回SQL语句的执行结果
     * @param sql 要执行的SQL语句
     * @return List<Map<String,String>> 存放着ResultSet的所有结果
     */
    public List<Map<String,String>> getResultSet(String sql){
        List<Map<String,String>> resultMap = new ArrayList<>();
        PreparedStatement pst = null;;
        ResultSet resultSet = null;

        try {
            conn.prepareStatement(sql);
            pst = conn.prepareStatement(sql);
            resultSet = pst.executeQuery();
            ResultSetMetaData rsm = resultSet.getMetaData();
            int columnCount = rsm.getColumnCount();

            while(resultSet.next()){
                Map<String,String> row = new HashMap<>();
                for(int index =1; index <= columnCount; index++){
                    row.put(rsm.getColumnName(index),resultSet.getString(index));
                }
                resultMap.add(row);
            }

        } catch (Exception e) {
            log.error("Fail to update database with SQL: " + sql);
            e.printStackTrace();
        }finally {
            try {
                if (null != pst) {
                    pst.close();
                }
            }catch (Exception e){
                log.error("Fail to close prepareStatement");
            }
        }

        return resultMap;
    }

    /**执行SQL并返回最后一行指定列的结果
     * @param sql 要执行的SQL语句
     * @param column 返回结果中指定的列
     * @return SQL执行结果中最后一行中指定的列的值
     */
    protected String getDataFromLastRow(String sql,int column){
        String lastResult = null;
        PreparedStatement pst = null;
        ResultSet res = null;

        try {
            pst = conn.prepareStatement(sql);
            res  = pst.executeQuery();
            res.last();
            lastResult=res.getString(column);
            log.info("Select value from DB is :"+lastResult);
        } catch (Exception e) {
            log.error("Fail to execute sql" + sql);
        }finally {
            try {
                if(null != res ) {
                    res.close();
                }

                if(null!=pst) {
                    pst.close();
                }
            }catch (Exception e){
                log.error("Fail to close result set or sql statement");
            }
        }

        return lastResult;
    }

    /**连接到数据库
     * @param DBName 数据库的名字
     */
    public void connectToDB(String DBName){
        String username = ConfigUtil.getValue(DBName, ConfigConst.USER);
        String password = ConfigUtil.getValue(DBName,ConfigConst.PASSWORD);
        String host = ConfigUtil.getValue(DBName,ConfigConst.HOSTNAME);
        String databaseName = ConfigUtil.getValue(DBName,ConfigConst.DATABASE_NAME);
        connectToDB(host,databaseName,username,password);
    }

    /**
     * Connect to Database by host,db name, user name and password
     * @param host name of the host on which DB is installed
     * @param databaseName database name
     * @param username user name
     * @param password user password
     */
    protected void connectToDB(String host, String databaseName, String username, String password) {
        final String DBDriver = "com.mysql.jdbc.Driver";

        try {

            //如果需要通过SSH跳板机中转,首先建立SSH tunnel并设置端口转发到数据库服务器对应端口，通常数据库连接端口默认为3306
            if (DatabaseOverSSH.isEnabled()) {
                session = SshUtil.connect(host);
                // If database over SSH is enabled, it should connect to local host instead of target DB host
                host = "localhost:" + DatabaseOverSSH.getLocalPort();
            }

            String connectURL = "jdbc:mysql://"+ host +"/"+ databaseName;
            log.info(connectURL);

            log.info("Connecting to database : " + databaseName);
            Class.forName(DBDriver);
            DriverManager.setLoginTimeout(CONNECTION_TIMEOUT);
            conn = DriverManager.getConnection(connectURL, username, password);
        }catch (Exception e){
            log.error("Fail to connect to database " + DBDriver);
            log.error(e.toString());
        }
    }

    /**关闭数据库
     */
    public void closeDB(){
        try {
            if((null != conn) && !conn.isClosed()) {
                conn.close();
            }

            // 断开SSH会话
            SshUtil.disconnect(session);

        }catch (Exception e){
            log.error("Fail to close database");
        }
    }
}
