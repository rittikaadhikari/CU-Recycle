package com.smapps.cu_recycle;

public class Search_Item {
    private String name;
    private String type;

    public Search_Item() {
        name = null;
        type = null;
    }
    public Search_Item(String movieName, String type) {
        this.name = movieName;
        this.type = type;
    }

    public String getObjectName() {
        return this.name;
    }

    public String getTypeName() {
        return this.type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
