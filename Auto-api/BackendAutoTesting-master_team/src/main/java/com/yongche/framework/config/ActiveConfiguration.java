package com.yongche.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

/**
 * The class describe the active configuration ini name and rootDir directory
 */
public class ActiveConfiguration {
    public static Logger log = LoggerFactory.getLogger(ActiveConfiguration.class);
    private static String DEFAULT_ROOT_DIR = "config";
    private static String DEFAULT_CONFIG_INI_NAME = "config_testing.ini";
    private static ActiveConfiguration s_activeConfiguration = null;


    private String rootDir;
    private String name;
    private boolean active;

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ActiveConfiguration(){}

    private ActiveConfiguration(String rootDir,String name, boolean active) {
        this.rootDir = rootDir;
        this.name = name;
        this.active = active;
    }

    @Override
    public String toString(){
        return rootDir + File.separator + name;
    }

    /**
     * Retrieve active config ini file name and location <br>
     *     1) if environment variable "ActiveConfigINI" exists, get its value which indicates the config ini file name
     *     and check if the file exists. if existed, create ActiveConfiguration and return; in the case, the root dir is
     *     .\config\ for the config ini file <br>
     *     2) else if the environment variable "ActiveConfigINI" does not exist or config ini file indicated by its value
     *     does not exist, try to read the root dir and name of config ini file from .\config\config.xml <br>
     *     3) if no valid ActiveConfiguration is retrieved from env variable or config.xml, then create a default ActiveConfiguration
     *     which root dir -> .\config\ and name -> config_testing.ini <br>
     * @param refresh true to retrieve the config ini file name and location again
     *                even the active configuration has already existed.
     * @return
     */
    public static synchronized ActiveConfiguration getActiveConfiguration( boolean refresh ){

        log.info("==================================================================");
        log.info("==================================================================");
        if ( null == s_activeConfiguration || true == refresh) {
            if (null != s_activeConfiguration && refresh) {
                log.info("Need to retrieve active config INI file name again");
            }

            String activeConfigIniName = System.getenv("ActiveConfigINI");
            File configIniFile = new File(DEFAULT_ROOT_DIR + File.separator + activeConfigIniName);

            final String Prefix = "Get active config Ini name from";
            if ( configIniFile.exists() && configIniFile.isFile()) {
                // env variable is available
                log.info(String.format("%32s : env variable \"ActiveConfigINI\"", Prefix));
                log.info(String.format("%32s : %s", "env variable value", activeConfigIniName));
                s_activeConfiguration = new ActiveConfiguration(DEFAULT_ROOT_DIR,activeConfigIniName,true);

            } else {
                // env variable is not available, retrieve active config ini file name from config.xml
                log.info(String.format("%32s : %s",Prefix,XmlConfigurationCollection.CONFIIG_XML_NAME));
                XmlConfigurationCollection xmlConfigList = XmlConfigurationCollection.getXmlConfigurations();
                for (ActiveConfiguration xmlConfig : xmlConfigList) {
                    // for multiple items, the first one which active is true will be chosen, others will be ignored.
                    if (xmlConfig.active == true) {
                        s_activeConfiguration = xmlConfig;
                        break;
                    }
                }
            }
        }

        // Check active name and rootDir , set them to default value if null or empty
        setDefaultValueIfNullOrEmpty();

        log.info(String.format("%32s : %s","Active Config Ini file is" , s_activeConfiguration.toString()));
        log.info("==================================================================");
        log.info("==================================================================");
        return s_activeConfiguration;
    }

    /**
     * Check active name and rootDir , set them to default value if null or empty <br>
     * 1) if no active configuration is read from env variable or config.xml, then create default ActiveConfiguration <br>
     * 2) else if rootDir or name is null or empty, set them to default value
     */
    private static void setDefaultValueIfNullOrEmpty(){

        if ( null != s_activeConfiguration) {
            log.info(String.format("%32s : %s","Config Ini root dir" , s_activeConfiguration.rootDir));
            log.info(String.format("%32s : %s","Active Config Ini name", s_activeConfiguration.name));
        } else {
            // Create a default active configuration
            s_activeConfiguration = new ActiveConfiguration();
            s_activeConfiguration.setActive(true);
        }

        // If name is null or empty, set it default active name
        if (null == s_activeConfiguration.name || s_activeConfiguration.name.isEmpty()) {
            s_activeConfiguration.setName(DEFAULT_CONFIG_INI_NAME);
            log.info("%32s : %s", "Reset Active Config Ini name", s_activeConfiguration.name);
        }

        // if rootDir is null or empty, set it default rootDir
        if (null == s_activeConfiguration.rootDir || s_activeConfiguration.rootDir.isEmpty()) {
            s_activeConfiguration.setRootDir(DEFAULT_ROOT_DIR);
            log.info("%32s : %s","Reset Config Ini root dir", s_activeConfiguration.rootDir);
        }
    }
}
