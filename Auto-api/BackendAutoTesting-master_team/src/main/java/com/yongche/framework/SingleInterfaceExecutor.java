package com.yongche.framework;

import com.yongche.framework.utils.dataProvider.TestDataProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.*;

/**
 * Run multiple xml cases without creating test methods
 * Use data provider "DataProvider_By_Configuration_File" to run test cases <br>
 * All test xml files are indicated by data_provider.xml <br>
 * Tester doesn't need to coding but prepare test xml files <br>
 */
public class SingleInterfaceExecutor extends SingleInterfaceBase{

    /**
     * Logger
     */
    public static Logger log = LoggerFactory.getLogger(SingleInterfaceExecutor.class);

    /**
     * Accept the test xml files indicated by data_provider.xml and run the xml case <br>
     * Usage: <br>
     *   // @Test(dataProvider = "DataProvider_By_Configuration_File", dataProviderClass = TestDataProvider.class) <br>
     *   // public void runTestCase(String xmlFileName) throws SkipException {...} <br>
     * @throws SkipException Skip the test if the attribute "description" in test xml file is null or empty <br>
     */
    @Test (dataProvider = "DataProvider_By_Configuration_File", dataProviderClass = TestDataProvider.class)
    public void runTestCase(String xmlFileName) throws SkipException {

        shouldSkipTestIfNullOrEmptyDescription(getDescription(xmlFileName), xmlFileName);

        CaseExecutor.runTestCase(xmlFileName);
    }

}
