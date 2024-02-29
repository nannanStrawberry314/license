package com.lemonzuo.license.mobaxterm.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author LemonZuo
 * @create 2024-02-29 20:37
 */
public interface MobaXtermService {
    /**
     * 生成license
     * @param name 授权人
     * @param version 版本
     * @param count 数量
     * @param response 响应
     */
    void generate(String name, String version, Integer count, HttpServletResponse response);
}
