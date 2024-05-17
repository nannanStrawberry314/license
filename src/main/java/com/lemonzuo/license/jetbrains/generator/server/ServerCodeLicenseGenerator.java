// package com.lemonzuo.license.jetbrains.generator.server;
//
// import cn.hutool.core.codec.Base64;
// import cn.hutool.crypto.PemUtil;
// import org.springframework.core.io.ClassPathResource;
//
// import java.security.PrivateKey;
// import java.security.Signature;
//
// /**
//  * @Author: Crazer
//  * @Date: 2022/8/11 15:31
//  * @version: 1.0.0
//  * @Description: 生成激活码
//  */
// public class ServerCodeLicenseGenerator {
//     // public static void main(String[] args) throws Exception {
//     //
//     //     // 自己修改 license内容
//     //     // 注意licenseId要一致
//     //     // 这里是测试的goland
//     //     String licenseId = RandomUtil.randomString(10).toUpperCase(); // todo 第一部分
//     //     String licenseeName = "Crazer";
//     //     String licensePart = "{\"licenseId\":\"" + licenseId + "\",\"licenseeName\":\"" + licenseeName + "\",\"assigneeName\":\"\",\"assigneeEmail\":\"\",\"licenseRestriction\":\"\",\"checkConcurrentUse\":false,\"products\":[{\"code\":\"PCWMP\",\"fallbackDate\":\"2026-09-14\",\"paidUpTo\":\"2026-09-14\",\"extended\":true},{\"code\":\"GO\",\"fallbackDate\":\"2026-09-14\",\"paidUpTo\":\"2026-09-14\",\"extended\":false},{\"code\":\"PSI\",\"fallbackDate\":\"2026-09-14\",\"paidUpTo\":\"2026-09-14\",\"extended\":true}],\"metadata\":\"0120220801PSAN000005\",\"hash\":\"TRIAL:1805249793\",\"gracePeriodDays\":7,\"autoProlongated\":false,\"isAutoProlongated\":false}";
//     //     String licensePartBase64 = Base64.encode(licensePart.getBytes()); // todo 第二部分
//     //
//     //     // 方式一：使用4096 位JetProfile CA证书生成License
//     //     // unstandardGenerateCACertToLicense(licenseId, licensePart, licensePartBase64);
//     //
//     //     // 方式二：使用JetProfile CA颁发的子证书生成License
//     //     standardGenerateCACertToLicense(licenseId, licensePart, licensePartBase64);
//     // }
//
//     /**
//      * 使用非标准生成JetProfile CA证书生成License
//      *
//      * @throws Exception
//      */
//     public static void unstandardGenerateCACertToLicense(String licenseId, String licensePart, String licensePartBase64) throws Exception {
//         byte[] codeCertBytes = PemUtil.readPem(new ClassPathResource("cert/CodeCA0.pem").getInputStream());
//         String certBase64 = Base64.encode(codeCertBytes);   // todo 第四部分
//
//
//         PrivateKey privateKey = PemUtil.readPemPrivateKey(new ClassPathResource("cert/privateKey4096.pem").getInputStream());
//         // Sign sign = SecureUtil.sign(SignAlgorithm.SHA1withRSA, Base64.encode(privateKey.getEncoded()), Base64.encode(certificate.getPublicKey().getEncoded()));
//         Signature signature = Signature.getInstance("SHA1withRSA");
//         signature.initSign(privateKey);
//         signature.update(licensePart.getBytes());
//
//         byte[] signed = signature.sign();
//
//         String signatureBase64 = Base64.encode(signed); // todo 第三部分
//
//         // 研究激活码 -> 生成的规则结果
//         String key = licenseId + "-" + licensePartBase64 + "-" + signatureBase64 + "-" + certBase64;
//         System.out.println(key);
//     }
//
//     /**
//      * 使用标准生成JetProfile CA证书生成License
//      *
//      * @throws Exception
//      */
//     public static void standardGenerateCACertToLicense(String licenseId, String licensePart, String licensePartBase64) throws Exception {
//         byte[] codeCertBytes = PemUtil.readPem(new ClassPathResource("cert/CodeCert.pem").getInputStream());
//         String certBase64 = Base64.encode(codeCertBytes);   // todo 第四部分
//
//         PrivateKey privateKey = PemUtil.readPemPrivateKey(new ClassPathResource("cert/privateKey2048.pem").getInputStream());
//         // Sign sign = SecureUtil.sign(SignAlgorithm.SHA1withRSA, Base64.encode(privateKey.getEncoded()), Base64.encode(certificate.getPublicKey().getEncoded()));
//         Signature signature = Signature.getInstance("SHA1withRSA");
//         signature.initSign(privateKey);
//         signature.update(licensePart.getBytes());
//
//         byte[] signed = signature.sign();
//
//         String signatureBase64 = Base64.encode(signed); // todo 第三部分
//
//         // 研究激活码 -> 生成的规则结果
//         String key = licenseId + "-" + licensePartBase64 + "-" + signatureBase64 + "-" + certBase64;
//         System.out.println(key);
//     }
// }