package com.lemonzuo.license.jrebel.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author LemonZuo
 * @create 2022-12-10 21:41
 */
@Data
@Accessors(chain = true)
public class JrebelValidateHandlerVO {
    private String serverVersion;
    private String serverProtocolVersion;
    private String serverGuid;
    private String groupType;
    private String statusCode;
    private String company;
    private Boolean canGetLease;
    private Integer licenseType;
    private Boolean evaluationLicense;
    private String seatPoolType;
}
