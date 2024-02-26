package com.lemonzuo.license.jrebel.util;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @author LemonZuo
 */
public class ByteUtil {
    private static final Random RANDOM;

    public static String a(final byte[] binaryData) {
        if (binaryData == null) {
            return null;
        }
        return new String(Base64.encodeBase64(binaryData), StandardCharsets.UTF_8);
    }

    public static byte[] decodeBase64(final String s) {
        if (s == null) {
            return null;
        }
        return Base64.decodeBase64(s.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] nextBytes(final int n) {
        final byte[] array = new byte[n];
        ByteUtil.RANDOM.nextBytes(array);
        return array;
    }

    static {
        RANDOM = new Random();
    }
}
