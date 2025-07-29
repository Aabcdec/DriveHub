package com.example.web.Result;



public enum DicEnum {

    APPELLATION("appellation"),

    SOURCE("source"),

    STATE("clueState"),

    INTENTIONSTATE("intentionState"),

    NEEDLOAN("needLoan"),

    PRODUCT("product"),

    ACTIVITY("activity")

    ;


    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    DicEnum(String code) {
        this.code = code;
    }

    DicEnum() {
    }
}
