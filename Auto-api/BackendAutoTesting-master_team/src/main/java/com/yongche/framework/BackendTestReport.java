package com.yongche.framework;

import com.yongche.framework.utils.TestNGResultStatus;
import org.testng.*;
import org.testng.xml.XmlSuite;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class BackendTestReport implements IReporter {

    public static final String TEST_REPORT_FILE_NAME = "BackEnd_API_Automation_Test_Report.html";
    public static final String TEST_REPORT_TITLE = "BackEnd API Automation Test Report";
    public static final String TEST_SUMMARY = "Test Summary";
    public static final String TOTAL_TEST_SUITE_AMOUNT = "Total Test Suite Amount";
    public static final String TOTAL_TEST_CASE_AMOUNT = "Total Test Case Amount";
    public static final String PASSED_TEST_CASE_AMOUNT = "Passed Test Case Amount";
    public static final String FAILED_TEST_CASE_AMOUNT = "Failed Test Case Amount";
    public static final String SKIPPED_TEST_CASE_AMOUNT = "Skipped Test Case Amount";
    public static final String TEST_CASE_PASS_RATE = "Test Case Pass Rate";

    public static final String COLOR_PASSED = "#00FF00";
    public static final String COLOR_FAILED = "#FF0000";
    public static final String COLOR_SKIPPED = "yellow";

    private int passedTestCaseAmount = 0;
    private int failedTestCaseAmount = 0;
    private int skippedTestCaseAmount = 0;
    private float passRate = 0;

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
                               String outputDir) {
        Date now = new Date();
        String testReportName = getCurrentTime(now) + TEST_REPORT_FILE_NAME;
        try {
            StringBuilder sb = new StringBuilder();
            PrintStream printStream = new PrintStream(new FileOutputStream(testReportName));
            // To create the head part of the test report.
            this.startHtml(sb);

            Map<String, Map<String, ArrayList<ITestResult>>> results = new HashMap<>(); // <TestSuiteName, <TestName, TestResultList>>
            // Iterate each test suite to get the test result details.
            for (ISuite suite : suites) {
                // get the test list, corresponding to feature list.
                // skip the empty main test suite.
                if (suite.getName().equals("MainTestSuite")) {
                    continue;
                }
                // get the result for each test method.
                Map<String, ArrayList<ITestResult>> featureResultMap = new HashMap<>();

                String testSuiteName = suite.getName();

                // get the test result maps of this test suite.
                Map<String, ISuiteResult> testSuiteResultsMap = suite.getResults();
                for (ISuiteResult suiteResult : testSuiteResultsMap.values()) {
                    ArrayList<ITestResult> testSuiteResultList = new ArrayList<>(); // contains all results under some a test
                    ITestContext context = suiteResult.getTestContext();

                    String testName = context.getCurrentXmlTest().getName();

                    // calculate the failed test result amount.
                    IResultMap failedResultsMap = context.getFailedTests();
                    for (ITestResult failedResult : failedResultsMap.getAllResults()) {
                        this.failedTestCaseAmount++;
                        testSuiteResultList.add(failedResult);
                    }

                    // calculate the passed test result amount.
                    IResultMap passedResultsMap = context.getPassedTests();
                    for (ITestResult passedResult : passedResultsMap.getAllResults()) {
                        this.passedTestCaseAmount++;
                        testSuiteResultList.add(passedResult);
                    }

                    // calculate the skipped test result amount.
                    IResultMap skippedResultsMap = context.getSkippedTests();
                    for (ITestResult skippedResult : skippedResultsMap.getAllResults()) {
                        this.skippedTestCaseAmount++;
                        testSuiteResultList.add(skippedResult);
                    }
                    featureResultMap.put(testName,testSuiteResultList);
                }
                // To generate the result map, which will be passed to html report creator method later.
                results.put(testSuiteName, featureResultMap);
            }

            int totalTestSuiteAmount = results.size();
            int totalTestCaseAmount = this.passedTestCaseAmount + this.failedTestCaseAmount + skippedTestCaseAmount;
            passRate = (float) passedTestCaseAmount / totalTestCaseAmount;
            this.printTestSummary(sb, totalTestSuiteAmount, totalTestCaseAmount, passedTestCaseAmount, failedTestCaseAmount, skippedTestCaseAmount, passRate,now);

            // sort the result map，print the result.
            for(String testSuiteName: results.keySet()){
                for(String testName:results.get(testSuiteName).keySet()){
                    Collections.sort(results.get(testSuiteName).get(testName), new SortByTestMethodName());
                }
                this.printSuiteResult(sb, testSuiteName,results);
            }

            this.endHtml(sb);
            // To generate the test report.
            printStream.println(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // HTML test report generator: test summary part.
        private void printTestSummary(StringBuilder sb,
        int totalTestSuiteAmount,
        int totalTestCaseAmount,
        int passedTestCaseAmount,
        int failedTestCaseAmount,
        int skippedTestCaseAmount,
        float passRate, Date timeStamp) {
        sb.append("<p>");
        sb.append("<h1>" + TEST_REPORT_TITLE + "</h1>");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
        sb.append("<h3> Generated Time : " + simpleDateFormat.format(timeStamp).toString() + "</h3>");

        sb.append("<h2>" + TEST_SUMMARY + "</h2>");
        sb.append("<table class=\"summary\">");
        sb.append("<tbody>");

        sb.append("<tr>");
        sb.append("<th>" + TOTAL_TEST_SUITE_AMOUNT + "</th>");
        sb.append("<td>" + totalTestSuiteAmount + "</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<th>" + TOTAL_TEST_CASE_AMOUNT + "</th>");
        sb.append("<td>" + totalTestCaseAmount + "</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<th>" + PASSED_TEST_CASE_AMOUNT + "</th>");
        sb.append("<td>" + passedTestCaseAmount + "</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<th>" + FAILED_TEST_CASE_AMOUNT + "</th>");
        sb.append("<td>" + failedTestCaseAmount + "</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<th>" + SKIPPED_TEST_CASE_AMOUNT + "</th>");
        sb.append("<td>" + skippedTestCaseAmount + "</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<th>" + TEST_CASE_PASS_RATE + "</th>");
        String passRateStr = "";
        if (passRate == 1.0) {
            passRateStr = "100%";
        } else {
            passRateStr = String.format("%.2f", passRate * 100) + "%";
        }
        sb.append("<td>" + passRateStr + "</td>");
        sb.append("</tr>");
        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</p>");
        sb.append("<br>");
    }

    // HTML test report generator: detailed test result of test suites.
    private void printSuiteResult(StringBuilder sb, String testSuiteName, Map<String, Map<String, ArrayList<ITestResult>>> results) {

        sb.append("<p>");
        sb.append("<h2> Test Suite: " + testSuiteName + "</h2>");
        sb.append("<table class=\"result\">");
        sb.append("<tbody>");
        for (String testName : results.get(testSuiteName).keySet()) {
            sb.append("<tr>");

            String testResultStatistics = "";
            if (results.get(testSuiteName).get(testName).size() > 0) {
                testResultStatistics = " - " +
                        getTestResultStatistics(results.get(testSuiteName).get(testName).get(0).getTestContext());
            }
            sb.append("<td class=\"feature\" colspan=4> Feature Under Test: " + testName + testResultStatistics + "</td>");
            sb.append("</tr>");

            sb.append("<tr>");
            sb.append("<th>Test Case Name</th>");
            sb.append("<th>Test Execution Result</th>");
            sb.append("<th>Test Execution Time</th>");
            sb.append("<th>Details</th>");
            sb.append("</tr>");
            for (ITestResult result : results.get(testSuiteName).get(testName)) {

                    String description = (String)result.getAttribute("description");
                    ITestNGMethod testMethod = result.getMethod();
                    // For single interface testing, all the tests invoked the same test method.
                    // Use description to identify the different tests
                    String testMethodName = (null != description)?description:testMethod.getMethodName();
                    long startMillis = result.getStartMillis();
                    long endMillis = result.getEndMillis();
                    long duration = endMillis - startMillis;

                    // get test result: Passed, Failed, Skipped; and test execution details.
                    int resultInt = result.getStatus();
                    String resultStr = "N/A";
                    String resultInfo = "N/A";
                    if (resultInt == result.SUCCESS) {
                        resultStr = "'" + COLOR_PASSED + "'>Passed";
                        //resultInfo = result.getMethod().getClass().toString();
                        resultInfo = result.getTestClass().getName();
                        if(null != description) { // then append with test method name
                            resultInfo += "."+testMethod.getMethodName();
                        }
                    } else if (resultInt == result.FAILURE) {
                        resultStr = "'" + COLOR_FAILED + "'>Failed";
                        resultInfo = result.getThrowable().toString();
                    } else if (resultInt == result.SKIP) {
                        resultStr = "'" + COLOR_SKIPPED + "'>Skipped";
                        resultInfo = result.getTestClass().getName();
                        if(null != description) { // then append with test method name
                            resultInfo += "."+testMethod.getMethodName();
                        }
                    }
                    sb.append("<tr>");
                    sb.append("<td>" + testMethodName + "</td>");
                    sb.append("<td bgcolor=" + resultStr + "</td>");
                    sb.append("<td>" + duration + "(ms)" + "</td>");
                    sb.append("<td>" + resultInfo + "</td>");
                    sb.append("</tr>");
            }
        }
        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</p>");
        sb.append("<br>");
    }

    // HTML test report generator: html head part.
    private void startHtml(StringBuilder sb) {
        sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        sb.append("<title>");
        sb.append(TEST_REPORT_TITLE);
        sb.append("</title>");
        sb.append("<style type=\"text/css\">");

        sb.append("body{");
        sb.append("text-align: center;");
        sb.append("background-color: #FFFFF0;");
        sb.append("font-family: verdana,arial,sans-serif;");
        sb.append("font-size: 75%;");
        sb.append("margin: auto;");
        sb.append("}");


        sb.append("table.summary{");
        sb.append("text-align: left;");
        sb.append("font-weight: bold;");
        sb.append("border-color: #000000;");
        sb.append("border-style:solid");
        sb.append("border-width: 1px solid black;");
        sb.append("border-collapse: collapse;");
        sb.append("width: 1200px;");
        sb.append("}");

        sb.append("table.summary th{");
        sb.append("text-align:left;");
        sb.append("background-color:#ADD8E6;");
        sb.append("border: 1.5px solid black;");
        sb.append("width: 35%;");
        sb.append("}");

        sb.append("table.summary td{");
        sb.append("text-align:left;");
        sb.append("border: 1.5px solid black;");
        sb.append("width: auto;");
        sb.append("}");

        sb.append("table.result{");
        sb.append("text-align: left;");
        sb.append("font-weight: bold;");
        sb.append("border-color: #000000;");
        sb.append("border-style:solid");
        sb.append("border-width: 1px solid black;");
        sb.append("border-collapse: collapse;");
        sb.append("width: 1200px;");
        sb.append("}");

        sb.append("table.result th{");
        sb.append("text-align:left;");
        sb.append("background-color:#ADD8E6;");
        sb.append("border: 1.5px solid black;");
        sb.append("}");

        sb.append("table.result td{");
        sb.append("text-align:left;");
        sb.append("border: 1.5px solid black;");
        sb.append("width: auto;");
        sb.append("}");

        sb.append("table.result td.feature {");
        sb.append("text-align: left;");
        sb.append("font-size:12px;");
        sb.append("border:1.5px;");
        sb.append("colspan:\"4\";");
        sb.append("height: 20px;");
        sb.append("vertical-align:bottom;");
        sb.append("}");

        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div align=\"center\">");
        sb.append("<p>");
    }

    // HTML test report generator: html tail part.
    private void endHtml(StringBuilder sb) {
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");
    }

    // get current time by string.
    private String getCurrentTime(Date timeStamp) {
        if (null == timeStamp) {
            timeStamp = new Date();
        }
        return new SimpleDateFormat("yyyy_M_d_HH_mm_ss_").format(timeStamp);
    }

    // The Comparator method
    private class SortByTestMethodName implements Comparator<ITestResult> {
        @Override
        public int compare(ITestResult t1, ITestResult t2) {
            String testName1 = t1.getMethod().getMethodName();
            String testName2 = t2.getMethod().getMethodName();
            return testName1.compareTo(testName2);
        }
    }

    /**
     * Get statistics of passed/failed/skipped results in a single test
     * If all passed, passed amount will be highlighted with Green, default color otherwise
     * If failed amount > 0, it will be highlighted with red, default color otherwise
     * if skipped amount > 0, it will be highlighted with yellow, default color otherwise
     * @param ctx
     * @return test statistics string with highlight color, e.g.
     *     <font style='background:#00FF00'>Passed: 4</font>  Failed : 0 Skipped : 0
     *     Passed: 0 <font style='background:#FF0000'>Failed: 4</font> Skipped : 0
     *     Passed: 1 Failed: 0 <font style='background:yellow'>Skipped: 1</font>
     */
    private String getTestResultStatistics(ITestContext ctx){

        int total = ctx.getPassedTests().size() + ctx.getFailedTests().size() + ctx.getSkippedTests().size();

        StringBuilder summary = new StringBuilder("Total : ").append(total).append(" ");

        // success results statistics
        summary = getTestResultStatistics(ctx, TestNGResultStatus.EnumStatus.SUCCESS,
                ctx.getPassedTests().size() == total,COLOR_PASSED,summary);
        // failure results statistics
        summary = getTestResultStatistics(ctx, TestNGResultStatus.EnumStatus.FAILURE,
            ctx.getFailedTests().size() > 0, COLOR_FAILED,summary);

        // skip results statistics
        summary = getTestResultStatistics(ctx, TestNGResultStatus.EnumStatus.SKIP,
                ctx.getSkippedTests().size() > 0, COLOR_SKIPPED,summary);

        return summary.toString();
    }

    /**
     * Get amount of some a kind of results in a single test
     * @param ctx
     * @param status
     * @param condition
     * @param fontBgColor
     * @return test statistics string with highlight color
     */
    private StringBuilder getTestResultStatistics(ITestContext ctx,
                                                  TestNGResultStatus.EnumStatus status,
                                                  Boolean condition,
                                                  String fontBgColor,
                                                  StringBuilder summaryBuilder

    ) {

        boolean colorized  = condition &&  null != fontBgColor && "" != fontBgColor;

        if (colorized) {
            summaryBuilder.append("<font style='background:"+ fontBgColor +"'>");
        }

        switch (status){
            case SUCCESS:
                summaryBuilder.append("Passed : ");
                summaryBuilder.append(ctx.getPassedTests().size());
                break;
            case FAILURE:
                summaryBuilder.append("Failed : ");
                summaryBuilder.append(ctx.getFailedTests().size());
                break;
            case SKIP:
                summaryBuilder.append("Skipped : ");
                summaryBuilder.append(ctx.getSkippedTests().size());
                break;
        }

        if (colorized) {
            summaryBuilder.append("</font>");
        }

        summaryBuilder.append(" ");

        return summaryBuilder;
    }
}

