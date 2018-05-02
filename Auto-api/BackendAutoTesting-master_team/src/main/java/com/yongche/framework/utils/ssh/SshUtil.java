package com.yongche.framework.utils.ssh;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class is to connect to SSH server and set port forwarding L.
 * NOTE : NOT thread-safe !!!
 */
public class SshUtil {
    public static Logger log = LoggerFactory.getLogger(SshUtil.class);

    /**
     * Establish SSH tunnel for database connection and set port forwarding with -L option <br>
     * @param hostname where DB installed
     * @return  SSH session if succeeded, null otherwise
     */
    public static Session connect(String hostname) {
        Session session = null;

        if (DatabaseOverSSH.isEnabled()){
            //建立SSH会话连接
            session =  connect(
                DatabaseOverSSH.getHost(),
                DatabaseOverSSH.getPort(),
                DatabaseOverSSH.getUser(),
                DatabaseOverSSH.getPassword(),
                DatabaseOverSSH.getSshConfig()
            );

            if (null != session) {
                //设置SSH端口转发
                int assigned_port = setPortForwardingL(hostname,session);

                //如果端口转发设置失败，断开SSH会话
                if (assigned_port <= 0){
                    session.disconnect();
                    session = null;
                }
            }
        }
        return session;
    }

    /**
     * Establish SSH tunnel to some a SSH server with user/password
     * @param host SSH server
     * @param port SSH server port, 22 by default
     * @param user user name to login SSH server
     * @param password user password to login SSH server
     * @param properties SSH config, could be null or empty
     * @return
     */
    public static Session connect(String host, int port, String user, String password, java.util.Properties properties){
        try{
            JSch jsch = new JSch();
            if (DatabaseOverSSH.isLogEnabled()) {
                //output internal SSH log
                JSch.setLogger(new JSchLogger());
            }
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);

            if ( null != properties && properties.size() > 0 ) {
                session.setConfig(properties);
            }

            session.connect();
            log.info("SSH Server version : " + session.getServerVersion());
            log.info("SSH Client version : " + session.getClientVersion());

            return session;

        }catch (Exception e) {
            log.error(String.format("Failed to connect to ssh host %s:%d, exception = %s"), host,port, e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 设置从SSH server到数据库服务器的端口转发，默认将本地3306端口通过SSH转发到MySQL的3306端口
     * @param hostname 要通过SSH tunnel连接到的数据库服务器
     * @param session 已经建立连接的SSH会话
     * @return 绑定的端口号，大于0表示设置端口转发成功， 0 otherwise
     */
    public static int setPortForwardingL(String hostname,Session session){
        int local_port = DatabaseOverSSH.getLocalPort();

        return setPortForwardingL(
                local_port,
                hostname,
                DatabaseOverSSH.getRemotePort(),
                session
        );
    }

    /**
     * Set port forward with -L option over SSH
     * @param localPort 本地端口
     * @param remoteHostName 远程服务器
     * @param remotePort 远程服务器上的端口
     * @param session 已经建立连接的SSH会话
     * @return 绑定的端口，大于0表示设置端口转发成功， 0 otherwise
     */
    public static int setPortForwardingL(int localPort, String remoteHostName,int remotePort, Session session){

        int assigned_port = 0;
        try {
            log.info(String.format("set port forwarding L over ssh, local port = %s, remote host = %s, remote port = %s",
                    localPort, remoteHostName, remotePort));

            //String strLocalPort = String.valueOf(localPort);
            assigned_port = session.setPortForwardingL(localPort, remoteHostName, remotePort);

            if (0 == assigned_port) {
                log.error("Failed to set port forwarding L : , assigned port is 0");
            } else if (assigned_port > 0) {
                log.info("Succeeded in setting port forwarding L");
                log.info("Assigned port : " + assigned_port);
            }
        } catch (Exception e) {
            log.error("Failed to set port forwarding L : " + e.getMessage());
            e.printStackTrace();
        }

        return assigned_port;
    }

    /**
     * Disconnect SSH session
     * @param session
     */
    public static void disconnect(Session session){
        if (null != session) {
            session.disconnect();
        }
    }
}
