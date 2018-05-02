package com.yd.test;

import com.yd.test.framework.common.AbstractTest;
import com.yd.test.framework.common.HttpResult;
import com.yd.test.framework.exception.TestHttpInvokeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by liuhailin on 2017/1/18.
 */
public class CreateAuthTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(CreateAuthTest.class);
    String timeStamp=String.valueOf(System.currentTimeMillis()/1000);
    String once=String.valueOf(Long.parseLong(timeStamp)+1000);

    Map<String, String> headerParams = new HashMap<>();
    Map<String, String> params = new HashMap<>();




    @BeforeClass
    public void setUp() throws Exception {
        log.info("setup....");
        headerParams.put("oauth_timestamp", timeStamp);
        headerParams.put("oauth_nonce",once);

        params.put("access_token", "");


    }


    @Test
    public void testCreateAuthCode(){
        try {
            log.info("response:-========");

            HttpResult result= execute(headerParams,params);
            log.info("response:-=======++++++++="+result.getResponseCode());

            log.info("response:"+result.toJSONResponseData().toString());
        } catch (TestHttpInvokeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String testDataFileName() {
        return "CreateAuthCode";
    }
}
