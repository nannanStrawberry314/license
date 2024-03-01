package com.lemonzuo.license.mobaxterm.enums;

import lombok.Getter;

/**
 * @author wanna
 * @since 2019-01-02
 */
@Getter
public enum LicenseEnum {

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

    private final int code;

    private final String name;

    LicenseEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
