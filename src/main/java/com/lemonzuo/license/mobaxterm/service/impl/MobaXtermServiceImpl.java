package com.lemonzuo.license.mobaxterm.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.lemonzuo.license.mobaxterm.generator.LicenseGenerator;
import com.lemonzuo.license.mobaxterm.service.MobaXtermService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
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
    public void generate(String name, String version, Integer count, HttpServletResponse response) throws Exception {

        String content = LicenseGenerator.generate(name, version, count);

        // 输出到响应流里面，文件下载
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=Custom.mxtpro");
        // 使用HttpServletResponse的输出流作为ZipOutputStream的输出目标
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            // 创建ZIP条目
            ZipEntry zipEntry = new ZipEntry("Pro.key");
            // put crc32
            CRC32 crc32 = new CRC32();
            crc32.update(content.getBytes());
            zipEntry.setCrc(crc32.getValue());
            zipEntry.setSize(content.length());
            zipEntry.setMethod(ZipEntry.STORED);

            zipOut.putNextEntry(zipEntry);
            zipOut.write(content.getBytes());

            // 完成条目添加
            zipOut.closeEntry();
        }
    }
}
