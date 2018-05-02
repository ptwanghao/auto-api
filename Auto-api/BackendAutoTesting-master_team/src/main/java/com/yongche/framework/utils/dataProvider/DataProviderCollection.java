package com.yongche.framework.utils.dataProvider;

import  java.util.*;


public class DataProviderCollection implements Iterable<DataProviderItem>{

    private List<DataProviderItem> collection = new ArrayList<>();

    /**
     * Implement the abstract method iterator()
     * @return
     */
    @Override
    public Iterator<DataProviderItem> iterator(){
        return  collection.iterator();
    }

    public boolean add(DataProviderItem element) {
        return this.collection.add(element);
    }

    public void add(int index, DataProviderItem element){
        collection.add(index, element);
    }

    public boolean remove(DataProviderItem element) {
        return collection.remove(element);
    }

    public DataProviderItem remove(int index){
        return collection.remove(index);
    }

    public long size(){
        return collection.size();
    }
}


