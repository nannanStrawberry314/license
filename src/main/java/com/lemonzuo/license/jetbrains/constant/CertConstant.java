package com.lemonzuo.license.jetbrains.constant;

/**
 * @author LemonZuo
 * @create 2024-05-17 15:37
 */
public class CertConstant {
    /**
     * 2048位 RSA 密钥对
     */
    public static final String PUBLIC_KEY_2048_PATH = Constant.PATH + "/publicKey2048.pem";
    public static final String PRIVATE_KEY_2048_PATH = Constant.PATH + "/privateKey2048.pem";
    /**
     * 4096位 RSA 密钥对
     */
    public static final String PUBLIC_KEY_4096_PATH = Constant.PATH + "/publicKey4096.pem";
    public static final String PRIVATE_KEY_4096_PATH = Constant.PATH + "/privateKey4096.pem";
    /**
     * JetBrains CA证书
     */
    public static final String JETBRAINS_CODE_CA_PATH = Constant.PATH + "/jetbrainsCodeCACert.pem";
    public static final String JETBRAINS_SERVER_CA_PATH = Constant.PATH + "/jetbrainsServerCACert.pem";
    /**
     * Code证书
     */
    public static final String CODE_CA_PATH = Constant.PATH + "/codeCA.pem";
    public static final String CODE_CERT_PATH = Constant.PATH + "/codeCert.pem";
    /**
     * Server证书
     */
    public static final String SERVER_CA_PATH = Constant.PATH + "/serverCA.pem";
    public static final String SERVER_INTERMEDIATE_CERT_PATH = Constant.PATH + "/serverIntermediateCert.pem";
    public static final String SERVER_CERT_PATH = Constant.PATH + "/serverCert.pem";

}
