package com.lemonzuo.license.gitlab.controller;


import com.lemonzuo.license.gitlab.entity.LicenseInfo;
import com.lemonzuo.license.gitlab.entity.LicenseInfoParam;
import com.lemonzuo.license.gitlab.util.LicenseUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LemonZuo
 * @create 2023-11-03 10:30
 */
@RestController
@RequestMapping("/gitlab")
public class GitLabController {

    /**
     * 生成license
     * @param param license信息
     * @param response response
     */
    @PostMapping("/generate")
    public void generate(@RequestBody LicenseInfoParam param, HttpServletResponse response) {
        LicenseUtil.generate(param, response);
    }
}
