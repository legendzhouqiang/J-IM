package org.tio.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/29
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ByteUtil {

    public static byte[] intToByteArray(final int integer) {
        int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer : integer)) / 8;
        byte[] byteArray = new byte[4];

        for (byte n = 0; n < (integer < 0 ? 4 : byteNum); n++)
            byteArray[3 - n] = (byte) (integer >>> (n * 8));

        return byteArray;
    }

    public static int byteArrayToInt(byte[] b) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0xFF) << shift;
        }
        return value;
    }

    public static byte[] longToByteArray(long l) {
        byte[] byteArray = new byte[8];
        for (byte n = 0; n < 8; n++)
            byteArray[7 - n] = (byte) (l >>> (n * 8));
        return byteArray;
    }

    public static long byteArrayToLong(byte[] b) {
        long value = 0;
        for (byte i = 0; i < 8; i++) {
            int shift = (8 - 1 - i) * 8;
            value += (b[i] & 0xFFL) << shift;
        }
        return value;
    }

    public static void main(String[] args) {
        byte[] bytes = intToByteArray(-145456562);
        int i = byteArrayToInt(bytes);
        System.out.println(i);

        bytes = longToByteArray(-22548855565L);
        long l = byteArrayToLong(bytes);
        System.out.println(l);


    }
}
