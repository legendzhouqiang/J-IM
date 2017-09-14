package org.tio.runnable;

import lombok.extern.slf4j.Slf4j;
import org.tio.coding.IEncoder;
import org.tio.common.packet.ReadPacket;
import org.tio.util.CheckSumUtil;

import java.nio.ByteBuffer;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/21
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@Slf4j
public class EncodeTaskQueue extends AbstractTaskQueue<ReadPacket> {


    private IEncoder encoder;


    public EncodeTaskQueue() {

    }

    public void setEncoder(IEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void runTask(ReadPacket packet) {
        if (context.useChecksum()) {
            byte[][] bytes = {packet.header(), packet.optional(), new byte[]{(byte) (packet.packetSeq() >> 8), (byte) packet.packetSeq()}, packet.body()};
            byte checkSum = CheckSumUtil.calcCheckSum(bytes);
            packet.setCheckSum(checkSum);
        }
        ByteBuffer buffer = encoder.encode(packet);
        SendTaskQueue sendRunnable = context.getSendRunnable();
        boolean flag = sendRunnable.addMsg(buffer);
        if (!flag) {
            log.error("lost parket ");
        }

    }
}
