package com.lemonzuo.license.jetbrains.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lemonzuo.license.jetbrains.service.JetbrainsService;
import com.lemonzuo.license.jetbrains.vo.License;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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

    /**
     * 生成license
     *
     * @param licenseeName 授权人
     * @param effectiveDateStr 有效日期
     * @return License 证书
     */
    @RequestMapping(value = "/generate", method = {RequestMethod.GET, RequestMethod.POST})
    public License generate(@RequestParam(required = false, defaultValue = "") String licenseeName,
                            @RequestParam(required = false, name = "effectiveDate", defaultValue = "") String effectiveDateStr) throws Exception {
        Date effectiveDate = DateUtil.parse(effectiveDateStr);
        if (ObjectUtil.isNotNull(effectiveDate)) {
            // 传递了effectiveDate情况下，有效日期默认为当天的23:59:59
            effectiveDate = DateUtil.endOfDay(effectiveDate);
        }
        return jetbrainsService.generateLicense(licenseeName, effectiveDate);
    }

}
