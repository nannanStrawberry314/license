package com.lemonzuo.license.mobaxterm.generator;

/**
 * @author wanna
 * @since 2019-01-02
 */
public class EncryptUtil {

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
