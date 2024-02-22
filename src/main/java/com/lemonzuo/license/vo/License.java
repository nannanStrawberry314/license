package com.lemonzuo.license.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LemonZuo
 * @create 2024-02-22 11:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class License {
    private String powerConfRule;
    private String activationCode;
}
