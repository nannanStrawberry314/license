
package com.lemonzuo.license.jrebel.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author LemonZuo
 */
@Data
@Accessors(chain = true)
public class JrebelLeasesHandlerVO {

    private String serverVersion;
    private String serverProtocolVersion;
    private String serverGuid;
    private String groupType;
    private Integer id;
    private Integer licenseType;
    private Boolean evaluationLicense;
    private String signature;
    private String serverRandomness;
    private String seatPoolType;
    private String statusCode;
    private Boolean offline;
    private Long validFrom;
    private Long validUntil;
    private String company;
    private String orderId;
    private List<String> zeroIds;
    private Long licenseValidFrom;
    private Long licenseValidUntil;

}