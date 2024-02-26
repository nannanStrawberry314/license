package com.lemonzuo.license.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.lemonzuo.license.entity.PluginEntity;
import com.lemonzuo.license.entity.ProductEntity;
import com.lemonzuo.license.service.CodeService;
import com.lemonzuo.license.service.PluginService;
import com.lemonzuo.license.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LemonZuo
 * @create 2024-02-22 11:00
 */
@Service
public class CodeServiceImpl implements CodeService {
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
        List<String> codeList = new ArrayList<>(productEntities.stream().map(ProductEntity::getProductCode).toList());
        codeList.addAll(pluginEntities.stream().map(PluginEntity::getPluginCode).toList());
        // 手动添加II
        codeList.addFirst("II");
        return codeList;
    }
}
