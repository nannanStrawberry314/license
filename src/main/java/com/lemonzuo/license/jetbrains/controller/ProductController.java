package com.lemonzuo.license.jetbrains.controller;

import com.lemonzuo.license.jetbrains.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LemonZuo
 * @create 2024-02-22 10:45
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    @Resource
    private ProductService productService;

    /**
     * 查询产品信息
     * @throws Exception 异常
     */
    @PostMapping("/fetchLatest")
    public void fetchLatest() throws Exception {
        productService.fetchLatest();
    }
}
