package com.lemonzuo.license.mobaxterm.controller;

import com.lemonzuo.license.mobaxterm.service.MobaXtermService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LemonZuo
 * @create 2024-02-29 20:33
 */
@RestController
@RequestMapping("/mobaxterm")
public class MobaXtermController {
    @Resource
    private MobaXtermService mobaXtermService;
    /**
     * 生成license
     * @param name 授权人
     * @param version 版本
     * @param count 数量
     */
    @RequestMapping(value = "/generate", method = {RequestMethod.GET, RequestMethod.POST})
    public void generate(String name, String version, Integer count, HttpServletResponse response) {
        mobaXtermService.generate(name, version, count, response);
    }
}
