package com.lemonzuo.license.jetbrains.online;

import cn.hutool.crypto.PemUtil;
import cn.hutool.crypto.SecureUtil;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @Author: Crazer
 * @Date: 2022/8/11 13:30
 * @version: 1.0.0
 * @Description: 生成4096、2048位 RSA密钥对
 */
public class RSAGenerator_1 {
    public static void main(String[] args) throws Exception {
        generateKeyPair(2048);
        generateKeyPair(4096);
        System.out.println("生成RSA密钥对完成！！！");
    }

    /**
     * 生成密钥对
     *
     * @param size 密钥长度
     * @throws Exception
     */
    public static void generateKeyPair(int size) throws Exception {
        KeyPair pair = SecureUtil.generateKeyPair("RSA", size);
        PublicKey publicKey = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();

        // 保存 公钥和私钥 到指定文件
        String publicKeyFile = "/opt/data/idea_data/license/src/main/resources/cert/publicKey" + size + ".pem";
        String privateKeyFile = "/opt/data/idea_data/license/src/main/resources/cert/privateKey" + size + ".pem";

        PemUtil.writePemObject("PUBLIC KEY", publicKey.getEncoded(), new FileOutputStream(publicKeyFile));
        PemUtil.writePemObject("RSA PRIVATE KEY", privateKey.getEncoded(), new FileOutputStream(privateKeyFile));
    }
}