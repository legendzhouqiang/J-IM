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

    private static byte[] intToByteArray(final int integer) {
        int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer : integer)) / 8;
        byte[] byteArray = new byte[4];

        for (int n = 0; n < byteNum; n++)
            byteArray[3 - n] = (byte) (integer >>> (n * 8));

        return (byteArray);
    }

    private static int byteArrayToInt(byte[] b) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

    public static void main(String[] args) {
        byte[] bytes = intToByteArray(6895854);
        int i = byteArrayToInt(bytes);
        System.out.println(i);
    }
}
