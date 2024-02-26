package com.lemonzuo.license.jetbrains.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LemonZuo
 * @create 2024-02-22 10:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_jetbrains_product")
public class ProductEntity {
    @TableId
    private Long id;
    private String productCode;
    private String productName;
    private String productDetail;
}
