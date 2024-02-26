package com.lemonzuo.license.gitlab.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author LemonZuo
 * @create 2023-11-03 10:31
 */
@Data
public class LicenseInfo {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Company")
    private String company;

    @JsonProperty("Email")
    private String email;
}
