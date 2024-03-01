package com.lemonzuo.license.mobaxterm.generator;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.lemonzuo.license.mobaxterm.dto.License;
import com.lemonzuo.license.mobaxterm.enums.LicenseEnum;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author wanna
 * @since 2019-01-03
 */
@Slf4j
public class LicenseGenerator {

    /**
     * 生成license
     */
    public static String generate(String userName, String version, int count) {
        if (StrUtil.isEmpty(userName) || StrUtil.isEmpty(version) || count <= 0) {
            throw new RuntimeException("参数错误");
        }

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

        License license = new License()
                .setLicenseType(LicenseEnum.Professional)
                .setUserName(userName)
                .setMajorVersion(majorVersion)
                .setMinorVersion(minorVersion)
                .setCount(count)
                .setOpenGames(true)
                .setOpenPlugins(true);

        String licenseKey = license.getLicenseKey();
        String encryptBytes = encryptBytes(0x787, licenseKey.getBytes(StandardCharsets.UTF_8));
        return VariantBase64.variantBase64Encode(encryptBytes.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 加密串
     *
     * @param key   key
     * @param bytes bytes
     * @return string
     */
    public static String encryptBytes(int key, byte[] bytes) {
        int length = bytes.length;
        byte[] newByte = new byte[length];
        for (int i = 0; i < length; i++) {
            int integer = bytes[i] ^ ((key >> 8) & 0xff);
            newByte[i] = ((byte) integer);
            key = newByte[newByte.length - 1] & key | 0x482D;
        }

        return new String(newByte);
    }
}
