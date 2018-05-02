package com.yongche.framework.utils.dataProvider;

import com.yongche.framework.SingleInterfaceExecutor;
import com.yongche.framework.utils.CommonUtil;
import org.apache.commons.digester3.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Gather the input test files and pass on to test method
 * All input test files should be under XMLCase directory
 */
public class TestDataProvider {
    public static Logger log = LoggerFactory.getLogger(SingleInterfaceExecutor.class);
    public static final String DATA_PROVIDER_CONFIGURATION_FILE = "XMLCase/data_provider.xml";
    public static final String XMLCASE_ROOT = "./XMLCase";

    /**
     * Collect all test xml files according to data provider configuration and pass on to test method
     */
    @DataProvider(name = "DataProvider_By_Configuration_File")
    public static Iterator<Object[]> dataProviderByConfiguration() {

        List<Object[]> parameterList = new ArrayList<>();

        DataProviderCollection collection = getDataProviderCollection(DATA_PROVIDER_CONFIGURATION_FILE);
        collection.forEach(dataProviderItem->{
            if(dataProviderItem.isEnable()) {
                parameterList.addAll(loadXmlFileList(dataProviderItem.getXmlFilesRoot(),dataProviderItem.isRecursive()));
            }
        });

        log.info("Data Provider by Configuration: input xml files count: " + parameterList.size() );
        parameterList.forEach( xmlFileArray -> log.info("    " + xmlFileArray[0]));
        return parameterList.iterator();

    }

    /**
     * Collect all test xml files according to test method annotation @XmlFileParameters.
     * @param testMethod
     * @return
     * @throws RuntimeException Could NOT find the annotation : XmlFileParameters for test method: + test method name
     */
    @DataProvider(name = "DataProvider_By_Parameter")
    public static Iterator<Object[]> dataProviderByXmlFileParameters(final Method testMethod) throws RuntimeException{
        XmlFileParameters parameters = testMethod.getAnnotation(XmlFileParameters.class);

        if(null != parameters) {
            List<Object[]> parameterList = loadXmlFileList(parameters.path(),parameters.recursive());
            log.info("Data Provider by Parameter : input xml files count: " + parameterList.size() );
            parameterList.forEach( xmlFileArray -> log.info("    " + xmlFileArray[0]));
            return parameterList.iterator();
        } else {
            throw new RuntimeException("Could NOT find the annotation : XmlFileParameters for test method: "
                    + testMethod.getName());
        }
    }

    private static List<Object[]> loadXmlFileList(String path,boolean recursive){
        List<Object[]> parameterList = new ArrayList<>();

        File xmlCaseRoot = new File(XMLCASE_ROOT);

        try {
            Path pathOfXMLCaseRoot = Paths.get(xmlCaseRoot.getCanonicalPath());

            CommonUtil.getAllFiles(path, recursive, null).forEach((xmlFile) -> {

                Path absolutePathOfXmlFile = Paths.get(xmlFile);
                // get the relative path to .\XMLCase
                // The relative path will look like : xxx.xml or featurefolder\xxx.xml. They are all under directory .\XMLCase
                Path relativePathOfXmlFile = pathOfXMLCaseRoot.relativize(absolutePathOfXmlFile);
                parameterList.add(new Object[]{ relativePathOfXmlFile.toString() });

            });
        }
        catch (IOException e) { // handle with exception raised by getCanonicalPath()
            e.printStackTrace();
            log.error(e.getMessage());
        }

        return parameterList;
    }

    public static DataProviderCollection getDataProviderCollection(String path){
        Digester digester = new Digester();
        digester.setValidating(false); //XMLDTDValidator.java seems not to validate schema correctly ?!
        digester.addObjectCreate("dataprovider", DataProviderCollection.class);
        digester.addSetProperties("dataprovider");

        digester.addObjectCreate("dataprovider/location", DataProviderItem.class);
        digester.addSetProperties("dataprovider/location");
        //digester.addBeanPropertySetter("dataprovider/location");
        digester.addSetNext("dataprovider/location", "add");

        DataProviderCollection collection = null;
        try {
            collection = digester.parse(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return collection;
    }
}
