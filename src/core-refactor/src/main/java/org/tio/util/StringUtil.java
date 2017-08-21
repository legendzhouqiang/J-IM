package org.tio.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/18
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 字符串工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {

    private static final Pattern IP_PATTERN = Pattern.compile("((1[0-9][0-9]\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)|([1-9][0-9]\\.)|([0-9]\\.)){3}((1[0-9][0-9])|(2[0-4][0-9])|(25[0-5])|([1-9][0-9])|([0-9]))");

    public static boolean isIp(String ipStr) {
        Matcher matcher = IP_PATTERN.matcher(ipStr);
        boolean b = matcher.find();
        String matchStr = matcher.group(0);
        System.out.println(matchStr);
        System.out.println(b);
        if (b && matchStr.length() == ipStr.length()) {
            return true;
        }
        return false;
    }
}
