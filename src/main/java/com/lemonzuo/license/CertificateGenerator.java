package com.lemonzuo.license;

/**
 * @author LemonZuo
 * @create 2024-02-20 22:03
 */

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
        // Yesterday
        Date notBefore = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        // 10 years later
        Date notAfter = new Date(System.currentTimeMillis() + 3650L * 24 * 60 * 60 * 1000);

        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(issuerName, serialNumber, notBefore, notAfter, subjectName, subPubKeyInfo);

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(privateKey);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(signer));
        // 将私钥写入 PEM 文件
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter("/opt/data/idea_data/jetbrains-license/src/main/resources/cert/ca.key"))) {
            pemWriter.writeObject(privateKey);
        }

        // 将证书写入 PEM 文件
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter("/opt/data/idea_data/jetbrains-license/src/main/resources/cert/ca.crt"))) {
            pemWriter.writeObject(cert);
        }

    }

    public static void main(String[] args) {
        try {
            genCrtKey();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
