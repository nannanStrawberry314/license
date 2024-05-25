package com.lemonzuo.license.gitlab.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author LemonZuo
 * @create 2023-11-03 10:31
 */
@Data
public class LicenseInfoParam {
    private String name;

    private String company;

    private String email;

    private Date expiration;
}
