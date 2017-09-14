package org.tio.coding;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tio.common.packet.AbsPacket;
import org.tio.common.packet.ReadPacket;
import org.tio.common.packet.SuperPacket;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/12
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 默认解码器
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultDecoder implements IDecoder {

    private static IDecoder decoder = new DefaultDecoder();

    public static IDecoder newInstance() {
        return decoder;
    }

    @Override
    public ReadPacket decode(ByteBuffer buffer, LinkedBlockingDeque<ByteBuffer> msgQueue) throws InterruptedException {
        AbsPacket packet = new AbsPacket();
        while (buffer.remaining() > 1) {
            short magic = buffer.getShort();
            if (SuperPacket.magic == magic) {
                break;
            }
        }

        while (!buffer.hasRemaining()) {
            buffer = mergeByteBuffer(buffer, msgQueue);
        }
        byte packetType = buffer.get();
        packet.setPacketType(packetType);

        while (!buffer.hasRemaining()) {
            buffer = mergeByteBuffer(buffer, msgQueue);
        }
        byte reserved = buffer.get();
        packet.setReserved(reserved);

        while (!buffer.hasRemaining()) {
            buffer = mergeByteBuffer(buffer, msgQueue);
        }
        byte optLen = buffer.get();
        packet.setOptionalLength(optLen);

        while (!(buffer.remaining() > 2)) {
            buffer = mergeByteBuffer(buffer, msgQueue);
        }
        short bodyLen = buffer.get();
        packet.setBodyLength(bodyLen);

        while (!buffer.hasRemaining()) {
            buffer = mergeByteBuffer(buffer, msgQueue);
        }
        byte checkSum = buffer.get();
        packet.setCheckSum(checkSum);

        while (!(buffer.remaining() > optLen)) {
            buffer = mergeByteBuffer(buffer, msgQueue);
        }
        byte[] optData = new byte[optLen];
        buffer.get(optData, 0, optLen);

        while (!(buffer.remaining() > 2)) {
            buffer = mergeByteBuffer(buffer, msgQueue);
        }
        short packetSeq = buffer.getShort();

        while (!(buffer.remaining() > bodyLen - 2)) {
            buffer = mergeByteBuffer(buffer, msgQueue);
        }
        byte[] bodyData = new byte[bodyLen - 2];
        buffer.get(bodyData, 0, bodyLen - 2);

        if (buffer.hasRemaining()) {
            boolean flag = msgQueue.offerFirst(buffer.slice());
            if (!flag) {
                log.warn("put the left ByteBuffer into msgQueue failed.");
            }
        }
        return packet;
    }

    private ByteBuffer mergeByteBuffer(ByteBuffer buffer, LinkedBlockingDeque<ByteBuffer> msgQueue) throws InterruptedException {
        int remaining = buffer.remaining();
        ByteBuffer nextBuffer = msgQueue.take();
        int capacity = remaining + nextBuffer.capacity();
        ByteBuffer newByteBuffer = ByteBuffer.allocateDirect(capacity);
        newByteBuffer.put(buffer.slice());
        newByteBuffer.put(nextBuffer);
        newByteBuffer.flip();
        return newByteBuffer;
    }
}
