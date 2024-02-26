package com.lemonzuo.license.jetbrains.service;

import java.util.List;

/**
 * @author LemonZuo
 * @create 2024-02-22 11:00
 */
public interface CodeService {
    void fetchLatest() throws Exception;
    /**
     * 获取代码列表
     * @return
     * @throws Exception
     */
    List<String> getCodeList() throws Exception;
}
