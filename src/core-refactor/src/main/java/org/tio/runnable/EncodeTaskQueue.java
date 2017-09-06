package org.tio.runnable;

import lombok.extern.slf4j.Slf4j;
import org.tio.common.ChannelContext;
import org.tio.common.CoreConstant;
import org.tio.common.packet.AbstractPacket;
import org.tio.util.CheckSumUtil;

import java.nio.ByteBuffer;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/21
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@Slf4j
public class EncodeTaskQueue extends AbstractTaskQueue<AbstractPacket> {

    private ChannelContext context;

    public EncodeTaskQueue(ChannelContext context) {
        this.context = context;
    }

    @Override
    public void runTask(AbstractPacket packet) {
        if (context.isUse_checksum()) {
            byte[][] bytes = {packet.header(), packet.optional(), new byte[]{(byte) (packet.packetSeq() >> 8), (byte) packet.packetSeq()}, packet.body()};
            byte checkSum = CheckSumUtil.calcCheckSum(bytes);
            packet.setCheckSum(checkSum);
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(packet.header().length + packet.optional().length + 2 + packet.body().length);
        buffer.order(CoreConstant.default_byte_order);
        buffer.put(packet.header());
        buffer.put(packet.optional());
        buffer.put(new byte[]{(byte) (packet.packetSeq() >> 8), (byte) packet.packetSeq()});
        buffer.put(packet.body());
        SendTaskQueue sendRunnable = context.getSendRunnable();
        sendRunnable.addMsg(buffer);
    }
}
