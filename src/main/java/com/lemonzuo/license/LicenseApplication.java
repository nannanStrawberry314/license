package com.lemonzuo.license;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author LemonZuo
 * @create 2024-02-22 8:57
 */
@Slf4j
@EnableScheduling
@SpringBootApplication
@MapperScan("com.lemonzuo.license.**.mapper")
public class LicenseApplication {

    private static void checkPathConfig() {
        String customPath = System.getProperty("path");
        if (StrUtil.isEmpty(customPath)) {
            log.error("请配置path参数, 例如: -Dpath=/license");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        checkPathConfig();
        SpringApplication.run(LicenseApplication.class, args);
    }
}
