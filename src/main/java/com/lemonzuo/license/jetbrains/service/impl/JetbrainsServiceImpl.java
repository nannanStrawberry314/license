package com.lemonzuo.license.jetbrains.service.impl;

import com.lemonzuo.license.jetbrains.generator.code.CertificateGenerator;
import com.lemonzuo.license.jetbrains.generator.code.LicenseGenerator;
import com.lemonzuo.license.jetbrains.generator.code.PowerConfRuleGenerator;
import com.lemonzuo.license.jetbrains.service.JetbrainsService;
import com.lemonzuo.license.jetbrains.vo.License;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    public String generateLicense(String licenseeName, Date effectiveDate) throws Exception {
        // 1. 生成证书和私钥
        CertificateGenerator.generate();
        // 2. 生成证书校验规则
        String powerConfRule = PowerConfRuleGenerator.generate();
        // 3. 生成证书
        String activationCode = LicenseGenerator.generate(licenseeName, effectiveDate);

        License license = new License();
        license.setPowerConfRule(powerConfRule);
        license.setActivationCode(activationCode);

        StringBuilder result = new StringBuilder();
        result
                .append("================== power.conf ==================")
                .append("\n[Result]")
                .append("\n; Lemon active by code\n")
                .append(powerConfRule)
                .append("\n================== power.conf ==================")
                .append("\n================== activation code ==================\n")
                .append(activationCode)
                .append("\n================== activation code ==================\n");

        return result.toString();
    }
}
