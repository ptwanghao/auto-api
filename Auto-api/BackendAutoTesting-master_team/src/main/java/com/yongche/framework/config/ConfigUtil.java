package com.yongche.framework.config;


import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;

/**
 * 读取ini文件
 */
public class ConfigUtil {
    public static Logger log = LoggerFactory.getLogger(ConfigUtil.class);
    private static INIConfiguration config;
    //private static final String configFile;

    static {
        config = loadConfigIni();
    }

    /**
     * Refresh config ini and reload
     */
    public static synchronized void refreshConfigIni(){
        config = loadConfigIni();
    }

    /**
     * 获取config ini文件中特定段落下特定字段的值<br>
     * @param sectionName 段落名称
     * @param key 字段名称
     * @return 类型 String, key所对应的值
     */
    public static String getValue(String sectionName, String key) {
        SubnodeConfiguration s = config.getSection(sectionName);
        return s.getString(key);
    }

    private static INIConfiguration loadConfigIni(){
        config = new INIConfiguration();

        try {
            config.read(new FileReader(ActiveConfiguration.getActiveConfiguration(true).toString()));
        } catch (Exception e) {
            log.error("Fail to read config file");
        }

        return config;
    }

}