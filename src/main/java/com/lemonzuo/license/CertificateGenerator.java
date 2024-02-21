package com.lemonzuo.license;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @author LemonZuo
 * @create 2024-02-20 22:03
 */
@Slf4j
public class CertificateGenerator {

    public static void genCrtKey() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(4096, new SecureRandom());
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        X500Name issuerName = new X500Name("CN=JetProfile CA");
        X500Name subjectName = new X500Name("CN=Novice-from-2024-01-19");
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        DateTime today = DateUtil.beginOfDay(DateUtil.date());
        // Yesterday
        Date notBefore = DateUtil.offset(today, DateField.DAY_OF_MONTH, -1);
        // 10 years later
        Date notAfter = DateUtil.offset(today, DateField.YEAR, 10);

        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(issuerName, serialNumber, notBefore, notAfter, subjectName, subPubKeyInfo);

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(privateKey);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(signer));
        // 将私钥写入 PEM 文件
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(String.format("%s/ca.key", Constant.PATH)))) {
            pemWriter.writeObject(privateKey);
        }

        // 将证书写入 PEM 文件
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(String.format("%s/ca.crt", Constant.PATH)))) {
            pemWriter.writeObject(cert);
        }

    }

    public static void main(String[] args) {
        try {
            genCrtKey();
        } catch (Exception e) {
            log.error("生成证书和私钥失败", e);
        }
    }
}
