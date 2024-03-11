package com.lemonzuo.license.finalshell.controller;

import cn.hutool.core.util.StrUtil;
import com.lemonzuo.license.finalshell.service.FinalShellService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LemonZuo
 * @create 2024-03-11 21:51
 */
@RestController
@RequestMapping("/final-shell")
public class FinalShellController {
    @Resource
    private FinalShellService finalShellService;

    /**
     * 生成license
     * @param machineCode 机器码
     * @return String 证书
     */
    @PostMapping("/generateLicense")
    public List<String> generateLicense(@RequestParam String machineCode) {
        if (StrUtil.isEmptyIfStr(machineCode)) {
            throw new RuntimeException("machineCode不能为空");
        }
        return finalShellService.generateLicense(machineCode);
    }
}
