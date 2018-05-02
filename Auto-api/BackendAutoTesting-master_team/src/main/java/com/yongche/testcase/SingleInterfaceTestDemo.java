package com.yongche.testcase;

import com.yongche.framework.CaseExecutor;
import com.yongche.framework.SingleInterfaceBase;
import com.yongche.framework.utils.dataProvider.TestDataProvider;
import com.yongche.framework.utils.dataProvider.XmlFileParameters;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Demo class derived from SingleInterfaceBase to run xml cases indicated by annotation XmlFileParameters <br>
 * Use data provider "DataProvider_By_Parameter" to collect the test xml files indicated by annotation XmlFileParameters <br>
 * Tester could create their own test method(s) like the demo ,even create their onw data provider. <br>
 */
public class SingleInterfaceTestDemo extends SingleInterfaceBase{

    /**
     * Use data provider "DataProvider_By_Parameter" to collect the test xml files indicated by annotation XmlFileParameters <br>
     * Usage : <br>
     *     // @Test(dataProvider = "DataProvider_By_Parameter",dataProviderClass = TestDataProvider.class) <br>
     *     // @XmlFileParameters(path="XMLCase/SITDemo/Demo2", recursive = true) <br>
     *     // public void runTestCase(String xmlFileName) throws SkipException {...} <br>
     * @throws SkipException Skip the test if the attribute "description" in the test xml file is null or empty
     */
    @Test(dataProvider = "DataProvider_By_Parameter",dataProviderClass = TestDataProvider.class)
    @XmlFileParameters(path="XMLCase/SITDemo/Demo2", recursive = true)
    public void runTestCase(String xmlFileName) throws SkipException {

        shouldSkipTestIfNullOrEmptyDescription(getDescription(xmlFileName), xmlFileName);

        CaseExecutor.runTestCase(xmlFileName);
    }
}
