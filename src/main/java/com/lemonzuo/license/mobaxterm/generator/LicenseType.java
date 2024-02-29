package com.lemonzuo.license.mobaxterm.generator;

/**
 * @author wanna
 * @since 2019-01-02
 */
public enum LicenseType {

    /**
     * 专业版
     */
    Professional(1, "专业版"),

    /**
     * 教育版
     */
    Educational(3, "教育版"),

    /**
     * 个人版
     */
    Personal(4, "个人版");

    private int code;

    private String name;

    LicenseType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }}
