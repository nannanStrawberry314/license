package com.lemonzuo.license.jetbrains.controller;

import com.lemonzuo.license.jetbrains.service.JetbrainsService;
import com.lemonzuo.license.jetbrains.vo.License;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author LemonZuo
 * @create 2024-02-20 23:13
 */
@Slf4j
@RestController
@RequestMapping("/jetbrains")
public class JetbrainsController {
    @Resource
    private JetbrainsService jetbrainsService;

    @RequestMapping(value = "/generate", method = {RequestMethod.GET, RequestMethod.POST})
    public License generate(@RequestParam(required = false) String licenseeName) throws Exception {
        return jetbrainsService.generateLicense(licenseeName);
    }

}
