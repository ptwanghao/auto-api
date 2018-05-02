package com.yd.test;

import com.yd.test.framework.common.AbstractTest;
import com.yd.test.framework.common.HttpResult;
import com.yd.test.framework.exception.TestHttpInvokeException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;


/**
 * Created by liuhailin on 2017/1/18.
 */
public class GetInitConfigure extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(GetInitConfigure.class);
    Map<String, String> params;




    @BeforeClass
    public void setUp() throws Exception {
        log.info("setup....");

    }


    @Test
    public void testCreateAuthCode(){

        try {
            log.info("response:-========");

            HttpResult result= this.execute(null);
            log.info("response:-=======++++++++="+result.getResponseCode());

            log.info("response:"+result.toJSONResponseData().toString());
        } catch (TestHttpInvokeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String testDataFileName() {
        return "GetInitConfigure";
    }
}
