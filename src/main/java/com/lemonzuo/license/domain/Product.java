package com.lemonzuo.license.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LemonZuo
 * @create 2024-02-22 00:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String code;
    private String fallbackDate;
    private String paidUpTo;
    private Boolean extended;

    public Product(String code, String date) {
        this.code = code;
        this.fallbackDate = date;
        this.paidUpTo = date;
        this.extended = true;
    }
}
