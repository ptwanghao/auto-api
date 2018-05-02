package com.yongche.framework.utils.ssh;

import com.yongche.framework.config.ConfigUtil;
import com.yongche.framework.core.ConfigConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * The class represents the information of the section "DATABASE_OVER_SSH" at config.ini
 */
public class DatabaseOverSSH {
    public static Logger log = LoggerFactory.getLogger(DatabaseOverSSH.class);

    private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
    private static final String COMPRESSION = "Compression";
    private static final String CONNECTION_ATTEMPTS = "ConnectionAttempts";
    private static final DatabaseOverSSH s_DatabaseOverSSH = new DatabaseOverSSH();

    private boolean enabled = false;
    private String host = null;
    private int port = 0;
    private String user = null;
    private String password = null;
    private int localPort = 0;
    private int remotePort = 0;
    private boolean logEnabled = false;
    private String strictHostKeyChecking = "no";
    private String compression = "yes";
    private String connectionAttempts = "3";
    private java.util.Properties sshConfig = null;

    private DatabaseOverSSH(){}

    static {
        try {
            s_DatabaseOverSSH.enabled = Boolean.parseBoolean(ConfigUtil.getValue(ConfigConst.DATABASE_OVER_SSH, "enabled"));
            if (s_DatabaseOverSSH.enabled) {
                s_DatabaseOverSSH.host = ConfigUtil.getValue(ConfigConst.DATABASE_OVER_SSH, "host");
                s_DatabaseOverSSH.port = Integer.parseInt(ConfigUtil.getValue(ConfigConst.DATABASE_OVER_SSH, "port"));
                s_DatabaseOverSSH.user = ConfigUtil.getValue(ConfigConst.DATABASE_OVER_SSH, "user");
                s_DatabaseOverSSH.password = ConfigUtil.getValue(ConfigConst.DATABASE_OVER_SSH, "password");
                s_DatabaseOverSSH.localPort = Integer.parseInt(ConfigUtil.getValue(ConfigConst.DATABASE_OVER_SSH, "localport"));
                s_DatabaseOverSSH.remotePort = Integer.parseInt(ConfigUtil.getValue(ConfigConst.DATABASE_OVER_SSH, "remoteport"));
                s_DatabaseOverSSH.logEnabled = Boolean.parseBoolean(ConfigUtil.getValue(ConfigConst.DATABASE_OVER_SSH, "logEnabled"));
                s_DatabaseOverSSH.strictHostKeyChecking = (ConfigUtil.getValue(ConfigConst.DATABASE_OVER_SSH, STRICT_HOST_KEY_CHECKING));
                s_DatabaseOverSSH.compression = ConfigUtil.getValue(ConfigConst.DATABASE_OVER_SSH, COMPRESSION);
                s_DatabaseOverSSH.connectionAttempts = ConfigUtil.getValue(ConfigConst.DATABASE_OVER_SSH, CONNECTION_ATTEMPTS);
                s_DatabaseOverSSH.sshConfig = new java.util.Properties();
                s_DatabaseOverSSH.sshConfig.put(STRICT_HOST_KEY_CHECKING, s_DatabaseOverSSH.strictHostKeyChecking);
                s_DatabaseOverSSH.sshConfig.put(COMPRESSION, s_DatabaseOverSSH.compression);
                s_DatabaseOverSSH.sshConfig.put(CONNECTION_ATTEMPTS, s_DatabaseOverSSH.connectionAttempts);
            }
        }catch (Exception e){
            log.info("Exception is hit during construct DatabaseOverSSH, error message = " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean isEnabled() {
        return s_DatabaseOverSSH.enabled;
    }

    public static String getHost() {
        log.info(String.format("%32s : %s","ssh host",s_DatabaseOverSSH.host));
        return s_DatabaseOverSSH.host;
    }

    public static int getPort() {
        log.info(String.format("%32s : %d","port",s_DatabaseOverSSH.port));
        return s_DatabaseOverSSH.port;
    }

    public static String getUser() {
        log.info(String.format("%32s : %s","user",s_DatabaseOverSSH.user));
        return s_DatabaseOverSSH.user;
    }

    public static String getPassword() {
        log.info(String.format("%32s : 就不告诉你","password"));
        return s_DatabaseOverSSH.password;
    }

    public static int getLocalPort() {
        log.info(String.format("%32s : %d","localPort",s_DatabaseOverSSH.localPort));
        return s_DatabaseOverSSH.localPort;
    }

    public static int getRemotePort() {
        log.info(String.format("%32s : %d","remotePort",s_DatabaseOverSSH.remotePort));
        return s_DatabaseOverSSH.remotePort;
    }

    public static boolean isLogEnabled() {
        return s_DatabaseOverSSH.logEnabled;
    }

    public static String getStrictHostKeyChecking() {
        return s_DatabaseOverSSH.strictHostKeyChecking;
    }

    public static String getCompression() {
        return s_DatabaseOverSSH.compression;
    }

    public static String getConnectionAttempts() {
        return s_DatabaseOverSSH.connectionAttempts;
    }

    public static Properties getSshConfig() {
        log.info(String.format("%32s : %s",STRICT_HOST_KEY_CHECKING,s_DatabaseOverSSH.sshConfig.get(STRICT_HOST_KEY_CHECKING)));
        log.info(String.format("%32s : %s",COMPRESSION,s_DatabaseOverSSH.sshConfig.get(COMPRESSION)));
        log.info(String.format("%32s : %s",CONNECTION_ATTEMPTS,s_DatabaseOverSSH.sshConfig.get(CONNECTION_ATTEMPTS)));
        return s_DatabaseOverSSH.sshConfig;
    }
}
