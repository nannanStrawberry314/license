package com.lemonzuo.license.jetbrains.generator.server;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.PemUtil;
import com.lemonzuo.license.jetbrains.constant.CertConstant;
import com.lemonzuo.license.jetbrains.util.X509CertUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
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
 * 生成服务器证书
 *
 * @author LemonZuo
 * @create 2024-05-17 00:12
 * step: 5.生成服务器证书
 */
@Slf4j
public class ServerCertificateGenerator {

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

        log.info("========== 生成License Servers CA证书 ==========");
        byte[] codeCertBytes = PemUtil.readPem(new FileInputStream(CertConstant.JETBRAINS_SERVER_CA_PATH));
        X509Certificate templateCert = X509CertUtils.createCertificate(codeCertBytes);

        String subName = "lemon.lsrv.jetbrains.com";

        // 1、生成License Servers CA证书
        log.info("========== 1、生成License Servers CA证书 ==========");
        generateRootCertificate(templateCert, publicKey4096, privateKey4096, CertConstant.SERVER_CA_PATH);

        // 2、生成服务器中间证书
        log.info("========== 2、生成服务器中间证书 ==========");
        X509Certificate myCACert = X509CertUtils.createCertificate(PemUtil.readPem(new FileInputStream(CertConstant.SERVER_CA_PATH)));
        String subNameIntermediate = "lsrv-prod-till-20280326-intermediate";
        issueChildCertificate(subNameIntermediate, myCACert, templateCert, publicKey2048, privateKey4096, CertConstant.SERVER_INTERMEDIATE_CERT_PATH, true);

        // 3、生成服务器证书
        log.info("========== 3、生成服务器证书 ==========");
        X509Certificate myIntermediateCert = X509CertUtils.createCertificate(PemUtil.readPem(new FileInputStream(CertConstant.SERVER_INTERMEDIATE_CERT_PATH)));
        String subNameEntity = "lemon.lsrv.jetbrains.com";
        issueChildCertificate(subNameEntity, myIntermediateCert, myCACert, publicKey2048, privateKey2048, CertConstant.SERVER_CERT_PATH, false);
    }

    /**
     * 生成标准License Servers CA证书
     *
     * @param templateCert JB CA模板证书
     * @param publicKey    4096位 公钥
     * @param privateKey   4096位 私钥
     * @param certPath     证书路径
     * @throws Exception 异常
     */
    public static void generateRootCertificate(X509Certificate templateCert, PublicKey publicKey, PrivateKey privateKey, String certPath) throws Exception {
        X500Name issuerName = new X500Name(templateCert.getIssuerX500Principal().getName());
        X500Name subjectName = new X500Name(templateCert.getSubjectX500Principal().getName());
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = templateCert.getNotBefore();
        Date notAfter = templateCert.getNotAfter();

        // 创建 SubjectPublicKeyInfo 对象
        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

        // 使用 JcaX509v3CertificateBuilder 构建 X509v3CertificateBuilder 对象
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(issuerName, serialNumber, notBefore, notAfter, subjectName, subPubKeyInfo);

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
     * @param subName          子证书名称
     * @param issuerCert       签发者证书
     * @param templateCert     模板证书
     * @param publicKey        公钥
     * @param issuerPrivateKey 签发者私钥
     * @param certPath         证书路径
     * @param isIntermediate   是否是中间证书
     * @throws Exception 异常
     */
    public static void issueChildCertificate(String subName, X509Certificate issuerCert, X509Certificate templateCert, PublicKey publicKey, PrivateKey issuerPrivateKey, String certPath, boolean isIntermediate) throws Exception {
        // 根证书 签发者
        X500Name issuerName = new X500Name(issuerCert.getSubjectX500Principal().getName());
        subName = "CN=" + subName;
        X500Name subjectName = new X500Name(subName);

        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        DateTime today = DateUtil.beginOfDay(DateUtil.date());
        Date notBefore = DateUtil.offset(today, DateField.DAY_OF_MONTH, -1);
        Date notAfter = DateUtil.offset(today, DateField.YEAR, 30);

        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(issuerName, serialNumber, notBefore, notAfter, subjectName, subPubKeyInfo);
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

        if (isIntermediate) {
            // entity cert
            certBuilder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));
            certBuilder.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));
            // 创建授权密钥标识符
            GeneralNamesBuilder generalNamesBuilder = new GeneralNamesBuilder();
            generalNamesBuilder.addName(new GeneralName(issuerName));
            GeneralNames generalNames = generalNamesBuilder.build();
            ASN1EncodableVector authorityKeyIdentifierVector = new ASN1EncodableVector();
            authorityKeyIdentifierVector.add(new DERTaggedObject(false, 0, extUtils.createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(templateCert.getPublicKey().getEncoded()))));
            // a1
            authorityKeyIdentifierVector.add(new DERTaggedObject(false, 1, new DERTaggedObject(false, 0, generalNames)));
            authorityKeyIdentifierVector.add(new DERTaggedObject(false, 2, new ASN1Integer(templateCert.getSerialNumber())));
            DERSequence authorityKeyIdentifierSequence = new DERSequence(authorityKeyIdentifierVector);
            certBuilder.addExtension(Extension.authorityKeyIdentifier, false, authorityKeyIdentifierSequence);
            certBuilder.addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(issuerCert.getPublicKey().getEncoded())));
        } else {
            certBuilder.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(new KeyPurposeId[]{KeyPurposeId.id_kp_serverAuth, KeyPurposeId.id_kp_clientAuth}));
            GeneralNamesBuilder generalNamesBuilder = new GeneralNamesBuilder();
            GeneralName dnsName = new GeneralName(GeneralName.dNSName, subName);
            generalNamesBuilder.addName(dnsName);
            GeneralNames generalNames = generalNamesBuilder.build();
            certBuilder.addExtension(Extension.subjectAlternativeName, false, generalNames);
            certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment | KeyUsage.keyAgreement));
            // 设置授权密钥标识符为中间证书的主题密钥标识符
            // 授权密钥标识
            certBuilder.addExtension(Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(templateCert.getPublicKey().getEncoded())));
            // 设置实体证书的主题密钥标识符为实体证书的公钥的密钥标识符
            certBuilder.addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded())));
        }
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(issuerPrivateKey);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(signer));
        X509CertUtils.writeCertificate(cert, certPath);
    }
}