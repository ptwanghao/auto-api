package com.yongche.testcase.order;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.TestParameter;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 *
 *
 */
public class ReceiveCashTest {

    //@Test
    public void receiveCash_Should_Success_With_Valid_Parameters() throws Exception{
        CaseExecutor.runTestCase("receiveCash.xml");
    }






}
