package com.lemonzuo.license.jetbrains.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lemonzuo.license.jetbrains.entity.ProductEntity;

/**
 * @author LemonZuo
 * @create 2024-02-22 10:44
 */
public interface ProductService extends IService<ProductEntity> {
    /**
     * 查询最新的产品信息
     */
    void fetchLatest() throws Exception;
}
