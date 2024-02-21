package com.lemonzuo.license;

import com.lemonzuo.license.generator.CertificateGenerator;
import com.lemonzuo.license.generator.LicenseGenerator;
import com.lemonzuo.license.generator.PowerConfRuleGenerator;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LemonZuo
 * @create 2024-02-20 23:13
 */
@Slf4j
public class JetbrainsLicense {
    public static void main(String[] args) throws Exception {
        // 1. 生成证书和私钥
        CertificateGenerator.generate();
        // 2. 生成证书校验规则
        PowerConfRuleGenerator.generate();
        // 3. 生成证书
        LicenseGenerator.generate();
    }
}
