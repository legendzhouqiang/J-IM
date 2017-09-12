package org.tio.coding;

import org.tio.common.packet.AbsPacket;
import org.tio.common.packet.ReadPacket;
import org.tio.common.packet.SuperPacket;

import java.nio.ByteBuffer;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/12
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 默认解码器
 */
public class DefaultDecoder {

    public ReadPacket decode(ByteBuffer buffer) {
        AbsPacket packet = new AbsPacket();
        short magic = buffer.getShort();
        if (SuperPacket.magic == magic) {
            return null;
        }

        byte packetType = buffer.get();
        packet.setPacketType(packetType);

        byte reserved = buffer.get();
        packet.setReserved(reserved);

        byte optLen = buffer.get();
        packet.setOptionalLength(optLen);

        short bodyLen = buffer.get();
        packet.setBodyLength(bodyLen);

        byte checkSum = buffer.get();
        packet.setCheckSum(checkSum);

        if (optLen != 0) {
            byte[] optData = new byte[optLen];
            buffer.get(optData, 0, optLen);
            packet.setOptional(optData);
        }

        if (bodyLen != 0) {
            short packetSeq = buffer.getShort();
            packet.setPacketSeq(packetSeq);

            byte[] bodyData = new byte[bodyLen - 2];
            buffer.get(bodyData, 0, bodyLen - 2);
            packet.setBody(bodyData);
        }
        return packet;
    }
}
