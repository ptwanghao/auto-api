package com.yongche.framework.config;
import org.apache.commons.digester3.Digester;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The class describe the top infrastructure of config.xml
 */
public class XmlConfigurationCollection implements Iterable<ActiveConfiguration> {

    public static final String CONFIIG_XML_NAME = "config/config.xml";

    private List<ActiveConfiguration> collection = new ArrayList<>();

    /**
     * Implement the abstract method iterator()
     * @return
     */
    @Override
    public Iterator<ActiveConfiguration> iterator(){
        return  collection.iterator();
    }

    public boolean add(ActiveConfiguration element) {
        return this.collection.add(element);
    }

    public void add(int index, ActiveConfiguration element){
        collection.add(index, element);
    }

    public boolean remove(ActiveConfiguration element) {
        return collection.remove(element);
    }

    public ActiveConfiguration remove(int index){
        return collection.remove(index);
    }

    public long size(){
        return collection.size();
    }


    public static XmlConfigurationCollection getXmlConfigurations(){
        return getXmlConfigurations(CONFIIG_XML_NAME);
    }

    public static XmlConfigurationCollection getXmlConfigurations(String path){
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("configurations", XmlConfigurationCollection.class);
        digester.addSetProperties("configurations");

        digester.addObjectCreate("configurations/configuration", ActiveConfiguration.class);
        digester.addSetProperties("configurations/configuration");
        digester.addSetNext("configurations/configuration", "add");

        XmlConfigurationCollection collection = null;
        try {
            collection = digester.parse(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return collection;
    }
}
