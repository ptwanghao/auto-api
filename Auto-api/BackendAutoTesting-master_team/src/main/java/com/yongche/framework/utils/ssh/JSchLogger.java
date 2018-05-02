package com.yongche.framework.utils.ssh;

import com.jcraft.jsch.Logger;
import org.slf4j.LoggerFactory;

public class JSchLogger implements Logger {
    public static org.slf4j.Logger log = LoggerFactory.getLogger(JSchLogger.class);
    @Override
    public void log(int i, String s) {
        log.info(s);
    }

    /**
     * Output all levels logs
     * @param i
     * @return
     */
    @Override
    public boolean isEnabled(int i) {
        return true;
    }
}
