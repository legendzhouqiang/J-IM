package org.tio.common;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 消息处理模式
 */
public enum PacketHandlerMode {

    /** 当前线程中处理=>同步处理 */
    SINGLE_THREAD(1),
    /** 把packet丢到一个队列中，让线程池去处理=>异步处理 */
    QUEUE(2);

    private final int value;

    public static PacketHandlerMode forNumber(int value) {
        switch (value) {
            case 1:
                return SINGLE_THREAD;
            case 2:
                return QUEUE;
            default:
                return null;
        }
    }

    PacketHandlerMode(int value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }
}
