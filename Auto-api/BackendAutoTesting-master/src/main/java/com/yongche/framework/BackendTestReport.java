package com.yongche.framework;

import org.testng.*;
import org.testng.xml.XmlSuite;
import java.io.FileOutputStream;
import java.io.PrintStream;
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

    private int passedTestCaseAmount = 0;
    private int failedTestCaseAmount = 0;
    private int skippedTestCaseAmount = 0;
    private float passRate = 0;

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
                               String outputDir) {
        String testReportName = getCurrentTime() + TEST_REPORT_FILE_NAME;
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
                ArrayList<ITestResult> testSuiteResultList = new ArrayList<>();
                String testSuiteName = suite.getName();

                // get the test result maps of this test suite.
                Map<String, ISuiteResult> testSuiteResultsMap = suite.getResults();
                for (ISuiteResult suiteResult : testSuiteResultsMap.values()) {
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
            this.printTestSummary(sb, totalTestSuiteAmount, totalTestCaseAmount, passedTestCaseAmount, failedTestCaseAmount, skippedTestCaseAmount, passRate);

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
    private void printTestSummary(StringBuilder sb, int totalTestSuiteAmount, int totalTestCaseAmount, int passedTestCaseAmount, int failedTestCaseAmount, int skippedTestCaseAmount, float passRate) {
        sb.append("<p>");
        sb.append("<h1>" + TEST_REPORT_TITLE + "</h1>");
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
            sb.append("<td class=\"feature\"> Feature Under Test: " + testName + "</td>");
            sb.append("</tr>");

            sb.append("<tr>");
            sb.append("<th>Test Case Name</th>");
            sb.append("<th>Test Execution Result</th>");
            sb.append("<th>Test Execution Time</th>");
            sb.append("<th>Details</th>");
            sb.append("</tr>");
            for (ITestResult result : results.get(testSuiteName).get(testName)) {
                if (result.getTestContext().getCurrentXmlTest().getName().equals(testName)) {
                    ITestNGMethod testMethod = result.getMethod();
                    String testMethodName = testMethod.getMethodName();
                    long startMillis = result.getStartMillis();
                    long endMillis = result.getEndMillis();
                    long duration = endMillis - startMillis;

                    // get test result: Passed, Failed, Skipped; and test execution details.
                    int resultInt = result.getStatus();
                    String resultStr = "N/A";
                    String resultInfo = "N/A";
                    if (resultInt == result.SUCCESS) {
                        resultStr = "\"#00FF00\">" + "Passed";
                        //resultInfo = result.getMethod().getClass().toString();
                        resultInfo = result.getTestClass().getName();
                    } else if (resultInt == result.FAILURE) {
                        resultStr = "\"#FF0000\">" + "Failed";
                        resultInfo = result.getThrowable().toString();
                    } else if (resultInt == result.SKIP) {
                        resultStr = "\"yellow\">" + "Skipped";
                        resultInfo = result.getTestClass().getName();
                    }
                    sb.append("<tr>");
                    sb.append("<td>" + testMethodName + "</td>");
                    sb.append("<td bgcolor=" + resultStr + "</td>");
                    sb.append("<td>" + duration + "(ms)" + "</td>");
                    sb.append("<td>" + resultInfo + "</td>");
                    sb.append("</tr>");
                }
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
    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        String day = Integer.toString(calendar.get(Calendar.DATE));
        String hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));
        String second = Integer.toString(calendar.get(Calendar.SECOND));
        String currentTimeStamp = year + "_" + month + "_" + day + "_" + hour + "_" + minute + "_" + second + "_";
        return currentTimeStamp;
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
}

