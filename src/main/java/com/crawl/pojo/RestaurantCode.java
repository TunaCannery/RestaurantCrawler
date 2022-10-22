package com.crawl.pojo;


public class RestaurantCode {
    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public RestaurantCode() {
    }

    public RestaurantCode(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
