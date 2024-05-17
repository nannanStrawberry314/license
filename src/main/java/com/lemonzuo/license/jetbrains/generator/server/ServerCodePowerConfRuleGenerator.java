package com.lemonzuo.license.jetbrains.generator.server;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.PemUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.lemonzuo.license.jetbrains.constant.CertConstant;
import com.lemonzuo.license.jetbrains.util.X509CertUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.*;
import org.bouncycastle.util.encoders.Hex;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

/**
 * 生成规则
 * @author LemonZuo
 * @create 2024-05-17 11:13
 */
@Slf4j
public class ServerCodePowerConfRuleGenerator {

    // public static void main(String[] args) throws Exception {
    //     // 使用JetProfile CA颁发的子证书生成License 生成的规则
    //     standardGenerateRules();
    // }



    /**
     * 使用标准生成的规则
     *
     * @throws Exception
     */
    public static String standardGenerateRules() throws Exception {
        // 读取CA证书
        byte[] codeCertBytes = PemUtil.readPem(new FileInputStream(CertConstant.CODE_CERT_PATH));
        X509Certificate certificate = X509CertUtils.createCertificate(codeCertBytes);
        // 读取JB的CA证书
        byte[] jbCACertBytes = PemUtil.readPem(new FileInputStream(CertConstant.JETBRAINS_CODE_CA_PATH));
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

        String rules = String.format("[Result]\n; Lemon active by code \nEQUAL,%s,%s,%s->%s", x, y, z, r);
        log.info("================== PowerConfRule Result ==================");
        log.info("\n{}", rules);
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
        return ("01" + "ff".repeat(Math.max(0, count)) + "00" + target).toUpperCase();
    }
}