package org.tio.common.packet;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/24
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 默认心跳包
 */
public class HeartBeatPacket implements ReadPacket,WritePacket {

    @Override
    public byte packetType() {
        return 0;

    }

    @Override
    public WritePacket setPacketType(byte packetType) {
        return null;
    }

    @Override
    public boolean needReply() {
        return false;
    }

    @Override
    public WritePacket setNeedReplay() {
        return null;
    }

    @Override
    public byte reserved() {
        return 0;
    }

    @Override
    public WritePacket setReserved(byte reserved) {
        return null;
    }

    @Override
    public byte optionalLength() {
        return 0;
    }

    @Override
    public WritePacket setOptionalLength(byte optionalLength) {
        return null;
    }

    @Override
    public short bodyLength() {
        return 0;
    }

    @Override
    public WritePacket setBodyLength(short bodyLength) {
        return null;
    }

    @Override
    public byte checkSum() {
        return 0;
    }

    @Override
    public WritePacket setCheckSum(byte checkSum) {
        return null;
    }

    @Override
    public byte[] header() {
        return new byte[0];
    }

    @Override
    public byte[] optional() {
        return new byte[0];
    }

    @Override
    public WritePacket setOptional(byte[] optional) {
        return null;
    }

    @Override
    public short packetSeq() {
        return 0;
    }

    @Override
    public WritePacket setPacketSeq(short count) {
        return null;
    }

    @Override
    public byte[] body() {
        return new byte[0];
    }

    @Override
    public WritePacket setBody(byte[] body) {
        return null;
    }
}
