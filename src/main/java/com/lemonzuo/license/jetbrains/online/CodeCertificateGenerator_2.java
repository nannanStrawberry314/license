package com.lemonzuo.license.jetbrains.online;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.PemUtil;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @Author: Crazer
 * @Date: 2022/8/11 14:01
 * @version: 1.0.0
 * @Description: 生成激活码证书
 */
public class CodeCertificateGenerator_2 {
    // 第一种方式
    private static final String CA_CERT0_PATH = "/opt/data/idea_data/license/src/main/resources/cert/CodeCA0.pem";
    // 第二种方式
    private static final String CA_CERT_PATH = "/opt/data/idea_data/license/src/main/resources/cert/CodeCA.pem";
    private static final String CODE_CERT_PATH = "/opt/data/idea_data/license/src/main/resources/cert/CodeCert.pem";


    public static void main(String[] args) throws Exception {
        // 读取code证书密钥对
        PrivateKey privateKey2048 = PemUtil.readPemPrivateKey(new ClassPathResource("cert/privateKey2048.pem").getInputStream());
        PublicKey publicKey2048 = PemUtil.readPemPublicKey(new ClassPathResource("cert/publicKey2048.pem").getInputStream());
        // 读取根CA密钥对
        PrivateKey privateKey4096 = PemUtil.readPemPrivateKey(new ClassPathResource("cert/privateKey4096.pem").getInputStream());
        PublicKey publicKey4096 = PemUtil.readPemPublicKey(new ClassPathResource("cert/publicKey4096.pem").getInputStream());

        System.out.println("========== 生成JetProfile CA证书 ==========");
        // 第一种方式
        // 生成JetProfile CA证书
        // unstandardGenerateCACert(publicKey4096, privateKey4096, CA_CERT0_PATH);

        // 第二种方式
        byte[] codeCertBytes = PemUtil.readPem(new ClassPathResource("cert/jb/JBCodeCACert.pem").getInputStream());
        X509Certificate templateCert = X509CertUtils.createCertificate(codeCertBytes);

        // 1、生成JetProfile CA证书
        standardGenerateCACert(templateCert, publicKey4096, privateKey4096, CA_CERT_PATH);

        System.out.println("========== 签发子证书 ==========");
        // 2、签发子证书
        byte[] myCodeCertBytes = PemUtil.readPem(new FileInputStream(CA_CERT_PATH));
        X509Certificate myCACert = X509CertUtils.createCertificate(myCodeCertBytes);
        issueChildCertificate(myCACert, templateCert, publicKey2048, privateKey4096, CODE_CERT_PATH);
    }

    /**
     * 非标准生成JetProfile CA证书
     *
     * @param publicKey  4096位 公钥
     * @param privateKey 4096位 私钥
     * @param certPath   证书路径
     * @throws Exception
     */
    public static void unstandardGenerateCACert(PublicKey publicKey, PrivateKey privateKey, String certPath) throws Exception {
        // 根证书 签发者
        X500Name issuerName = new X500Name("CN=JetProfile CA");
        X500Name subjectName = new X500Name("CN=crazer-from-20220801");
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        DateTime today = DateUtil.beginOfDay(DateUtil.date());
        // Yesterday
        Date notBefore = DateUtil.offset(today, DateField.DAY_OF_MONTH, -1);
        // 30 years later
        Date notAfter = DateUtil.offset(today, DateField.YEAR, 30);

        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(issuerName, serialNumber, notBefore, notAfter, subjectName, subPubKeyInfo);
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(privateKey);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(signer));

        // 将证书写入 PEM 文件
        X509CertUtils.writeCertificate(cert, certPath);
    }

    /**
     * 标准生成JetProfile CA证书
     *
     * @param templateCert JB CA模板证书
     * @param publicKey    4096位 公钥
     * @param privateKey   4096位 私钥
     * @param certPath     证书路径
     * @throws Exception
     */
    public static void standardGenerateCACert(X509Certificate templateCert, PublicKey publicKey, PrivateKey privateKey, String certPath) throws Exception {
        X500Name issuerName = new X500Name(templateCert.getIssuerX500Principal().getName());
        X500Name subjectName = new X500Name(templateCert.getSubjectX500Principal().getName());
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = templateCert.getNotBefore();
        Date notAfter = templateCert.getNotAfter();

        // 创建 SubjectPublicKeyInfo 对象
        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

        // 使用 JcaX509v3CertificateBuilder 构建 X509v3CertificateBuilder 对象
        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuerName,            // 颁发者信息
                serialNumber,          // 序列号
                notBefore,             // 证书生效日期
                notAfter,              // 证书失效日期
                subjectName,           // 证书的主题信息
                subPubKeyInfo          // 证书的公钥信息
        );
        // 复制模板证书中的拓展信息到新证书中
        X509CertUtils.copyExtensions(templateCert, certBuilder);

        // 使用 JcaContentSignerBuilder 构建 ContentSigner 对象
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(privateKey);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(signer));
        // 将证书写入 PEM 文件
        X509CertUtils.writeCertificate(cert, certPath);
    }

    /**
     * 签发子证书
     *
     * @param MyCACert         我的CA根证书
     * @param JBCACert         JB的CA证书
     * @param publicKey        2048位 公钥
     * @param issuerPrivateKey 4096位 签发者私钥
     * @param certPath         证书路径
     * @throws Exception
     */
    public static void issueChildCertificate(X509Certificate MyCACert, X509Certificate JBCACert, PublicKey publicKey, PrivateKey issuerPrivateKey, String certPath) throws Exception {
        X500Name issuerName = new X500Name(MyCACert.getSubjectX500Principal().getName()); // 根证书 签发者
        X500Name subjectName = new X500Name("CN=crazer-from-20220801");
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        DateTime today = DateUtil.beginOfDay(DateUtil.date());
        // Yesterday
        Date notBefore = DateUtil.offset(today, DateField.DAY_OF_MONTH, -1);
        // 30 years later
        Date notAfter = DateUtil.offset(today, DateField.YEAR, 30);

        // 创建 SubjectPublicKeyInfo 对象
        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

        // 使用 JcaX509v3CertificateBuilder 构建 X509v3CertificateBuilder 对象
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(issuerName, serialNumber, notBefore, notAfter, subjectName, subPubKeyInfo);

        // 添加扩展
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));    // entity cert
        certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        certBuilder.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(new KeyPurposeId[]{KeyPurposeId.id_kp_serverAuth}));

        SubjectPublicKeyInfo issuerPublicKeyInfo = SubjectPublicKeyInfo.getInstance(JBCACert.getPublicKey().getEncoded());
        AuthorityKeyIdentifier authorityKeyIdentifier = extUtils.createAuthorityKeyIdentifier(issuerPublicKeyInfo);
        certBuilder.addExtension(Extension.authorityKeyIdentifier, false, authorityKeyIdentifier); // 授权密钥标识

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(MyCACert.getPublicKey().getEncoded());
        SubjectKeyIdentifier subjectKeyIdentifier = extUtils.createSubjectKeyIdentifier(subjectPublicKeyInfo);
        certBuilder.addExtension(Extension.subjectKeyIdentifier, false, subjectKeyIdentifier);

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(issuerPrivateKey);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(signer));
        // 将证书写入 PEM 文件
        X509CertUtils.writeCertificate(cert, certPath);
    }

}