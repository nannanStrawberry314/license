package com.lemonzuo.license.gitlab.entity;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author LemonZuo
 * @create 2023-11-03 10:31
 */
@Data
public class License {
    private Integer version;

    @JsonProperty("licensee")
    private LicenseInfo license;

    @JsonProperty("issued_at")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    private Date startsAt;

    @JsonProperty("expires_at")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    private Date expiresAt;

    @JsonProperty("notify_admins_at")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    private Date notifyAdminsAt;

    @JsonProperty("notify_users_at")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    private Date notifyUsersAt;

    @JsonProperty("block_changes_at")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    private Date blockChangesAt;

    @JsonProperty("cloud_licensing_enabled")
    private Boolean cloudLicensingEnabled;

    @JsonProperty("offline_cloud_licensing_enabled")
    private Boolean offlineCloudLicensingEnabled;

    @JsonProperty("auto_renew_enabled")
    private Boolean autoRenewEnabled;

    @JsonProperty("seat_reconciliation_enabled")
    private Boolean seatReconciliationEnabled;

    @JsonProperty("operational_metrics_enabled")
    private Boolean operationalMetricsEnabled;

    @JsonProperty("generated_from_customers_dot")
    private Boolean generatedFromCustomersDot;

    @JsonProperty("restrictions")
    private Restriction restrictions;
}
