package com.lemonzuo.license.jetbrains.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.PemUtil;
import com.lemonzuo.license.jetbrains.constant.CertConstant;
import lombok.SneakyThrows;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;

/**
 * @Author: Crazer
 * @Date: 2022/9/19 15:30
 * @version: 1.0.0
 * @Description: TODO
 */
public class LicenseServerUtils {
    public static final String SERVER_UID = "lemon";
    public static final String LEASE_CONTENT = "4102415999000:" + SERVER_UID;

    public static String getJetBrainsGenCert() {
        return getCertBase64(CertConstant.CODE_CERT_PATH);
    }

    public static PrivateKey getJetBrainsPrivateKey() {
        return getPrivateKey(CertConstant.PRIVATE_KEY_2048_PATH);
    }

    public static String getMyLicenseGenCert() {
        return getCertBase64(CertConstant.SERVER_CERT_PATH);
    }

    public static String getMyInLicenseGenCert() {
        return getCertBase64(CertConstant.SERVER_INTERMEDIATE_CERT_PATH);
    }

    public static PrivateKey getMyLicensePrivateKey() {
        return getPrivateKey(CertConstant.PRIVATE_KEY_2048_PATH);
    }


    /**
     * 获取证书Base64加密后结果
     *
     * @param path
     * @return
     */
    @SneakyThrows
    public static String getCertBase64(String path) {
        InputStream is = new FileInputStream(path);
        byte[] certBytes = PemUtil.readPem(is);
        return Base64.encode(certBytes);
    }

    /**
     * 获取私钥
     *
     * @param path
     * @return
     */
    @SneakyThrows
    public static PrivateKey getPrivateKey(String path) {
        InputStream is = new FileInputStream(path);
        PrivateKey privateKey = PemUtil.readPemPrivateKey(is);
        return privateKey;
    }

    /**
     * @param machineId 设备Id
     * @return 字符串类型：ConfirmationStamp
     * @throws Exception
     */
    public static String getConfirmationStamp(String machineId) {
        long timeStamp = System.currentTimeMillis();
        String signature = signContent(timeStamp + ":" + machineId, getMyLicensePrivateKey(), "SHA1withRSA");
        return timeStamp + ":" + machineId + ":" + "SHA1withRSA" + ":" + signature + ":" + getMyLicenseGenCert() + ":" + getMyInLicenseGenCert();
    }

    /**
     * 签名content
     *
     * @param content       内容
     * @param privateKey    私钥
     * @param signAlgorithm 算法
     * @return 字符串类型：signContent
     * @throws Exception
     */
    @SneakyThrows
    public static String signContent(String content, PrivateKey privateKey, String signAlgorithm) {
        Signature signature = Signature.getInstance(signAlgorithm);
        signature.initSign(privateKey);
        signature.update(content.getBytes(StandardCharsets.UTF_8));
        return Base64.encode(signature.sign());
    }

    /**
     * 获取签名
     *
     * @return 字符串类型：LeaseSignature
     * @throws Exception
     */
    public static String getLeaseSignature() {
        String signature = signContent(LEASE_CONTENT, getJetBrainsPrivateKey(), "SHA512withRSA");
        return "SHA512withRSA-" + signature + "-" + getJetBrainsGenCert();
    }

    /**
     * 获取并签名xml
     *
     * @param xml
     * @return
     */
    @SneakyThrows
    public static String getSignXml(Object xml) {
        JAXBContext jaxbContext = JAXBContext.newInstance(xml.getClass());

        // 保存文件，将Java对象转化为xml文件
        Marshaller marshaller = jaxbContext.createMarshaller();
        // marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // 设置 Marshaller 属性，去掉 xml 标识
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

        // 将Java对象序列化为XML字符串
        StringWriter writer = new StringWriter();
        marshaller.marshal(xml, writer);
        return "<!-- SHA1withRSA-" + signContent(writer.toString(), getMyLicensePrivateKey(), "SHA1withRSA") + "-" + getMyLicenseGenCert() + "-" + getMyInLicenseGenCert() + " -->\n" + writer.toString();
    }
}