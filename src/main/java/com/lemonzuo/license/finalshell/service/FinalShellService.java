package com.lemonzuo.license.finalshell.service;

import java.util.List;

/**
 * @author LemonZuo
 * @create 2024-03-11 21:58
 */
public interface FinalShellService {
    /**
     * 生成license
     * @param machineCode 机器码
     * @return String 证书
     */
    List<String> generateLicense(String machineCode);
}
