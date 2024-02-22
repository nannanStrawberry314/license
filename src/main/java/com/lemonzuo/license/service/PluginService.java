package com.lemonzuo.license.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lemonzuo.license.entity.PluginEntity;

/**
 * @author LemonZuo
 * @create 2024-02-22 9:34
 */
public interface PluginService extends IService<PluginEntity> {
    /**
     * 查询jetbrains付费插件信息
     * @throws Exception 异常
     */
    void fetchLatest() throws Exception;
}
