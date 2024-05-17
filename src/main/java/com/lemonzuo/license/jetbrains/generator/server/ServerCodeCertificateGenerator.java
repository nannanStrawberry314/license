package com.lemonzuo.license.jetbrains.generator.server;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.PemUtil;
import com.lemonzuo.license.jetbrains.constant.CertConstant;
import com.lemonzuo.license.jetbrains.util.X509CertUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * 生成激活码证书
 *
 * @author LemonZuo
 * @create 2024-05-17 00:12
 * step: 2.生成激活码证书
 */
@Slf4j
public class ServerCodeCertificateGenerator {

    @SneakyThrows
    public static void main(String[] args) {
        generate();
    }

    public static void generate() throws Exception {
        // 读取code证书密钥对
        PrivateKey privateKey2048 = PemUtil.readPemPrivateKey(new FileInputStream(CertConstant.PRIVATE_KEY_2048_PATH));
        PublicKey publicKey2048 = PemUtil.readPemPublicKey(new FileInputStream(CertConstant.PUBLIC_KEY_2048_PATH));
        // 读取根CA密钥对
        PrivateKey privateKey4096 = PemUtil.readPemPrivateKey(new FileInputStream(CertConstant.PRIVATE_KEY_4096_PATH));
        PublicKey publicKey4096 = PemUtil.readPemPublicKey(new FileInputStream(CertConstant.PUBLIC_KEY_4096_PATH));

        // 第二种方式
        byte[] codeCertBytes = PemUtil.readPem(new FileInputStream(CertConstant.JETBRAINS_CODE_CA_PATH));
        X509Certificate templateCert = X509CertUtils.createCertificate(codeCertBytes);

        // 1、生成JetProfile CA证书
        standardGenerateCaCert(templateCert, publicKey4096, privateKey4096, CertConstant.CODE_CA_PATH);

        log.info("========== 签发子证书 ==========");
        // 2、签发子证书
        byte[] myCodeCertBytes = PemUtil.readPem(new FileInputStream(CertConstant.CODE_CA_PATH));
        X509Certificate certificate = X509CertUtils.createCertificate(myCodeCertBytes);
        issueChildCertificate(certificate, templateCert, publicKey2048, privateKey4096, CertConstant.CODE_CERT_PATH);
    }

    /**
     * 标准生成JetProfile CA证书
     *
     * @param templateCert JB CA模板证书
     * @param publicKey    4096位 公钥
     * @param privateKey   4096位 私钥
     * @param certPath     证书路径
     * @throws Exception 异常
     */
    public static void standardGenerateCaCert(X509Certificate templateCert, PublicKey publicKey, PrivateKey privateKey, String certPath) throws Exception {
        X500Name issuerName = new X500Name(templateCert.getIssuerX500Principal().getName());
        X500Name subjectName = new X500Name(templateCert.getSubjectX500Principal().getName());
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = templateCert.getNotBefore();
        Date notAfter = templateCert.getNotAfter();

        // 创建 SubjectPublicKeyInfo 对象
        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

        // 使用 JcaX509v3CertificateBuilder 构建 X509v3CertificateBuilder 对象
        // 颁发者信息、序列号、证书生效日期、证书失效日期、证书的主题信息、证书的公钥信息
        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuerName,
                serialNumber,
                notBefore,
                notAfter,
                subjectName,
                subPubKeyInfo
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
     * @param rootCaCert          我的CA根证书
     * @param jetbrainsCaCert JB的CA证书
     * @param publicKey           2048位 公钥
     * @param issuerPrivateKey    4096位 签发者私钥
     * @param certPath            证书路径
     * @throws Exception 异常
     */
    public static void issueChildCertificate(X509Certificate rootCaCert, X509Certificate jetbrainsCaCert, PublicKey publicKey, PrivateKey issuerPrivateKey, String certPath) throws Exception {
        // 根证书 签发者
        X500Name issuerName = new X500Name(rootCaCert.getSubjectX500Principal().getName());
        X500Name subjectName = new X500Name("CN=lemon-from-20220801");
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
        // entity cert
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
        certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        certBuilder.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(new KeyPurposeId[]{KeyPurposeId.id_kp_serverAuth}));

        SubjectPublicKeyInfo issuerPublicKeyInfo = SubjectPublicKeyInfo.getInstance(jetbrainsCaCert.getPublicKey().getEncoded());
        AuthorityKeyIdentifier authorityKeyIdentifier = extUtils.createAuthorityKeyIdentifier(issuerPublicKeyInfo);
        // 授权密钥标识
        certBuilder.addExtension(Extension.authorityKeyIdentifier, false, authorityKeyIdentifier);

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(rootCaCert.getPublicKey().getEncoded());
        SubjectKeyIdentifier subjectKeyIdentifier = extUtils.createSubjectKeyIdentifier(subjectPublicKeyInfo);
        certBuilder.addExtension(Extension.subjectKeyIdentifier, false, subjectKeyIdentifier);

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(issuerPrivateKey);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(signer));
        // 将证书写入 PEM 文件
        X509CertUtils.writeCertificate(cert, certPath);
    }

}