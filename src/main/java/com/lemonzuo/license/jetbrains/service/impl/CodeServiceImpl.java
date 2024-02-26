package com.lemonzuo.license.jetbrains.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.lemonzuo.license.jetbrains.entity.PluginEntity;
import com.lemonzuo.license.jetbrains.entity.ProductEntity;
import com.lemonzuo.license.jetbrains.service.CodeService;
import com.lemonzuo.license.jetbrains.service.PluginService;
import com.lemonzuo.license.jetbrains.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author LemonZuo
 * @create 2024-02-22 11:00
 */
@Service
public class CodeServiceImpl implements CodeService {
    /**
     * 部分code值从热佬网站获取
     */
    private static final String[] PRODUCT_CODES = {"II", "PS", "AC", "DB", "RM", "WS", "RD", "CL", "PC", "GO", "DS", "DC", "DPN", "DM"};
    @Resource
    private PluginService pluginService;
    @Resource
    private ProductService productService;

    @Override
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void fetchLatest() throws Exception {
        productService.fetchLatest();
        pluginService.fetchLatest();
    }

    @Override
    public List<String> getCodeList() throws Exception {
        List<ProductEntity> productEntities = productService.list();
        if (CollectionUtil.isEmpty(productEntities)) {
            productService.fetchLatest();
            productEntities = productService.list();
        }
        List<PluginEntity> pluginEntities = pluginService.list();
        if (CollectionUtil.isEmpty(pluginEntities)) {
            pluginService.fetchLatest();
            pluginEntities = pluginService.list();
        }
        List<String> codeList = new ArrayList<>();
        Collections.addAll(codeList, PRODUCT_CODES);
        List<String> codes = productEntities.stream().map(ProductEntity::getProductCode).toList();
        codeList.addAll(codes);
        codeList.addAll(pluginEntities.stream().map(PluginEntity::getPluginCode).toList());
        return codeList;
    }
}
