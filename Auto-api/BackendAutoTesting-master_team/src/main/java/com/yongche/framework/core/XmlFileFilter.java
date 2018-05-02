package com.yongche.framework.core;

import java.io.File;
import java.io.FileFilter;

public class XmlFileFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        return pathname.getName().toLowerCase().endsWith(".xml") && pathname.isFile();
    }

    @Override
    public String toString(){
        return "xml";
    }
}