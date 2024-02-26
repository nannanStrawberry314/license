package com.lemonzuo.license.jrebel.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author LemonZuo
 * @create 2022-12-10 18:03
 */
@Data
@Accessors(chain = true)
public class JrebelLeasesOneHandlerVO {
    private String serverVersion;
    private String serverProtocolVersion;
    private String serverGuid;
    private String groupType;
    private String statusCode;
    private String msg;
    private String statusMessage;
    private String company;
}
