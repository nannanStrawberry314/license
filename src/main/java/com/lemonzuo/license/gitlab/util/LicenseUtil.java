package com.lemonzuo.license.gitlab.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemonzuo.license.gitlab.entity.License;
import com.lemonzuo.license.gitlab.entity.LicenseInfo;
import com.lemonzuo.license.gitlab.entity.Restriction;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author LemonZuo
 * @create 2023-11-03 10:35
 */
@Slf4j
@Component
public class LicenseUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static String PRIVATE_KEY = "";
    private static String PUBLIC_KEY = "";


    @PostConstruct
    private void loadKey() {
        PRIVATE_KEY = ResourceUtil.readUtf8Str("files/.license_decryption_key.pri")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\r", "")
                .replaceAll("\\n", "");

        PUBLIC_KEY = ResourceUtil.readUtf8Str("files/.license_encryption_key.pub")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\r", "")
                .replaceAll("\\n", "");

        log.info("LicenseUtil loadKey PRIVATE_KEY:{}", PRIVATE_KEY);
        log.info("LicenseUtil loadKey PRIVATE_KEY:{}", PUBLIC_KEY);
    }

    /**
     * 创建证书JSON信息
     *
     * @param licenseInfo 基础信息
     * @return
     * @throws Exception
     */
    public static String createLicenseJson(LicenseInfo licenseInfo) throws Exception {
        License license = new License();

        Restriction restriction = new Restriction();
        restriction.setActiveUserCount(10000);
        restriction.setPlan("ultimate");

        license.setVersion(1);
        license.setLicense(licenseInfo);
        license.setRestrictions(restriction);

        license.setStartsAt(new Date());
        license.setExpiresAt(DateUtil.parse("2100-12-31 23:59:59", DatePattern.NORM_DATETIME_PATTERN));
        license.setNotifyAdminsAt(DateUtil.parse("2100-12-31 23:59:59", DatePattern.NORM_DATETIME_PATTERN));
        license.setNotifyUsersAt(DateUtil.parse("2100-12-31 23:59:59", DatePattern.NORM_DATETIME_PATTERN));
        license.setBlockChangesAt(DateUtil.parse("2100-12-31 23:59:59", DatePattern.NORM_DATETIME_PATTERN));

        license.setCloudLicensingEnabled(false);
        license.setOfflineCloudLicensingEnabled(false);
        license.setAutoRenewEnabled(false);
        license.setSeatReconciliationEnabled(false);
        license.setOperationalMetricsEnabled(false);
        license.setGeneratedFromCustomersDot(false);

        String jsonData = OBJECT_MAPPER.writeValueAsString(license);
        log.info("jsonData:{}", jsonData);
        return jsonData;
    }

    /**
     * 随机生成 IV
     *
     * @return
     */
    private static byte[] generateRandomIv() {
        // AES 使用的 IV 长度通常为 16 个字节
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }

    /**
     * 加密License
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String encryptLicense(String data) throws Exception {
        // 生成AES Key 和 iv
        byte[] aesKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        byte[] aesIv = generateRandomIv();

        // 创建 AES 加密器，指定为 CBC 模式和 PKCS7Padding 填充（等同于 PKCS5Padding）
        SymmetricCrypto aes = new AES("CBC", "PKCS7Padding", aesKey, aesIv);
        // 先使用AES对需要加密的数据进行加密
        byte[] encryptedData = aes.encrypt(data);

        RSA rsa = new RSA(PRIVATE_KEY, null);
        // 然后使用RSA采用私钥加密的方式对AES key 进行加密
        byte[] encryptedKey = rsa.encrypt(aesKey, KeyType.PrivateKey);
        // 组装最后的数据结构
        Map<String, String> map = new HashMap<>();
        // Base64编码过的原始加密数据
        map.put("data", Base64.encode(encryptedData));
        // Base64编码过的 RSA加密key
        map.put("key", Base64.encode(encryptedKey));
        // Base64编码过的 AES IV
        map.put("iv", Base64.encode(aesIv));
        // 转成成json数据
        String jsonData = OBJECT_MAPPER.writeValueAsString(map);
        // 最后使用 Base64 编码 json 得到最终的数据
        String encode = Base64.encode(jsonData);
        log.info("encryptLicense encode:{}", encode);
        return encode;
    }

    /**
     * 解密License
     *
     * @param encryptStr
     * @throws Exception
     */
    public static void decryptLicense(String encryptStr) throws Exception {
        // 对整体加密数据进行Base64解码 得到json数据
        String jsonData = Base64.decodeStr(encryptStr);
        Map<String, String> map = OBJECT_MAPPER.readValue(jsonData, new TypeReference<>() {
        });
        // 使用Base64解码加密的证书信息
        byte[] data = Base64.decode(map.get("data"));
        // 使用Base64解码 经过RSA加密的AES key
        byte[] key = Base64.decode(map.get("key"));
        // 使用Base64解码 AES IV
        byte[] aesIv = Base64.decode(map.get("iv"));

        RSA rsa = new RSA(null, PUBLIC_KEY);
        // 使用RSA 采用公钥解密 AES key
        byte[] aesKey = rsa.decrypt(key, KeyType.PublicKey);

        SymmetricCrypto aes = new AES("CBC", "PKCS7Padding", aesKey, aesIv);
        // 使用AES 解密加密的数据，得到最原始的数据
        String result = aes.decryptStr(data);
        log.info("result:{}", result);
    }

    private static void createLicense(LicenseInfo licenseInfo, HttpServletResponse response) {
        try {
            // 创建证书所有的json数据
            String licenseJson = createLicenseJson(licenseInfo);
            // 对json数据加密
            String encryptedLicense = encryptLicense(licenseJson);
            // 导出证书压缩包
            exportZipStream(encryptedLicense, response);
        } catch (Exception e) {
            log.error("生成证书失败", e);
        }
    }

    public static void exportZipStream(String encryptedLicense, HttpServletResponse response) throws IOException {
        // 定义ZIP输出流，这里我们用ByteArrayOutputStream来捕获输出
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream);

        // 创建ZIP条目，这个名称可以包含路径信息
        ZipEntry pub = new ZipEntry("license/.license_encryption_key.pub");
        zos.putNextEntry(pub);
        // 写入内容到ZIP条目，这里可以是文件内容的字节流
        zos.write(ResourceUtil.readBytes("files/.license_encryption_key.pub"));
        zos.closeEntry();

        ZipEntry license = new ZipEntry("license/license.gitlab-license");
        zos.putNextEntry(license);
        zos.write(encryptedLicense.getBytes());
        zos.closeEntry();

        // 完成所有条目的添加
        zos.finish();

        // 设置响应头
        response.setHeader("Content-Disposition", "attachment; filename=license.zip");
        response.setContentType("application/zip");

        // 获取输出流
        ServletOutputStream outputStream = response.getOutputStream();

        // 将ZIP数据写入输出流
        byteArrayOutputStream.writeTo(outputStream);
        outputStream.flush();

        // 关闭ZIP输出流和捕获流
        IoUtil.close(zos);
        IoUtil.close(byteArrayOutputStream);
    }

    public static void generate(LicenseInfo licenseInfo, HttpServletResponse response) {
        createLicense(licenseInfo, response);
    }
}
