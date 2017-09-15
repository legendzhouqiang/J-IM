package org.tio.runnable;

import lombok.extern.slf4j.Slf4j;
import org.tio.coding.IEncoder;
import org.tio.common.packet.AbsPacket;
import org.tio.common.packet.CheckSumSupport;
import org.tio.common.packet.ReadPacket;
import org.tio.runnable.common.AbstractTaskQueue;
import org.tio.util.CheckSumUtil;

import java.nio.ByteBuffer;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/21
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@Slf4j
public class EncodeTaskQueue<P> extends AbstractTaskQueue<P> {


    private IEncoder encoder;


    public EncodeTaskQueue() {

    }

    public void setEncoder(IEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void runTask(P p) {
        if (p instanceof CheckSumSupport) {
            CheckSumSupport support = (CheckSumSupport) p;
            byte[][] bytes = support.checkSum();
            byte checkSum = CheckSumUtil.calcCheckSum(bytes);
            support.setCheckSum(checkSum);
        }
        ByteBuffer buffer = encoder.encode(p);
        SendTaskQueue sendRunnable = context.getSendRunnable();
        boolean flag = sendRunnable.addMsg(buffer);
        if (!flag) {
            log.error("lost parket ");
        }

    }
}
