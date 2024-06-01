package com.lemonzuo.license.jetbrains.controller;

import com.lemonzuo.license.jetbrains.service.CodeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LemonZuo
 * @create 2024-02-22 10:58
 */
@RestController
@RequestMapping("/jetbrains/code")
public class CodeController {
    @Resource
    private CodeService codeService;


    /**
     * 获取code列表
     * @return code列表
     * @throws Exception 异常
     */
    @GetMapping("/getCodeList")
    public List<String> getCodeList() throws Exception {
        return codeService.getCodeList();
    }
}
