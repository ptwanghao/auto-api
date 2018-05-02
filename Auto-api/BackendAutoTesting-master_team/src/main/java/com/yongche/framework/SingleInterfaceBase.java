package com.yongche.framework;

import com.yongche.framework.core.TestCase;
import com.yongche.framework.core.TestSuite;
import com.yongche.framework.utils.TestNGResultStatus;
import com.yongche.framework.utils.XMLUtil;
import com.yongche.framework.utils.dataProvider.TestDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.TestNGUtils;
import org.testng.annotations.*;

import java.lang.reflect.Method;

/**
 * Base class to support single interface testing <br>
 * Tester could run multiple xml cases by invoke one test method without programming or just by creating one test method <br>
 * The test xml files are collected by data provider and passed on to the test method.<br>
 * The test xml file must contains attribute 'description" in order that different test cases could be identified
 * The derived class must implement the abstract method "runTestCase" and add annotation Test and XmlFileParameters(depends on data provider) <br>
 * The derived class should  call shouldSkipTestIfNullOrEmptyDescription() inside the test method
 */
abstract public class SingleInterfaceBase {

    /**
     * Test xml file root directory
     */
    public static final String XML_CASE_ROOT = "XMLCase/";

    /**
     * Logger
     */
    public static Logger log = LoggerFactory.getLogger(SingleInterfaceBase.class);

    /**
     * Single interface test entrance <br>
     * Derived class must implement the abstract method and provide the data provider in annotation Test<br>
     * There are 2 data providers : <br>
     *     1) DataProvider_By_Configuration_File, collect the test xml files indicated by data_provider.xml under dir XMLCase
     *         and pass them  to test method parameter one by one <br>
     *        Usage :<br>
     *        // @Test(dataProvider = "DataProvider_By_Configuration_File", dataProviderClass = TestDataProvider.class) <br>
     *        // public void runTestCase(String xmlFleName) {...} <br>
     *     2) DataProvider_By_Parameter , collect the test xml files indicated by annotation  XmlFileParameters <br>
     *         and pass them to test method parameter one by one <br>
     *        Usage: <br>
     *        // @Test(dataProvider = "DataProvider_By_Parameter",dataProviderClass = TestDataProvider.class) <br>
     *        // @XmlFileParameters(path="XMLCase/SITDemo/Demo2", recursive = true) <br>
     *        // public void runTestCase(String xmlFileName) throws SkipException {...} <br>
     * @param xmlFleName test xml file passed by data provider
     */
    //@Test (dataProvider = "DataProvider_By_Configuration_File", dataProviderClass = TestDataProvider.class)
    abstract public void runTestCase(String xmlFleName) throws SkipException;

    /**
     * Log test description to identify different test starting in test log
     */
    @BeforeMethod
    public void methodSetup(Object[] parameters, Method testMethod) {
        String xmlFileName = (String)parameters[0];
        String description = getDescription(xmlFileName);

        log.info("============================"+testMethod.getName()+ " Starts: " + description +"================================");

        log.info("Test description : " + description);
        if(descriptionIsNullOrEmpty(description)) {
            String message = "description is a MUST for single interface testing, please check your test xml file :"
                    + ( XML_CASE_ROOT + xmlFileName);
            log.error(message);
        }
    }

    /**
     * 1.Log test description to identify different test End in test log <br>
     * 2.Set the test description to test result for later reporting : <br>
     *     The report class uses the test method to identify different tests <br>
     *     But the single interface tests invoke the same test method <br>
     *     In the case, test description will be shown instead of test method in report. <br>
     * 3.Log the test result status  <br>
     */
    @AfterMethod
    public void methodCleanup(ITestResult result,Method testMethod){
        String description = getDescription(result);
        if(descriptionIsNullOrEmpty(description)) {
            String xmlFileName = (String) result.getParameters()[0];
            description = (XML_CASE_ROOT + xmlFileName);
            log.info("Test description is set to xml file name : " + description );
        }

        // Add test description to result for later reporting
        result.setAttribute("description",description);

        log.info("Test description : " + description);
        log.info("Test status : " + TestNGResultStatus.toString(result.getStatus()));

        log.info("============================"+testMethod.getName()+ " Ends: " + description +"================================\n");

    }

    public static boolean descriptionIsNullOrEmpty(String description){
        return (null == description || "" == description);
    }

    /**
     * Check if the description is null or empty, if true, then throw SkipExcetption in order to skip the test
     * @param description Attribute description in test xml file to describe the test case
     * @param xmlFileName test xml file name, will be shown when test description is null or empty
     */
    public static void shouldSkipTestIfNullOrEmptyDescription(String description,String xmlFileName){
        if(descriptionIsNullOrEmpty(description)){
            // Skip the test if no description is set
            // Because all the tests invoke the same test method
            // No description, in test report, all results begin with same test method name thus no test is identified
            throw new SkipException(
                    "description is a MUST for single interface testing, please check your test xml file :" +
                            ( XML_CASE_ROOT + xmlFileName )
            );
        }
    }

    /**
     * Get test description by test xml file name
     * @param xmlFileName text xml file name
     * @return description of the test, which is stored in xml attribute 'description'
     *
     */
    public static String getDescription(String xmlFileName) {
        TestSuite suite = XMLUtil.getTestSuiteByPath( XML_CASE_ROOT + xmlFileName);
        TestCase testCase = suite.getTestCase().get(0);
        return testCase.getDescription();
    }

    /**
     * Get test description by test result
     * @return description of the test, which is stored in xml attribute 'description'
     */
    public static String getDescription(ITestResult result){
        String xmlFileName = result.getParameters()[0].toString();
        return getDescription(xmlFileName);
    }


    /**
     * Log the test class Starting
     */
    @BeforeClass
    public void classSetup(){
        log.info("===========================================================================");
        log.info("====================Single Interface Test Class starts=====================");
        log.info("===========================================================================");
    }

    /**
     * Log the test class ending
     */
    @AfterClass
    public void classCleanup(){
        log.info("===========================================================================");
        log.info("====================Single Interface Test Class Ends=======================");
        log.info("===========================================================================");
    }

}
