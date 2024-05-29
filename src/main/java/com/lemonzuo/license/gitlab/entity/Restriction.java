package com.lemonzuo.license.gitlab.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author LemonZuo
 * @create 2023-11-03 10:30
 */
@Data
public class Restriction {
    @JsonProperty("active_user_count")
    private Integer activeUserCount;

    private String plan;

    /**
     * 额外的功能,暂时未生效
     */
    @JsonProperty("add_ons")
    private Map<String, Integer> addOns;
}
