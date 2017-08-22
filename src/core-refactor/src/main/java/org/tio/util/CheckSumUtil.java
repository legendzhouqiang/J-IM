package org.tio.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/22
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 校验和工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckSumUtil {

    /**
     * 求校验和
     *
     * @param msg
     * @return
     */
    public static byte calcCheckSum(byte[][] msg) {

        /** 逐Byte添加位数和 */
        long mSum = 0;
        for (byte[] msgByteArr : msg) {
            for (byte byteMsg : msgByteArr) {
                long mNum = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);
                mSum += mNum;
            }
        }
        /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++) */

        return (byte) ~(mSum & 0xff);
    }

    /**
     * 检查校验和
     *
     * @param msg
     * @return
     */
    public static boolean judgeCheckSum(byte[][] msg) {
        long mSum = 0;
        for (byte[] msgByteArr : msg) {
            for (byte byteMsg : msgByteArr) {
                long mNum = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);
                mSum += mNum;
            }
        }
        return mSum == 0;
    }
}
