package com.lemonzuo.license.jetbrains.controller;

import com.lemonzuo.license.jetbrains.service.PluginService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LemonZuo
 * @create 2024-02-22 10:07
 */
@RestController
@RequestMapping("/plugin")
public class PluginController {
    @Resource
    private PluginService pluginService;

    @PostMapping("/fetchLatest")
    public void fetchLatest() throws Exception {
        pluginService.fetchLatest();
    }
}
