package com.yongche.framework.api.charge;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.core.TestParameter;

import java.util.Map;

/**
 *
 */
public class FreeDistance {

    /**
     * 获取免费长途公里费用<br>
     */
    public static int getFreeDistance(Map<String,TestParameter> inputParamMap){
        String caseFile = "charge/charge_freeDistance_getAll.xml";

        Map<String,TestParameter> response = CaseExecutor.runTestCase(caseFile,inputParamMap);
        TestParameter freeLongDistance = response.get("free_long_distance_kilometer");
        return Integer.parseInt(freeLongDistance.getValue());
    }
}
