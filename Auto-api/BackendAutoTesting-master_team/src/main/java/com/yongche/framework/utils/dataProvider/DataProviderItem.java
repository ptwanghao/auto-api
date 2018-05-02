package com.yongche.framework.utils.dataProvider;


public class DataProviderItem {

    private String name;
    private String value;
    private boolean enable;
    private boolean recursive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getXmlFilesRoot(){
        return value;
    }

    /**
     * Get the root dir of the test files. As same as getXmlFilesRoot() or getValue()
     * @return
     */
    public String getRoot(){
        return value;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }


}
