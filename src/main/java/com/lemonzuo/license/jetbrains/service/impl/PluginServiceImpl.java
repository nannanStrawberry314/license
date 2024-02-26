package com.lemonzuo.license.jetbrains.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemonzuo.license.jetbrains.entity.PluginEntity;
import com.lemonzuo.license.jetbrains.mapper.PluginMapper;
import com.lemonzuo.license.jetbrains.service.PluginService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LemonZuo
 * @create 2024-02-22 9:35
 */
@Slf4j
@Service
public class PluginServiceImpl extends ServiceImpl<PluginMapper, PluginEntity> implements PluginService {
    @Resource
    private ObjectMapper mapper;

    /**
     * 获取付费插件信息
     */
    private static final String PAID_PLUGINS_URL = "https://plugins.jetbrains.com/api/searchPlugins?excludeTags=theme&max=500&offset=0&orderBy=downloads&pricingModels=PAID";
    /**
     * 获取可免费增值插件信息
     */
    private static final String FREEMIUM_PLUGINS_URL = "https://plugins.jetbrains.com/api/searchPlugins?excludeTags=theme&max=500&offset=0&orderBy=downloads&pricingModels=FREEMIUM";
    /**
     * 获取插件详情
     */
    private static final String PLUGIN_DETAIL_URL = "https://plugins.jetbrains.com/api/plugins/";
    /**
     * user-agent
     */
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3";

    private List<PluginEntity> fetchPaidPlugins(String url) throws Exception {
        HttpResponse response = HttpRequest
                .get(url)
                .header("User-Agent", USER_AGENT)
                .execute();

        if (!response.isOk()) {
            throw new RuntimeException("获取插件信息失败");
        }
        JsonNode node = mapper.readTree(response.body());
        JsonNode plugins = node.get("plugins");
        List<PluginEntity> list = new ArrayList<>();
        int size = plugins.size();
        AtomicInteger index = new AtomicInteger(1);
        for (JsonNode plugin : plugins) {
            Long pluginId = plugin.get("id").asLong();

            log.info("待处理插件总数:{},当前正在处理第:{}个,插件Id:{}", size, index.getAndIncrement(), pluginId);
            // 获取详情
            HttpResponse detailResponse = HttpRequest
                    .get(PLUGIN_DETAIL_URL + pluginId)
                    .header("User-Agent", USER_AGENT)
                    .execute();

            if (!detailResponse.isOk()) {
                log.error("获取插件详情失败, id:{}", pluginId);
                // 休眠一会
                Thread.sleep(RandomUtil.randomInt(100, 500));
                continue;
            }

            JsonNode detail = mapper.readTree(detailResponse.body());
            String pluginName = detail.get("name").asText();
            String pluginCode = detail.get("purchaseInfo").get("productCode").asText();
            PluginEntity entity = new PluginEntity();
            entity.setPluginId(pluginId);
            entity.setPluginName(pluginName);
            entity.setPluginCode(pluginCode);
            entity.setPluginApiDetail(detail.toPrettyString());
            list.add(entity);
            // 休眠一会
            Thread.sleep(RandomUtil.randomInt(100, 500));
        }

        return list;
    }

    /**
     * 查询jetbrains付费插件信息
     *
     * @throws Exception 异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fetchLatest() throws Exception {

        List<PluginEntity> list = new ArrayList<>();

        list.addAll(fetchPaidPlugins(PAID_PLUGINS_URL));
        list.addAll(fetchPaidPlugins(FREEMIUM_PLUGINS_URL));

        if (!list.isEmpty()) {
            // 清空表
            baseMapper.truncate();
            // 批量插入
            this.saveBatch(list);
        }
    }
}
