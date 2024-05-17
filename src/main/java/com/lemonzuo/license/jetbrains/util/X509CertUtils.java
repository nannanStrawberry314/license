package com.lemonzuo.license.jetbrains.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Set;

/**
 * @Author: Crazer
 * @Date: 2022/8/11 14:40
 * @version: 1.0.0
 * @Description: X509证书工具类
 */
public class X509CertUtils {
    /**
     * 生成X509证书
     *
     * @param certBytes
     * @return
     * @throws Exception
     */
    public static X509Certificate createCertificate(byte[] certBytes) throws Exception {
        CertificateFactory x509factory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) x509factory.generateCertificate(new ByteArrayInputStream(certBytes));
        return cert;
    }

    /**
     * 将对象转换为PEM格式字符串
     *
     * @param cert 证书
     * @return
     * @throws IOException
     */
    public static String x509CertificateToPem(final X509Certificate cert) throws IOException {
        final StringWriter writer = new StringWriter();
        final JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
        pemWriter.writeObject(cert);
        pemWriter.flush();
        pemWriter.close();
        return writer.toString();
    }

    /**
     * 将证书写入文件
     *
     * @param cert     证书
     * @param certPath 证书路径
     * @throws Exception
     */
    public static void writeCertificate(X509Certificate cert, String certPath) throws Exception {
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(certPath))) {
            pemWriter.writeObject(cert);
        }
    }

    /**
     * 复制模板证书中的拓展信息到新证书中
     *
     * @param templateCertificate 模板证书
     * @param certBuilder         新证书构建器
     * @throws Exception
     */
    public static void copyExtensions(X509Certificate templateCertificate, X509v3CertificateBuilder certBuilder) throws Exception {
        // 获取模板证书中的拓展信息
        Set<String> criticalExtensions = templateCertificate.getCriticalExtensionOIDs();
        if (criticalExtensions != null) {
            for (String oid : criticalExtensions) {
                byte[] extensionValue = templateCertificate.getExtensionValue(oid);
                if (extensionValue != null) {
                    certBuilder.addExtension(new ASN1ObjectIdentifier(oid), true, ASN1OctetString.getInstance(extensionValue).getOctets());
                }
            }
        }

        Set<String> nonCriticalExtensions = templateCertificate.getNonCriticalExtensionOIDs();
        if (nonCriticalExtensions != null) {
            for (String oid : nonCriticalExtensions) {
                byte[] extensionValue = templateCertificate.getExtensionValue(oid);
                if (extensionValue != null) {
                    certBuilder.addExtension(new ASN1ObjectIdentifier(oid), false, ASN1OctetString.getInstance(extensionValue).getOctets());
                }
            }
        }
    }
}