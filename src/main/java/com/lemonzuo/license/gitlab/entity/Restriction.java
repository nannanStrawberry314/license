package com.lemonzuo.license.gitlab.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author LemonZuo
 * @create 2023-11-03 10:30
 */
@Data
public class Restriction {
    @JsonProperty("active_user_count")
    private Integer activeUserCount;

    private String plan;
}
