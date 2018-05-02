package com.yongche.framework.utils;


import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;

/**
 * 读取ini文件
 */
public class ConfigUtil {
    public static Logger log = LoggerFactory.getLogger(ConfigUtil.class);
    private static INIConfiguration config;
    private static final String configFile;

    static {
        configFile = System.getProperty("user.dir") + File.separator + "config.ini";
        config = new INIConfiguration();

        try {
            config.read(new FileReader(configFile));
        } catch (Exception e) {
            log.error("Fail to read config file");
        }
    }

    /**
     * 根据不同的订单类型创建订单<br>
     * @param sectionName 段落名称
     * @param key 字段名称
     * @return 类型 String, key所对应的值
     */
    public static String getValue(String sectionName, String key) {
        SubnodeConfiguration s = config.getSection(sectionName);
        return s.getString(key);
    }

}