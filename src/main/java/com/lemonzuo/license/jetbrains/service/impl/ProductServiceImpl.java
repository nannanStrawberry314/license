package com.lemonzuo.license.jetbrains.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemonzuo.license.jetbrains.entity.ProductEntity;
import com.lemonzuo.license.jetbrains.mapper.ProductMapper;
import com.lemonzuo.license.jetbrains.service.ProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LemonZuo
 * @create 2024-02-22 10:44
 */
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductEntity> implements ProductService {
    @Resource
    private ObjectMapper mapper;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);
    /**
     * 查询最新的产品信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fetchLatest() {
        EXECUTOR.submit(() -> {
            try {
                this.executeFetchLatest();
            } catch (Exception e) {
                log.error("获取产品信息失败", e);
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void executeFetchLatest() throws Exception {
        HttpResponse response = HttpRequest
                .get("https://data.services.jetbrains.com/products")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                .execute();
        if (!response.isOk()) {
            log.error("获取产品信息失败");
            throw new Exception("获取产品信息失败");
        }
        JsonNode products = mapper.readTree(response.body());
        int size = products.size();
        AtomicInteger index = new AtomicInteger(1);
        List<ProductEntity> list = new ArrayList<>();
        for (JsonNode product : products) {
            log.info("待处Product总数:{},当前正在处理第:{}个", size, index.getAndIncrement());
            ProductEntity entity = new ProductEntity();
            entity.setProductDetail(product.toPrettyString());
            entity.setProductCode(product.get("code").asText());
            entity.setProductName(product.get("name").asText());
            list.add(entity);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            baseMapper.truncate();
            this.saveBatch(list);
        }
    }
}
