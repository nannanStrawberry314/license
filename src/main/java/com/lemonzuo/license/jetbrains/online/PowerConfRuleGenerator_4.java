package com.lemonzuo.license.jetbrains.online;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.PemUtil;
import cn.hutool.crypto.digest.DigestUtil;
import org.bouncycastle.asn1.*;
import org.bouncycastle.util.encoders.Hex;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

/**
 * @Author: Crazer
 * @Date: 2024/2/28 16:48
 * @version: 1.0.0
 * @Description: 生成规则
 */
public class PowerConfRuleGenerator_4 {
    private static final String JB_CODE_CA_CERT_PATH = "src/main/resources/cert/jb/JBCodeCACert.pem";
    // 第一种方式
    private static final String CODE_CA_CERT0_PATH = "src/main/resources/cert/CodeCA0.pem";
    // 第二种方式
    private static final String CODE_CERT_PATH = "src/main/resources/cert/CodeCert.pem";

    public static void main(String[] args) throws Exception {
        // 方式一：使用4096 位JetProfile CA证书生成License 生成的规则
        // unstandardGenerateRules(CODE_CA_CERT0_PATH, JB_CODE_CA_CERT_PATH);
        // System.out.println("=====================");
        // 方式二：使用JetProfile CA颁发的子证书生成License 生成的规则
        standardGenerateRules(CODE_CERT_PATH, JB_CODE_CA_CERT_PATH);
    }

    /**
     * 使用非标准生成的规则
     *
     * @return
     * @throws Exception
     */
    private static String unstandardGenerateRules(String codeCertPath, String jbCodeCAPath) throws Exception {
        // 读取CA证书
        byte[] codeCertBytes = PemUtil.readPem(new FileInputStream(codeCertPath));
        X509Certificate certificate = X509CertUtils.createCertificate(codeCertBytes);
        // 读取JB的CA证书
        byte[] jbCACertBytes = PemUtil.readPem(new FileInputStream(jbCodeCAPath));
        X509Certificate jbCACert = X509CertUtils.createCertificate(jbCACertBytes);
        RSAPublicKey jbPublicKey = (RSAPublicKey) jbCACert.getPublicKey();

        // x：证书的签名密文
        BigInteger x = new BigInteger(1, certificate.getSignature());
        // y：证书指数 固定65537
        BigInteger y = new BigInteger(String.valueOf(jbPublicKey.getPublicExponent()));
        // z：内置根证书的公钥
        BigInteger z = jbPublicKey.getModulus();
        // r：fake result
        RSAPublicKey certPublicKey = (RSAPublicKey) certificate.getPublicKey();
        BigInteger r = x.modPow(certPublicKey.getPublicExponent(), certPublicKey.getModulus());

        String rules = String.format("[Result]\n; Crazer\nEQUAL,%s,%s,%s->%s", x, y, z, r);
        System.out.println(rules);
        return rules;
    }


    /**
     * 使用标准生成的规则
     *
     * @return
     * @throws Exception
     */
    private static String standardGenerateRules(String codeCaPath, String JBCodeCAPath) throws Exception {
        // 读取CA证书
        byte[] codeCertBytes = PemUtil.readPem(new FileInputStream(codeCaPath));
        X509Certificate certificate = X509CertUtils.createCertificate(codeCertBytes);
        // 读取JB的CA证书
        byte[] jbCACertBytes = PemUtil.readPem(new FileInputStream(JBCodeCAPath));
        X509Certificate jbCACert = X509CertUtils.createCertificate(jbCACertBytes);
        RSAPublicKey jbPublicKey = (RSAPublicKey) jbCACert.getPublicKey();

        // x：证书的签名密文
        BigInteger x = new BigInteger(1, certificate.getSignature());
        // y：证书指数 固定65537
        BigInteger y = new BigInteger(String.valueOf(jbPublicKey.getPublicExponent()));
        // z：内置根证书的公钥
        BigInteger z = jbPublicKey.getModulus();
        // r：fake result

        byte[] tbsCertificate = certificate.getTBSCertificate();
        // 1、证书sha256摘要结果
        byte[] bytes = DigestUtil.sha256(tbsCertificate);
        String sha256Str = HexUtil.encodeHexStr(bytes);
        // 2、计算的结果转换为ASN1格式数据
        String transit = convertDataToASN1Format(sha256Str);
        // 3、ASN1格式数据再进行填充
        String fillingStr = filling512(transit);
        // 4、填充后的数据转换为BigInteger数据，BigInteger输出的结果就是规则中替换的结果。
        BigInteger r = new BigInteger(HexUtil.decodeHex(fillingStr));

        String rules = String.format("[Result]\n; Crazer\nEQUAL,%s,%s,%s->%s", x, y, z, r);
        System.out.println(rules);
        return rules;
    }

    /**
     * 证书sha256摘要结果转换为ASN1格式数据
     *
     * @param sha256Data 证书sha256摘要结果
     * @return
     */
    public static String convertDataToASN1Format(String sha256Data) throws Exception {
        // 构建内层 SEQUENCE
        // sha-256
        ASN1ObjectIdentifier algorithmOid = new ASN1ObjectIdentifier("2.16.840.1.101.3.4.2.1");
        ASN1Encodable[] innerSequenceElements = {algorithmOid, DERNull.INSTANCE};
        DERSequence innerSequence = new DERSequence(innerSequenceElements);

        // 构建外层 SEQUENCE
        byte[] octetStringBytes = Hex.decode(sha256Data);
        ASN1Encodable octetString = new DEROctetString(octetStringBytes);
        ASN1Encodable[] outerSequenceElements = {innerSequence, octetString};
        DERSequence outerSequence = new DERSequence(outerSequenceElements);

        // 将ASN.1结构编码为DER格式
        byte[] encodedData = outerSequence.getEncoded();

        // 将字节数组转换为十六进制字符串
        return Hex.toHexString(encodedData);
    }

    private static String filling512(String target) {
        return filling(target, 512);
    }

    private static String filling256(String target) {
        return filling(target, 256);
    }

    private static String filling(String target, int length) {
        int count = length - target.length() / 2 - 3;
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            strBuilder.append("ff");
        }
        return ("01" + strBuilder + "00" + target).toUpperCase();
    }
}