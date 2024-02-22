package com.lemonzuo.license.generator;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.lemonzuo.license.constant.Constant;
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

    public static void generate() throws Exception {
        String keyPath = String.format("%s/ca.key", Constant.PATH);
        String crtPath = String.format("%s/ca.crt", Constant.PATH);
        String keyContent = null;
        if (FileUtil.exist(keyPath)) {
            keyContent = FileUtil.readUtf8String(keyPath);
        }
        String crtContent = null;
        if (FileUtil.exist(crtPath)) {
            crtContent = FileUtil.readUtf8String(crtPath);
        }
        if (StrUtil.isAllNotEmpty(keyContent, crtContent)) {
            log.warn("证书和私钥已存在，无需重新生成");
            return;
        }

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
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(keyPath))) {
            pemWriter.writeObject(privateKey);
        }

        // 将证书写入 PEM 文件
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(crtPath))) {
            pemWriter.writeObject(cert);
        }
    }
}
