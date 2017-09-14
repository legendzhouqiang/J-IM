package org.tio.runnable;

import lombok.extern.slf4j.Slf4j;
import org.tio.coding.IDecoder;
import org.tio.common.IHandleStream;
import org.tio.common.packet.ReadPacket;
import org.tio.runnable.common.AbstractTaskQueue;
import org.tio.util.CheckSumUtil;

import java.nio.ByteBuffer;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/20
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:  t-io解码器
 */
@Slf4j
public class DecodeTaskQueue extends AbstractTaskQueue<ByteBuffer> {

    private IHandleStream stream;

    private boolean useChecksum;

    private IDecoder decoder;

    public DecodeTaskQueue(IHandleStream stream, boolean useChecksum) {
        this.stream = stream;
        this.useChecksum = useChecksum;
    }

    public void setDecoder(IDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public void runTask(ByteBuffer byteBuffer) throws InterruptedException {

        ReadPacket packet = decoder.decode(byteBuffer, msgQueue);

        if (useChecksum) {
            byte[][] bytes = {
                    packet.header(), packet.optional(), new byte[]{(byte) (packet.packetSeq() >> 8), (byte) packet.packetSeq()}, packet.body()
            };
            if (CheckSumUtil.judgeCheckSum(bytes)) {
                stream.handlePacket(packet);
            } else {
                log.warn("validate checkSum failed, and the packet[{}] will be discard.", packet.toString());
            }
        }
    }
}
