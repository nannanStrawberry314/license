package com.lemonzuo.license.finalshell.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.lemonzuo.license.finalshell.service.FinalShellService;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LemonZuo
 * @create 2024-03-11 22:01
 */
@Service
public class FinalShellServiceImpl implements FinalShellService {

    private String md5(String msg) {
        return DigestUtil.md5Hex(msg).substring(8, 24);
    }

    private String keccak384(String msg) {
        Keccak.Digest384 digest = new Keccak.Digest384();
        byte[] hashBytes = digest.digest(msg.getBytes());
        return bytesToHex(hashBytes).substring(12, 28);
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public List<String> generateLicense(String machineCode) {
        List<String> result = new ArrayList<>();
        result.add("版本号 < 3.9.6 高级版: " + md5("61305" + machineCode + "8552"));
        result.add("版本号 < 3.9.6 专业版: " + md5("2356" + machineCode + "13593"));
        result.add("版本号 >= 3.9.6 高级版: " + keccak384(machineCode + "hSf(78cvVlS5E"));
        result.add("版本号 >= 3.9.6 专业版: " + keccak384(machineCode + "FF3Go(*Xvbb5s2"));
        return result;
    }
}
