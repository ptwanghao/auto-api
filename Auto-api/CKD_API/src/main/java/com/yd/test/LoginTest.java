package com.yd.test;

import com.yd.test.framework.common.AbstractTest;
import com.yd.test.framework.common.HttpResult;
import com.yd.test.framework.common.ISign;
import com.yd.test.framework.exception.TestHttpInvokeException;
import com.yd.test.framework.utils.PropUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;


/**
 * Created by liuhailin on 2017/1/18.
 */
public class LoginTest extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(LoginTest.class);
    Map<String, String> params;


    public void setUp() throws Exception {
        log.info("setup....");

    }


    @Test
    public void testLogin(){

        try {
            this.execute(new ISign() {
                @Override
                public String signName() {
                    return "liuhailin";
                }

                @Override
                public String generatorSignString(Map<String, String> map) {
                    return "123";
                }
            },null,null);
        } catch (TestHttpInvokeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String testDataFileName() {
        return "login";
    }
}
