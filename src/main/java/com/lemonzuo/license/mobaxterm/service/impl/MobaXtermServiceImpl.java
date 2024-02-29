package com.lemonzuo.license.mobaxterm.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.lemonzuo.license.mobaxterm.generator.LicenseGenerator;
import com.lemonzuo.license.mobaxterm.service.MobaXtermService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author LemonZuo
 * @create 2024-02-29 20:37
 */
@Slf4j
@Service
public class MobaXtermServiceImpl implements MobaXtermService {
    @Override
    public void generate(String name, String version, Integer count, HttpServletResponse response) {
        try {
            // 查分大小版本号
            String[] versionArr = version.split("\\.");
            if (versionArr.length != 2) {
                throw new RuntimeException("版本号格式错误");
            }
            if (!NumberUtil.isNumber(versionArr[0]) || !NumberUtil.isNumber(versionArr[1])) {
                throw new RuntimeException("版本号格式错误");
            }
            // 提取大小版本号，调用LicenseGenerator生成license
            int majorVersion = Integer.parseInt(versionArr[0]);
            int minorVersion = Integer.parseInt(versionArr[1]);
            String license = LicenseGenerator.generateLicense(name, majorVersion, minorVersion);
            // 设置文件名
            String fileName = "Custom.mxtpro";

            // 输出到响应流里面，文件下载
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", String.format("attachment;filename=%s", fileName));
            // 使用HttpServletResponse的输出流作为ZipOutputStream的输出目标
            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
                // 创建ZIP条目
                ZipEntry zipEntry = new ZipEntry("Pro.key");
                zipOut.putNextEntry(zipEntry);

                // 假设`getEncodedLicenseString()`方法返回编码后的许可证字符串
                // String encodedLicenseString = getEncodedLicenseString();
                zipOut.write(license.getBytes(StandardCharsets.UTF_8));

                // 完成条目添加
                zipOut.closeEntry();
            }
        } catch (Exception e) {
            log.error("生成license失败", e);
            throw new RuntimeException("生成license失败");
        }
    }
}
