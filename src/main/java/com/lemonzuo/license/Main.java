package com.lemonzuo.license;

/**
 * @author LemonZuo
 * @create 2024-02-20 23:13
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // 1. 生成证书和私钥
        CertificateGenerator.main(args);
        // 2. 生成证书校验规则
        PowerConfRuleGen.main(args);
        // 3. 生成证书
        LicenseGenerator.main(args);
    }
}
