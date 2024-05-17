package com.lemonzuo.license.jetbrains.generator.server;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.PemUtil;
import cn.hutool.crypto.SecureUtil;
import com.lemonzuo.license.jetbrains.constant.Constant;
import lombok.SneakyThrows;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * RSA密钥对生成器
 *
 * @author LemonZuo
 * @create 2024-05-17 00:12
 * step: 1. 生成密钥对
 */
public class ServerRsaGenerator {

    @SneakyThrows
    public static void main(String[] args) {
        generateKeyPair(2048);
        generateKeyPair(4096);
    }

    /**
     * 生成密钥对
     *
     * @param size 密钥长度
     * @throws Exception 异常
     */
    public static void generateKeyPair(int size) throws Exception {
        KeyPair pair = SecureUtil.generateKeyPair("RSA", size);
        PublicKey publicKey = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();

        // 保存 公钥和私钥 到指定文件
        String publicKeyFile = StrUtil.format("{}/publicKey{}.pem", Constant.PATH, size);
        String privateKeyFile = StrUtil.format("{}/privateKey{}.pem", Constant.PATH, size);

        PemUtil.writePemObject("PUBLIC KEY", publicKey.getEncoded(), new FileOutputStream(publicKeyFile));
        PemUtil.writePemObject("RSA PRIVATE KEY", privateKey.getEncoded(), new FileOutputStream(privateKeyFile));
    }
}