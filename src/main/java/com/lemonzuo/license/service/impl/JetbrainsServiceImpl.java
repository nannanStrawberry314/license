package com.lemonzuo.license.service.impl;

import com.lemonzuo.license.generator.CertificateGenerator;
import com.lemonzuo.license.generator.LicenseGenerator;
import com.lemonzuo.license.generator.PowerConfRuleGenerator;
import com.lemonzuo.license.service.JetbrainsService;
import com.lemonzuo.license.vo.License;
import org.springframework.stereotype.Service;

/**
 * @author LemonZuo
 * @create 2024-02-22 11:09
 */
@Service
public class JetbrainsServiceImpl implements JetbrainsService {
    /**
     * 生成license
     *
     * @return License
     */
    @Override
    public License generateLicense(String licenseeName) throws Exception {
        // 1. 生成证书和私钥
        CertificateGenerator.generate();
        // 2. 生成证书校验规则
        String powerConfRule = PowerConfRuleGenerator.generate();
        // 3. 生成证书
        String activationCode = LicenseGenerator.generate(licenseeName);

        License license = new License();
        license.setPowerConfRule(powerConfRule);
        license.setActivationCode(activationCode);
        return license;
    }
}
