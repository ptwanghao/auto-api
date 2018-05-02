package com.yongche.framework.utils;

import com.yongche.framework.core.*;
import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class XMLUtil {
    public static TestSuite getTestSuiteByPath(String path){
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("testsuite", TestSuite.class);
        digester.addSetProperties("testsuite");

        digester.addObjectCreate("testsuite/testcase", TestCase.class);
        digester.addSetProperties("testsuite/testcase");

        digester.addObjectCreate("testsuite/testcase/input/parameter", TestParameter.class);
        digester.addSetProperties("testsuite/testcase/input/parameter");
        digester.addBeanPropertySetter("testsuite/testcase/input/parameter","value");
        digester.addSetNext("testsuite/testcase/input/parameter", "addInputParameter");

        digester.addObjectCreate("testsuite/testcase/expectresult/result", TestExpectResult.class);
        digester.addSetProperties("testsuite/testcase/expectresult/result");
        digester.addBeanPropertySetter("testsuite/testcase/expectresult/result","value");
        digester.addSetNext("testsuite/testcase/expectresult/result", "addExpectResult");

        digester.addObjectCreate("testsuite/testcase/output/parameter", TestParameter.class);
        digester.addSetProperties("testsuite/testcase/output/parameter");
        digester.addBeanPropertySetter("testsuite/testcase/output/parameter","value");
        digester.addSetNext("testsuite/testcase/output/parameter", "addOutputParameter");

        digester.addSetNext("testsuite/testcase", "addTestCase");

        TestSuite ts = null;
        try {
            ts = digester.parse(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ts;
    }

    public static Map<String, TestParameter>  getParameterFromXML(String xmlFileName,String caseName){
        String xmlFilePath = "XMLCase/" + xmlFileName.trim();
        TestSuite suite = XMLUtil.getTestSuiteByPath(xmlFilePath);
        TestCase testcase = suite.getTestCaseByName(caseName);
        Map<String, TestParameter> parameters = testcase.getInputParameterMap();
        return parameters;
    }
}

