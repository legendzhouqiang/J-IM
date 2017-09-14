package org.tio.coding;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.tio.common.CoreConstant;
import org.tio.common.packet.ReadPacket;

import java.nio.ByteBuffer;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/12
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultEncoder implements IEncoder {

    private static IEncoder encoder = new DefaultEncoder();

    public static IEncoder newInstance() {
        return encoder;
    }

    @Override
    public ByteBuffer encode(ReadPacket packet){
        ByteBuffer buffer = ByteBuffer.allocateDirect(packet.header().length + packet.optional().length + 2 + packet.body().length);
        buffer.order(CoreConstant.default_byte_order);
        buffer.put(packet.header());
        buffer.put(packet.optional());
        buffer.put(new byte[]{(byte) (packet.packetSeq() >> 8), (byte) packet.packetSeq()});
        buffer.put(packet.body());
        return buffer;
    }
}
