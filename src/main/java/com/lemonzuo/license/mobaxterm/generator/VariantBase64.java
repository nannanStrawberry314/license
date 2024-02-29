package com.lemonzuo.license.mobaxterm.generator;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wanna
 * @since 2019-01-02
 */
public class VariantBase64 {

    private static final String TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    private static Map<Integer, String> MAP = new HashMap<>(128);

    static {
        String[] split = TABLE.split("");
        for (int i = 0; i < split.length; i++) {
            String str = split[i];
            MAP.put(i, str);
        }
    }

    public static String variantBase64Encode(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        int length = bytes.length;
        int blockCount = length / 3;
        int leftBytes = length % 3;
        for (int i = 0; i < blockCount; i++) {
            int start = 3 * i;
            byte[] subBytes = new String(Arrays.copyOfRange(bytes, start, start + 3)).getBytes();
            int codingInt = convertByteToInt(subBytes);
            String block = MAP.get(codingInt & 0x3f);
            block += MAP.get((codingInt >> 6) & 0x3f);
            block += MAP.get((codingInt >> 12) & 0x3f);
            block += MAP.get((codingInt >> 18) & 0x3f);
            result.append(block);
        }
        if (leftBytes == 0) {
            return result.toString();
        } else if (leftBytes == 1) {
            byte[] subBytes = Arrays.copyOfRange(bytes, 3 * blockCount, length);
            String block = rightMoveSix(subBytes);
            result.append(block);
            return result.toString();
        } else {
            byte[] subBytes = Arrays.copyOfRange(bytes, 3 * blockCount, length);
            int codingInt = convertByteToInt(subBytes);
            String block = rightMoveSix(subBytes);
            block += MAP.get((codingInt >> 12) & 0x3f);
            result.append(block);
            return result.toString();
        }
    }

    /**
     * 向右移 6 位
     *
     * @param subBytes 子数组
     * @return string
     */
    private static String rightMoveSix(byte[] subBytes) {
        int codingInt = convertByteToInt(subBytes);
        String block = MAP.get(codingInt & 0x3f);
        block += MAP.get((codingInt >> 6) & 0x3f);
        return block;
    }

    /**
     * java 实现
     * int.from_bytes(bytes[], 'little')
     *
     * @param bytes 字节数组
     * @return int 值
     * @link https://stackoverflow.com/questions/36637561/convert-bytes-to-integer-in-java-vs-python
     */

    private static int convertByteToInt(byte[] bytes) {
        int half = 2;
        for (int i = 0; i < bytes.length / half; i++) {
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = temp;
        }
        return new BigInteger(1, bytes).intValue();
    }

}
