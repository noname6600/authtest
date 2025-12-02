package com.learn.test.enumerate;

public enum OrderStatus {
    N("NEW"),
    C("CANCER"),
    A("ACTIVE"),
    ;
    private final String desc;
    OrderStatus(String desc){this.desc = desc;}

}
