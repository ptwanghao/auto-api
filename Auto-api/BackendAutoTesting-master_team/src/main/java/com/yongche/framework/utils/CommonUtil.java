package com.yongche.framework.utils;


import com.yongche.driver.client.DriverInfo;
import com.yongche.framework.core.*;
import com.yongche.framework.config.ConfigUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                if (line.charAt(0) == '#')
                    continue;
                String[] values = line.split(",");
                RiderInfo riderInfo = new RiderInfo();
                riderInfo.setUserId(values[0]);
                riderInfo.setCellphone(values[1]);
                //only rider name starting with test-dispatch could be accepted by testing drivers
                riderInfo.setPassengerName("test-dispatch-" + riderInfo.getCellphone());

                riderList.add(riderInfo);
            }
        }catch (Exception e){
            log.info("Failed to read rider data info. " + e.getMessage());
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

    //返回指定Index的riderInfo
    public static RiderInfo getRider(int index){
        return (riderList.size() > index && index >= 0 )? riderList.get(index) : null;
    }

    /**
     * Get all files under some a directory
     * @param path
     * @param recursive
     * @Param filenameFilter filter by file name , indicates which type of files will be searched for
     * @return absolute path list of all files
     */
    public static List<String> getAllFiles(String path, boolean recursive, FileFilter fileFilter) {

        //log.info("Try getting all files under "+ path +" , recursive is " + recursive);
        List<String> fileList = new ArrayList<>();

        File root = new File(path);
        if ( !root.exists()){
            log.info(path + " : does NOT exist!!!");

            return fileList;
        }

        /**
         * 如果没有传递文件过滤器，就接受所有文件
         */
        if (null == fileFilter) {
            fileFilter = (file) -> file.isFile();
        }

        if (root.isDirectory()){

            File[] files = root.listFiles(fileFilter);
            for(File file : files){
                log.info("Found : " + file.getAbsolutePath());
                fileList.add(file.getAbsolutePath());
            }

            if (recursive) {
                File[] dirs = root.listFiles(file->file.isDirectory());
                for(File dir : dirs) {
                    log.info("Found : " + dir.getAbsolutePath() + ", type is directory, recursive is " + recursive);
                    fileList.addAll(getAllFiles(dir.getAbsolutePath(), recursive, fileFilter));
                }
            }
        }
        else {
            if (fileFilter.accept(root)) {
                log.info(path + " is some a file path actually");
                log.info("Found : " + root.getAbsolutePath());
                fileList.add(root.getAbsolutePath());
            } else {
                log.info(path + " is some a file path actually");
                log.info(path + " is NOT the target file type : " + fileFilter.toString());
            }
        }

        return fileList;
    }

    /**
     *  解析response里的result字段下的子项，并以hashmap形式返回
     * @param responseMap 从CaseExecutor.RunTestCase()返回的包含response的Map
     * @return 保存在hashmap里的 result字段下的子项
     */
    public static Map<String,TestParameter> OutputResult(Map<String,TestParameter> responseMap){

        String response_result = MapUtil.getString(responseMap, ParamValue.RESPONSE_RESULT);

        return OutputResult(response_result);
    }

    /**
     * 解析response里的result字段下的子项，并以hashmap形式返回
     * @param response http/https/psf返回的response结果
     * @return 保存在hashmap里的 result字段下的子项
     */
    public static Map<String,TestParameter> OutputResult(String response){
        Map<String,TestParameter> outputParameterMap = new HashMap<>();

        //String jsonStr = (String)response.get("result");
        JSONObject jsonObj;

        try {
            // Exception will be thrown when json string is something like this {"ret_code":200,"result":true}
            jsonObj = JsonUtil.getJSONObject(response);
            String res = jsonObj.getString("result");
            jsonObj = JsonUtil.getJSONObject(res);
        }catch (Exception e){
            log.error("Fail to convert to JSONObject for result string : " + response + " " );
            log.error(e.getMessage());
            return outputParameterMap;
        }

        jsonObj.forEach( (k,v) ->  {
            String key = k.toString();
            String value = v.toString();
            log.info("Output parameter : " + key + " = " + value);
            outputParameterMap.put(key,new TestParameter(key,value));
        });

        log.info("Output : " + outputParameterMap.values());
        return outputParameterMap;
    }
}
